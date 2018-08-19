package com.builtbroken.craftblocks.content.stone;

import com.builtbroken.craftblocks.CraftingBlocks;
import com.builtbroken.craftblocks.content.TileEntityCrafter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/19/2018.
 */
public class TileEntityStoneCutter extends TileEntityCrafter
{
    public static final int INVENTORY_SIZE = 5;
    public static final int SPECIAL_SLOT = 4;

    //recipe.painting.mod:item.name
    public static final String RECIPE_UNLOCALIZATION_PREFX = "recipe.cutter.stone";

    /** Supported recipes */
    public static final List<StoneCutterRecipe> recipes = new ArrayList();
    public static final Map<ResourceLocation, StoneCutterRecipe> nameToRecipe = new HashMap();

    public final ItemStackHandler inventory = new ItemStackHandler(INVENTORY_SIZE)
    {
        @Override
        protected void onContentsChanged(int slot)
        {
            TileEntityStoneCutter.this.markDirty();
            TileEntityStoneCutter.this.checkRecipe();
        }
    };

    @Override
    public ItemStackHandler getInventory()
    {
        return inventory;
    }

    @Override
    public StoneCutterRecipe getCurrentRecipe()
    {
        if (!recipes.isEmpty() && recipeIndex < recipes.size() && recipeIndex >= 0)
        {
            return recipes.get(recipeIndex);
        }
        return null;
    }

    @Override
    public boolean isTool(ItemStack stack)
    {
        return stack.getItem() == CraftingBlocks.itemStoneChisel;
    }

    @Override
    public String getRecipeName()
    {
        return getCurrentRecipe() != null ? getCurrentRecipe().getRegistryName().toString() : "none";
    }

    @Override
    public int getIndexForRecipe(ResourceLocation location)
    {
        StoneCutterRecipe recipe = nameToRecipe.get(location);
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

    public static void registerRecipe(StoneCutterRecipe recipe)
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
