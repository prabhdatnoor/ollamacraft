package name.modid

import com.mojang.brigadier.context.CommandContext
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.util.Formatting

// send error message to player
fun sendErrorMessageToPlayer(entity: Entity?, message: String) {
    if (entity == null) {
        return
    }
    entity.server?.sendMessage(Text.literal(message).formatted(Formatting.RED))
}

// send error message to minecraft world
fun sendErrorMessageToWorld(message: String) {
    // get the player
    val player = MinecraftClient.getInstance().player
    player?.sendMessage(Text.literal(message).formatted(Formatting.RED), false)
}