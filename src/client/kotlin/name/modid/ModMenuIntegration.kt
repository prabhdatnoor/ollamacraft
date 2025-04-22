package name.modid

import com.sun.jna.platform.unix.X11
import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import eu.midnightdust.fabric.example.config.MinarratorConfig
import eu.midnightdust.lib.config.MidnightConfig
import net.minecraft.client.gui.screen.Screen

class ModMenuIntegration : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return ConfigScreenFactory { parent: Screen? ->
            MidnightConfig.getScreen(parent, "mi-narrator")
        }
    }
}