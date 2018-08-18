package com.builtbroken.craftblocks.content.item;

import com.builtbroken.craftblocks.CraftingBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/18/2018.
 */
public class ItemCraftingTool extends Item
{
    public ItemCraftingTool(String name, int durability)
    {
        setRegistryName(CraftingBlocks.PREFIX + name);
        setUnlocalizedName(CraftingBlocks.PREFIX + name.replace("_", "."));
        setMaxDamage(durability);
    }

    /**
     * Called to consume uses
     * @param stack
     * @param use
     * @return true if broken
     */
    public boolean consumeUse(ItemStack stack, int use)
    {
        setDamage(stack, stack.getCount() + use);
        return stack.getItemDamage() > stack.getMaxDamage();
    }
}
