package name.modid

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import name.modid.Constants.Client.DEFAULT_MODELNAME
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents.AfterDamage
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandManager.RegistrationEnvironment
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import java.util.function.Supplier
import kotlin.random.Random

object OllamaCraftClient : ClientModInitializer {
    var MODELNAME: String = DEFAULT_MODELNAME

    private lateinit var api: PromptHandler

    override fun onInitializeClient() {
        try {
            try {
                // initialize the api
                api = PromptHandler(
                    OllamaCraftConfig.port,
                    OllamaCraftConfig.hostAddress
                )
            } catch (e: Exception) {
                // handle error
                println("Error initializing LlamaAPI: ${e.message}")
                sendErrorMessageToWorld("LlamaAPI could not be initialized!")
                e.printStackTrace()
            }

            // register a prompt command
            CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource?>?, registryAccess: CommandRegistryAccess?, environment: RegistrationEnvironment? ->
                dispatcher!!.register(
                    CommandManager.literal("ollama")
                        .then(
                            CommandManager.literal("prompt")
                                .then(
                                    CommandManager.argument("prompt", StringArgumentType.greedyString())
                                        .executes(this::executeManualPrompt)
                                )
                        ).then(
                            CommandManager.literal("list").executes(this::listModels)
                        ).then(
                            CommandManager.literal("select")
                                .then(
                                    CommandManager.argument("model", StringArgumentType.string())
                                        .executes(this::selectModel)
                                )
                        )
                )
            })


            // This code is injected into the start of MinecraftClient.run()V
            ServerLivingEntityEvents.AFTER_DAMAGE.register(AfterDamage { e: LivingEntity?, damageSource: DamageSource?, damage: Float, entityHealth: Float, entity: Boolean ->
                // if entity doesnt exist or isnt a player
                if (e!= null && !e.isPlayer) {
                    return@AfterDamage
                }

                var damageSourceName = damageSource?.name ?: "Unknown"
                if (damageSourceName.startsWith("inWall")) {
                    damageSourceName = "in wall"
                }

                println("Entity $e took $damage damage from $damageSourceName")

                if (e != null) {
                    onPlayerDamage(e as PlayerEntity, damageSource as DamageSource, damage.toDouble())
                }
            })

        } catch (e: Exception) {
            // handle error
            println("Error initializing OLLamaCraftClient: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun onPlayerDamage(playerEntity: PlayerEntity, damageSource: DamageSource, amount: Double) {
        // player's health percent now
        val healthPercentage: Int = ((playerEntity.health / playerEntity.maxHealth) * 100f).toInt()
        // how much percent of their health was removed
        val damagePercentage: Int = ((amount / playerEntity.maxHealth) * 100f).toInt()
        // distance from entity
        var distance: String = ""

        if (damageSource.source != null) {
            val distanceUnits = playerEntity.distanceTo(damageSource.source)
           distance = ", from $distanceUnits units away";
        }

        var prompt =
            "Player was just damaged. here is info: ${damageSource.name} did $damagePercentage % to the player$distance. Player's health is at $healthPercentage % now. Some more info on damage source: ${damageSource.type.toString()}"

        // if player health is now very low
        if (healthPercentage < 0.10f) {
            prompt += "Player health is very low"
            // if the hit took more than 50% damage off
        } else if (damagePercentage >= 0.35f) {
            prompt += "This hit dealth huge damage"
            // for 10% of all events do this
        } else if (boolByPercentage(70)) {
            // 70% of the time we dont want to send a prompt
            prompt = ""
        }

        executePrompt(playerEntity, prompt)
    }


    private fun executeManualPrompt(context: CommandContext<ServerCommandSource?>): Int {
        // get the string prompt
        val prompt = context.getArgument("prompt", String::class.java)

        return executePrompt(context.source?.entity as PlayerEntity?, prompt)
    }

    // ollama prompt <prompt>
    // if given context
    private fun executePrompt(entity: PlayerEntity?, prompt: String): Int {
        if (entity == null) {
            println("no valid source for action provided!")
            return 0
        }

        val userName = entity.displayName?.string ?: "User"
        lateinit var output: String
        try {
            // generate
            output = api.prompt(MODELNAME, prompt, userName, Constants.Client.SystemPrompts.GENERAL)
        } catch (e: Exception) {
            // handle error
            println("Error generating prompt: ${e.message}")
            sendErrorMessageToPlayer(entity, "Error generating prompt. Please refer to the logs.")
            return 0
        }

        // send feedback to the player
        entity.sendMessage(Text.literal(output), false)
        return 1
    }

    fun selectModel(context: CommandContext<ServerCommandSource?>): Int {
        var model = context.getArgument("model", String::class.java)

        // if model is null
        if (model == null) {
            println("No model provided!. Please provide exact model name or index from `ollama list`")
        }

        val models = api.listModels()

        if (models.isEmpty()) {
            println("No models in Ollama!")
        }

        // see if model exists in model list
        if (models.find { it == model } == null) {
            // try to parse model int
            val modelParsed = model.toIntOrNull()
            if (modelParsed == null || modelParsed < 1 || modelParsed > models.size) {
                println("Model, or model index not found: $model")
                sendErrorMessageToPlayer(
                    context.source?.entity as PlayerEntity?,
                    "Ollama model could not be found. If using index from `ollama list`, please ensure you are using a valid index. Otherwise please check the model name is correct"
                )
                return 0
            }

            // convert to 0-index
            model = models[modelParsed - 1]
        }

        // set model name
        MODELNAME = model

        // tell server what model we set
        context.getSource()!!.sendFeedback(Supplier { Text.literal("Set model to: $model") }, false)
        return 1
    }

    fun listModels(context: CommandContext<ServerCommandSource?>): Int {
        lateinit var output: String

        try {
            // output models with number in front to pick easily in switch command
            output = api.listModels().mapIndexed { index, string -> "${index + 1}: $string" }.joinToString("\n")
        } catch (e: Exception) {
            println("Error listing models: ${e.message}")
            sendErrorMessageToPlayer(
                context.source?.entity as PlayerEntity?,
                "Error listing models. Please refer to the logs."
            )
            return 0
        }

        // send list to player
        context.getSource()!!.sendFeedback(Supplier { Text.literal(output) }, false)
        return 1
    }
}

// % chance to return true. provide as int 0-100
fun boolByPercentage(percent: Int): Boolean {
    return Random.nextFloat() < (percent / 100)
}