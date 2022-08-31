package net.quiltservertools.interdimensional.gui.biomeSource

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryEntry
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.source.BiomeSource
import net.minecraft.world.biome.source.FixedBiomeSource
import net.quiltservertools.interdimensional.gui.elements.BiomeSourceElement
import net.quiltservertools.interdimensional.text

class SingleBiomeResult(private val element: BiomeSourceElement, private val biome: RegistryEntry<Biome>) : BiomeSourceResult(element) {
    override fun getItemStack(): ItemStack {
        return ItemStack(getItem()).setCustomName(element.handler.player.server.registryManager.get(Registry.BIOME_KEY).getId(biome.value())?.path?.text())
    }

    override val biomeSource: BiomeSource
        get() = TODO("Not yet implemented")

    private fun getItem(): Item {
        return Items.GRASS_BLOCK
        }
    }
