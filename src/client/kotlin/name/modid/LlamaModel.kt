package name.modid

import de.kherud.llama.InferenceParameters
import de.kherud.llama.LlamaModel
import de.kherud.llama.ModelParameters
import eu.midnightdust.fabric.example.config.MODELPATH


object LlamaModel {
    private lateinit var model: LlamaModel
    private var gpuLayers: Int = 0
    private var temperature: Float = 0.5F
    private var maxTokens: Int = 256
    private var modelPath: String = MODELPATH

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
        modelPath: String = this.modelPath,
        gpuLayers: Int = this.gpuLayers,
        temperature: Float = this.temperature,
        maxTokens: Int = this.maxTokens
    ) {
        this.modelPath = modelPath
        this.gpuLayers = gpuLayers
        this.temperature = temperature
        this.maxTokens = maxTokens
        model = LlamaModel(
            ModelParameters()
                .setModel(modelPath)
                .setGpuLayers(gpuLayers)
        )
    }

    fun prompt(text: String, preprompt:String = NARRATOR_PRE): String {
        require(::model.isInitialized) { "Narrator not initialized. Call initialize() first." }


        val prompt = """
        $preprompt
        User: $text
        Llama: 
    """.trimIndent()

        return buildString {
            model.generate(
                InferenceParameters(prompt)
                    .setTemperature(temperature)
                    .setStopStrings("\n\n", "###", "```")
            ).forEach { append(it) }
        }
    }

    fun close() {
        if (::model.isInitialized) {
            model.close()
        }
    }
}






