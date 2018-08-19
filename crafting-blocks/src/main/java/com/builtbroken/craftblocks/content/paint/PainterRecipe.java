package com.builtbroken.craftblocks.content.paint;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.HashMap;
import java.util.Map;

/**
 * Recipe for the painting machine
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/17/2018.
 */
public class PainterRecipe extends IForgeRegistryEntry.Impl<PainterRecipe>
{
    /** Time to take to produce the item */
    public int ticksToComplete = 120;

    /** How much durability to use of the brush item */
    public int brushUses = 1;

    /** Dyes to use for the recipe */
    public Map<EnumDyeColor, Integer> dyeToUsage = new HashMap();

    /** Items to produce */
    public final ItemStack input;

    /** Items to produce */
    public final ItemStack output;

    public final String unlocalizedName;

    public int index = -1;

    public PainterRecipe(String unlocalizedName, ItemStack input, ItemStack output, EnumDyeColor... dyes)
    {
        this.unlocalizedName = unlocalizedName;
        this.input = input;
        this.output = output;
        if (dyes != null)
        {
            for (EnumDyeColor dye : dyes)
            {
                setDyeUsage(dye, 1);
            }
        }
    }

    public PainterRecipe setDyeUsage(EnumDyeColor dye, int count)
    {
        if (dye != null)
        {
            dyeToUsage.put(dye, count);
        }
        return this;
    }

    public PainterRecipe setCompletionTime(int time)
    {
        this.ticksToComplete = time;
        return this;
    }

    /**
     * Called to check if the machine has the requirements for the recipe
     *
     * @param painter - machine
     * @return true if has the requirements
     */
    public boolean hasRecipe(TileEntityPainter painter)
    {
        //Check that we have a brush
        if (painter.getToolUses() < brushUses)
        {
            return false;
        }

        //Check that we have expect input
        ItemStack inputStack = painter.inventory.extractItem(TileEntityPainter.INPUT_SLOT, input.getCount(), true);
        if (!isMatchingItem(inputStack, input))
        {
            return false;
        }

        //Check if we have all dye required
        for (Map.Entry<EnumDyeColor, Integer> entry : dyeToUsage.entrySet())
        {
            //If we have too few dye then we have no recipe
            if (painter.getDyeCount(entry.getKey()) < entry.getValue())
            {
                return false;
            }
        }

        //Check if we can output the item (simulated)
        return painter.inventory.insertItem(TileEntityPainter.OUTPUT_SLOT, output, true).isEmpty();
    }

    /**
     * Called to complete the recipe. Will check if the recipe requirements
     * are meet before doing recipe.
     *
     * @param painter
     * @return true if recipe was run, false if not
     */
    public boolean doRecipe(TileEntityPainter painter)
    {
        if (hasRecipe(painter))
        {
            //Consume brush uses
            painter.useTool(brushUses);

            //Consume input
            painter.inventory.extractItem(TileEntityPainter.INPUT_SLOT, input.getCount(), false);

            //Consume dye
            for (Map.Entry<EnumDyeColor, Integer> entry : dyeToUsage.entrySet())
            {
                painter.consumeDye(entry.getKey(), entry.getValue());
            }

            //Output item
            painter.inventory.insertItem(TileEntityPainter.OUTPUT_SLOT, output.copy(), false);

            return true;
        }
        return false;
    }

    public boolean isMatchingItem(ItemStack inputStack, ItemStack expectedStack)
    {
        return !inputStack.isEmpty() && ItemStack.areItemsEqual(inputStack, expectedStack) && ItemStack.areItemStackTagsEqual(inputStack, expectedStack);
    }

    public String toString()
    {
        return "PainterRecipe(" + unlocalizedName + "," + (getRegistryName() != null ? getRegistryName().toString() : "null") + ")";
    }
}
