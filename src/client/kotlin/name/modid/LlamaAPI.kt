package name.modid

import io.github.ollama4j.OllamaAPI
import io.github.ollama4j.models.chat.OllamaChatMessageRole
import io.github.ollama4j.models.chat.OllamaChatRequestBuilder
import io.github.ollama4j.utils.Options
import io.github.ollama4j.utils.OptionsBuilder

object LlamaAPI {
    private var port: Int = 11434
    private var hostAddress: String = "localhost"
    private lateinit var ollamaAPI: OllamaAPI

    // for the narrator
    val NARRATOR_PRE = """
        You are a very drunk Minecraft narrator. You misremember things, 
        repeat yourself, and get overly emotional about blocks. You think 
        you're profound but you're just slurring. Respond in 1-2 short 
        sentences max (to save CPU).
    """.trimIndent()

    // for general prompts
    val GENERAL_PRE = """
        You are a very drunk Minecraft assistant. You misremember things, 
        repeat yourself, and get overly emotional about blocks. You think 
        you're profound but you're just slurring. Respond in 1-2 short 
        sentences max (to save CPU)
    """.trimIndent()

    fun initialize(
        hostAddress: String = this.hostAddress,
        port: Int = this.port,
    ) {
        try {
            // set values
            this.hostAddress = hostAddress
            this.port = port

            // setup api
            ollamaAPI = OllamaAPI("${hostAddress}:${port}")
            ollamaAPI.setVerbose(false)

            // ping the server
            if (!ollamaAPI.ping()) {
                // handle error
                println("Error: Ollama API is not reachable at $hostAddress:$port")
            } else {
                println("Ollama API initialized successfully at $hostAddress:$port")
            }
        } catch (e: Exception) {
            // handle error
            println("Error initializing Ollama API: ${e.message}")
            e.printStackTrace()
        }

    }

    fun prompt(
        modelName: String,
        text: String,
        preprompt: String = NARRATOR_PRE,
        options: Options = OptionsBuilder().setTemperature(0.5f).setNumGpu(0).build()
    ): String {

        // select model
        var chatRequestBuilder = OllamaChatRequestBuilder.getInstance(modelName)

        val prompt = """
        User: $text
        Llama: 
    """.trimIndent()


        var requestModel = chatRequestBuilder.withMessage(OllamaChatMessageRole.SYSTEM, preprompt)
            .withMessage(OllamaChatMessageRole.USER, prompt)
            .withOptions(options)
            .build()


        return ollamaAPI.chat(requestModel)
            .responseModel.message.content
    }
}






