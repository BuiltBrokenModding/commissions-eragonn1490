package com.builtbroken.energystorageblock.content.wireless.connector;

import com.builtbroken.energystorageblock.config.ConfigWirelessEnergyTower;
import com.builtbroken.energystorageblock.content.TileEntityEnergy;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/3/2018.
 */
public class TileEntityWirelessConnector extends TileEntityEnergy
{
    public boolean isEnergyOutput = false;

    @Override
    public boolean canInputEnergySide(@Nullable EnumFacing side)
    {
        return !isEnergyOutput ;
    }

    @Override
    public boolean canOutputEnergySide(@Nullable EnumFacing side)
    {
        return isEnergyOutput;
    }

    @Override
    public int getInputLimit(@Nullable EnumFacing side)
    {
        return ConfigWirelessEnergyTower.INPUT_LIMIT;
    }

    @Override
    public int getOutputLimit(@Nullable EnumFacing side)
    {
        return ConfigWirelessEnergyTower.OUTPUT_LIMIT;
    }
}
