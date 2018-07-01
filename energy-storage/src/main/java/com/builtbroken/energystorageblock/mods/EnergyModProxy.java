package com.builtbroken.energystorageblock.mods;

import com.builtbroken.energystorageblock.EnergyStorageBlockMod;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/22/2018.
 */
public class EnergyModProxy
{

    public static boolean chargeTile(TileEntity target, TileEntity source, IEnergyStorage energyStorage, EnumFacing side)
    {
        for (EnergyModProxy proxy : EnergyStorageBlockMod.energyModProxies)
        {
            if (proxy.outputPower(target, source, energyStorage, side))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Called to attempt to charge the battery using mod proxy handling
     * <p>
     * Should run FE capability charging before this
     *
     * @param energyStorage - storage to remove power from
     * @param stack         - battery to add power to
     * @return true if one of the proxies handled the battery
     */
    public static boolean chargeBattery(IEnergyStorage energyStorage, ItemStack stack)
    {
        for (EnergyModProxy proxy : EnergyStorageBlockMod.energyModProxies)
        {
            if (proxy.handleBatteryCharge(energyStorage, stack))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Called to attempt to discharge the battery using mod proxy handling
     * <p>
     * Should run FE capability charging before this
     *
     * @param energyStorage - storage to add power to
     * @param stack         - battery to drain
     * @return true if one of the proxies handled the battery
     */
    public static boolean dischargeBattery(IEnergyStorage energyStorage, ItemStack stack)
    {
        for (EnergyModProxy proxy : EnergyStorageBlockMod.energyModProxies)
        {
            if (proxy.handleBatteryDischarge(energyStorage, stack))
            {
                return true;
            }
        }
        return false;
    }

    public void preInit()
    {

    }

    public void init()
    {

    }

    public void postInit()
    {

    }

    /**
     * Called to output power
     *
     * @param target        - tile to feed power
     * @param source        - source of the power
     * @param energyStorage - storage to drain power from
     * @param side          - side of target accessed
     * @return true to consume action
     */
    public boolean outputPower(TileEntity target, TileEntity source, IEnergyStorage energyStorage, EnumFacing side)
    {
        return false;
    }

    /**
     * Called to output power to a battery item
     *
     * @param energyStorage - tile giving energy
     * @param stack         - battery stack to fill
     * @return true if this proxy can handle or gave power to the battery
     */
    protected boolean handleBatteryCharge(IEnergyStorage energyStorage, ItemStack stack)
    {
        return false;
    }

    /**
     * Called to take power from a battery item
     *
     * @param energyStorage - tile to add power to
     * @param stack         - battery stack to drain
     * @return true if this proxy can handle or took power from the stack
     */
    protected boolean handleBatteryDischarge(IEnergyStorage energyStorage, ItemStack stack)
    {
        return false;
    }

    public void onTileValidate(TileEntity tile)
    {

    }

    public void onTileInvalidate(TileEntity tile)
    {

    }

    public boolean isBattery(ItemStack stack)
    {
        return false;
    }
}
