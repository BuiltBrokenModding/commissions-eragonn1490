package com.builtbroken.energystorageblock;

import com.builtbroken.energystorageblock.block.BlockEnergyStorage;
import com.builtbroken.energystorageblock.block.TileEntityEnergyStorage;
import com.builtbroken.energystorageblock.mods.ModProxy;
import com.builtbroken.energystorageblock.mods.buildcraft.BuildcraftProxy;
import com.builtbroken.energystorageblock.mods.ic2.IC2Proxy;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
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

    public static Block blockTrigger;

    @Mod.Instance(DOMAIN)
    public static EnergyStorageBlockMod INSTANCE;

    public static List<ModProxy> modProxies = new ArrayList();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (Loader.isModLoaded("ic2"))
        {
            modProxies.add(IC2Proxy.INSTANCE);
        }
        if (Loader.isModLoaded("buildcraftenergy"))
        {
            modProxies.add(BuildcraftProxy.INSTANCE);
        }

        modProxies.forEach(proxy -> proxy.preInit());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        modProxies.forEach(proxy -> proxy.init());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        modProxies.forEach(proxy -> proxy.postInit());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new ItemBlock(blockTrigger).setRegistryName(blockTrigger.getRegistryName()));
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().register(blockTrigger = new BlockEnergyStorage());
        GameRegistry.registerTileEntity(TileEntityEnergyStorage.class, PREFIX + "energy_storage");
    }
}
