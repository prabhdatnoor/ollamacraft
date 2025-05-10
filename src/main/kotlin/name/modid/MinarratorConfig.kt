package name.modid

import eu.midnightdust.lib.config.MidnightConfig

class MinarratorConfig : MidnightConfig() {
    @Suppress("unused")
    companion object {
        @Entry(category = "Network", name = "Server Port", min = 1.0, max = 65535.0)
        @JvmStatic
        var port: Int = 25565  // Default Minecraft server port

        @Entry(category = "Network", name = "Server Host Address")
        @JvmStatic
        var hostAddress = "localhost"  // Default host address
    }
}