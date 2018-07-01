package com.builtbroken.energystorageblock.network;

import com.builtbroken.energystorageblock.EnergyStorageBlockMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/1/2018.
 */
public class MessageDesc extends MessageTile
{
    protected NBTTagCompound tag;

    public MessageDesc()
    {
        //Empty for packet builder
    }

    public MessageDesc(int dim, BlockPos pos, NBTTagCompound tag)
    {
        super(dim, pos);
        this.tag = tag;
    }


    public static void send(TileEntity tile)
    {
        if (tile instanceof IDescMessageTile)
        {
            MessageDesc messageDesc = new MessageDesc(
                    tile.getWorld().provider.getDimension(),
                    tile.getPos(),
                    ((IDescMessageTile) tile).writeDescMessage(new NBTTagCompound()));

            NetworkHandler.sendToAllAround(tile, messageDesc);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        super.fromBytes(buf);
        tag = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        super.toBytes(buf);
        ByteBufUtils.writeTag(buf, tag);
    }

    //Should only get packets client side
    public static class MessageHandler implements IMessageHandler<MessageDesc, MessageTile>
    {
        @Override
        public MessageTile onMessage(MessageDesc message, MessageContext ctx)
        {
            World world = EnergyStorageBlockMod.proxy.getLocalWorld();
            if (world != null && world.provider.getDimension() == message.dim)
            {
                if (world.isBlockLoaded(message.blockPos))
                {
                    TileEntity tile = world.getTileEntity(message.blockPos);
                    if (tile instanceof IDescMessageTile)
                    {
                        ((IDescMessageTile) tile).readDescMessage(message.tag);
                    }
                }
            }
            return null;
        }
    }
}
