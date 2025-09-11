package name.modid

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Formatting

// send error message to player
fun sendErrorMessageToPlayer(entity: PlayerEntity?, message: String) {
    if (entity == null) {
        return
    }
    entity.sendMessage(Text.literal(message).formatted(Formatting.RED), false)
}

// send error message to minecraft world
fun sendErrorMessageToWorld(message: String) {
    // get the player
    val player = MinecraftClient.getInstance().player
    player?.sendMessage(Text.literal(message).formatted(Formatting.RED), false)
}