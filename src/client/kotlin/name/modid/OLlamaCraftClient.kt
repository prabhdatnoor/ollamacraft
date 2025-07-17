package name.modid

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandManager.RegistrationEnvironment
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import java.util.function.Supplier

import name.modid.Constants.Client.DEFAULT_MODELNAME

object OLlamaCraftClient : ClientModInitializer {
    var MODELNAME: String = DEFAULT_MODELNAME

    private lateinit var api: PromptHandler

    override fun onInitializeClient() {
        try {
            try {
                // initialize the api
                api = PromptHandler(
                    OLlamaCraftConfig.port,
                    OLlamaCraftConfig.hostAddress
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
                                        .executes(this::executePrompt)
                                )
                        )
                )
            })

            // register a list models command
            CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource?>?, registryAccess: CommandRegistryAccess?, environment: RegistrationEnvironment? ->
                dispatcher!!.register(
                    CommandManager.literal("ollama")
                        .then(
                            CommandManager.literal("list").executes(this::listModels)
                        )
                )
            })
        } catch (e: Exception) {
            // handle error
            println("Error initializing OLLamaCraftClient: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun checkApi(context: CommandContext<ServerCommandSource?>) {
        if (!::api.isInitialized) {
            sendErrorMessageToPlayer(context, "Ollama API is not initialized. Please check your configuration.")
            throw IllegalStateException("Ollama API is not initialized!")
        }
    }

    // minar prompt <prompt>
    // if given context
    private fun executePrompt(context: CommandContext<ServerCommandSource?>): Int {
        // get the string prompt
        val prompt = context.getArgument("prompt", String::class.java)

        // get the username of the user or just use User
        val userName = context.source?.player?.displayName?.string ?: "User"

        lateinit var output: String
        try {
            // generate
            output = api.prompt(MODELNAME, prompt, userName, Constants.Client.SystemPrompts.GENERAL)
        } catch (e: Exception) {
            // handle error
            println("Error generating prompt: ${e.message}")
            sendErrorMessageToPlayer(context, "Error generating prompt. Please refer to the logs.")
            return 0
        }

        // send feedback to the player
        context.getSource()!!.sendFeedback(Supplier { Text.literal(output) }, false)
        return 1
    }

    fun listModels(context: CommandContext<ServerCommandSource?>): Int {
        lateinit var output: String

        try {
            // output models with number in front to pick easily in switch command
            output = api.listModels().mapIndexed { index, string -> "${index + 1}: $string" }.joinToString("\n")
        } catch (e: Exception) {
            println("Error listing models: ${e.message}")
            sendErrorMessageToPlayer(context, "Error listing models. Please refer to the logs.")
            return 0
        }

        // send list to player
        context.getSource()!!.sendFeedback(Supplier { Text.literal(output) }, false)
        return 1
    }
}