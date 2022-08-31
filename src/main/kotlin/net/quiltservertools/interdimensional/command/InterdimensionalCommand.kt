package net.quiltservertools.interdimensional.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.tree.LiteralCommandNode
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.LiteralTextContent
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.text.Style

object InterdimensionalCommand {
    fun String.error(): Text {
        return Text.literal("Error: ").formatted(Formatting.RED).append(Text.literal(this))
    }

    fun String.success(): Text {
        return Text.literal("Success: ").formatted(Formatting.GREEN).append(Text.literal(this))
    }

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>): LiteralCommandNode<ServerCommandSource> {
        return dispatcher.register(
            CommandManager.literal("dim").requires(Permissions.require("interdimensional.command.root", 3))
        )
    }
}
