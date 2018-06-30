package com.builtbroken.triggerblock.block;

import com.builtbroken.triggerblock.TriggerBlockMod;
import com.builtbroken.triggerblock.cap.CapabilityTriggerHz;
import com.builtbroken.triggerblock.cap.ITriggerHz;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/25/2018.
 */
public class BlockTrigger extends Block implements ITileEntityProvider
{
    public static final PropertyBool POWERED = PropertyBool.create("powered");

    public BlockTrigger()
    {
        super(Material.ROCK);
        this.setDefaultState(this.blockState.getBaseState().withProperty(POWERED, false));
        setRegistryName(TriggerBlockMod.DOMAIN, "trigger_block");
        setUnlocalizedName(TriggerBlockMod.PREFIX + "trigger");
        setCreativeTab(CreativeTabs.REDSTONE);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEntityTrigger)
        {
            ItemStack stack = playerIn.getHeldItem(hand);
            if (stack != null)
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
            else if (stack.getItem() == Items.STICK) //Debug remove if you want
            {
                playerIn.sendMessage(new TextComponentTranslation(getUnlocalizedName() + ".info.triggered." + ((TileEntityTrigger) tile).isTriggered()));
                return true;
            }

        }
        return false;
    }

    @Override
    public boolean canProvidePower(IBlockState state)
    {
        return true;
    }

    @Override
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return getStrongPower(blockState, blockAccess, pos, side);
    }

    @Override
    public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return blockState.getValue(POWERED) ? 15 : 0;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityTrigger();
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(POWERED, meta == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(POWERED) ? 1 : 0;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, POWERED);
    }
}
