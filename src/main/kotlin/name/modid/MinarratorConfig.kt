package eu.midnightdust.fabric.example.config

import eu.midnightdust.lib.config.MidnightConfig
import javax.swing.JFileChooser

class MinarratorConfig : MidnightConfig() {
    companion object {
        const val MODEL = "model"
        @Comment(category = MODEL, name = "Select the path to your .gguf model file")
        lateinit var modelPathComment: Comment

        @Entry(
            category = MODEL,
            selectionMode = JFileChooser.FILES_ONLY,
            fileExtensions = ["gguf"],
            fileChooserType = JFileChooser.OPEN_DIALOG,
            name = "Model Path",
        )
        @JvmField
        var model_path: String = ""
    }
}