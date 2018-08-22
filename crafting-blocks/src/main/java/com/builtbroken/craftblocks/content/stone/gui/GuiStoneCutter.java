package com.builtbroken.craftblocks.content.stone.gui;

import com.builtbroken.craftblocks.CraftingBlocks;
import com.builtbroken.craftblocks.content.gui.ButtonArrow;
import com.builtbroken.craftblocks.content.gui.ButtonOnOff;
import com.builtbroken.craftblocks.content.stone.StoneCutterRecipe;
import com.builtbroken.craftblocks.content.stone.TileEntityStoneCutter;
import com.builtbroken.craftblocks.network.MessageOnState;
import com.builtbroken.craftblocks.network.MessagePainterRecipeToggle;
import com.builtbroken.craftblocks.network.NetworkHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/22/2018.
 */
public class GuiStoneCutter extends GuiContainer
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(CraftingBlocks.DOMAIN, "textures/gui/stone_cutter.png");

    private final TileEntityStoneCutter stoneCutter;

    private GuiButton onButton;
    private GuiButton offButton;

    public GuiStoneCutter(EntityPlayer player, TileEntityStoneCutter stoneCutter)
    {
        super(new ContainerStoneCutter(player, stoneCutter));
        this.stoneCutter = stoneCutter;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.buttonList.clear();

        this.buttonList.add(onButton = new ButtonOnOff(0, this.width / 2 - 81, this.height / 2 - 75, true));
        this.buttonList.add(offButton = new ButtonOnOff(1, this.width / 2 - 81, this.height / 2 - 59, false));

        this.buttonList.add(new ButtonArrow(2, this.width / 2 - 54, this.height / 2 - 28, true));
        this.buttonList.add(new ButtonArrow(3, this.width / 2 + 54 - 19, this.height / 2 - 28, false));
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        int buttonID = par1GuiButton.id;
        if (buttonID == 0)
        {
            stoneCutter.machineOn = true;
            onButton.enabled = false;
            offButton.enabled = true;
            NetworkHandler.sendToServer(new MessageOnState(stoneCutter, true));
        }
        else if (buttonID == 1)
        {
            stoneCutter.machineOn = false;
            onButton.enabled = true;
            offButton.enabled = false;
            NetworkHandler.sendToServer(new MessageOnState(stoneCutter, false));
        }
        else if (buttonID == 2)
        {
            NetworkHandler.sendToServer(new MessagePainterRecipeToggle(stoneCutter, false));
        }
        else if (buttonID == 3)
        {
            NetworkHandler.sendToServer(new MessagePainterRecipeToggle(stoneCutter, true));
        }
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

        onButton.enabled = !stoneCutter.machineOn;
        offButton.enabled = stoneCutter.machineOn;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        //Display title
        final String title = I18n.format(CraftingBlocks.blockStoneCutter.getUnlocalizedName() + ".name");
        this.fontRenderer.drawString(title, this.xSize / 2 - this.fontRenderer.getStringWidth(title) / 2, 6, 4210752);

        //Display recipe
        StoneCutterRecipe recipe = stoneCutter.getCurrentRecipe();
        final String recipeName = I18n.format((recipe != null ? TileEntityStoneCutter.RECIPE_UNLOCALIZATION_PREFX + recipe.unlocalizedName : (CraftingBlocks.blockPainter.getUnlocalizedName() + ".recipe.none")));
        this.fontRenderer.drawString(recipeName, this.xSize / 2 - this.fontRenderer.getStringWidth(recipeName) / 2, 55, Color.WHITE.getRGB());
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        final int left = (this.width - this.xSize) / 2;
        final int top = (this.height - this.ySize) / 2;

        //Draw background
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);

        this.drawTexturedModalRect(left, top, 0, 0, this.xSize, this.ySize);

        //Draw progress arrow
        StoneCutterRecipe recipe = stoneCutter.getCurrentRecipe();
        if (recipe != null && stoneCutter.canDoRecipe)
        {
            float p = (float) stoneCutter.recipeTicks / (float) stoneCutter.getCurrentRecipe().ticksToComplete;
            int width = 22 - (int) Math.ceil(22 * p);
            if (width > 0)
            {
                this.drawTexturedModalRect(left + 76, top + 20, 177, 0, width, 16);
            }
        }
    }
}
