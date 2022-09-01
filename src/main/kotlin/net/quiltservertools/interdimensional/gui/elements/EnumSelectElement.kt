package net.quiltservertools.interdimensional.gui.elements

import com.ginsberg.cirkle.circular
import eu.pb4.sgui.api.elements.GuiElementInterface
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.LiteralTextContent
import net.minecraft.text.Style

class EnumSelectElement<T : Enum<T>>(
    private val name: String,
    private val getEnum: () -> T,
    private val setEnum: (T) -> Any,
    private val items: Map<T, Item> = mapOf(),
    private val values: Array<T>
) : GuiElementInterface {
    override fun getItemStack(): ItemStack = (items[getEnum()]?.defaultStack
        ?: Items.STONE.defaultStack).setCustomName(LiteralTextContent("$name: ${getEnum().name}").parse(null, null, 0))

    override fun getGuiCallback() =
        GuiElementInterface.ClickCallback { index, type, action, gui ->
            val circularList = values.asList().circular()
            if (type.isLeft) {
                setEnum(circularList[getEnum().ordinal + 1])
            } else if (type.isRight) {
                setEnum(circularList[getEnum().ordinal - 1])
            }
        }

    companion object {
        inline operator fun <reified T : Enum<T>> invoke(
            name: String,
            noinline getEnum: () -> T,
            noinline setEnum: (T) -> Any,
            items: Map<T, Item> = mapOf(),
        ): EnumSelectElement<T> {
            return EnumSelectElement(name, getEnum, setEnum, items, enumValues<T>())
        }
    }
}