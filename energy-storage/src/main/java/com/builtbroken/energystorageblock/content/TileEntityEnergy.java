package com.builtbroken.energystorageblock.content;

import com.builtbroken.energystorageblock.EnergyStorageBlockMod;
import com.builtbroken.energystorageblock.TileEntityMachine;
import com.builtbroken.energystorageblock.config.ConfigEnergyStorage;
import com.builtbroken.energystorageblock.config.ConfigPowerSystem;
import com.builtbroken.energystorageblock.lib.energy.EnergyStorageTile;
import com.builtbroken.energystorageblock.lib.mods.EnergyModProxy;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nullable;

/**
 * Prefab for any machine that supports energy connections and storage
 * <p>
 * Designed to work with {@link EnergyStorageTile} and any {@link com.builtbroken.energystorageblock.lib.mods.EnergyModProxy}
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/3/2018.
 */
@Optional.InterfaceList({
        @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "ic2"),
        @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySource", modid = "ic2")
})
public abstract class TileEntityEnergy extends TileEntityMachine implements IEnergySink, IEnergySource
{
    //NBT keys
    public static final String NBT_ENERGY = "energy";

    /** Main power storage */
    public final EnergyStorageTile energyStorage = new EnergyStorageTile(this);

    private boolean hasValidated = false;

    /**
     * Called to output power to tiles on all 6 sides
     */
    protected void outputPowerToConnectedTiles()
    {
        //Loop all 6 sides
        for (EnumFacing enumFacing : EnumFacing.VALUES)
        {
            //Check that we can output
            if (canOutputEnergySide(enumFacing))
            {
                //Ensure tile exists, without can cause chunks to load
                BlockPos pos = getPos().add(enumFacing.getDirectionVec());
                if (world.isBlockLoaded(pos))
                {
                    //Get tile
                    TileEntity tileEntity = world.getTileEntity(pos);
                    if (tileEntity != null)
                    {
                        //Check if tile supports ForgeEnergy
                        if (tileEntity.hasCapability(CapabilityEnergy.ENERGY, enumFacing.getOpposite()))
                        {
                            //Get cap, null check because some mods do not read the API
                            IEnergyStorage cap = tileEntity.getCapability(CapabilityEnergy.ENERGY, enumFacing.getOpposite());
                            if (cap != null)
                            {
                                //Get energy to offer
                                int give = energyStorage.extractEnergy(getOutputLimit(enumFacing), true);

                                //Offer energy, and see how much was taken
                                int taken = cap.receiveEnergy(give, false);

                                //Remove taken energy from internal storage
                                energyStorage.extractEnergy(taken, false);
                            }
                        }
                        //If not try mod energy proxies
                        else
                        {
                            EnergyModProxy.chargeTile(tileEntity, this, energyStorage, enumFacing.getOpposite());
                        }
                    }
                }
            }
        }
    }

    //<editor-fold desc="implement">

    /**
     * Checks if the machine can input energy for the side
     *
     * @param side - face of the machine, pointing out of the machine
     * @return true if can input energy for given side
     */
    public abstract boolean canInputEnergySide(@Nullable EnumFacing side);

    /**
     * Checks if the machine can output energy for the side
     *
     * @param side - face of the machine, pointing out of the machine
     * @return true if can output energy for given side
     */
    public abstract boolean canOutputEnergySide(@Nullable EnumFacing side);

    /**
     * Gets input limit for the side
     *
     * @param side - face of the machine, pointing out of the machine
     * @return input limit
     */
    public abstract int getInputLimit(@Nullable EnumFacing side);

    /**
     * Gets output limit for the side
     *
     * @param side - face of the machine, pointing out of the machine
     * @return output limit
     */
    public abstract int getOutputLimit(@Nullable EnumFacing side);

    /**
     * Amount of energy this tile can store
     *
     * @return energy to store
     */
    public abstract int getEnergyCapacity();
    //</editor-fold>

    //<editor-fold desc="save/load">
    protected void readData(NBTTagCompound compound)
    {
        super.readData(compound);
        //Load energy
        energyStorage.setEnergy(compound.getInteger(NBT_ENERGY));
    }

    protected NBTTagCompound writeData(NBTTagCompound compound)
    {
        //Save energy
        compound.setInteger(NBT_ENERGY, energyStorage.getEnergyStored());
        return super.writeData(compound);
    }
    //</editor-fold>

    //<editor-fold desc="capability">
    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityEnergy.ENERGY)
        {
            if (facing != null)
            {
                return canOutputEnergySide(facing) || canInputEnergySide(facing);
            }
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    @Nullable
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityEnergy.ENERGY)
        {
            return (T) energyStorage;
        }
        return super.getCapability(capability, facing);
    }
    //</editor-fold>

    //<editor-fold desc="ic2 power">
    @Override
    @Optional.Method(modid = "ic2")
    public double getDemandedEnergy()
    {
        if (ConfigPowerSystem.ENABLE_IC2)
        {
            int need = energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored();
            return need / ConfigPowerSystem.FE_PER_EU;
        }
        return 0;
    }

    @Override
    @Optional.Method(modid = "ic2")
    public int getSinkTier()
    {
        return ConfigPowerSystem.ENABLE_IC2 ? 4 : 0;
    }

    @Override
    @Optional.Method(modid = "ic2")
    public double injectEnergy(EnumFacing directionFrom, double amount, double voltage)
    {
        if (ConfigPowerSystem.ENABLE_IC2)
        {
            int energy = (int) Math.floor(amount * ConfigPowerSystem.FE_PER_EU);
            int received = energyStorage.receiveEnergy(energy, false);
            return amount - (received / ConfigPowerSystem.FE_PER_EU);
        }
        return amount;
    }

    @Override
    @Optional.Method(modid = "ic2")
    public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing side)
    {
        return ConfigPowerSystem.ENABLE_IC2
                && hasCapability(CapabilityEnergy.ENERGY, side)
                && canInputEnergySide(side);
    }


    @Override
    @Optional.Method(modid = "ic2")
    public double getOfferedEnergy()
    {
        if (ConfigPowerSystem.ENABLE_IC2)
        {
            int out = energyStorage.extractEnergy(ConfigEnergyStorage.OUTPUT_LIMIT, true);
            return out / ConfigPowerSystem.FE_PER_EU;
        }
        return 0;
    }

    @Override
    @Optional.Method(modid = "ic2")
    public void drawEnergy(double amount)
    {
        int energy = (int) Math.ceil(amount * ConfigPowerSystem.FE_PER_EU);
        energyStorage.extractEnergy(energy, false);
    }

    @Override
    @Optional.Method(modid = "ic2")
    public int getSourceTier()
    {
        return 1; //Might need increased to allow more power flow
    }

    @Override
    @Optional.Method(modid = "ic2")
    public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing side)
    {
        return ConfigPowerSystem.ENABLE_IC2
                && hasCapability(CapabilityEnergy.ENERGY, side)
                && canOutputEnergySide(side);
    }
    //</editor-fold>

    @Override
    public void invalidate()
    {
        super.invalidate();
        if (!hasValidated)
        {
            hasValidated = true;
            EnergyStorageBlockMod.energyModProxies.forEach(proxy -> proxy.onTileInvalidate(this));
        }
    }

    @Override
    public void validate()
    {
        boolean wasInvalid = tileEntityInvalid;
        super.validate();
        if (hasValidated && wasInvalid)
        {
            hasValidated = false;
            EnergyStorageBlockMod.energyModProxies.forEach(proxy -> proxy.onTileValidate(this));
        }
    }
}
