package com.builtbroken.energystorageblock.gui;

import com.builtbroken.energystorageblock.EnergyStorageBlockMod;
import com.builtbroken.energystorageblock.block.TileEntityEnergyStorage;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/1/2018.
 */
public class GuiEnergyStorage extends GuiContainer
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyStorageBlockMod.DOMAIN, "textures/gui/energy.storage.png");
    private static final String ENERGY_FORMAT = "%,d FE";

    private final TileEntityEnergyStorage energyStorage;

    public GuiEnergyStorage(EntityPlayer player, TileEntityEnergyStorage energyStorage)
    {
        super(new ContainerEnergyStorage(player, energyStorage));
        this.energyStorage = energyStorage;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        //Display title
        final String title = "Energy Storage Block";
        this.fontRenderer.drawString(title, this.xSize / 2 - this.fontRenderer.getStringWidth(title) / 2, 6, 4210752);

        //Display energy
        final String energy = String.format(ENERGY_FORMAT,energyStorage.energyStorage.getEnergyStored());
        this.fontRenderer.drawString(energy, 65, 42, 4210752);

        //Display inventory title
        this.fontRenderer.drawString("Inventory", 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        //Draw background
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

        //Draw energy bar
        final int barX = 45;
        final int barY = 38;
        final int barU = 0;
        final int barV = 180;
        final int barWidth = 86;
        final int barHeight = 14;

        float energyPercentage = energyStorage.energyStorage.getEnergyStored() / (float) energyStorage.energyStorage.getMaxEnergyStored();
        int barScale = Math.max(0, Math.min(barWidth, (int) Math.ceil(barWidth * energyPercentage)));

        this.drawTexturedModalRect(i + barX, j + barY, barU, barV, barScale, barHeight);
    }
}
