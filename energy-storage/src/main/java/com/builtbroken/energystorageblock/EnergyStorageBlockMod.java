package com.builtbroken.energystorageblock;

import com.builtbroken.energystorageblock.content.cube.BlockEnergyStorage;
import com.builtbroken.energystorageblock.content.cube.TileEntityEnergyStorage;
import com.builtbroken.energystorageblock.content.wireless.connector.BlockWirelessConnector;
import com.builtbroken.energystorageblock.content.wireless.connector.TileEntityWirelessConnector;
import com.builtbroken.energystorageblock.content.wireless.controller.BlockWirelessController;
import com.builtbroken.energystorageblock.content.wireless.controller.TileEntityWirelessController;
import com.builtbroken.energystorageblock.lib.mods.EnergyModProxy;
import com.builtbroken.energystorageblock.lib.mods.buildcraft.BuildcraftProxy;
import com.builtbroken.energystorageblock.lib.mods.ic2.IC2Proxy;
import com.builtbroken.energystorageblock.lib.network.NetworkHandler;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/30/2018.
 */
@Mod(modid = EnergyStorageBlockMod.DOMAIN, name = "Energy Storage Block Mod")
@Mod.EventBusSubscriber(modid = EnergyStorageBlockMod.DOMAIN)
public class EnergyStorageBlockMod
{
    public static final String DOMAIN = "energystorageblock";
    public static final String PREFIX = DOMAIN + ":";

    public static Block blockEnergyCube;

    public static Block blockWirelessController;
    public static Block blockWirelessConnector;

    @Mod.Instance(DOMAIN)
    public static EnergyStorageBlockMod INSTANCE;

    @Mod.Metadata(DOMAIN)
    public static ModMetadata metadata;

    public static List<EnergyModProxy> energyModProxies = new ArrayList();

    @SidedProxy(clientSide = "com.builtbroken.energystorageblock.client.ClientProxy", serverSide = "com.builtbroken.energystorageblock.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        NetworkHandler.init();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);

        if (Loader.isModLoaded("ic2"))
        {
            energyModProxies.add(IC2Proxy.INSTANCE);
        }
        if (Loader.isModLoaded("buildcraftenergy"))
        {
            energyModProxies.add(BuildcraftProxy.INSTANCE);
        }

        energyModProxies.forEach(proxy -> proxy.preInit());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        setModMetadata(DOMAIN, "Energy Storage Block Mod", metadata);
        energyModProxies.forEach(proxy -> proxy.init());

        TileEntityWirelessController.multiBlockLayout = new Block[]
                {
                        Blocks.WEB,
                        Blocks.DIRT,
                        Blocks.DIRT,
                        Blocks.COBBLESTONE,
                        blockWirelessController,
                        blockWirelessConnector,
                        Blocks.COBBLESTONE
                };
        TileEntityWirelessController.multiBlockDeltaY = -2;

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        energyModProxies.forEach(proxy -> proxy.postInit());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new ItemBlock(blockEnergyCube).setRegistryName(blockEnergyCube.getRegistryName()));
        event.getRegistry().register(new ItemBlock(blockWirelessConnector).setRegistryName(blockWirelessConnector.getRegistryName()));
        event.getRegistry().register(new ItemBlock(blockWirelessController).setRegistryName(blockWirelessController.getRegistryName()));
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().register(blockEnergyCube = new BlockEnergyStorage());
        GameRegistry.registerTileEntity(TileEntityEnergyStorage.class, PREFIX + "energy_storage");

        event.getRegistry().register(blockWirelessConnector = new BlockWirelessConnector());
        GameRegistry.registerTileEntity(TileEntityWirelessConnector.class, PREFIX + "wireless_energy_connector");

        event.getRegistry().register(blockWirelessController = new BlockWirelessController());
        GameRegistry.registerTileEntity(TileEntityWirelessController.class, PREFIX + "wireless_energy_controller");
    }

    @SubscribeEvent
    public static void onConfigChangedEvent(final ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(DOMAIN))
        {
            ConfigManager.sync(DOMAIN, Config.Type.INSTANCE);
        }
    }

    public void setModMetadata(String id, String name, ModMetadata metadata)
    {
        metadata.modId = id;
        metadata.name = name;
        metadata.description = "Commission mod to create a universal power cube";
        metadata.url = "http://www.builtbroken.com/";
        metadata.version = "0.0.1";
        metadata.authorList = Arrays.asList(new String[]{"DarkCow"});
        metadata.credits = "eragon1490";
        metadata.autogenerated = false;
    }
}
