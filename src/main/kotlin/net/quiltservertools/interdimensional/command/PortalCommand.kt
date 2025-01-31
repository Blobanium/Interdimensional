package net.quiltservertools.interdimensional.command

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.command.argument.BlockStateArgument
import net.minecraft.command.argument.BlockStateArgumentType
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.quiltservertools.interdimensional.command.InterdimensionalCommand.success
import net.quiltservertools.interdimensional.command.argument.PortalOptionsArgumentType
import net.quiltservertools.interdimensional.command.argument.ServerDimensionArgument
import net.quiltservertools.interdimensional.portals.portal.PortalIgnitionSource
import net.quiltservertools.interdimensional.portals.util.ColorUtil
import net.quiltservertools.interdimensional.world.Portal
import net.quiltservertools.interdimensional.world.PortalManager
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.util.registry.DynamicRegistryManager
import com.mojang.serialization.Dynamic
import net.minecraft.command.CommandSource
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.CommandManager.*
import net.minecraft.server.dedicated.MinecraftDedicatedServer

object PortalCommand : Command {
    override fun register(source: DynamicRegistryManager.Immutable): LiteralCommandNode<ServerCommandSource> {
        return literal("portal")
            .requires(Permissions.require("interdimensional.command.portal", 3))
            .then(
                literal("add").then(
                    argument("name", StringArgumentType.string()).then(
                        ServerDimensionArgument.dimension("destination")
                            .then(
                                argument(
                                    "frame_block",
                                    BlockStateArgumentType.blockState(CommandRegistryAccess(source))
                                )
                                    .executes {
                                        return@executes add(
                                            it.source,
                                            StringArgumentType.getString(it, "name"),
                                            ServerDimensionArgument.get(it, "destination").registryKey.value,
                                            BlockStateArgumentType.getBlockState(it, "frame_block"),
                                            ""
                                        )
                                    }
                                    .then(
                                        argument("options", StringArgumentType.greedyString()).suggests(
                                            PortalOptionsArgumentType()
                                        ).executes {
                                            return@executes add(
                                                it.source,
                                                StringArgumentType.getString(it, "name"),
                                                ServerDimensionArgument.get(it, "destination").registryKey.value,
                                                BlockStateArgumentType.getBlockState(it, "frame_block"),
                                                StringArgumentType.getString(it, "options")
                                            )
                                        }
                                    )
                            )
                    )
                ))
            .then(
                literal("remove").requires(Permissions.require("interdimensional.command.portal.delete", 4))
                    .then(argument("name", StringArgumentType.string()).executes {
                        remove(
                            it.source,
                            StringArgumentType.getString(it, "name")
                        )
                    })
            )
            .build()
    }

    private fun add(
        source: ServerCommandSource,
        name: String,
        destination: Identifier,
        blockState: BlockStateArgument,
        properties: String
    ): Int {
        val props = PortalOptionsArgumentType().rawProperties(properties)
        val flat = props.containsKey("flat") && (props["flat"] as Boolean)

        val ignitionSource = PortalIgnitionSource.FIRE


        val sourceWorld = if (props.containsKey("source_world")) {
            props["source_world"] as Identifier
        } else {
            source.server.overworld.registryKey.value
        }

        val permission = if (props.containsKey("permission")) {
            0
        } else {
            props["permission"] as Int
        }

        if (props.containsKey("color")) {
            val color = props["color"] as Formatting
            val rgb: FloatArray = ColorUtil.getColorForBlock(color.colorValue ?: 0)
            PortalManager.addPortal(
                Portal(
                    name,
                    blockState.blockState.block,
                    destination,
                    sourceWorld,
                    (rgb[0] * 255).toInt(),
                    (rgb[1] * 255).toInt(),
                    (rgb[2] * 255).toInt(),
                    flat,
                    ignitionSource,
                    permission
                )
            )
        } else {
            PortalManager.addPortal(
                Portal(
                    name,
                    blockState.blockState.block,
                    destination,
                    sourceWorld,
                    (0),
                    (0),
                    (0),
                    flat,
                    ignitionSource,
                    permission
                )
            )
        }

        source.sendFeedback(
            "Created portal from $sourceWorld to $destination with frame ${blockState.blockState.block}".success(),
            false
        )

        return 1
    }

    private fun remove(source: ServerCommandSource, name: String): Int {
        PortalManager.removePortal(name)
        source.sendFeedback("Removed portal $name".success(), false)
        return 1
    }
}