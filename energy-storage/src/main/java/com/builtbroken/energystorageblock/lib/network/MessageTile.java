package com.builtbroken.energystorageblock.lib.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/1/2018.
 */
public abstract class MessageTile implements IMessage
{
    protected int dim;
    protected BlockPos blockPos;

    public MessageTile()
    {
        //Empty for packet builder
    }

    public MessageTile(int dim, BlockPos blockPos)
    {
        this.dim = dim;
        this.blockPos = blockPos;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        dim = buf.readInt();
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        blockPos = new BlockPos(x, y, z);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(dim);
        buf.writeInt(blockPos.getX());
        buf.writeInt(blockPos.getY());
        buf.writeInt(blockPos.getZ());
    }
}
