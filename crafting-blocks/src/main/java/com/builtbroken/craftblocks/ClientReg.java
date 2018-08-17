package com.builtbroken.craftblocks;

import com.builtbroken.triggerblock.TriggerBlockMod;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = TriggerBlockMod.DOMAIN)
public class ClientReg
{
    @SubscribeEvent
    public static void registerAllModels(ModelRegistryEvent event)
    {
        //ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TriggerBlockMod.blockTrigger),
        //        0, new ModelResourceLocation(TriggerBlockMod.blockTrigger.getRegistryName(), "inventory"));
    }
}
