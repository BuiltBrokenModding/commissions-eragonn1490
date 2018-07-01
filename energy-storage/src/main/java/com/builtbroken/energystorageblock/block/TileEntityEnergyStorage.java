package com.builtbroken.energystorageblock.block;

import com.builtbroken.energystorageblock.EnergyStorageBlockMod;
import com.builtbroken.energystorageblock.config.ConfigEnergyStorage;
import com.builtbroken.energystorageblock.config.ConfigPowerSystem;
import com.builtbroken.energystorageblock.energy.EnergyBlockStorage;
import com.builtbroken.energystorageblock.energy.EnergySideState;
import com.builtbroken.energystorageblock.energy.EnergySideWrapper;
import com.builtbroken.energystorageblock.mods.ModProxy;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/30/2018.
 */
@Optional.InterfaceList({
        @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "ic2")
})
public class TileEntityEnergyStorage extends TileEntity implements ITickable, IEnergySink, IEnergySource
{
    public static final String NBT_ENERGY = "energy";
    public static final String NBT_ENERGY_SIDES = "energy_sides";

    public final EnergyBlockStorage energyStorage = new EnergyBlockStorage();
    private EnergySideWrapper[] energySideWrapper = new EnergySideWrapper[6];

    @Override
    public void update()
    {
        if (!world.isRemote && energyStorage.getEnergyStored() > 0)
        {
            for (EnumFacing enumFacing : EnumFacing.VALUES)
            {
                if (getEnergySideWrapper(enumFacing).sideState == EnergySideState.OUTPUT)
                {
                    BlockPos pos = getPos().add(enumFacing.getDirectionVec());
                    if (world.isBlockLoaded(pos))
                    {
                        TileEntity tileEntity = world.getTileEntity(pos);
                        if (tileEntity != null)
                        {
                            if (tileEntity.hasCapability(CapabilityEnergy.ENERGY, enumFacing.getOpposite()))
                            {
                                IEnergyStorage cap = tileEntity.getCapability(CapabilityEnergy.ENERGY, enumFacing.getOpposite());
                                if (cap != null)
                                {
                                    int give = energyStorage.extractEnergy(ConfigEnergyStorage.OUTPUT_LIMIT, true);
                                    int taken = cap.receiveEnergy(give, false);
                                    energyStorage.extractEnergy(taken, false);
                                }
                            }
                            else
                            {
                                givePower(tileEntity, enumFacing.getOpposite());
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean givePower(TileEntity target, EnumFacing side)
    {
        for (ModProxy proxy : EnergyStorageBlockMod.modProxies)
        {
            if (proxy.outputPower(this, target, side))
            {
                return true;
            }
        }
        return false;
    }

    public EnergySideState toggleEnergySide(EnumFacing side)
    {
        EnergySideWrapper wrapper = getEnergySideWrapper(side);
        wrapper.sideState = wrapper.sideState.next();
        return wrapper.sideState;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        //Load energy
        energyStorage.setEnergy(compound.getInteger(NBT_ENERGY));

        //Load side data
        if (compound.hasKey(NBT_ENERGY_SIDES))
        {
            NBTTagCompound sideSave = compound.getCompoundTag(NBT_ENERGY_SIDES);
            for (EnumFacing facing : EnumFacing.VALUES)
            {
                EnergySideWrapper wrapper = getEnergySideWrapper(facing);
                byte i = sideSave.getByte(facing.getName());
                if (i >= 0 && i < EnergySideState.values().length)
                {
                    wrapper.sideState = EnergySideState.values()[i];
                }
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        //Save energy
        compound.setInteger(NBT_ENERGY, energyStorage.getEnergyStored());

        //Save side data
        NBTTagCompound sideSave = new NBTTagCompound();
        for (EnumFacing facing : EnumFacing.VALUES)
        {
            EnergySideWrapper wrapper = energySideWrapper[facing.ordinal()];
            if (wrapper != null)
            {
                sideSave.setByte(facing.name(), (byte) wrapper.sideState.ordinal());
            }
        }
        compound.setTag(NBT_ENERGY_SIDES, sideSave);

        return super.writeToNBT(compound);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityEnergy.ENERGY)
        {
            if (facing != null)
            {
                return getEnergySideWrapper(facing).sideState != EnergySideState.NONE;
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
            return (T) (facing == null ? energyStorage : getEnergySideWrapper(facing));
        }
        return super.getCapability(capability, facing);
    }

    private EnergySideWrapper getEnergySideWrapper(@Nonnull EnumFacing facing)
    {
        if (energySideWrapper[facing.ordinal()] == null)
        {
            energySideWrapper[facing.ordinal()] = new EnergySideWrapper(energyStorage);
        }
        return energySideWrapper[facing.ordinal()];
    }


    //<editor-fold desc="ic2 power">
    @Override
    @Optional.Method(modid = "ic2")
    public double getDemandedEnergy()
    {
        if (ConfigPowerSystem.ENABLE_IC2)
        {
            int need = energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored();
            return need / ConfigPowerSystem.FROM_IC2;
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
            int energy = (int) Math.floor(amount * ConfigPowerSystem.FROM_IC2);
            int received = energyStorage.receiveEnergy(energy, false);
            return amount - (received / ConfigPowerSystem.FROM_IC2);
        }
        return amount;
    }

    @Override
    @Optional.Method(modid = "ic2")
    public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing side)
    {
        return ConfigPowerSystem.ENABLE_IC2 && hasCapability(CapabilityEnergy.ENERGY, side);
    }


    @Override
    @Optional.Method(modid = "ic2")
    public double getOfferedEnergy()
    {
        return energyStorage.extractEnergy(ConfigEnergyStorage.OUTPUT_LIMIT, true) / ConfigPowerSystem.FROM_IC2;
    }

    @Override
    @Optional.Method(modid = "ic2")
    public void drawEnergy(double amount)
    {
        int energy = (int) Math.ceil(amount * ConfigPowerSystem.FROM_IC2);
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
        return ConfigPowerSystem.ENABLE_IC2 && hasCapability(CapabilityEnergy.ENERGY, side);
    }
    //</editor-fold>

    @Override
    public void invalidate()
    {
        super.invalidate();
        EnergyStorageBlockMod.modProxies.forEach(proxy -> proxy.onTileInvalidate(this));
    }

    @Override
    public void validate()
    {
        super.validate();
        EnergyStorageBlockMod.modProxies.forEach(proxy -> proxy.onTileValidate(this));
    }
}
