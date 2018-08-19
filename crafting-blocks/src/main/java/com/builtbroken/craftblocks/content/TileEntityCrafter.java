package com.builtbroken.craftblocks.content;

import com.builtbroken.craftblocks.CraftingBlocks;
import com.builtbroken.craftblocks.content.item.ItemCraftingPower;
import com.builtbroken.craftblocks.network.IDescMessageTile;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.ItemStackHandler;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/19/2018.
 */
public abstract class TileEntityCrafter extends TileEntity implements IDescMessageTile
{
    public static final String NBT_INVENTORY = "inventory";
    public static final String NBT_ON_STATE = "machine_on";
    public static final String NBT_RECIPE_NAME = "recipe_name";
    public static final String NBT_RECIPE_TICKS = "canDoRecipe";

    public static final int OUTPUT_SLOT = 0;
    public static final int POWER_SLOT = 1;
    public static final int INPUT_SLOT = 2;
    public static final int TOOL_SLOT = 3;

    public int recipeIndex = 0;

    public boolean machineOn = false;
    public boolean canDoRecipe = false;
    public boolean syncClient = false;

    public int recipeTicks;

    public abstract ItemStackHandler getInventory();

    public abstract String getRecipeName();

    public abstract int getIndexForRecipe(ResourceLocation resourceLocation);

    public abstract int getRecipeCount();

    public void toggleRecipe(boolean next)
    {
        //Cycle index
        if (next)
        {
            recipeIndex++;
        }
        else
        {
            recipeIndex--;
        }

        //Clamp index
        if (recipeIndex >= getRecipeCount())
        {
            recipeIndex = -1;
        }
        else if (recipeIndex < -1)
        {
            recipeIndex = getRecipeCount() - 1;
        }

        //Schedule packet
        syncClient = true;

        //Reset recipe
        canDoRecipe = false;
        recipeTicks = 10;
    }





    protected void consumeWorkerPower()
    {
        ItemStack stack = getInventory().getStackInSlot(POWER_SLOT);
        if (stack.getItem() instanceof ItemCraftingPower)
        {
            ((ItemCraftingPower) stack.getItem()).consumeUse(stack, 1);
        }
    }

    protected boolean hasWorkerPower()
    {
        ItemStack stack = getInventory().getStackInSlot(POWER_SLOT);
        if (stack.getItem() instanceof ItemCraftingPower)
        {
            return ((ItemCraftingPower) stack.getItem()).getUsesLeft(stack) > 0;
        }
        return false;
    }

    public int getToolUses()
    {
        ItemStack stack = getInventory().getStackInSlot(TOOL_SLOT);
        if (stack.getItem() == CraftingBlocks.itemPaintBrush)
        {
            return CraftingBlocks.itemPaintBrush.getUsesLeft(stack);
        }
        return 0;
    }

    public void useTool(int uses)
    {
        ItemStack stack = getInventory().getStackInSlot(TOOL_SLOT);
        if (stack.getItem() == CraftingBlocks.itemPaintBrush)
        {
            if (CraftingBlocks.itemPaintBrush.consumeUse(stack, uses))
            {
                getInventory().setStackInSlot(TOOL_SLOT, ItemStack.EMPTY);
            }
            else
            {
                getInventory().setStackInSlot(TOOL_SLOT, stack);
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        getInventory().deserializeNBT(compound.getCompoundTag(NBT_INVENTORY));
        readDescMessage(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setTag(NBT_INVENTORY, getInventory().serializeNBT());
        return super.writeToNBT(writeDescMessage(compound));
    }

    @Override
    public NBTTagCompound writeDescMessage(NBTTagCompound tagCompound)
    {
        tagCompound.setBoolean("canDoRecipe", canDoRecipe);
        tagCompound.setInteger(NBT_RECIPE_TICKS, recipeTicks);
        tagCompound.setBoolean(NBT_ON_STATE, machineOn);
        tagCompound.setString(NBT_RECIPE_NAME, getRecipeName());
        return tagCompound;
    }

    @Override
    public void readDescMessage(NBTTagCompound tagCompound)
    {
        canDoRecipe = tagCompound.getBoolean("canDoRecipe");
        recipeTicks = tagCompound.getInteger(NBT_RECIPE_TICKS);
        machineOn = tagCompound.getBoolean(NBT_ON_STATE);
        String recipeName = tagCompound.getString(NBT_RECIPE_NAME);
        if (recipeName.equalsIgnoreCase("none"))
        {
            recipeIndex = -1;
        }
        else
        {
            ResourceLocation location = new ResourceLocation(recipeName);
            recipeIndex = getIndexForRecipe(location);
        }
    }
}
