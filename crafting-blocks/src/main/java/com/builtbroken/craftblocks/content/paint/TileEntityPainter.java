package com.builtbroken.craftblocks.content.paint;

import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/15/2018.
 */
public class TileEntityPainter extends TileEntity implements ITickable
{
    public static final int INVENTORY_SIZE = 20;
    public static final int OUTPUT_SLOT = 0;
    public static final int POWER_SLOT = 1;
    public static final int INPUT_SLOT = 2;
    public static final int BRUSH_SLOT = 3;
    public static final int DYE_SLOT_START = 4;

    public static final String NBT_INVENTORY = "inventory";

    /** Supported recipes */
    public static final List<PainterRecipe> recipes = new ArrayList();

    public final ItemStackHandler inventory = new ItemStackHandler(INVENTORY_SIZE)
    {
        @Override
        protected void onContentsChanged(int slot)
        {
            TileEntityPainter.this.markDirty();
            TileEntityPainter.this.checkRecipe();
        }
    };

    public int recipeIndex = 0;

    public boolean machineOn = false;
    public boolean canDoRecipe = false;
    public int recipeTicks;

    @Override
    public void update()
    {
        final PainterRecipe currentRecipe = getCurrentRecipe();

        //Only do logic server side, when machine is on, and if we have a worker for power
        if(!world.isRemote && machineOn && hasWorkerPower())
        {
            //Only do logic if a recipe is active
            if (currentRecipe != null)
            {
                //If no recipe try to find one (delay to avoid wasting CPU time)
                if(!canDoRecipe && recipeTicks-- <= 0)
                {
                    //Check if we can do recipe
                    checkRecipe();

                    //If can do recipe set timer to recipe
                    if(!canDoRecipe)
                    {
                        recipeTicks = 10;
                    }
                }

                //Check if we can do recipe
                if(canDoRecipe && recipeTicks-- <= 0)
                {
                    //Do recipe
                    if(currentRecipe.doRecipe(this))
                    {
                        //Consume power
                        consumeWorkerPower();
                    }
                    else
                    {
                        recipeTicks++; //keeps ticks from under flowing
                    }
                }
            }
        }
    }

    protected void checkRecipe()
    {
        final PainterRecipe currentRecipe = getCurrentRecipe();
        if(currentRecipe != null)
        {
            //Check if we can do recipe
            canDoRecipe = currentRecipe.hasRecipe(this);

            //If can do recipe set timer to recipe
            if (canDoRecipe)
            {
                recipeTicks = currentRecipe.ticksToComplete;
            }
        }
    }

    public PainterRecipe getCurrentRecipe()
    {
        if(!recipes.isEmpty() && recipeIndex < recipes.size())
        {
            return recipes.get(recipeIndex);
        }
        return null;
    }

    protected void consumeWorkerPower()
    {
        //TODO implement
    }

    protected boolean hasWorkerPower()
    {
        return true; //TODO implement
    }

    public int getBrushUses()
    {
        return 1; //TODO implement
    }

    public void useBrush(int uses)
    {
        //TODO implement
    }

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
