package com.builtbroken.energystorageblock.client;

import com.builtbroken.energystorageblock.CommonProxy;
import com.builtbroken.energystorageblock.block.TileEntityEnergyStorage;
import com.builtbroken.energystorageblock.gui.GuiEnergyStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/1/2018.
 */
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID == 0)
        {
            TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
            if (tile instanceof TileEntityEnergyStorage)
            {
                return new GuiEnergyStorage(player, (TileEntityEnergyStorage) tile);
            }
        }
        return null;
    }

    @Override
    public World getLocalWorld()
    {
        return Minecraft.getMinecraft().world;
    }
}
