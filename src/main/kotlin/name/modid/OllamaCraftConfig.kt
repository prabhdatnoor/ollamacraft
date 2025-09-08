package name.modid

import eu.midnightdust.lib.config.MidnightConfig
import name.modid.Constants.Client.DEFAULT_OLLAMA_HOST
import name.modid.Constants.Client.DEFAULT_OLLAMA_PORT

class OllamaCraftConfig : MidnightConfig() {
    @Suppress("unused")
    companion object {
        @Entry(category = "Network", name = "Server Host Address")
        @JvmStatic
        var hostAddress = DEFAULT_OLLAMA_HOST // Default host address

        @Entry(category = "Network", name = "Server Port", min = 1.0, max = 65535.0)
        @JvmStatic
        var port: Int = DEFAULT_OLLAMA_PORT  // Default Minecraft server port
    }
}