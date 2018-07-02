package com.builtbroken.energystorageblock.mods.ic2;

import com.builtbroken.energystorageblock.config.ConfigPowerSystem;
import com.builtbroken.energystorageblock.mods.EnergyModProxy;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/22/2018.
 */
public class IC2Proxy extends EnergyModProxy
{
    public static final IC2Proxy INSTANCE = new IC2Proxy();

    @Override
    @Optional.Method(modid = "ic2")
    public boolean outputPower(TileEntity target, TileEntity source, IEnergyStorage energyStorage, EnumFacing enumFacing)
    {
        if (source.hasCapability(CapabilityEnergy.ENERGY, enumFacing.getOpposite()))
        {
            if (target instanceof IEnergySink && ((IEnergySink) target).acceptsEnergyFrom(null, enumFacing))
            {
                //Get demand and convert to FE power
                double demand = ((IEnergySink) target).getDemandedEnergy();
                int request = (int) Math.floor(demand * ConfigPowerSystem.FROM_IC2);

                //Check how much power we can remove
                int give = energyStorage.extractEnergy(request, true);

                //Convert give to IC2
                double inject = give / ConfigPowerSystem.FROM_IC2;

                //Inject energy
                double leftOver = ((IEnergySink) target).injectEnergy(enumFacing, inject, 1);

                //Remove energy from storage
                inject -= leftOver;
                int remove = (int) Math.ceil(inject * ConfigPowerSystem.FROM_IC2);
                energyStorage.extractEnergy(remove, false);
                return true;
            }
        }
        return false;
    }

    @Override
    @Optional.Method(modid = "ic2")
    protected boolean handleBatteryCharge(IEnergyStorage energyStorage, int limit, ItemStack stack)
    {
        if (stack.getItem() instanceof IElectricItem)
        {
            //Get energy to offer
            int offer = energyStorage.extractEnergy(limit, true);

            if (offer > 0)
            {
                //Convert to IC2 power
                double insert = offer / ConfigPowerSystem.FROM_IC2;

                //Give energy
                int tier = ((IElectricItem) stack.getItem()).getTier(stack);
                double taken = ElectricItem.manager.charge(stack, insert, tier, false, false);

                //Drain energy from storage
                int energy = (int) Math.ceil(taken * ConfigPowerSystem.FROM_IC2);
                energyStorage.extractEnergy(energy, false);
            }

            return true;

        }
        return false;
    }

    @Override
    @Optional.Method(modid = "ic2")
    protected boolean handleBatteryDischarge(IEnergyStorage energyStorage, int limit, ItemStack stack)
    {
        if (stack.getItem() instanceof IElectricItem)
        {
            //Calculate drain from battery
            double drain = limit / ConfigPowerSystem.FROM_IC2;
            int tier = ((IElectricItem) stack.getItem()).getTier(stack);
            drain = ElectricItem.manager.discharge(stack, drain, tier, false, true, true);

            //Calculate how much we can insert into tile
            int input = (int) Math.ceil(drain * ConfigPowerSystem.FROM_IC2);
            input = energyStorage.receiveEnergy(input, true);

            //Drain battery
            drain = input / ConfigPowerSystem.FROM_IC2;
            drain = ElectricItem.manager.discharge(stack, drain, tier, false, true, false);

            //Insert into tile
            input = (int) Math.ceil(drain * ConfigPowerSystem.FROM_IC2);
            energyStorage.receiveEnergy(input, false);

            return true;
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
