package eu.midnightdust.fabric.example.config

import eu.midnightdust.lib.config.MidnightConfig
import javax.swing.JFileChooser

class MinarratorConfig : MidnightConfig() {
    companion object {
        @Comment(category = "files", name = "Select the path to your .gguf model file")
        lateinit var modelPathComment: Comment

        @Entry(
            category = "files",
            selectionMode = JFileChooser.FILES_ONLY,
            fileExtensions = ["gguf"],
            fileChooserType = JFileChooser.OPEN_DIALOG,
            name = "Model Path"
        )
        var model_path: String = ""
    }
}