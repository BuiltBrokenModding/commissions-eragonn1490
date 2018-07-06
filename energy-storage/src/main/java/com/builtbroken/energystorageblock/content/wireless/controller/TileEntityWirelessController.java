package com.builtbroken.energystorageblock.content.wireless.controller;

import com.builtbroken.energystorageblock.TileEntityMachine;
import com.builtbroken.energystorageblock.config.ConfigWirelessEnergyTower;
import com.builtbroken.energystorageblock.content.wireless.connector.TileEntityWirelessConnector;
import com.builtbroken.energystorageblock.content.wireless.controller.network.WirelessTowerHzHandler;
import com.builtbroken.energystorageblock.content.wireless.controller.prop.EnumWirelessState;
import com.builtbroken.energystorageblock.lib.network.MessageDesc;
import com.builtbroken.triggerblock.cap.CapabilityTriggerHz;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/3/2018.
 */
public class TileEntityWirelessController extends TileEntityMachine implements ITickable
{
    public static final String NBT_FREQUENCY = "frequency";

    public static Block[] multiBlockLayout;
    public static int multiBlockDeltaY;

    protected boolean hzChanged = false;

    private final CapabilityWirelessHz capabilityHz = new CapabilityWirelessHz(this);

    private TileEntityWirelessConnector connector;
    private List<BlockPos> connectionPoints = new ArrayList();


    private boolean sendDescPacket = false;
    private boolean isMultiBlockFormed = false;

    private int ticks = 0;

    @Override
    public void update()
    {
        //Count ticks
        if (ticks++ >= Short.MAX_VALUE)
        {
            ticks = 1;
        }

        //Server logic
        if (!world.isRemote)
        {
            //First tick run logic
            if (ticks == 0)
            {
                isMultiBlockFormed = checkMultiBlock();
                handleWirelessState();
            }
            //Check multi-block
            else if (ticks % ConfigWirelessEnergyTower.MULTI_BLOCK_SCAN_RATE == 0)
            {
                handleMultiBlock();
            }

            //Look for connections
            if (ticks % ConfigWirelessEnergyTower.CONNECTION_CHECK_RATE == 0)
            {
                scanForWirelessConnections();
            }

            //Update state if hz changed
            if (hzChanged)
            {
                hzChanged = false;
                handleWirelessState();
            }

            //Handle wireless network (output power)
            if (ticks % 5 == 0)
            {
                handleWirelessNetwork();
            }

            //Send packet if needed
            if (sendDescPacket)
            {
                sendDescPacket = false;
                MessageDesc.send(this);
            }
        }
    }

    protected void scanForWirelessConnections()
    {
        connectionPoints.clear();
        connectionPoints = WirelessTowerHzHandler.getTiles(capabilityHz.getTriggerHz(),
                getWorld(), getPos(), ConfigWirelessEnergyTower.RANGE);

        handleWirelessState();
    }

    protected void handleWirelessState()
    {
        if (isMultiBlockFormed())
        {
            if (capabilityHz.getTriggerHz() != 0)
            {
                if (connectionPoints.size() > 0)
                {
                    world.setBlockState(getPos(), world.getBlockState(getPos()).withProperty(BlockWirelessController.WIRELESS_STATE, EnumWirelessState.LINKED));
                }
                else
                {
                    world.setBlockState(getPos(), world.getBlockState(getPos()).withProperty(BlockWirelessController.WIRELESS_STATE, EnumWirelessState.CONNECTED));
                }
            }
            else
            {
                world.setBlockState(getPos(), world.getBlockState(getPos()).withProperty(BlockWirelessController.WIRELESS_STATE, EnumWirelessState.DISCONNECTED));
            }
        }
        else
        {
            world.setBlockState(getPos(), world.getBlockState(getPos()).withProperty(BlockWirelessController.WIRELESS_STATE, EnumWirelessState.INCOMPLETE));
        }
        sendDescPacket = true;
    }

    protected void handleWirelessNetwork()
    {
        //Only run logic if we have a frequency and are built
        if (capabilityHz.getTriggerHz() != 0 && isMultiBlockFormed())
        {
            if (connector != null)
            {
                final IEnergyStorage energyStorage = connector.energyStorage;

                int energyToOffer = energyStorage.getEnergyStored();
                int connectors = connectionPoints.size(); //Will always be 1 connector (self)

                if (energyToOffer > 0 && connectors > 1)
                {
                    //Loop possible connections
                    for (BlockPos pos : connectionPoints)
                    {
                        //Make sure its loaded
                        if (world.isBlockLoaded(pos))
                        {
                            //Get tile, make sure its a controller
                            TileEntity tile = world.getTileEntity(pos);
                            if (tile instanceof TileEntityWirelessController && tile != this)
                            {
                                IEnergyStorage targetStorage = ((TileEntityWirelessController) tile).getEnergyStorage();
                                if (targetStorage != null)
                                {
                                    //Get energy to move
                                    int offer = Math.min(ConfigWirelessEnergyTower.TRANSFER_LIMIT, (int) Math.floor(energyToOffer / (double) connectors));

                                    //Dump energy into target and get energy moved
                                    int energyMoved = targetStorage.receiveEnergy(offer, false);

                                    //Decrease internal energy storage
                                    energyToOffer -= energyStorage.extractEnergy(energyMoved, false);
                                }
                                connectors--;
                            }
                        }
                    }
                }
            }
        }
    }

    protected IEnergyStorage getEnergyStorage()
    {
        if (connector != null)
        {
            return connector.energyStorage;
        }
        return null;
    }

    protected void handleMultiBlock()
    {
        //Check structure
        boolean prevMultiBlockFormed = isMultiBlockFormed();
        isMultiBlockFormed = checkMultiBlock();

        //Play effect to act as user feedback
        if (prevMultiBlockFormed != isMultiBlockFormed())
        {
            handleWirelessState();
        }

        //Find connector
        if (connector == null || connector.isInvalid())
        {
            TileEntity tile = world.getTileEntity(getPos().down());
            if (tile instanceof TileEntityWirelessConnector)
            {
                connector = (TileEntityWirelessConnector) tile;
                connector.setController(this);
            }
        }
    }

    @Override
    protected void readData(NBTTagCompound compound)
    {
        capabilityHz.setTriggerHz(compound.getInteger(NBT_FREQUENCY));
    }

    @Override
    protected NBTTagCompound writeData(NBTTagCompound compound)
    {
        compound.setInteger(NBT_FREQUENCY, capabilityHz.getTriggerHz());
        return compound;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityTriggerHz.CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    @Nullable
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityTriggerHz.CAPABILITY)
        {
            return (T) capabilityHz;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return oldState.getBlock() != newSate.getBlock();
    }

    public boolean isMultiBlockFormed()
    {
        return isMultiBlockFormed;
    }

    protected boolean checkMultiBlock()
    {
        return true;
    }

    public boolean isOutputMode()
    {
        return connector != null && !connector.shouldOutputEnergyToTiles();
    }
}
