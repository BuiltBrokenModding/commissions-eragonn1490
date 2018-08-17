package com.builtbroken.craftblocks.paint;

import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/15/2018.
 */
public class TileEntityPainter extends TileEntity
{
    public static final int INVENTORY_SIZE = 18;
    public static final int OUTPUT_SLOT = 0;
    public static final int POWER_SLOT = 1;
    public static final int DYE_SLOT_START = 2;

    public static final String NBT_INVENTORY = "inventory";

    /** Supported recipes */
    public static final List<PainterRecipe> recipes = new ArrayList();

    public final ItemStackHandler inventory = new ItemStackHandler(INVENTORY_SIZE)
    {
        @Override
        protected void onContentsChanged(int slot)
        {
            TileEntityPainter.this.markDirty();
        }
    };

    public PainterRecipe currentRecipe;

    public int getDyeCount(EnumDyeColor enumDyeColor)
    {
        int slot = getDyeSlot(enumDyeColor);
        ItemStack stack = inventory.getStackInSlot(slot);
        if (isDyeItem(enumDyeColor, stack))
        {
            return stack.getCount();
        }
        return 0;
    }

    public void consumeDye(EnumDyeColor enumDyeColor, int count)
    {
        int slot = getDyeSlot(enumDyeColor);
        ItemStack stack = inventory.getStackInSlot(slot);
        if (isDyeItem(enumDyeColor, stack))
        {
            stack.setCount(stack.getCount() - count);
            if(stack.getCount() <= 0)
            {
                stack = null;
            }
            inventory.setStackInSlot(slot, stack);
        }
    }

    public boolean isDyeItem(EnumDyeColor enumDyeColor, ItemStack stack)
    {
        return !stack.isEmpty()
                && stack.getItem() == Items.DYE
                && stack.getItemDamage() == enumDyeColor.getDyeDamage();
    }

    public boolean hasDye(EnumDyeColor enumDyeColor)
    {
        return getDyeCount(enumDyeColor) > 0;
    }

    public int getDyeSlot(EnumDyeColor enumDyeColor)
    {
        return DYE_SLOT_START + enumDyeColor.ordinal();
    }

    public ItemStack getStackInOutput()
    {
        return inventory.getStackInSlot(OUTPUT_SLOT);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        inventory.deserializeNBT(compound.getCompoundTag(NBT_INVENTORY));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setTag(NBT_INVENTORY, inventory.serializeNBT());
        return compound;
    }
}
