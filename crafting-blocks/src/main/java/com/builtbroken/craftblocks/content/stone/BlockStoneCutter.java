package com.builtbroken.craftblocks.content.stone;

import com.builtbroken.craftblocks.CraftingBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/19/2018.
 */
public class BlockStoneCutter extends Block implements ITileEntityProvider
{
    public BlockStoneCutter()
    {
        super(Material.ROCK);
        setRegistryName(CraftingBlocks.DOMAIN, "stone_cutter");
        setUnlocalizedName(CraftingBlocks.PREFIX + "stone.cutter");
        setCreativeTab(CreativeTabs.REDSTONE);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityStoneCutter();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEntityStoneCutter)
        {
            if (!worldIn.isRemote)
            {
                playerIn.openGui(CraftingBlocks.DOMAIN, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
            }
            return true;
        }
        return false;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntityStoneCutter)
        {
            ItemStackHandler handler = ((TileEntityStoneCutter) tileentity).inventory;
            for (int i = 0; i < handler.getSlots(); ++i)
            {
                ItemStack itemstack = handler.getStackInSlot(i);

                if (!itemstack.isEmpty())
                {
                    InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), itemstack);
                    handler.setStackInSlot(i, ItemStack.EMPTY);
                }
            }
        }
        super.breakBlock(worldIn, pos, state);
    }
}
