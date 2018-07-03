package com.builtbroken.energystorageblock.lib.energy;

import com.builtbroken.energystorageblock.config.ConfigEnergyStorage;
import com.builtbroken.energystorageblock.content.TileEntityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * Implementation of {@link IEnergyStorage} designed to work with {@link TileEntityEnergy}
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/30/2018.
 */
public class EnergyStorageTile implements IEnergyStorage
{
    protected int energy;

    public final TileEntityEnergy host;

    public EnergyStorageTile(TileEntityEnergy host)
    {
        this.host = host;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate)
    {
        if (canReceive() && maxReceive > 0)
        {

            int energyReceived = Math.min(getMaxEnergyStored() - energy, Math.min(host.getInputLimit(null), maxReceive));
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
            int energyExtracted = Math.min(energy, Math.min(host.getOutputLimit(null), maxExtract));
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
        return host.getOutputLimit(null) > 0;
    }

    @Override
    public boolean canReceive()
    {
        return host.getInputLimit(null) > 0;
    }

    public void setEnergy(int energy)
    {
        this.energy = energy;
    }
}
