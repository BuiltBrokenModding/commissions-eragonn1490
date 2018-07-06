package com.builtbroken.triggerblock.item;

import com.builtbroken.triggerblock.TriggerBlockMod;
import com.builtbroken.triggerblock.cap.CapabilityTriggerHz;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/25/2018.
 */
public class ItemTriggerClear extends Item
{
    public ItemTriggerClear()
    {
        setUnlocalizedName(TriggerBlockMod.PREFIX + "trigger.clear");
        setRegistryName(TriggerBlockMod.DOMAIN, "trigger_clear");
        setCreativeTab(CreativeTabs.REDSTONE);
        setMaxStackSize(1);
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
    {
        if (!worldIn.isRemote)
        {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity != null && tileEntity.hasCapability(CapabilityTriggerHz.CAPABILITY, null))
            {
                tileEntity.getCapability(CapabilityTriggerHz.CAPABILITY, null).setTriggerHz(0);
                player.sendMessage(new TextComponentTranslation(getUnlocalizedName() + ".info.cleared"));
            }
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        return EnumActionResult.SUCCESS;
    }
}
