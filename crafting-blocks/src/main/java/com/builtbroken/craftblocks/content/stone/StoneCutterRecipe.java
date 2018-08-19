package com.builtbroken.craftblocks.content.stone;

import com.builtbroken.craftblocks.content.CrafterRecipe;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/19/2018.
 */
public class StoneCutterRecipe extends CrafterRecipe<StoneCutterRecipe, TileEntityStoneCutter>
{
    public int chiselUses = 1;

    public final ItemStack specialityInput;

    public StoneCutterRecipe(String unlocalizedName, ItemStack input, ItemStack output, ItemStack specialityInput)
    {
        super(unlocalizedName, input, output);
        this.specialityInput = specialityInput;
    }

    @Override
    public boolean hasRecipe(TileEntityStoneCutter crafter)
    {
        return false;
    }

    @Override
    public boolean doRecipe(TileEntityStoneCutter crafter)
    {
        return false;
    }
}
