package com.builtbroken.energystorageblock.client;

import com.builtbroken.energystorageblock.EnergyStorageBlockMod;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = EnergyStorageBlockMod.DOMAIN)
public class ClientReg
{
    @SubscribeEvent
    public static void registerAllModels(ModelRegistryEvent event)
    {
        registerBlockModel(EnergyStorageBlockMod.blockEnergyCube);
        registerBlockModel(EnergyStorageBlockMod.blockWirelessController);
        registerBlockModel(EnergyStorageBlockMod.blockWirelessConnector);
    }

    private static void registerBlockModel(Block block)
    {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block),
                0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
    }
}
