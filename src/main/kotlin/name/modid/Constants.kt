package name.modid

object Constants {
    object Client {
        const val DEFAULT_OLLAMA_HOST = "http://localhost"
        const val DEFAULT_OLLAMA_PORT = 11434
        const val DEFAULT_MODELNAME = "hf.co/huggingfacetb/smollm2-360m-instruct-gguf"

        object SystemPrompts {
            // for the narrator
            val NARRATOR = """
        You are a very distracted Minecraft narrator. You misremember things, 
        repeat yourself, and get overly emotional about blocks. You think 
        you're profound but you're just slurring. Respond in 1-2 short 
        sentences max (to save CPU).
    """.trimIndent()

            // for general prompts
            val GENERAL = """
        You are a very distracted Minecraft assistant. You misremember things, 
        repeat yourself, and get overly emotional about blocks. You think 
        you're profound but you're just slurring. Respond in 1-2 short 
        sentences max (to save CPU)
    """.trimIndent()
        }
    }
}