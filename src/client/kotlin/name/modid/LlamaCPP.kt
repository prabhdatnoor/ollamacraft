package name.modid

import de.kherud.llama.ModelParameters
import eu.midnightdust.fabric.example.config.MinarratorConfig.Companion.gpuLayers
import eu.midnightdust.fabric.example.config.MinarratorConfig.Companion.modelPath
import eu.midnightdust.fabric.example.config.MinarratorConfig.Companion.temperature


class LlamaCPP {
    init {
        var modelParams: ModelParameters = ModelParameters()
            .setModel(modelPath)
            .setGpuLayers(gpuLayers)
            .setTemp(temperature)
    }


}