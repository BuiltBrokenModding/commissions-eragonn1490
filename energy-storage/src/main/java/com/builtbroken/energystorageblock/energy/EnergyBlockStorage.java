package com.builtbroken.energystorageblock.energy;

import com.builtbroken.energystorageblock.config.ConfigEnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/30/2018.
 */
public class EnergyBlockStorage implements IEnergyStorage
{
    protected int energy;

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate)
    {
        if (!canReceive())
        {
            return 0;
        }

        int energyReceived = Math.min(getMaxEnergyStored() - energy, Math.min(ConfigEnergyStorage.INPUT_LIMIT, maxReceive));
        if (!simulate)
        {
            energy += energyReceived;
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate)
    {
        if (!canExtract())
        {
            return 0;
        }

        int energyExtracted = Math.min(energy, Math.min(ConfigEnergyStorage.OUTPUT_LIMIT, maxExtract));
        if (!simulate)
        {
            energy -= energyExtracted;
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored()
    {
        return energy;
    }

    @Override
    public int getMaxEnergyStored()
    {
        return ConfigEnergyStorage.CAPACITY;
    }

    @Override
    public boolean canExtract()
    {
        return ConfigEnergyStorage.OUTPUT_LIMIT > 0;
    }

    @Override
    public boolean canReceive()
    {
        return ConfigEnergyStorage.INPUT_LIMIT > 0;
    }

    public void setEnergy(int energy)
    {
        this.energy = energy;
    }
}
