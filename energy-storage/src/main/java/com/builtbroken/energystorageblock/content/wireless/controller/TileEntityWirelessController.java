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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
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

    public static Object[] multiBlockLayout;
    public static int[] multiBlockMetaLayout;
    public static int multiBlockDeltaY;

    protected boolean hzChanged = false;

    public final CapabilityWirelessHz capabilityHz = new CapabilityWirelessHz(this);

    private TileEntityWirelessConnector connector;
    public List<BlockPos> connectionPoints = new ArrayList();

    private boolean sendDescPacket = false;
    private boolean isMultiBlockFormed = false;
    private boolean prevMultiBlockFormed = false;

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
                handleMultiBlock();
                handleWirelessState();
            }
            //Check multi-block
            else if (ticks % ConfigWirelessEnergyTower.MULTI_BLOCK_SCAN_RATE == 0)
            {
                boolean prevMultiBlockFormed = isMultiBlockFormed;
                handleMultiBlock();
                //If built, display some effects to show it was built
                if (isMultiBlockFormed && !prevMultiBlockFormed && ticks > 0)
                {
                    AxisAlignedBB bb = new AxisAlignedBB(getPos())
                            .expand(10, 10, 10)
                            .expand(-10, -10, -10);
                    List<EntityPlayer> list = world.getEntitiesWithinAABB(EntityPlayer.class, bb, EntitySelectors.IS_ALIVE);
                    for (EntityPlayer player : list)
                    {
                        player.sendStatusMessage(new TextComponentTranslation("tile.energystorageblock:wireless.energy.controller.info.built"), true);
                    }
                }
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

                int energyToOffer = energyStorage.getEnergyStored() / 2; //Only send half of our power
                int connectors = connectionPoints.size() - 1; // Will always be 1 connector (self)

                if (energyToOffer > 0 && connectors > 0)
                {
                    //Loop possible connections
                    for (BlockPos pos : connectionPoints)
                    {
                        //Make sure its loaded
                        if (world.isBlockLoaded(pos))
                        {
                            //Get tile, make sure its a controller
                            TileEntity targetTile = world.getTileEntity(pos);
                            if (targetTile instanceof TileEntityWirelessController && targetTile != this)
                            {
                                //Get tile's energy storage and try to send it power
                                IEnergyStorage targetStorage = ((TileEntityWirelessController) targetTile).getEnergyStorage();
                                if (targetStorage != null)
                                {
                                    //Get energy to move
                                    int offer = energyToOffer / connectors; //Send a percentage of power to allow sharing
                                    offer += energyToOffer % connectors; //Add remainder to prevent energy from stagnating
                                    offer = Math.min(ConfigWirelessEnergyTower.TRANSFER_LIMIT, offer);

                                    //Check that we can remove that much energy
                                    offer = energyStorage.extractEnergy(offer, true);

                                    //Dump energy into target and get energy moved
                                    int energyMoved = targetStorage.receiveEnergy(offer, false);

                                    //Decrease internal energy storage
                                    energyToOffer -= energyStorage.extractEnergy(energyMoved, false);
                                }

                                //Reduce connector count
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

    public boolean isMultiBlockFormed()
    {
        return isMultiBlockFormed;
    }

    protected boolean checkMultiBlock()
    {
        //Get start position
        BlockPos pos = getPos().up(multiBlockDeltaY);

        //Loop array that defines the multi-block
        for (int i = multiBlockLayout.length - 1; i >= 0; i--)
        {
            //Get entry (should be block or block state)
            Object entry = multiBlockLayout[i];

            if (entry instanceof Block)
            {
                //Get block at position
                Block checkBlock = (Block) entry;

                //Check block
                IBlockState blockState = world.getBlockState(pos);
                if (checkBlock != blockState.getBlock())
                {
                    return false;
                }
            }
            else if (entry instanceof IBlockState)
            {
                IBlockState state = (IBlockState) entry;

                //Check block
                IBlockState blockState = world.getBlockState(pos);
                if (!state.equals(blockState))
                {
                    return false;
                }
            }

            //Move up
            pos = pos.up();
        }
        return true;
    }

    public boolean isOutputMode()
    {
        return connector != null && !connector.shouldOutputEnergyToTiles();
    }
}
