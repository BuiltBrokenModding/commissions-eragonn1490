package com.builtbroken.craftblocks.network;

import com.builtbroken.craftblocks.CraftingBlocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/1/2018.
 */
public class NetworkHandler
{
    public static SimpleNetworkWrapper NETWORK;

    public static void init()
    {
        NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(CraftingBlocks.DOMAIN);
        NETWORK.registerMessage(MessageOnState.MessageHandler.class, MessageOnState.class, 0, Side.CLIENT);
        NETWORK.registerMessage(MessageDesc.MessageHandler.class, MessageDesc.class, 1, Side.CLIENT);
    }

    public static void sendToAllAround(TileEntity tileEntity, IMessage message)
    {
        NETWORK.sendToAllAround(message,
                new NetworkRegistry.TargetPoint(
                        tileEntity.getWorld().provider.getDimension(),
                        tileEntity.getPos().getX(),
                        tileEntity.getPos().getY(),
                        tileEntity.getPos().getZ(),
                        64
                ));
    }
}
