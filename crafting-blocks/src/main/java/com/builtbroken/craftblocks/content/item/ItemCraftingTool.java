package com.builtbroken.craftblocks.content.item;

import com.builtbroken.craftblocks.CraftingBlocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

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
        setMaxStackSize(1);
        setMaxDamage(durability);
        setCreativeTab(CreativeTabs.TOOLS);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        //TODO translate
        tooltip.add("Consumable item for crafting");
    }

    /**
     * Called to consume uses
     * @param stack
     * @param use
     * @return true if broken
     */
    public boolean consumeUse(ItemStack stack, int use)
    {
        setDamage(stack, stack.getItemDamage() + use);
        return getUsesLeft(stack) <= 0;
    }

    public int getUsesLeft(ItemStack stack)
    {
        return stack.getMaxDamage() - stack.getItemDamage();
    }
}
