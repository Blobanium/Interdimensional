package net.quiltservertools.interdimensional

import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarted
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopping
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.WorldSavePath
import net.minecraft.util.registry.DynamicRegistryManager
import net.quiltservertools.interdimensional.command.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import xyz.nucleoid.fantasy.Fantasy

object Interdimensional : ModInitializer {
    lateinit var FANTASY: Fantasy
    val LOGGER: Logger = LogManager.getLogger()
    private lateinit var CONFIG: Config
    lateinit var REGISTRY: DynamicRegistryManager.Immutable

    override fun onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(ServerStarted { server: MinecraftServer ->
            serverStarting(server)
        })
        ServerLifecycleEvents.SERVER_STOPPING.register(ServerStopping {
            serverStopping()
        })
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { commandDispatcher: CommandDispatcher<ServerCommandSource>, commandRegistryAccess: CommandRegistryAccess, registrationEnvironment: CommandManager.RegistrationEnvironment ->
            registerCommands(commandDispatcher)
        })
    }

    private fun serverStarting(server: MinecraftServer) {
        FANTASY = Fantasy.get(server)
        CONFIG = Config.createConfig(server.getSavePath(WorldSavePath.ROOT).resolve("dimensions.json"))
        REGISTRY = server.registryManager
    }

    private fun registerCommands(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val root = InterdimensionalCommand.register(dispatcher)

        dispatcher.root.addChild(root)
        root.addChild(CreateCommand.register(REGISTRY))
        root.addChild(DeleteCommand.register(REGISTRY))
        root.addChild(PortalCommand.register(REGISTRY))
    }

    private fun serverStopping() {
        CONFIG.shutdown()
    }
}