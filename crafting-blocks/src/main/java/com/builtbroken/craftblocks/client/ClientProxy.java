package com.builtbroken.craftblocks.client;

import com.builtbroken.craftblocks.CommonProxy;
import com.builtbroken.craftblocks.content.paint.TileEntityPainter;
import com.builtbroken.craftblocks.content.paint.gui.GuiPainter;
import com.builtbroken.craftblocks.content.stone.TileEntityStoneCutter;
import com.builtbroken.craftblocks.content.stone.gui.GuiStoneCutter;
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
 * Created by Dark(DarkGuardsman, Robert) on 8/17/2018.
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
            if(tile instanceof TileEntityPainter)
            {
                return new GuiPainter(player, (TileEntityPainter) tile);
            }
            else if(tile instanceof TileEntityStoneCutter)
            {
                return new GuiStoneCutter(player, (TileEntityStoneCutter) tile);
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
