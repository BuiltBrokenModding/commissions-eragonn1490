package com.builtbroken.energystorageblock.mods.buildcraft;

import buildcraft.api.mj.IMjConnector;
import buildcraft.api.mj.IMjReceiver;
import buildcraft.api.mj.MjAPI;
import com.builtbroken.energystorageblock.EnergyStorageBlockMod;
import com.builtbroken.energystorageblock.block.TileEntityEnergyStorage;
import com.builtbroken.energystorageblock.config.ConfigPowerSystem;
import com.builtbroken.energystorageblock.mods.ModProxy;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void attachCapabilityItem(AttachCapabilitiesEvent<TileEntity> event)
    {
        if (event.getObject() instanceof TileEntityEnergyStorage)
        {
            event.addCapability(
                    new ResourceLocation(EnergyStorageBlockMod.DOMAIN, "wrapper.buildcraft.energy"),
                    new MjCapabilityProvider((TileEntityEnergyStorage) event.getObject()));
        }
    }

    @Override
    public boolean outputPower(TileEntityEnergyStorage tile, TileEntity target, EnumFacing enumFacing)
    {
        if (tile.hasCapability(CapabilityEnergy.ENERGY, enumFacing.getOpposite()))
        {
            IEnergyStorage energyStorage = tile.getCapability(CapabilityEnergy.ENERGY, enumFacing.getOpposite());
            if (energyStorage != null)
            {
                if (canConnect(tile, target, enumFacing))
                {
                    if (target.hasCapability(MjAPI.CAP_RECEIVER, enumFacing))
                    {
                        IMjReceiver receiver = target.getCapability(MjAPI.CAP_RECEIVER, enumFacing);
                        if (receiver != null && receiver.canReceive())
                        {
                            long request = receiver.getPowerRequested();
                            if (request > 0)
                            {
                                //Convert and check extract
                                int energy = (int) Math.floor(request * ConfigPowerSystem.FROM_BUILDCRAFT);
                                energy = energyStorage.extractEnergy(energy, true);

                                if (energy > 0)
                                {
                                    //Convert and insert
                                    long insert = (long) Math.floor(energy / ConfigPowerSystem.FROM_BUILDCRAFT);
                                    long leftOver = receiver.receivePower(insert, false);

                                    //Get energy taken
                                    long taken = insert - leftOver;

                                    //convert and remove energy
                                    energy = (int) Math.ceil(taken * ConfigPowerSystem.FROM_BUILDCRAFT);
                                    energyStorage.extractEnergy(energy, false);
                                }
                            }
                            return true;
                        }
                    }
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
