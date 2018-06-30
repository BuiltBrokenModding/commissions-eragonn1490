package com.builtbroken.triggerblock.item;

import com.builtbroken.triggerblock.TriggerBlockMod;
import com.builtbroken.triggerblock.block.TileEntityTrigger;
import com.builtbroken.triggerblock.block.TriggerHzHandler;
import com.builtbroken.triggerblock.cap.CapabilityTriggerHz;
import com.builtbroken.triggerblock.cap.ITriggerHz;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/25/2018.
 */
@Mod.EventBusSubscriber(modid = TriggerBlockMod.DOMAIN)
public class ItemTriggerRemote extends Item
{
    double range = 10;

    public static final ResourceLocation REMOTE_CAP = new ResourceLocation(TriggerBlockMod.DOMAIN, "trigger.hz.remote");

    public ItemTriggerRemote()
    {
        setUnlocalizedName(TriggerBlockMod.PREFIX + "trigger.remote");
        setRegistryName(TriggerBlockMod.DOMAIN, "trigger_remote");
        setCreativeTab(CreativeTabs.REDSTONE);
        setMaxStackSize(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        if (stack.hasCapability(CapabilityTriggerHz.CAPABILITY, null))
        {
            ITriggerHz capabilityTriggerHz = stack.getCapability(CapabilityTriggerHz.CAPABILITY, null);
            tooltip.add("Hz: " + capabilityTriggerHz.getTriggerHz());
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        if (!worldIn.isRemote)
        {
            ItemStack itemStack = playerIn.getHeldItem(handIn);
            if (itemStack != null && itemStack.hasCapability(CapabilityTriggerHz.CAPABILITY, null))
            {
                ITriggerHz capabilityTriggerHz = itemStack.getCapability(CapabilityTriggerHz.CAPABILITY, null);
                int hz = capabilityTriggerHz.getTriggerHz();
                if (hz != 0)
                {
                    List<TileEntityTrigger> tiles = TriggerHzHandler.getTiles(hz,
                            playerIn.world, playerIn.posX, playerIn.posY, playerIn.posZ,
                            range);

                    int activated = 0;
                    int deactivated = 0;
                    for (TileEntityTrigger tile : tiles)
                    {
                        if (tile.hasFrequency())
                        {
                            boolean state = tile.isTriggered();
                            boolean newState = tile.toggleTrigger();
                            if (state != newState)
                            {
                                if (newState)
                                {
                                    activated++;
                                }
                                else
                                {
                                    deactivated++;
                                }
                            }
                        }
                    }
                    String translation_key = getUnlocalizedName() + ".info.result";
                    playerIn.sendMessage(new TextComponentTranslation(translation_key, activated, deactivated));
                }
            }
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }

    @PostConstruct
    public void onUpdate(ItemStack item, World world, Entity player, int itemSlot, boolean isSelected)
    {
        if (item.hasCapability(CapabilityTriggerHz.CAPABILITY, null))
        {
            ITriggerHz capabilityTriggerHz = item.getCapability(CapabilityTriggerHz.CAPABILITY, null);
            if (capabilityTriggerHz.getTriggerHz() == 0)
            {
                int min = Integer.MIN_VALUE;
                int max = Integer.MAX_VALUE;
                int channel = ThreadLocalRandom.current().nextInt(min + 1, max);
                capabilityTriggerHz.setTriggerHz(channel);
            }
        }
    }

    @SubscribeEvent
    public static void attachCapabilityItem(AttachCapabilitiesEvent<ItemStack> event)
    {
        if (event.getObject().getItem() == TriggerBlockMod.itemTrigger)
        {
            event.addCapability(REMOTE_CAP, new CapabilityTriggerHz());
        }
    }
}
