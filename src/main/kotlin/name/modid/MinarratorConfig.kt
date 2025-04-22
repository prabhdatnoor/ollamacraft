package eu.midnightdust.fabric.example.config

import eu.midnightdust.lib.config.MidnightConfig
import javax.swing.JFileChooser

// global constant of filepath
const val MODELPATH = "~/models/smollm2-360m-instruct-q8_0.gguf"


class MinarratorConfig : MidnightConfig() {
    companion object {
        const val MODEL = "model"
        @Comment(category = MODEL) lateinit var modelPathComment: Comment

        @Entry(
            category = MODEL,
            selectionMode = JFileChooser.FILES_ONLY,
            fileExtensions = ["gguf"],
            fileChooserType = JFileChooser.OPEN_DIALOG,
        )
        @JvmField
        var modelPath: String = MODELPATH

        @Comment(category = MODEL) lateinit var modelConfiguration: Comment
        // temperature
        @Entry(category = MODEL, max = 1.0, min= 0.0) var temperature: Float = 0.5F
        // gpu layers
        @Entry(category= MODEL) var gpuLayers : Int = 0
        @Entry(category = MODEL) var maxTokens : Int = 256
    }
}