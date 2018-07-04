package com.builtbroken.energystorageblock.content.wireless.connector;

import com.builtbroken.energystorageblock.config.ConfigWirelessEnergyTower;
import com.builtbroken.energystorageblock.content.TileEntityEnergy;
import com.builtbroken.energystorageblock.content.wireless.controller.TileEntityWirelessController;
import com.builtbroken.energystorageblock.lib.network.MessageDesc;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

import javax.annotation.Nullable;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/3/2018.
 */
public class TileEntityWirelessConnector extends TileEntityEnergy implements ITickable
{
    public static final String NBT_OUTPUT_ENERGY = "shouldOutputEnergy";

    private boolean shouldOutputEnergy = false;
    private boolean sendDescPacket = false;
    private TileEntityWirelessController controller;

    @Override
    public void update()
    {
        if (!world.isRemote)
        {
            //Only move power if we have power to move
            if (energyStorage.getEnergyStored() > 0)
            {
                //Last handle connected tiles
                outputPowerToConnectedTiles();
            }

            if (sendDescPacket)
            {
                sendDescPacket = false;
                MessageDesc.send(this);
            }
        }
    }

    @Override
    protected void readData(NBTTagCompound compound)
    {
        super.readData(compound);
        shouldOutputEnergy = compound.getBoolean(NBT_OUTPUT_ENERGY);
    }

    @Override
    protected NBTTagCompound writeData(NBTTagCompound compound)
    {
        compound.setBoolean(NBT_OUTPUT_ENERGY, shouldOutputEnergy);
        return super.writeData(compound);
    }

    @Override
    public boolean canInputEnergySide(@Nullable EnumFacing side)
    {
        return !shouldOutputEnergyToTiles() && getController() != null;
    }

    @Override
    public boolean canOutputEnergySide(@Nullable EnumFacing side)
    {
        return shouldOutputEnergyToTiles();
    }

    @Override
    public int getInputLimit(@Nullable EnumFacing side)
    {
        return ConfigWirelessEnergyTower.INPUT_LIMIT;
    }

    @Override
    public int getOutputLimit(@Nullable EnumFacing side)
    {
        return ConfigWirelessEnergyTower.OUTPUT_LIMIT;
    }

    @Override
    public int getEnergyCapacity()
    {
        return ConfigWirelessEnergyTower.CAPACITY;
    }

    public boolean shouldOutputEnergyToTiles()
    {
        return shouldOutputEnergy;
    }

    public void setOutputEnergyToTiles(boolean energyOutput)
    {
        shouldOutputEnergy = energyOutput;
        sendDescPacket = true;
    }

    public TileEntityWirelessController getController()
    {
        if (controller != null && controller.isInvalid())
        {
            controller = null;
        }
        return controller;
    }

    public void setController(TileEntityWirelessController controller)
    {
        this.controller = controller;
    }
}
