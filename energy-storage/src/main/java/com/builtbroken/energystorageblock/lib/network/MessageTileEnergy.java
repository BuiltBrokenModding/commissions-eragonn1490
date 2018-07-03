package com.builtbroken.energystorageblock.lib.network;

import com.builtbroken.energystorageblock.EnergyStorageBlockMod;
import com.builtbroken.energystorageblock.content.cube.parts.EnergyBlockStorage;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
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

    public MessageTileEnergy(TileEntity tile, int energy)
    {
        this(tile.getWorld().provider.getDimension(), tile.getPos(), energy);
    }

    public MessageTileEnergy(int dim, BlockPos pos, int energy)
    {
        super(dim, pos);
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
            if (world != null && world.provider.getDimension() == message.dim)
            {
                if (world.isBlockLoaded(message.blockPos))
                {
                    TileEntity tile = world.getTileEntity(message.blockPos);
                    if (tile != null && tile.hasCapability(CapabilityEnergy.ENERGY, null))
                    {
                        IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, null);
                        if (storage instanceof EnergyBlockStorage)
                        {
                            ((EnergyBlockStorage) storage).setEnergy(message.energy);
                        }
                    }
                }
            }
            return null;
        }
    }
}
