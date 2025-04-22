package name.modid

import eu.midnightdust.fabric.example.config.MinarratorConfig
import eu.midnightdust.lib.config.MidnightConfig
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory


object Minarrator : ModInitializer {
    private val logger = LoggerFactory.getLogger("mi-narrator")

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		logger.info("Hello Fabric world!")
        MidnightConfig.init("minarrator", MinarratorConfig::class.java)
    }
}

