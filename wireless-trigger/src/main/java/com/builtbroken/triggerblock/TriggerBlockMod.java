package com.builtbroken.triggerblock;

import com.builtbroken.triggerblock.block.BlockTrigger;
import com.builtbroken.triggerblock.block.TileEntityTrigger;
import com.builtbroken.triggerblock.cap.CapabilityTriggerHz;
import com.builtbroken.triggerblock.item.ItemTriggerClear;
import com.builtbroken.triggerblock.item.ItemTriggerRemote;
import com.builtbroken.triggerblock.reprogrammer.BlockReprogrammer;
import com.builtbroken.triggerblock.reprogrammer.TileEntityReprogrammer;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/28/2018.
 */
@Mod(modid = TriggerBlockMod.DOMAIN, name = "Trigger Block Mod")
@Mod.EventBusSubscriber(modid = TriggerBlockMod.DOMAIN)
public class TriggerBlockMod
{
    public static final String DOMAIN = "triggerblockmod";
    public static final String PREFIX = DOMAIN + ":";

    public static Block blockTrigger;
    public static Block blockReprogrammer;
    public static Item itemTrigger;
    public static Item itemClear;

    @Mod.Instance(DOMAIN)
    public static TriggerBlockMod INSTANCE;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        CapabilityTriggerHz.register();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new ItemBlock(blockTrigger).setRegistryName(blockTrigger.getRegistryName()));
        event.getRegistry().register(new ItemBlock(blockReprogrammer).setRegistryName(blockReprogrammer.getRegistryName()));

        event.getRegistry().register(itemTrigger = new ItemTriggerRemote());
        event.getRegistry().register(itemClear = new ItemTriggerClear());
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().register(blockTrigger = new BlockTrigger());
        GameRegistry.registerTileEntity(TileEntityTrigger.class, PREFIX + "trigger");

        event.getRegistry().register(blockReprogrammer = new BlockReprogrammer());
        GameRegistry.registerTileEntity(TileEntityReprogrammer.class, PREFIX + "reprogrammer");
    }
}
