package com.builtbroken.craftblocks.client;

import com.builtbroken.craftblocks.CraftingBlocks;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = CraftingBlocks.DOMAIN)
public class ClientReg
{
    @SubscribeEvent
    public static void registerAllModels(ModelRegistryEvent event)
    {
        ModelLoader.setCustomModelResourceLocation(CraftingBlocks.itemBlockPainter,0, new ModelResourceLocation(CraftingBlocks.itemBlockPainter.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(CraftingBlocks.itemBlockStoneCutter,0, new ModelResourceLocation(CraftingBlocks.itemBlockStoneCutter.getRegistryName(), "inventory"));

        ModelLoader.setCustomModelResourceLocation(CraftingBlocks.itemPaintBrush,0, new ModelResourceLocation(CraftingBlocks.itemPaintBrush.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(CraftingBlocks.itemStoneChisel,0, new ModelResourceLocation(CraftingBlocks.itemStoneChisel.getRegistryName(), "inventory"));

        ModelLoader.setCustomModelResourceLocation(CraftingBlocks.itemCrafterPower,0, new ModelResourceLocation(CraftingBlocks.itemCrafterPower.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(CraftingBlocks.itemCrafterPowerLibrarian,0, new ModelResourceLocation(CraftingBlocks.itemCrafterPowerLibrarian.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(CraftingBlocks.itemCrafterPowerPainter,0, new ModelResourceLocation(CraftingBlocks.itemCrafterPowerPainter.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(CraftingBlocks.itemCrafterPowerCraftsman,0, new ModelResourceLocation(CraftingBlocks.itemCrafterPowerCraftsman.getRegistryName(), "inventory"));
    }
}
