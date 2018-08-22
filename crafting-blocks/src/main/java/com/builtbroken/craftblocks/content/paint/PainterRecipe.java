package com.builtbroken.craftblocks.content.paint;

import com.builtbroken.craftblocks.content.CrafterRecipe;
import com.builtbroken.craftblocks.content.TileEntityCrafter;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Recipe for the painting machine
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/17/2018.
 */
public class PainterRecipe extends CrafterRecipe<PainterRecipe, TileEntityPainter>
{
    /** How much durability to use of the brush item */
    public int brushUses = 1;

    /** Dyes to use for the recipe */
    public Map<EnumDyeColor, Integer> dyeToUsage = new HashMap();

    public PainterRecipe(String unlocalizedName, ItemStack input, ItemStack output, EnumDyeColor... dyes)
    {
        super(unlocalizedName, input, output);
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

    @Override
    public boolean hasRecipe(TileEntityPainter painter)
    {
        //Check that we have a brush
        if (painter.getToolUses() < brushUses)
        {
            return false;
        }

        //Check that we have expect input
        ItemStack inputStack = painter.inventory.extractItem(TileEntityCrafter.INPUT_SLOT, input.getCount(), true);
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
        return painter.inventory.insertItem(TileEntityCrafter.OUTPUT_SLOT, output, true).isEmpty();
    }

    @Override
    public boolean doRecipe(TileEntityPainter painter)
    {
        if (hasRecipe(painter))
        {
            //Consume brush uses
            painter.useTool(brushUses);

            //Consume input
            painter.inventory.extractItem(TileEntityCrafter.INPUT_SLOT, input.getCount(), false);

            //Consume dye
            for (Map.Entry<EnumDyeColor, Integer> entry : dyeToUsage.entrySet())
            {
                painter.consumeDye(entry.getKey(), entry.getValue());
            }

            //Output item
            painter.inventory.insertItem(TileEntityCrafter.OUTPUT_SLOT, output.copy(), false);

            return true;
        }
        return false;
    }

    @Override
    public String toString()
    {
        return "PainterRecipe(" + unlocalizedName + "," + (getRegistryName() != null ? getRegistryName().toString() : "null") + ")";
    }
}
