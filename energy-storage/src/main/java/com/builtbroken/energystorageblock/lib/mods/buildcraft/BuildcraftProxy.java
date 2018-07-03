package com.builtbroken.energystorageblock.lib.mods.buildcraft;

import buildcraft.api.mj.IMjReceiver;
import buildcraft.api.mj.MjAPI;
import com.builtbroken.energystorageblock.EnergyStorageBlockMod;
import com.builtbroken.energystorageblock.content.cube.TileEntityEnergyStorage;
import com.builtbroken.energystorageblock.config.ConfigEnergyStorage;
import com.builtbroken.energystorageblock.config.ConfigPowerSystem;
import com.builtbroken.energystorageblock.lib.mods.EnergyModProxy;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/22/2018.
 */
public class BuildcraftProxy extends EnergyModProxy
{
    public static final String BC = "buildcraftenergy";
    public static final BuildcraftProxy INSTANCE = new BuildcraftProxy();

    @Override
    @Optional.Method(modid = "buildcraftenergy")
    public void preInit()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    @Optional.Method(modid = "buildcraftenergy")
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
    @Optional.Method(modid = "buildcraftenergy")
    public boolean outputPower(TileEntity target, TileEntity source, IEnergyStorage energyStorage, EnumFacing enumFacing)
    {
        //Check that we support output for side
        if (ConfigPowerSystem.ENABLE_BUILDCRAFT && source.hasCapability(CapabilityEnergy.ENERGY, enumFacing.getOpposite()))
        {
            //Check that target can receive energy
            if (target.hasCapability(MjAPI.CAP_RECEIVER, enumFacing))
            {
                IMjReceiver receiver = target.getCapability(MjAPI.CAP_RECEIVER, enumFacing);
                if (receiver != null && receiver.canReceive())
                {
                    long request = receiver.getPowerRequested();
                    request = Math.min(request, ConfigEnergyStorage.OUTPUT_LIMIT_BC * MjAPI.ONE_MINECRAFT_JOULE);
                    if (request > 0)
                    {
                        //Convert and check extract
                        int energy = (int) Math.floor(toForgeEnergy(request));
                        energy = energyStorage.extractEnergy(energy, true);

                        if (energy > 0)
                        {
                            //Convert and insert
                            long insert = (long) Math.floor(toBuildcraftEnergy(energy));
                            long leftOver = receiver.receivePower(insert, false);

                            //Get energy taken
                            long taken = insert - leftOver;

                            //convert and remove energy
                            energy = (int) Math.ceil(toForgeEnergy(taken));
                            energyStorage.extractEnergy(energy, false);
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static double toBuildcraftEnergy(int fe)
    {
        return (fe / ConfigPowerSystem.FE_PER_MJ) * MjAPI.MJ;
    }

    public static double toForgeEnergy(long mj)
    {
        return (mj / (double) MjAPI.MJ) * ConfigPowerSystem.FE_PER_MJ;
    }
}
