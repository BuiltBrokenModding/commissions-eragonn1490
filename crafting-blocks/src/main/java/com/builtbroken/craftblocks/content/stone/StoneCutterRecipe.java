package com.builtbroken.craftblocks.content.stone;

import com.builtbroken.craftblocks.content.CrafterRecipe;
import com.builtbroken.craftblocks.content.TileEntityCrafter;
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
        this.specialityInput = specialityInput != null ? specialityInput : ItemStack.EMPTY;
    }

    @Override
    public boolean hasRecipe(TileEntityStoneCutter crafter)
    {
        //Check that we have a chisel
        if (crafter.getToolUses() < chiselUses)
        {
            return false;
        }

        //Check that we have expect input
        ItemStack inputStack =  crafter.inventory.extractItem(TileEntityCrafter.INPUT_SLOT, input.getCount(), true);
        if (!isMatchingItem(inputStack, input))
        {
            return false;
        }

        //Check if we need and have speciality input
        if(!specialityInput.isEmpty())
        {
            inputStack =  crafter.inventory.extractItem(TileEntityStoneCutter.SPECIAL_SLOT, specialityInput.getCount(), true);
            if (!isMatchingItem(inputStack, specialityInput) && inputStack.getCount() >= specialityInput.getCount())
            {
                return false;
            }
        }

        //Check if we can output the item (simulated)
        return  crafter.inventory.insertItem(TileEntityCrafter.OUTPUT_SLOT, output, true).isEmpty();
    }

    @Override
    public boolean doRecipe(TileEntityStoneCutter crafter)
    {
        if (hasRecipe(crafter))
        {
            //Consume brush uses
            crafter.useTool(chiselUses);

            //Consume input
            crafter.inventory.extractItem(TileEntityCrafter.INPUT_SLOT, input.getCount(), false);

            //Consume speciality input
            if(!specialityInput.isEmpty())
            {
                crafter.inventory.extractItem(TileEntityStoneCutter.SPECIAL_SLOT, specialityInput.getCount(), false);
            }

            //Output item
            crafter.inventory.insertItem(TileEntityCrafter.OUTPUT_SLOT, output.copy(), false);

            return true;
        }
        return false;
    }
}
