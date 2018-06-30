package com.builtbroken.energystorageblock.mods.buildcraft;

import buildcraft.api.mj.IMjConnector;
import buildcraft.api.mj.IMjReceiver;
import buildcraft.api.mj.MjAPI;
import com.builtbroken.energystorageblock.block.TileEntityEnergyStorage;
import com.builtbroken.energystorageblock.mods.ModProxy;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/22/2018.
 */
public class BuildcraftProxy extends ModProxy
{
    public static final String BC = "buildcraftenergy";
    public static final BuildcraftProxy INSTANCE = new BuildcraftProxy();

    @Override
    public void preInit()
    {

    }

    @Override
    @Optional.Method(modid = BC)
    public boolean outputPower(TileEntityEnergyStorage energyStorage, TileEntity target, EnumFacing enumFacing)
    {
        if (canConnect(energyStorage, target, enumFacing))
        {
            if (energyStorage.hasCapability(MjAPI.CAP_RECEIVER, enumFacing))
            {
                IMjReceiver receiver = energyStorage.getCapability(MjAPI.CAP_RECEIVER, enumFacing.getOpposite());
                if(receiver != null)
                {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canConnect(TileEntityEnergyStorage energyStorage, TileEntity target, EnumFacing enumFacing)
    {
        if (energyStorage.hasCapability(MjAPI.CAP_CONNECTOR, enumFacing))
        {
            IMjConnector sourceConnector = energyStorage.getCapability(MjAPI.CAP_CONNECTOR, enumFacing.getOpposite());
            if (sourceConnector != null && target.hasCapability(MjAPI.CAP_CONNECTOR, enumFacing))
            {
                IMjConnector connector = target.getCapability(MjAPI.CAP_CONNECTOR, enumFacing);
                return connector != null && connector.canConnect(sourceConnector);
            }
        }
        return false;
    }

}
