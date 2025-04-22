package name.modid
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import eu.midnightdust.fabric.example.config.MinarratorConfig.Companion.gpuLayers
import eu.midnightdust.fabric.example.config.MinarratorConfig.Companion.maxTokens
import eu.midnightdust.fabric.example.config.MinarratorConfig.Companion.modelPath
import eu.midnightdust.fabric.example.config.MinarratorConfig.Companion.temperature

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandManager.RegistrationEnvironment
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import java.util.function.Supplier


object MinarratorClient : ClientModInitializer {
	override fun onInitializeClient() {
		try {
			LlamaModel.initialize(modelPath, gpuLayers, temperature, maxTokens)

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

		} finally {
			LlamaModel.close()
		}
	}

	// minar prompt <prompt>
	// if given context
	private fun executePrompt(context: CommandContext<ServerCommandSource?>): Int {
		// get the string prompt
		val prompt = context.getArgument("prompt", String::class.java)
		// generate
		val output = LlamaModel.prompt(prompt, LlamaModel.GENERAL_PRE)
		// send feedback to the player
		context.getSource()!!.sendFeedback(Supplier { Text.literal(output) }, false)
		return 1
	}
}