package com.builtbroken.craftblocks;

import com.builtbroken.craftblocks.paint.BlockPainter;
import com.builtbroken.craftblocks.paint.TileEntityPainter;
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
 * Created by Dark(DarkGuardsman, Robert) on 8/15/2018.
 */
@Mod(modid = CraftingBlocks.DOMAIN, name = "Crafting Block Mod")
@Mod.EventBusSubscriber(modid = CraftingBlocks.DOMAIN)
public class CraftingBlocks
{
    public static final String DOMAIN = "craftingblockmod";
    public static final String PREFIX = DOMAIN + ":";

    public static Block blockPainter;


    public static Item itemBlockPainter;


    public static Item itemPaintBrush;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(itemBlockPainter = new ItemBlock(blockPainter).setRegistryName(blockPainter.getRegistryName()));
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().register(blockPainter = new BlockPainter());

        GameRegistry.registerTileEntity(TileEntityPainter.class, PREFIX + "painter_block");
    }
}
