package com.builtbroken.craftblocks.content.gui;

import com.builtbroken.craftblocks.content.paint.gui.GuiPainter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/18/2018.
 */
public class ButtonOnOff extends GuiButton
{
    public static final int textureX = 176 + 18 + 18;
    public static final int textureY = 39;

    public final boolean onState;

    public ButtonOnOff(int buttonId, int x, int y, boolean onState)
    {
        super(buttonId, x, y, onState ? "O" : "I");
        this.onState = onState;
        this.width = 18;
        this.height = 15;
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

            //Trigger mouse drag
            this.mouseDragged(mc, mouseX, mouseY);

            //Draw text
            int j = 14737632;

            if (packedFGColour != 0)
            {
                j = packedFGColour;
            }
            else
            if (!this.enabled)
            {
                j = 10526880;
            }
            else if (this.hovered)
            {
                j = 16777120;
            }

            this.drawCenteredString(mc.fontRenderer, this.displayString, this.x + this.width / 2, this.y + 4, j);
        }
    }
}
