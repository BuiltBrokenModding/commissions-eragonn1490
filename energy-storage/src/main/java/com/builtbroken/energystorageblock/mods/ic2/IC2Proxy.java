package com.builtbroken.energystorageblock.mods.ic2;

import com.builtbroken.energystorageblock.block.TileEntityEnergyStorage;
import com.builtbroken.energystorageblock.config.ConfigPowerSystem;
import com.builtbroken.energystorageblock.mods.ModProxy;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergyTile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.Optional;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/22/2018.
 */
public class IC2Proxy extends ModProxy
{
    public static final IC2Proxy INSTANCE = new IC2Proxy();

    @Override
    @Optional.Method(modid = "ic2")
    public boolean outputPower(TileEntityEnergyStorage tile, TileEntity target, EnumFacing enumFacing)
    {
        if (tile.hasCapability(CapabilityEnergy.ENERGY, enumFacing.getOpposite()))
        {
            if (target instanceof IEnergySink && ((IEnergySink) target).acceptsEnergyFrom(null, enumFacing))
            {
                //Get demand and convert to FE power
                double demand = ((IEnergySink) target).getDemandedEnergy();
                int request = (int) Math.floor(demand * ConfigPowerSystem.FROM_IC2);

                //Check how much power we can remove
                int give = tile.energyStorage.extractEnergy(request, true);

                //Convert give to IC2
                double inject = give / ConfigPowerSystem.FROM_IC2;

                //Inject energy
                double leftOver = ((IEnergySink) target).injectEnergy(enumFacing, inject, 1);

                //Remove energy from storage
                inject -= leftOver;
                int remove = (int) Math.ceil(inject * ConfigPowerSystem.FROM_IC2);
                tile.energyStorage.extractEnergy(remove, false);
                return true;
            }
        }
        return false;
    }

    @Override
    @Optional.Method(modid = "ic2")
    public void onTileValidate(TileEntity tile)
    {
        if (ConfigPowerSystem.ENABLE_IC2 && tile instanceof IEnergyTile && !tile.getWorld().isRemote)
        {
            MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent((IEnergyTile) tile));
        }
    }

    @Override
    @Optional.Method(modid = "ic2")
    public void onTileInvalidate(TileEntity tile)
    {
        if (ConfigPowerSystem.ENABLE_IC2 && tile instanceof IEnergyTile && !tile.getWorld().isRemote)
        {
            MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent((IEnergyTile) tile));
        }
    }
}
