package com.builtbroken.energystorageblock.block;

import com.builtbroken.energystorageblock.EnergyStorageBlockMod;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/30/2018.
 */
public class BlockEnergyStorage extends Block implements ITileEntityProvider
{
    public BlockEnergyStorage()
    {
        super(Material.IRON);
        setRegistryName(EnergyStorageBlockMod.DOMAIN, "energy_storage");
        setUnlocalizedName(EnergyStorageBlockMod.PREFIX + "energy.storage");
        setCreativeTab(CreativeTabs.REDSTONE);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEntityEnergyStorage)
        {
            if (playerIn.getHeldItem(hand).getItem() == Items.STICK)
            {
                if (!worldIn.isRemote)
                {
                    playerIn.sendMessage(new TextComponentTranslation(
                            getUnlocalizedName() + ".info.power",
                            ((TileEntityEnergyStorage) tile).energyStorage.getEnergyStored(),
                            ((TileEntityEnergyStorage) tile).energyStorage.getMaxEnergyStored()));

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
        return new TileEntityEnergyStorage();
    }
}
