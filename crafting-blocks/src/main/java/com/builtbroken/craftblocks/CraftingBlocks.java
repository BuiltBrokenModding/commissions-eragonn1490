package com.builtbroken.craftblocks;

import com.builtbroken.craftblocks.content.item.ItemCraftingPower;
import com.builtbroken.craftblocks.content.item.ItemCraftingTool;
import com.builtbroken.craftblocks.content.paint.BlockPainter;
import com.builtbroken.craftblocks.content.paint.PainterRecipe;
import com.builtbroken.craftblocks.content.paint.TileEntityPainter;
import com.builtbroken.craftblocks.content.stone.BlockStoneCutter;
import com.builtbroken.craftblocks.content.stone.StoneCutterRecipe;
import com.builtbroken.craftblocks.content.stone.TileEntityStoneCutter;
import com.builtbroken.craftblocks.network.NetworkHandler;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

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
    public static Block blockStoneCutter;

    public static Item itemBlockPainter;
    public static Item itemBlockStoneCutter;

    public static ItemCraftingTool itemPaintBrush;
    public static ItemCraftingTool itemStoneChisel;

    public static ItemCraftingPower itemCrafterPower;
    public static ItemCraftingPower itemCrafterPowerLibrarian;
    public static ItemCraftingPower itemCrafterPowerPainter;
    public static ItemCraftingPower itemCrafterPowerCraftsman;

    @SidedProxy(clientSide = "com.builtbroken.craftblocks.client.ClientProxy", serverSide = "com.builtbroken.craftblocks.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Metadata(DOMAIN)
    public static ModMetadata metadata;

    public static Logger logger = LogManager.getLogger(DOMAIN);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        NetworkHandler.init();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        setModMetadata(DOMAIN, "Crafting Block Mod", metadata);

        for (EnumDyeColor enumDyeColor : EnumDyeColor.values())
        {
            TileEntityPainter.registerRecipe(new PainterRecipe("wool." + enumDyeColor.getUnlocalizedName(),
                    new ItemStack(Blocks.WOOL, 1, EnumDyeColor.WHITE.getMetadata()),
                    new ItemStack(Blocks.WOOL, 1, enumDyeColor.getMetadata()),
                    enumDyeColor
                    ).setRegistryName(PREFIX + "wool." + enumDyeColor.getName()));
        }

        TileEntityStoneCutter.registerRecipe(new StoneCutterRecipe("stone.brick",
                new ItemStack(Blocks.STONE, 1, 0),
                new ItemStack(Blocks.STONEBRICK, 1, 0),
                ItemStack.EMPTY).setRegistryName(PREFIX + "stone.brick"));

        TileEntityStoneCutter.registerRecipe(new StoneCutterRecipe("stone.cracked",
                new ItemStack(Blocks.STONE, 1, 0),
                new ItemStack(Blocks.STONEBRICK, 1, 2),
                ItemStack.EMPTY).setRegistryName(PREFIX + "stone.cracked"));

        TileEntityStoneCutter.registerRecipe(new StoneCutterRecipe("stone.chiseled",
                new ItemStack(Blocks.STONE, 1, 0),
                new ItemStack(Blocks.STONEBRICK, 1, 3),
                ItemStack.EMPTY).setRegistryName(PREFIX + "stone.chiseled"));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(itemBlockPainter = new ItemBlock(blockPainter).setRegistryName(blockPainter.getRegistryName()));
        event.getRegistry().register(itemBlockStoneCutter = new ItemBlock(blockStoneCutter).setRegistryName(blockStoneCutter.getRegistryName()));

        event.getRegistry().register(itemPaintBrush = new ItemCraftingTool("brush", 100));
        event.getRegistry().register(itemStoneChisel = new ItemCraftingTool("chisel", 100));

        event.getRegistry().register(itemCrafterPower = new ItemCraftingPower("power_default", 10));
        event.getRegistry().register(itemCrafterPowerLibrarian = new ItemCraftingPower("power_librarian", 30));
        event.getRegistry().register(itemCrafterPowerPainter = new ItemCraftingPower("power_painter", 100));
        event.getRegistry().register(itemCrafterPowerCraftsman = new ItemCraftingPower("power_craftsman", 100));
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().register(blockPainter = new BlockPainter());
        event.getRegistry().register(blockStoneCutter = new BlockStoneCutter());

        GameRegistry.registerTileEntity(TileEntityPainter.class, PREFIX + "painter_block");
        GameRegistry.registerTileEntity(TileEntityStoneCutter.class, PREFIX + "stone_cutter");
    }

    public void setModMetadata(String id, String name, ModMetadata metadata)
    {
        metadata.modId = id;
        metadata.name = name;
        metadata.description = "Commission mod to create crafting machines";
        metadata.url = "http://www.builtbroken.com/";
        metadata.version = "0.0.1";
        metadata.authorList = Arrays.asList(new String[]{"DarkCow"});
        metadata.credits = "eragon1490";
        metadata.autogenerated = false;
    }
}
