package com.builtbroken.craftblocks.paint;

import com.builtbroken.craftblocks.CraftingBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/15/2018.
 */
public class BlockPainter extends Block implements ITileEntityProvider
{
    public BlockPainter()
    {
        super(Material.ROCK);
        setRegistryName(CraftingBlocks.DOMAIN, "trigger_block");
        setUnlocalizedName(CraftingBlocks.PREFIX + "trigger");
        setCreativeTab(CreativeTabs.REDSTONE);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityPainter();
    }
}
