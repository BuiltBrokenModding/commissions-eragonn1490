package com.builtbroken.craftblocks.network;

import com.builtbroken.craftblocks.content.paint.TileEntityPainter;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/17/2018.
 */
public class MessageOnState extends MessageTile
{
    protected boolean onState;

    public MessageOnState()
    {
        //Empty for packet builder
    }

    public MessageOnState(TileEntity tile, boolean onState)
    {
        this(tile.getWorld().provider.getDimension(), tile.getPos(), onState);
    }

    public MessageOnState(int dim, BlockPos pos, boolean onState)
    {
        super(dim, pos);
        this.onState = onState;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        super.fromBytes(buf);
        onState = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        super.toBytes(buf);
        buf.writeBoolean(onState);
    }

    public static class MessageHandler implements IMessageHandler<MessageOnState, MessageTile>
    {
        @Override
        public MessageTile onMessage(MessageOnState message, MessageContext ctx)
        {
            World world = message.getWorld(ctx);
            if (world != null && !world.isRemote && world.provider.getDimension() == message.dim)
            {
                if (world.isBlockLoaded(message.blockPos))
                {
                    TileEntity tile = world.getTileEntity(message.blockPos);
                    if (tile instanceof TileEntityPainter)
                    {
                        ((WorldServer)world).addScheduledTask(() -> {
                            ((TileEntityPainter) tile).machineOn = message.onState;
                            ((TileEntityPainter) tile).syncClient = true;
                        });
                    }
                }
            }
            return null;
        }
    }
}
