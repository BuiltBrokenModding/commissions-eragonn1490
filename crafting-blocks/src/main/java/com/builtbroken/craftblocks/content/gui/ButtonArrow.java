package com.builtbroken.craftblocks.content.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/18/2018.
 */
public class ButtonArrow extends GuiButton
{
    public static final int textureX = 176 + 18 + 18;
    public static final int textureY = 28;
    public static final int arrowX = 176;
    public static final int arrowY = 20;

    public final boolean left;

    public ButtonArrow(int buttonId, int x, int y, boolean left)
    {
        super(buttonId, x, y, "");
        this.left = left;
        this.width = 18;
        this.height = 8;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            //Set texture and reset color
            mc.getTextureManager().bindTexture(GuiPainter.TEXTURE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            //Check if mouse is over button
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int hoverIndex = this.getHoverState(this.hovered);

            //Enable blending (TODO not sure why)
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            //Draw body
            this.drawTexturedModalRect(this.x, this.y, textureX - width * hoverIndex, textureY, this.width, this.height);

            //Draw arrow
            this.drawTexturedModalRect(this.x, this.y, arrowX + (left ? 0 : width), arrowY, this.width, this.height);

            //Trigger mouse drag
            this.mouseDragged(mc, mouseX, mouseY);
        }
    }
}
