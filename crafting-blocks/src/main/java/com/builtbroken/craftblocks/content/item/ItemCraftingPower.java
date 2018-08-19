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
 * Item used to power the crafting machines
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/19/2018.
 */
public class ItemCraftingPower extends Item
{
    public ItemCraftingPower(String name, int durability)
    {
        setRegistryName(CraftingBlocks.PREFIX + name);
        setUnlocalizedName(CraftingBlocks.PREFIX + name.replace("_", "."));
        setMaxStackSize(1);
        setMaxDamage(durability + 1); //Increase by 1 so we have a buffer to prevent breaking item
        setCreativeTab(CreativeTabs.TOOLS);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        //TODO translate
        tooltip.add("Powers crafting benches");
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        //Reduce by 1, since 1 is considered no uses left. 99/100 -> 99/99
        return (double) stack.getItemDamage() / (double) (stack.getMaxDamage() - 1);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return true; //Always show
    }

    /**
     * Called to consume uses
     *
     * @param stack
     * @param use
     * @return true if broken
     */
    public boolean consumeUse(ItemStack stack, int use)
    {
        setDamage(stack, Math.min(getMaxUses(stack), stack.getItemDamage() + use));
        return getUsesLeft(stack) <= 0;
    }

    public int getUsesLeft(ItemStack stack)
    {
        return getMaxUses(stack) - stack.getItemDamage();
    }

    public int getMaxUses(ItemStack stack)
    {
        return stack.getMaxDamage() - 1;
    }
}
