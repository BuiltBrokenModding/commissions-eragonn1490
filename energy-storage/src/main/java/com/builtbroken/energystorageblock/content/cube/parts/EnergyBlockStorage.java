package com.builtbroken.energystorageblock.content.cube.parts;

import com.builtbroken.energystorageblock.content.cube.TileEntityEnergyStorage;
import com.builtbroken.energystorageblock.config.ConfigEnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/30/2018.
 */
public class EnergyBlockStorage implements IEnergyStorage
{
    protected int energy;

    public final TileEntityEnergyStorage host;

    public EnergyBlockStorage(TileEntityEnergyStorage host)
    {
        this.host = host;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate)
    {
        if (canReceive() && maxReceive > 0)
        {

            int energyReceived = Math.min(getMaxEnergyStored() - energy, Math.min(ConfigEnergyStorage.INPUT_LIMIT, maxReceive));
            if (!simulate)
            {
                energy += energyReceived;

                //Mark the tile has changed so it saves
                host.markDirty();
            }
            return energyReceived;
        }
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate)
    {
        if (canExtract() && maxExtract > 0)
        {
            int energyExtracted = Math.min(energy, Math.min(ConfigEnergyStorage.OUTPUT_LIMIT, maxExtract));
            if (!simulate)
            {
                energy -= energyExtracted;

                //Mark the tile has changed so it saves
                host.markDirty();
            }
            return energyExtracted;
        }
        return 0;
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
