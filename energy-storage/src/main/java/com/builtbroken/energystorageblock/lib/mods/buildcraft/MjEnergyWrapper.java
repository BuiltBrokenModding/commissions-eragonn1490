package com.builtbroken.energystorageblock.lib.mods.buildcraft;

import buildcraft.api.mj.IMjConnector;
import buildcraft.api.mj.IMjPassiveProvider;
import buildcraft.api.mj.IMjReceiver;
import buildcraft.api.mj.MjAPI;
import com.builtbroken.energystorageblock.content.cube.TileEntityEnergyStorage;
import com.builtbroken.energystorageblock.config.ConfigEnergyStorage;
import com.builtbroken.energystorageblock.config.ConfigPowerSystem;
import net.minecraft.util.EnumFacing;
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
    private final EnumFacing side;

    public MjEnergyWrapper(TileEntityEnergyStorage tile, EnumFacing side)
    {
        this.tile = tile;
        this.side = side;
    }

    @Override
    public long getPowerRequested()
    {
        if (ConfigPowerSystem.ENABLE_BUILDCRAFT && tile.canInputEnergySide(side))
        {
            IEnergyStorage energyStorage = tile.getCapability(CapabilityEnergy.ENERGY, null);
            if (energyStorage != null)
            {
                int energyNeeded = energyStorage.receiveEnergy(ConfigEnergyStorage.INPUT_LIMIT, true);
                return (long) Math.min(ConfigEnergyStorage.OUTPUT_LIMIT_BC * MjAPI.ONE_MINECRAFT_JOULE, Math.floor(BuildcraftProxy.toBuildcraftEnergy(energyNeeded)));
            }
        }
        return 0;
    }

    @Override
    public long receivePower(long microJoules, boolean simulate)
    {
        if (ConfigPowerSystem.ENABLE_BUILDCRAFT && tile.canInputEnergySide(side))
        {
            IEnergyStorage energyStorage = tile.getCapability(CapabilityEnergy.ENERGY, null);
            if (energyStorage != null)
            {
                microJoules = Math.min(microJoules, ConfigEnergyStorage.INPUT_LIMIT_BC * MjAPI.ONE_MINECRAFT_JOULE);
                //Convert to FE
                int energy = (int) Math.floor(BuildcraftProxy.toForgeEnergy(microJoules));

                //Get amount received
                int taken = energyStorage.receiveEnergy(energy, simulate);

                //Convert
                long taken_mj = (long) Math.ceil(BuildcraftProxy.toBuildcraftEnergy(taken));

                //Return remain
                return microJoules - taken_mj;
            }
        }
        return microJoules;
    }

    @Override
    public boolean canConnect(@Nonnull IMjConnector other)
    {
        return ConfigPowerSystem.ENABLE_BUILDCRAFT;
    }

    @Override
    public long extractPower(long min, long max, boolean simulate)
    {
        if (ConfigPowerSystem.ENABLE_BUILDCRAFT && tile.canOutputEnergySide(side))
        {
            IEnergyStorage energyStorage = tile.getCapability(CapabilityEnergy.ENERGY, null);
            if (energyStorage != null)
            {
                int fe = (int) Math.floor(BuildcraftProxy.toForgeEnergy(max));
                fe = energyStorage.extractEnergy(fe, true);

                long energy = (long) Math.min(ConfigEnergyStorage.OUTPUT_LIMIT_BC * MjAPI.ONE_MINECRAFT_JOULE, Math.floor(BuildcraftProxy.toBuildcraftEnergy(fe)));

                if (energy > min)
                {
                    energy = Math.min(energy, max);
                    if (!simulate)
                    {
                        fe = (int) Math.ceil(BuildcraftProxy.toForgeEnergy(energy ));
                        energyStorage.extractEnergy(fe, false);
                    }
                    return fe;
                }
            }
        }
        return 0;
    }
}
