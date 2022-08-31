package net.quiltservertools.interdimensional.command

import com.mojang.brigadier.tree.LiteralCommandNode
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.registry.DynamicRegistryManager

interface Command {
    fun register(source: DynamicRegistryManager.Immutable): LiteralCommandNode<ServerCommandSource>
}