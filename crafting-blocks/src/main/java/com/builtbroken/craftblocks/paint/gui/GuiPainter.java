package com.builtbroken.craftblocks.paint.gui;

import com.builtbroken.craftblocks.CraftingBlocks;
import com.builtbroken.craftblocks.paint.TileEntityPainter;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/17/2018.
 */
public class GuiPainter extends GuiContainer
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(CraftingBlocks.DOMAIN, "textures/gui/painter.png");

    private final TileEntityPainter painter;

    private GuiButton onButton;
    private GuiButton offButton;

    public GuiPainter(EntityPlayer player, TileEntityPainter painter)
    {
        super(new ContainerPainter(player, painter));
        this.painter = painter;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        this.buttonList.add(onButton = new GuiButton(0, this.width / 2 - 82, this.height / 2 - 75, 20, 15, "On"));
        onButton.enabled = !painter.machineOn;

        this.buttonList.add(offButton = new GuiButton(1, this.width / 2 - 82, this.height / 2 - 60, 20, 15, "Off"));
        offButton.enabled = painter.machineOn;

        this.buttonList.add(new GuiButton(2, this.width / 2 - 83, this.height / 2 - 20, 20, 20, "<<"));
        this.buttonList.add(new GuiButton(3, this.width / 2 + 83 - 20, this.height / 2 - 20, 20, 20, ">>"));
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        int buttonID = par1GuiButton.id;
        if (buttonID == 0)
        {
            painter.machineOn = true;
            onButton.enabled = false;
            offButton.enabled = true;
            //TODO send packet
        }
        else if (buttonID == 1)
        {
            painter.machineOn = false;
            onButton.enabled = true;
            offButton.enabled = false;
            //TODO send packet
        }
        else if (buttonID == 2)
        {
            //TODO send packet to switch recipe
        }
        else if (buttonID == 3)
        {
            //TODO send packet to switch recipe
        }
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
        final String recipeName = "painting x3";
        this.fontRenderer.drawString(recipeName, this.xSize / 2 - this.fontRenderer.getStringWidth(recipeName) / 2, 36, 4210752);
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
    }
}
