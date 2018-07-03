package com.builtbroken.energystorageblock.mods.buildcraft;

import buildcraft.api.mj.MjAPI;
import com.builtbroken.energystorageblock.block.TileEntityEnergyStorage;
import com.builtbroken.energystorageblock.config.ConfigPowerSystem;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/30/2018.
 */
public class MjCapabilityProvider implements ICapabilityProvider
{
    private final TileEntityEnergyStorage tile;
    private final MjEnergyWrapper[] wrappers = new MjEnergyWrapper[6];

    public MjCapabilityProvider(TileEntityEnergyStorage tile)
    {
        this.tile = tile;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        if (ConfigPowerSystem.ENABLE_BUILDCRAFT && tile.hasCapability(CapabilityEnergy.ENERGY, facing))
        {
            return capability == MjAPI.CAP_RECEIVER || capability == MjAPI.CAP_CONNECTOR || capability == MjAPI.CAP_PASSIVE_PROVIDER;
        }
        return false;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (hasCapability(capability, facing))
        {
            if (wrappers[facing.ordinal()] == null)
            {
                wrappers[facing.ordinal()] = new MjEnergyWrapper(tile, facing);
            }
            return (T) wrappers[facing.ordinal()];
        }
        return null;
    }
}
