package com.builtbroken.energystorageblock.block;

import com.builtbroken.energystorageblock.EnergyStorageBlockMod;
import com.builtbroken.energystorageblock.config.ConfigEnergyStorage;
import com.builtbroken.energystorageblock.config.ConfigPowerSystem;
import com.builtbroken.energystorageblock.energy.EnergyBlockStorage;
import com.builtbroken.energystorageblock.energy.EnergySideState;
import com.builtbroken.energystorageblock.energy.EnergySideWrapper;
import com.builtbroken.energystorageblock.mods.EnergyModProxy;
import com.builtbroken.energystorageblock.network.IDescMessageTile;
import com.builtbroken.energystorageblock.network.MessageDesc;
import com.builtbroken.energystorageblock.network.MessageTileEnergy;
import com.builtbroken.energystorageblock.network.NetworkHandler;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/30/2018.
 */
@Optional.InterfaceList({
        @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "ic2")
})
public class TileEntityEnergyStorage extends TileEntity implements ITickable, IEnergySink, IEnergySource, IDescMessageTile
{
    //NBT keys
    public static final String NBT_ENERGY = "energy";
    public static final String NBT_ENERGY_SIDES = "energy_sides";
    public static final String NBT_INVENTORY = "inventory";

    //Inventory constants
    public static final int INVENTORY_SIZE = 2;
    public static final int SLOT_BATTERY_DISCHARGE = 0;
    public static final int SLOT_BATTERY_CHARGE = 1;

    /** Main power storage */
    public final EnergyBlockStorage energyStorage = new EnergyBlockStorage(this);
    /** Main inventory */
    public final ItemStackHandler inventory = new ItemStackHandler(INVENTORY_SIZE);

    //Side handler for input/output/disconnect state
    private EnergySideWrapper[] energySideWrapper = new EnergySideWrapper[6];

    private int prevEnergy = -1;
    private boolean sendDescPacket = false;
    private boolean markForRender = true;

    @Override
    public void update()
    {
        if (!world.isRemote)
        {
            //Pull power in first
            dischargeBattery();

            //Only move power if we have power to move
            if (energyStorage.getEnergyStored() > 0)
            {
                //Output to charge item, second to allow users to charge items
                chargeBattery();
                //Last handle connected tiles
                handlePowerTiles();
            }

            if (sendDescPacket)
            {
                sendDescPacket = false;
                prevEnergy = energyStorage.getEnergyStored();
                MessageDesc.send(this);
            }
            else if (energyStorage.getEnergyStored() != prevEnergy)
            {
                prevEnergy = energyStorage.getEnergyStored();
                NetworkHandler.sendToAllAround(this, new MessageTileEnergy(this, prevEnergy));
            }
        }
        else if (markForRender)
        {
            markForRender = false;

            markDirty();

            //Update blocks so render changes
            world.markAndNotifyBlock(pos,
                    world.getChunkFromBlockCoords(getPos()),
                    world.getBlockState(getPos()),
                    world.getBlockState(getPos()), 3);
        }
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return oldState.getBlock() != newSate.getBlock();
    }

    protected void dischargeBattery()
    {
        ItemStack batteryToDischarge = inventory.getStackInSlot(SLOT_BATTERY_DISCHARGE);
        if (!batteryToDischarge.isEmpty())
        {
            if (batteryToDischarge.hasCapability(CapabilityEnergy.ENERGY, null))
            {
                IEnergyStorage storage = batteryToDischarge.getCapability(CapabilityEnergy.ENERGY, null);
                if (storage != null && storage.canExtract())
                {
                    //Check how much can be drained
                    int offer = storage.extractEnergy(ConfigEnergyStorage.INPUT_LIMIT, true);

                    //Fill tile storage
                    int taken = energyStorage.receiveEnergy(offer, false);

                    //Drain battery
                    storage.extractEnergy(taken, false);

                    //Trigger inventory update, without saving may fail
                    inventory.setStackInSlot(SLOT_BATTERY_DISCHARGE, batteryToDischarge);
                }
            }
            else
            {
                EnergyModProxy.dischargeBattery(energyStorage, ConfigEnergyStorage.INPUT_LIMIT, batteryToDischarge);
            }
        }
    }

    protected void chargeBattery()
    {
        ItemStack batteryToCharge = inventory.getStackInSlot(SLOT_BATTERY_CHARGE);
        if (!batteryToCharge.isEmpty())
        {
            if (batteryToCharge.hasCapability(CapabilityEnergy.ENERGY, null))
            {
                IEnergyStorage storage = batteryToCharge.getCapability(CapabilityEnergy.ENERGY, null);
                if (storage != null && storage.canReceive())
                {
                    //Check how much can be inserted
                    int request = storage.receiveEnergy(ConfigEnergyStorage.OUTPUT_LIMIT, true);

                    //Drain power from tile
                    int drained = energyStorage.extractEnergy(request, false);

                    //Charge battery
                    storage.receiveEnergy(drained, false);

                    //Trigger inventory update, without saving may fail
                    inventory.setStackInSlot(SLOT_BATTERY_CHARGE, batteryToCharge);
                }
            }
            else
            {
                EnergyModProxy.chargeBattery(energyStorage, ConfigEnergyStorage.OUTPUT_LIMIT, batteryToCharge);
            }
        }
    }

    protected void handlePowerTiles()
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
                            EnergyModProxy.chargeTile(tileEntity, this, energyStorage, enumFacing.getOpposite());
                        }
                    }
                }
            }
        }
    }

    public EnergySideState toggleEnergySide(EnumFacing side)
    {
        //update state
        EnergySideWrapper wrapper = getEnergySideWrapper(side);
        wrapper.sideState = wrapper.sideState.next();

        //Next tick send packet
        sendDescPacket = true;

        //Mark so the block saves
        markDirty();

        //Update blocks so wire connections change
        world.markAndNotifyBlock(pos,
                world.getChunkFromBlockCoords(getPos()),
                world.getBlockState(getPos()),
                world.getBlockState(getPos()), 3);

        //Return new state
        return wrapper.sideState;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound writeDescMessage(NBTTagCompound tagCompound)
    {
        return writeData(tagCompound);
    }

    @Override
    public void readDescMessage(NBTTagCompound tagCompound)
    {
        readData(tagCompound);
        markForRender = true;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        inventory.deserializeNBT(compound.getCompoundTag(NBT_INVENTORY));
        readData(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setTag(NBT_INVENTORY, inventory.serializeNBT());
        return writeData(super.writeToNBT(compound));
    }

    protected void readData(NBTTagCompound compound)
    {
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

    protected NBTTagCompound writeData(NBTTagCompound compound)
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
                sideSave.setByte(facing.getName(), (byte) wrapper.sideState.ordinal());
            }
        }
        compound.setTag(NBT_ENERGY_SIDES, sideSave);

        return compound;
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

    public EnergySideWrapper getEnergySideWrapper(@Nonnull EnumFacing facing)
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
        if (ConfigPowerSystem.ENABLE_IC2)
        {
            int out = energyStorage.extractEnergy(ConfigEnergyStorage.OUTPUT_LIMIT, true);
            return out / ConfigPowerSystem.FROM_IC2;
        }
        return 0;
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
        return ConfigPowerSystem.ENABLE_IC2
                && hasCapability(CapabilityEnergy.ENERGY, side)
                && getEnergySideWrapper(side).sideState == EnergySideState.OUTPUT;
    }
    //</editor-fold>

    @Override
    public void invalidate()
    {
        super.invalidate();
        EnergyStorageBlockMod.energyModProxies.forEach(proxy -> proxy.onTileInvalidate(this));
    }

    @Override
    public void validate()
    {
        super.validate();
        EnergyStorageBlockMod.energyModProxies.forEach(proxy -> proxy.onTileValidate(this));
    }
}
