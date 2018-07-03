package com.builtbroken.energystorageblock.lib.energy;

import net.minecraftforge.energy.IEnergyStorage;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/30/2018.
 */
public class EnergySideWrapper implements IEnergyStorage
{
    private final IEnergyStorage mainStorage;
    public EnergySideState sideState = EnergySideState.INPUT;

    public EnergySideWrapper(IEnergyStorage mainStorage)
    {
        this.mainStorage = mainStorage;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate)
    {
        if (canReceive())
        {
            return mainStorage.receiveEnergy(maxReceive, simulate);
        }
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate)
    {
        if (canReceive())
        {
            return mainStorage.receiveEnergy(maxExtract, simulate);
        }
        return 0;
    }

    @Override
    public int getEnergyStored()
    {
        return mainStorage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored()
    {
        return mainStorage.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract()
    {
        return sideState == EnergySideState.OUTPUT;
    }

    @Override
    public boolean canReceive()
    {
        return sideState == EnergySideState.INPUT;
    }
}
