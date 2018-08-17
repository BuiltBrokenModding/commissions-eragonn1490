package com.builtbroken.craftblocks.paint;

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
public class PainterRecipe
{
    /** Time to take to produce the item */
    public int ticksToComplete = 120;

    /** Dyes to use for the recipe */
    public Map<EnumDyeColor, Integer> dyeToUsage = new HashMap();

    /** Items to produce */
    public ItemStack output;

    /**
     * Called to check if the machine has the requirements for the recipe
     *
     * @param painter - machine
     * @return true if has the requirements
     */
    public boolean hasRecipe(TileEntityPainter painter)
    {
        //Check if we have all dye required
        for (Map.Entry<EnumDyeColor, Integer> entry : dyeToUsage.entrySet())
        {
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
}
