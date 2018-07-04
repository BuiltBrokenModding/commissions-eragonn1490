package com.builtbroken.energystorageblock;

import com.builtbroken.energystorageblock.lib.network.IDescMessageTile;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/4/2018.
 */
public abstract class TileEntityMachine extends TileEntity implements IDescMessageTile
{
    //<editor-fold desc="packets">
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
    }
    //</editor-fold>

    //<editor-fold desc="save/load">
    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        readData(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        return writeData(super.writeToNBT(compound));
    }

    protected void readData(NBTTagCompound compound)
    {

    }

    protected NBTTagCompound writeData(NBTTagCompound compound)
    {
        return compound;
    }
    //</editor-fold>
}
