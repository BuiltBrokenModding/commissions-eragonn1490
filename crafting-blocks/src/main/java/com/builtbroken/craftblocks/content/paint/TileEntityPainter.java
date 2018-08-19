package com.builtbroken.craftblocks.content.paint;

import com.builtbroken.craftblocks.CraftingBlocks;
import com.builtbroken.craftblocks.content.TileEntityCrafter;
import com.builtbroken.craftblocks.network.IDescMessageTile;
import com.builtbroken.craftblocks.network.MessageDesc;
import com.builtbroken.craftblocks.network.NetworkHandler;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
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
public class TileEntityPainter extends TileEntityCrafter implements ITickable, IDescMessageTile
{
    public static final int INVENTORY_SIZE = 20;
    public static final int DYE_SLOT_START = 4;

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


    @Override
    public void update()
    {
        final PainterRecipe currentRecipe = getCurrentRecipe();

        //Only do logic server side,
        if (!world.isRemote)
        {
            //Only run logic when machine is on, and if we have a worker for power
            if (machineOn && hasWorkerPower())
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

            if (syncClient)
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
    public ItemStackHandler getInventory()
    {
        return inventory;
    }

    @Override
    public String getRecipeName()
    {
        return getCurrentRecipe() != null ? getCurrentRecipe().getRegistryName().toString() : "none";
    }

    @Override
    public int getIndexForRecipe(ResourceLocation location)
    {
        PainterRecipe recipe = nameToRecipe.get(location);
        if(recipe != null)
        {
            return recipe.index;
        }
        return -1;
    }

    @Override
    public int getRecipeCount()
    {
        return recipes.size();
    }

    public static void registerRecipe(PainterRecipe recipe)
    {
        if (recipe.getRegistryName() == null)
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
