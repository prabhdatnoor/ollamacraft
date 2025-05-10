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

var DEFAULT_MODELNAME = "SmolLM2-360M-Instruct-GGUF"

object MinarratorClient : ClientModInitializer {
	var MODELNAME : String = DEFAULT_MODELNAME

	override fun onInitializeClient() {
		try {
			// initialize the api
			LlamaAPI.initialize(
				hostAddress = MinarratorConfig.hostAddress,
				port = MinarratorConfig.port
			)


			CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource?>?, registryAccess: CommandRegistryAccess?, environment: RegistrationEnvironment? ->
				dispatcher!!.register(
					CommandManager.literal("minar")
						.then(
							CommandManager.literal("prompt")
								.then(
									CommandManager.argument("prompt", StringArgumentType.greedyString())
										.executes(this::executePrompt)
								)
						)
				)
			})

		} catch (e: Exception) {
			// handle error
			println("Error initializing MinarratorClient: ${e.message}")
			e.printStackTrace()
		}
	}

	// minar prompt <prompt>
	// if given context
	private fun executePrompt(context: CommandContext<ServerCommandSource?>): Int {
		// get the string prompt
		val prompt = context.getArgument("prompt", String::class.java)
		// generate
		val output = LlamaAPI.prompt(MODELNAME, prompt, LlamaAPI.GENERAL_PRE)
		// send feedback to the player
		context.getSource()!!.sendFeedback(Supplier { Text.literal(output) }, false)
		return 1
	}
}