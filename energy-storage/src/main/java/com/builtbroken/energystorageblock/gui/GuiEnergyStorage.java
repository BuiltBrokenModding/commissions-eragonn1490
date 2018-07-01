package com.builtbroken.energystorageblock.gui;

import com.builtbroken.energystorageblock.block.TileEntityEnergyStorage;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/1/2018.
 */
public class GuiEnergyStorage extends GuiContainer
{
    public GuiEnergyStorage(TileEntityEnergyStorage energyStorage, EntityPlayer player)
    {
        super(new ContainerEnergyStorage(energyStorage, player));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {

    }
}
