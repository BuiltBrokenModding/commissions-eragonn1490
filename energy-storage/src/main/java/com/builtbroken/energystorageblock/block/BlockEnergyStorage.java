package com.builtbroken.energystorageblock.block;

import com.builtbroken.energystorageblock.EnergyStorageBlockMod;
import com.builtbroken.energystorageblock.energy.EnergySideState;
import com.builtbroken.energystorageblock.energy.EnergySideWrapper;
import com.builtbroken.energystorageblock.energy.PropertyEnergySideState;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/30/2018.
 */
public class BlockEnergyStorage extends Block implements ITileEntityProvider
{
    public static PropertyEnum<EnergySideState>[] side_state_props = new PropertyEnum[6];

    static
    {
        for (EnumFacing facing : EnumFacing.VALUES)
        {
            side_state_props[facing.ordinal()] = new PropertyEnergySideState("ess_" + facing.getName());
        }
    }

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
            if (playerIn.getHeldItem(hand).getItem() == Items.STICK) //Debug, can remove if you want
            {
                if (!worldIn.isRemote)
                {
                    if (tile.hasCapability(CapabilityEnergy.ENERGY, null))
                    {
                        IEnergyStorage energyStorage = tile.getCapability(CapabilityEnergy.ENERGY, null);
                        if (energyStorage != null)
                        {
                            playerIn.sendMessage(new TextComponentTranslation(
                                    getUnlocalizedName() + ".info.power",
                                    energyStorage.getEnergyStored(),
                                    energyStorage.getMaxEnergyStored()));
                        }
                    }
                }
                return true;
            }
            else if (playerIn.getHeldItem(hand).getItem() == Items.BLAZE_ROD) //Could replace with wrench for side toggle
            {
                if (!worldIn.isRemote)
                {
                    EnergySideState energySideState = ((TileEntityEnergyStorage) tile).toggleEnergySide(facing);
                    playerIn.sendMessage(new TextComponentTranslation(
                            getUnlocalizedName() + ".info.power.side.set." + facing.getName() + "." + energySideState.name().toLowerCase()));
                }
                return true;
            }
            else
            {
                if (!worldIn.isRemote)
                {
                    playerIn.openGui(EnergyStorageBlockMod.DOMAIN, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
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

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TileEntityEnergyStorage)
        {
            for (EnumFacing facing : EnumFacing.VALUES)
            {
                EnergySideWrapper wrapper = ((TileEntityEnergyStorage) tileEntity).getEnergySideWrapper(facing);
                state = state.withProperty(side_state_props[facing.ordinal()], wrapper.sideState);
            }
        }
        return state;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, side_state_props);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }
}
