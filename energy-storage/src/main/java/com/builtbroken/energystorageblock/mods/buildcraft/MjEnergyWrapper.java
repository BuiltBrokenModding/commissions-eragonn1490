package com.builtbroken.energystorageblock.mods.buildcraft;

import buildcraft.api.mj.IMjConnector;
import buildcraft.api.mj.IMjPassiveProvider;
import buildcraft.api.mj.IMjReceiver;
import com.builtbroken.energystorageblock.block.TileEntityEnergyStorage;
import com.builtbroken.energystorageblock.config.ConfigEnergyStorage;
import com.builtbroken.energystorageblock.config.ConfigPowerSystem;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/30/2018.
 */
public class MjEnergyWrapper implements IMjReceiver, IMjPassiveProvider
{
    private final TileEntityEnergyStorage tile;

    public MjEnergyWrapper(TileEntityEnergyStorage tile)
    {
        this.tile = tile;
    }

    @Override
    public long getPowerRequested()
    {
        if (tile.hasCapability(CapabilityEnergy.ENERGY, null))
        {
            IEnergyStorage energyStorage = tile.getCapability(CapabilityEnergy.ENERGY, null);
            if (energyStorage != null)
            {
                int energyNeeded = energyStorage.receiveEnergy(ConfigEnergyStorage.INPUT_LIMIT, true);
                return (long) Math.floor(energyNeeded / ConfigPowerSystem.FROM_BUILDCRAFT);
            }
        }
        return 0;
    }

    @Override
    public long receivePower(long microJoules, boolean simulate)
    {
        if (tile.hasCapability(CapabilityEnergy.ENERGY, null))
        {
            IEnergyStorage energyStorage = tile.getCapability(CapabilityEnergy.ENERGY, null);
            if (energyStorage != null)
            {
                //Convert to FE
                int energy = (int) Math.floor(microJoules * ConfigPowerSystem.FROM_BUILDCRAFT);

                //Get amount received
                int taken = energyStorage.receiveEnergy(energy, simulate);

                //Convert
                long taken_mj = (long) Math.ceil(taken / ConfigPowerSystem.FROM_BUILDCRAFT);

                //Return remain
                return microJoules - taken_mj;
            }
        }
        return microJoules;
    }

    @Override
    public boolean canConnect(@Nonnull IMjConnector other)
    {
        return true;
    }

    @Override
    public long extractPower(long min, long max, boolean simulate)
    {
        if (tile.hasCapability(CapabilityEnergy.ENERGY, null))
        {
            IEnergyStorage energyStorage = tile.getCapability(CapabilityEnergy.ENERGY, null);
            if (energyStorage != null)
            {
                int fe = (int) Math.floor(max / ConfigPowerSystem.FROM_BUILDCRAFT);
                fe = energyStorage.extractEnergy(fe, true);

                long energy = (long) Math.floor(fe * ConfigPowerSystem.FROM_BUILDCRAFT);

                if (energy > min)
                {
                    energy = Math.min(energy, max);
                    if (!simulate)
                    {
                        fe = (int) Math.ceil(energy / ConfigPowerSystem.FROM_BUILDCRAFT);
                        energyStorage.extractEnergy(fe, false);
                    }
                    return fe;
                }
            }
        }
        return 0;
    }
}
