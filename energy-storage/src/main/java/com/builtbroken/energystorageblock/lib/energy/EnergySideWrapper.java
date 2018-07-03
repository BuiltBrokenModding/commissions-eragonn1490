package com.builtbroken.energystorageblock.lib.energy;

import com.builtbroken.energystorageblock.content.TileEntityEnergy;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/30/2018.
 */
public class EnergySideWrapper implements IEnergyStorage
{
    private final TileEntityEnergy host;
    public final EnumFacing side;
    public EnergySideState sideState = EnergySideState.INPUT;

    public EnergySideWrapper(TileEntityEnergy host, EnumFacing side)
    {
        this.host = host;
        this.side = side;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate)
    {
        if (canReceive())
        {
            return host.energyStorage.receiveEnergy(maxReceive, simulate);
        }
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate)
    {
        if (canReceive())
        {
            return host.energyStorage.receiveEnergy(maxExtract, simulate);
        }
        return 0;
    }

    @Override
    public int getEnergyStored()
    {
        return host.energyStorage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored()
    {
        return host.energyStorage.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract()
    {
        return host.getOutputLimit(side) > 0;
    }

    @Override
    public boolean canReceive()
    {
        return host.getInputLimit(side) > 0;
    }
}
