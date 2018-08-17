package com.builtbroken.craftblocks.paint;

import net.minecraft.item.EnumDyeColor;
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

    }

    public boolean hasDye(EnumDyeColor enumDyeColor)
    {
        int slot = getDyeSlot(enumDyeColor);

        return
    }

    public int getDyeSlot(EnumDyeColor enumDyeColor)
    {
        return DYE_SLOT_START + enumDyeColor.ordinal();
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
