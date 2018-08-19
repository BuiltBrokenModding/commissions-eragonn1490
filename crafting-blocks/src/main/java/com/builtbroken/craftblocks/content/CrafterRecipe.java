package com.builtbroken.craftblocks.content;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/19/2018.
 */
public abstract class CrafterRecipe<R extends CrafterRecipe, M extends TileEntityCrafter>
{
    /** Items to produce */
    public final ItemStack input;

    /** Items to produce */
    public final ItemStack output;

    /** Localization key */
    public final String unlocalizedName;

    /** Index of the recipe */
    public int index = -1;

    /** Time to take to produce the item */
    public int ticksToComplete = 120;

    private ResourceLocation registryName = null;

    public CrafterRecipe(String unlocalizedName, ItemStack input, ItemStack output)
    {
        this.unlocalizedName = unlocalizedName;
        this.input = input;
        this.output = output;
    }

    public final R setRegistryName(String name)
    {
        return setRegistryName(new ResourceLocation(name));
    }

    public final R setRegistryName(ResourceLocation name)
    {
        registryName = name;
        return (R) this;
    }

    public ResourceLocation getRegistryName()
    {
        return registryName;
    }

    public R setCompletionTime(int time)
    {
        this.ticksToComplete = time;
        return (R) this;
    }

    /**
     * Called to check if the machine has the requirements for the recipe
     *
     * @param crafter - machine
     * @return true if has the requirements
     */
    public abstract boolean hasRecipe(M crafter);

    /**
     * Called to complete the recipe. Will check if the recipe requirements
     * are meet before doing recipe.
     *
     * @param crafter - machine
     * @return true if recipe was run, false if not
     */
    public abstract boolean doRecipe(M crafter);


    public boolean isMatchingItem(ItemStack inputStack, ItemStack expectedStack)
    {
        return !inputStack.isEmpty() && ItemStack.areItemsEqual(inputStack, expectedStack) && ItemStack.areItemStackTagsEqual(inputStack, expectedStack);
    }
}
