package com.builtbroken.craftblocks.network;

import com.builtbroken.craftblocks.content.CrafterRecipe;
import com.builtbroken.craftblocks.content.TileEntityCrafter;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/18/2018.
 */
public class MessagePainterRecipeToggle extends MessageTile
{
    protected boolean increase;

    public MessagePainterRecipeToggle()
    {
        //Empty for packet builder
    }

    public MessagePainterRecipeToggle(TileEntity tile, boolean increase)
    {
        this(tile.getWorld().provider.getDimension(), tile.getPos(), increase);
    }

    public MessagePainterRecipeToggle(int dim, BlockPos pos, boolean increase)
    {
        super(dim, pos);
        this.increase = increase;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        super.fromBytes(buf);
        increase = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        super.toBytes(buf);
        buf.writeBoolean(increase);
    }

    public static class MessageHandler implements IMessageHandler<MessagePainterRecipeToggle, MessageTile>
    {
        @Override
        public MessageTile onMessage(MessagePainterRecipeToggle message, MessageContext ctx)
        {
            World world = message.getWorld(ctx);
            if (world != null && !world.isRemote && world.provider.getDimension() == message.dim)
            {
                if (world.isBlockLoaded(message.blockPos))
                {
                    TileEntity tile = world.getTileEntity(message.blockPos);
                    if (tile instanceof TileEntityCrafter)
                    {
                        ((WorldServer) world).addScheduledTask(() -> {
                            ((TileEntityCrafter<CrafterRecipe>) tile).toggleRecipe(message.increase);
                        });
                    }
                }
            }
            return null;
        }
    }
}
