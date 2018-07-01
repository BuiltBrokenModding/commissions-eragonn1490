package com.builtbroken.energystorageblock.network;

import com.builtbroken.energystorageblock.EnergyStorageBlockMod;
import net.minecraftforge.fml.common.network.NetworkRegistry;
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
        NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(EnergyStorageBlockMod.DOMAIN);
        NETWORK.registerMessage(MessageTileEnergy.MessageHandler.class, MessageTileEnergy.class, 0, Side.CLIENT);
    }
}
