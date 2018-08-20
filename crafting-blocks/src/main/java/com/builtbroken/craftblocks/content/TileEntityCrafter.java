package com.builtbroken.craftblocks.content;

import com.builtbroken.craftblocks.content.item.ItemCraftingPower;
import com.builtbroken.craftblocks.content.item.ItemCraftingTool;
import com.builtbroken.craftblocks.network.IDescMessageTile;
import com.builtbroken.craftblocks.network.MessageDesc;
import com.builtbroken.craftblocks.network.NetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.ItemStackHandler;

/**
 * Base class for crafter machines that use a input, output, power item, and tool item.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/19/2018.
 */
public abstract class TileEntityCrafter<R extends CrafterRecipe> extends TileEntity implements IDescMessageTile, ITickable
{
    public static final String NBT_INVENTORY = "inventory";
    public static final String NBT_ON_STATE = "machineEnabled";
    public static final String NBT_RECIPE_NAME = "recipeName";
    public static final String NBT_RECIPE_TICKS = "recipeTicks";
    public static final String NBT_CAN_DO_RECIPE = "recipeCanDo";

    public static final int OUTPUT_SLOT = 0;
    public static final int POWER_SLOT = 1;
    public static final int INPUT_SLOT = 2;
    public static final int TOOL_SLOT = 3;

    public int recipeIndex = 0;

    /** Toggle state if the machine is on */
    public boolean machineOn = false;
    /** Check state if the machine can do the recipe */
    public boolean canDoRecipe = false;
    /** Toggle state to sync packet data next tick */
    public boolean syncClient = false;

    /** Ticks left on recipe or until next recipe check */
    public int recipeTicks;

    /**
     * Inventory for this machine
     *
     * @return inventory
     */
    public abstract ItemStackHandler getInventory();

    /**
     * Registry name of the current recipe. Used for
     * syncing recipe to client
     *
     * @return registry name of current recipe, none for null
     */
    public abstract String getRecipeName();

    /**
     * Gets the index of the recipe
     *
     * @param resourceLocation - recipe registry name
     * @return index or -1 for not found
     */
    public abstract int getIndexForRecipe(ResourceLocation resourceLocation);

    /**
     * Gets the number of recipes for the machine
     *
     * @return recipe count
     */
    public abstract int getRecipeCount();

    /**
     * Gets the current recipe object
     *
     * @return recipe, or null for nothing selected
     */
    public abstract R getCurrentRecipe();

    /**
     * Checks if the input is a tool for the machine
     *
     * @param stack - item
     * @return true if tool
     */
    public abstract boolean isTool(ItemStack stack);

    @Override
    public void update()
    {
        final R currentRecipe = getCurrentRecipe();

        //Only do logic server side,
        if (!world.isRemote)
        {
            //Only run logic when machine is on, and if we have a worker for power
            if (machineOn && hasWorkerPower())
            {
                //Only do logic if a recipe is active
                if (currentRecipe != null)
                {
                    //Check recipe state
                    if (!canDoRecipe && recipeTicks-- <= 0)
                    {
                        //Check if we can do recipe
                        checkRecipe();

                        //If can't do recipe, reset check timer
                        if (!canDoRecipe)
                        {
                            recipeTicks = 10;
                        }
                    }

                    //Check if we can do recipe
                    if (canDoRecipe && recipeTicks-- <= 0)
                    {
                        //Do recipe
                        if (currentRecipe.doRecipe(this))
                        {
                            //Consume power
                            consumeWorkerPower();
                            canDoRecipe = false;
                        }
                        //Error state reset if something change to cause recipe to fail
                        else if (recipeTicks <= -10)
                        {
                            canDoRecipe = false;
                        }
                    }
                }
            }

            //Update client
            NetworkHandler.sendToAllAround(this, new MessageDesc(this));
        }
    }

    /**
     * Checks if the recipe can be processed
     * aka has all components and energy needed
     */
    protected void checkRecipe()
    {
        final R currentRecipe = getCurrentRecipe();
        if (currentRecipe != null)
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

    /**
     * Toggles to the next selected recipe. Will
     * wrap back to -1 (none) if goes above recipe
     * count. Will wrap to (recipe count - 1) if goes
     * bellow -1
     *
     * @param next -true to move forward
     */
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

    /**
     * Called to consume worker power
     */
    protected void consumeWorkerPower()
    {
        ItemStack stack = getInventory().getStackInSlot(POWER_SLOT);
        if (stack.getItem() instanceof ItemCraftingPower)
        {
            ((ItemCraftingPower) stack.getItem()).consumeUse(stack, 1);
        }
    }

    /**
     * Called to check if there is a worker
     * with enough power to do the recipe.
     *
     * @return true if enough worker power
     */
    protected boolean hasWorkerPower()
    {
        ItemStack stack = getInventory().getStackInSlot(POWER_SLOT);
        if (stack.getItem() instanceof ItemCraftingPower)
        {
            return ((ItemCraftingPower) stack.getItem()).getUsesLeft(stack) > 0;
        }
        return false;
    }

    /**
     * Gets the number of uses left on the tool
     *
     * @return 0 if no uses or no tool, else tool uses left
     */
    public int getToolUses()
    {
        ItemStack stack = getInventory().getStackInSlot(TOOL_SLOT);
        if (isTool(stack) && stack.getItem() instanceof ItemCraftingTool)
        {
            return ((ItemCraftingTool) stack.getItem()).getUsesLeft(stack);
        }
        return 0;
    }

    /**
     * Will use the tool. When tool goes to or bellow
     * zero uses it will break
     *
     * @param uses - number of uses to use
     */
    public void useTool(int uses)
    {
        ItemStack stack = getInventory().getStackInSlot(TOOL_SLOT);
        if (isTool(stack) && stack.getItem() instanceof ItemCraftingTool)
        {
            if (((ItemCraftingTool) stack.getItem()).consumeUse(stack, uses))
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
        tagCompound.setBoolean(NBT_CAN_DO_RECIPE, canDoRecipe);
        tagCompound.setInteger(NBT_RECIPE_TICKS, recipeTicks);
        tagCompound.setBoolean(NBT_ON_STATE, machineOn);
        tagCompound.setString(NBT_RECIPE_NAME, getRecipeName());
        return tagCompound;
    }

    @Override
    public void readDescMessage(NBTTagCompound tagCompound)
    {
        canDoRecipe = tagCompound.getBoolean(NBT_CAN_DO_RECIPE);
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
