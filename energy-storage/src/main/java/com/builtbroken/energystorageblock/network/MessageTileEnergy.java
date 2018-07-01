package com.builtbroken.energystorageblock.network;

import com.builtbroken.energystorageblock.EnergyStorageBlockMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/1/2018.
 */
public class MessageTileEnergy extends MessageTile
{
    protected int energy;

    public MessageTileEnergy()
    {
        //Empty for packet builder
    }

    public MessageTileEnergy(int dim, int x, int y, int z, int energy)
    {
        super(dim, x, y, z);
        this.energy = energy;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        super.fromBytes(buf);
        energy = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        super.toBytes(buf);
        buf.writeInt(energy);
    }

    //Should only get packets client side
    public static class MessageHandler implements IMessageHandler<MessageTileEnergy, MessageTile>
    {
        @Override
        public MessageTile onMessage(MessageTileEnergy message, MessageContext ctx)
        {
            World world = EnergyStorageBlockMod.proxy.getLocalWorld();
            if (world != null)
            {

            }
            return null;
        }
    }
}
