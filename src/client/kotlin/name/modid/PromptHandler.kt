package name.modid

import io.github.ollama4j.OllamaAPI
import io.github.ollama4j.models.chat.OllamaChatMessageRole
import io.github.ollama4j.models.chat.OllamaChatRequestBuilder
import io.github.ollama4j.utils.Options
import io.github.ollama4j.utils.OptionsBuilder
import name.modid.Constants.Client.DEFAULT_OLLAMA_HOST
import name.modid.Constants.Client.DEFAULT_OLLAMA_PORT
import name.modid.Constants.Client.SystemPrompts.NARRATOR

class PromptHandler (port : Int = DEFAULT_OLLAMA_PORT, hostAddress: String = DEFAULT_OLLAMA_HOST) {
    private lateinit var ollamaAPI: OllamaAPI
    init {
        try {
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
        userName: String = "User",
        systemPrompt: String = NARRATOR,
        options: Options = OptionsBuilder().setTemperature(0.5f).setNumGpu(0).build()
    ): String {
        // select model
        var chatRequestBuilder = OllamaChatRequestBuilder.getInstance(modelName)

        val prompt = """
        $userName: $text
        Llama: 
    """.trimIndent()


        var requestModel = chatRequestBuilder.withMessage(OllamaChatMessageRole.SYSTEM, systemPrompt)
            .withMessage(OllamaChatMessageRole.USER, prompt)
            .withOptions(options)
            .build()


        var response = ollamaAPI.chat(requestModel).responseModel

        // check if error
        if (response.error != null) {
            throw Exception("Error: ${response.error}")
        }

        return response.message.content
    }

    fun listModels(): List<String> {
        return ollamaAPI.listModels().map{ it.name }
    }
}






