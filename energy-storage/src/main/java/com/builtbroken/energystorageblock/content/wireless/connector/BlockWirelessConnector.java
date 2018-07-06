package com.builtbroken.energystorageblock.content.wireless.connector;

import com.builtbroken.energystorageblock.EnergyStorageBlockMod;
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
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/2/2018.
 */
public class BlockWirelessConnector extends Block implements ITileEntityProvider
{
    public static final PropertyBool OUTPUT_MODE = PropertyBool.create("output");

    public BlockWirelessConnector()
    {
        super(Material.IRON);
        this.setDefaultState(this.blockState.getBaseState().withProperty(OUTPUT_MODE, false));
        setRegistryName(EnergyStorageBlockMod.DOMAIN, "wireless_energy_connector");
        setUnlocalizedName(EnergyStorageBlockMod.PREFIX + "wireless.energy.connector");
        setCreativeTab(CreativeTabs.REDSTONE);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEntityWirelessConnector)
        {
            ItemStack stack = playerIn.getHeldItem(hand);
            if (!stack.isEmpty())
            {
                if (stack.getItem() == Items.STICK)
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
                else if (stack.getItem() == Items.BLAZE_ROD)
                {
                    if (!worldIn.isRemote)
                    {
                        ((TileEntityWirelessConnector) tile).setOutputEnergyToTiles(!((TileEntityWirelessConnector) tile).shouldOutputEnergyToTiles());
                        playerIn.sendMessage(new TextComponentTranslation(
                                getUnlocalizedName() + ".info.power.output." + ((TileEntityWirelessConnector) tile).shouldOutputEnergyToTiles()));
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityWirelessConnector();
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(OUTPUT_MODE, meta == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(OUTPUT_MODE) ? 1 : 0;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, OUTPUT_MODE);
    }
}
