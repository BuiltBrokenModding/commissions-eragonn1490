package com.builtbroken.energystorageblock.content.wireless.controller;

import com.builtbroken.energystorageblock.EnergyStorageBlockMod;
import com.builtbroken.energystorageblock.content.wireless.controller.prop.EnumWirelessState;
import com.builtbroken.energystorageblock.content.wireless.controller.prop.PropWirelessState;
import com.builtbroken.triggerblock.cap.CapabilityTriggerHz;
import com.builtbroken.triggerblock.cap.ITriggerHz;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/2/2018.
 */
public class BlockWirelessController extends Block implements ITileEntityProvider
{
    public static final PropWirelessState WIRELESS_STATE = new PropWirelessState();

    public BlockWirelessController()
    {
        super(Material.IRON);
        this.setDefaultState(this.blockState.getBaseState().withProperty(WIRELESS_STATE, EnumWirelessState.INCOMPLETE));
        setRegistryName(EnergyStorageBlockMod.DOMAIN, "wireless_energy_controller");
        setUnlocalizedName(EnergyStorageBlockMod.PREFIX + "wireless.energy.controller");
        setCreativeTab(CreativeTabs.REDSTONE);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEntityWirelessController)
        {
            ItemStack stack = playerIn.getHeldItem(hand);
            if (!stack.isEmpty())
            {
                if (stack.hasCapability(CapabilityTriggerHz.CAPABILITY, null))
                {
                    if (!worldIn.isRemote)
                    {
                        ITriggerHz stackCap = stack.getCapability(CapabilityTriggerHz.CAPABILITY, null);
                        if (stackCap.getTriggerHz() != 0)
                        {
                            ITriggerHz tileCap = tile.getCapability(CapabilityTriggerHz.CAPABILITY, null);
                            if (tileCap.getTriggerHz() == 0)
                            {
                                tileCap.setTriggerHz(stackCap.getTriggerHz());
                                playerIn.sendMessage(new TextComponentTranslation(getUnlocalizedName() + ".info.set.new", tileCap.getTriggerHz()));
                            }
                            else
                            {
                                playerIn.sendMessage(new TextComponentTranslation(getUnlocalizedName() + ".info.set.already"));
                            }
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityWirelessController();
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(WIRELESS_STATE, EnumWirelessState.get(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(WIRELESS_STATE).ordinal();
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, WIRELESS_STATE);
    }
}
