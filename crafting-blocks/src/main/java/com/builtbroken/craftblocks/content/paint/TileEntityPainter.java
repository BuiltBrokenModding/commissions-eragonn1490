package com.builtbroken.craftblocks.content.paint;

import com.builtbroken.craftblocks.CraftingBlocks;
import com.builtbroken.craftblocks.network.IDescMessageTile;
import com.builtbroken.craftblocks.network.MessageDesc;
import com.builtbroken.craftblocks.network.NetworkHandler;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/15/2018.
 */
public class TileEntityPainter extends TileEntity implements ITickable, IDescMessageTile
{
    public static final int INVENTORY_SIZE = 20;
    public static final int OUTPUT_SLOT = 0;
    public static final int POWER_SLOT = 1;
    public static final int INPUT_SLOT = 2;
    public static final int BRUSH_SLOT = 3;
    public static final int DYE_SLOT_START = 4;

    public static final String NBT_INVENTORY = "inventory";
    public static final String NBT_ON_STATE = "machine_on";
    public static final String NBT_RECIPE_NAME = "recipe_name";
    public static final String NBT_RECIPE_TICKS = "recipe_ticks";

    //recipe.painting.mod:item.name
    public static final String RECIPE_UNLOCALIZATION_PREFX = "recipe.painting";

    /** Supported recipes */
    public static final List<PainterRecipe> recipes = new ArrayList();
    public static final Map<ResourceLocation, PainterRecipe> nameToRecipe = new HashMap();

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
    public boolean syncClient = false;

    public int recipeTicks;

    @Override
    public void update()
    {
        final PainterRecipe currentRecipe = getCurrentRecipe();

        //Only do logic server side,
        if (!world.isRemote )
        {
            //Only run logic when machine is on, and if we have a worker for power
            if(machineOn && hasWorkerPower())
            {
                //Only do logic if a recipe is active
                if (currentRecipe != null)
                {
                    //If no recipe try to find one (delay to avoid wasting CPU time)
                    if (!canDoRecipe && recipeTicks-- <= 0)
                    {
                        //Check if we can do recipe
                        checkRecipe();

                        //If can do recipe set timer to recipe
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
                        }
                        else
                        {
                            recipeTicks++; //keeps ticks from under flowing
                        }
                    }
                }
            }

            if(syncClient)
            {
                syncClient = false;
            }

            NetworkHandler.sendToAllAround(this, new MessageDesc(this));
        }
    }

    protected void checkRecipe()
    {
        final PainterRecipe currentRecipe = getCurrentRecipe();
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

    public PainterRecipe getCurrentRecipe()
    {
        if (!recipes.isEmpty() && recipeIndex < recipes.size() && recipeIndex >= 0)
        {
            return recipes.get(recipeIndex);
        }
        return null;
    }

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
        if (recipeIndex >= recipes.size())
        {
            recipeIndex = -1;
        }
        else if (recipeIndex < -1)
        {
            recipeIndex = recipes.size() - 1;
        }

        //Schedule packet
        syncClient = true;

        //Reset recipe
        canDoRecipe = false;
        recipeTicks = 10;
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
            if (stack.getCount() <= 0)
            {
                stack = ItemStack.EMPTY;
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
        readDescMessage(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setTag(NBT_INVENTORY, inventory.serializeNBT());
        return super.writeToNBT(writeDescMessage(compound));
    }

    @Override
    public NBTTagCompound writeDescMessage(NBTTagCompound tagCompound)
    {
        tagCompound.setInteger(NBT_RECIPE_TICKS, recipeTicks);
        tagCompound.setBoolean(NBT_ON_STATE, machineOn);
        tagCompound.setString(NBT_RECIPE_NAME, getCurrentRecipe() != null ? getCurrentRecipe().getRegistryName().toString() : "none");
        return tagCompound;
    }

    @Override
    public void readDescMessage(NBTTagCompound tagCompound)
    {
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
            PainterRecipe recipe = nameToRecipe.get(location);
            if (recipe != null)
            {
                recipeIndex = recipe.index;
            }
            else
            {
                CraftingBlocks.logger.error("TileEntityPainter#readDescMessage(nbt) -> Error: Could not find a recipe with name '" + recipeName + "'", new RuntimeException());
                recipeIndex = -1;
            }
        }
    }

    public static void registerRecipe(PainterRecipe recipe)
    {
        if(recipe.getRegistryName() == null)
        {
            throw new RuntimeException("TileEntityPainter#registerRecipe(recipe) recipe needs to have a registry name");
        }
        if (!nameToRecipe.containsKey(recipe.getRegistryName()))
        {
            nameToRecipe.put(recipe.getRegistryName(), recipe);
            recipes.add(recipe);
            recipe.index = recipes.size() - 1;
        }
        else
        {
            CraftingBlocks.logger.error("TileEntityPainter#registerRecipe( " + recipe + " ) -> Error: Recipe was registered with an existing registry name", new RuntimeException());
        }
    }
}
