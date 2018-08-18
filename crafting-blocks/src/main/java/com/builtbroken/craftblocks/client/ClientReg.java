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
    }
}
