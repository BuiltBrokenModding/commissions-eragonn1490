package com.builtbroken.triggerblock;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = TriggerBlockMod.DOMAIN)
public class ClientReg
{
    @SubscribeEvent
    public static void registerAllModels(ModelRegistryEvent event)
    {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TriggerBlockMod.blockTrigger),
                0, new ModelResourceLocation(TriggerBlockMod.blockTrigger.getRegistryName(), "inventory"));

        ModelLoader.setCustomModelResourceLocation(TriggerBlockMod.itemTrigger,
                0, new ModelResourceLocation(TriggerBlockMod.itemTrigger.getRegistryName(), "inventory"));

        ModelLoader.setCustomModelResourceLocation(TriggerBlockMod.itemClear,
                0, new ModelResourceLocation(TriggerBlockMod.itemClear.getRegistryName(), "inventory"));
    }
}
