package name.modid

import eu.midnightdust.lib.config.MidnightConfig
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory


object LlamaCraft : ModInitializer {
    private val logger = LoggerFactory.getLogger("llamacraft")

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		logger.info("Hello Fabric world!")
        MidnightConfig.init("llamacraft", LlamaCraftConfig::class.java)
    }
}

