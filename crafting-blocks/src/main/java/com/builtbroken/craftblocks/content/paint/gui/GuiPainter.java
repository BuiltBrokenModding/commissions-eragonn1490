package com.builtbroken.craftblocks.content.paint.gui;

import com.builtbroken.craftblocks.CraftingBlocks;
import com.builtbroken.craftblocks.content.paint.PainterRecipe;
import com.builtbroken.craftblocks.content.paint.TileEntityPainter;
import com.builtbroken.craftblocks.network.MessageOnState;
import com.builtbroken.craftblocks.network.MessagePainterRecipeToggle;
import com.builtbroken.craftblocks.network.NetworkHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/17/2018.
 */
public class GuiPainter extends GuiContainer
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(CraftingBlocks.DOMAIN, "textures/gui/painter.png");

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
        this.buttonList.clear();

        this.buttonList.add(onButton = new ButtonOnOff(0, this.width / 2 - 81, this.height / 2 - 75, true));
        this.buttonList.add(offButton = new  ButtonOnOff(1, this.width / 2 - 81, this.height / 2 - 59, false));

        this.buttonList.add(new ButtonArrow(2, this.width / 2 - 54, this.height / 2 - 46, true));
        this.buttonList.add(new ButtonArrow(3, this.width / 2 + 54 - 19, this.height / 2 - 46, false));
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
            NetworkHandler.sendToServer(new MessageOnState(painter, true));
        }
        else if (buttonID == 1)
        {
            painter.machineOn = false;
            onButton.enabled = true;
            offButton.enabled = false;
            NetworkHandler.sendToServer(new MessageOnState(painter, false));
        }
        else if (buttonID == 2)
        {
            NetworkHandler.sendToServer(new MessagePainterRecipeToggle(painter, false));
        }
        else if (buttonID == 3)
        {
            NetworkHandler.sendToServer(new MessagePainterRecipeToggle(painter, true));
        }
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

        onButton.enabled = !painter.machineOn;
        offButton.enabled = painter.machineOn;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        //Display title
        final String title = "Painter Bench"; //TODO translate
        this.fontRenderer.drawString(title, this.xSize / 2 - this.fontRenderer.getStringWidth(title) / 2, 6, 4210752);

        this.fontRenderer.drawString("" + painter.recipeTicks, this.xSize / 2 - this.fontRenderer.getStringWidth(title) / 2, -10, 4210752);

        //Display energy
        PainterRecipe recipe = painter.getCurrentRecipe();
        final String recipeName = (recipe != null ? recipe.unlocalizedName : "none"); //TODO translate
        this.fontRenderer.drawString(recipeName, this.xSize / 2 - this.fontRenderer.getStringWidth(recipeName) / 2, 37, Color.WHITE.getRGB());
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
