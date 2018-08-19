package com.builtbroken.craftblocks.content.stone;

import com.builtbroken.craftblocks.content.TileEntityCrafter;
import com.builtbroken.craftblocks.content.paint.TileEntityPainter;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.ItemStackHandler;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/19/2018.
 */
public class TileEntityStoneCutter extends TileEntityCrafter
{
    public static final int INVENTORY_SIZE = 5;
    public static final int SPECIAL_SLOT = 4;

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
    public String getRecipeName()
    {
        return null;
    }

    @Override
    public int getIndexForRecipe(ResourceLocation resourceLocation)
    {
        return 0;
    }

    @Override
    public int getRecipeCount()
    {
        return 0;
    }
}
