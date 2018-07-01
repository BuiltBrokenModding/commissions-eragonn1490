package com.builtbroken.energystorageblock.gui;

import com.builtbroken.energystorageblock.block.TileEntityEnergyStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/1/2018.
 */
public class ContainerEnergyStorage extends Container
{
    private final TileEntityEnergyStorage energyStorage;

    public ContainerEnergyStorage(TileEntityEnergyStorage energyStorage, EntityPlayer player)
    {
        this.energyStorage = energyStorage;


        //Add player inventory
        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k)
        {
            this.addSlotToContainer(new Slot(player.inventory, k, 8 + k * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return playerIn.getDistanceSqToCenter(energyStorage.getPos()) <= 100;
    }
}
