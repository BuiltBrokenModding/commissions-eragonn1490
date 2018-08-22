package com.builtbroken.craftblocks.content.stone.gui;

import com.builtbroken.craftblocks.CraftingBlocks;
import com.builtbroken.craftblocks.content.item.ItemCraftingPower;
import com.builtbroken.craftblocks.content.stone.StoneCutterRecipe;
import com.builtbroken.craftblocks.content.stone.TileEntityStoneCutter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/22/2018.
 */
public class ContainerStoneCutter extends Container
{
    private final TileEntityStoneCutter stoneCutter;

    public ContainerStoneCutter(EntityPlayer player, TileEntityStoneCutter stoneCutter)
    {
        this.stoneCutter = stoneCutter;
        //Add slots, order does matter for shift click to work
        this.addSlotToContainer(new SlotItemHandler(stoneCutter.inventory, TileEntityStoneCutter.OUTPUT_SLOT, 106, 19));
        this.addSlotToContainer(new SlotItemHandler(stoneCutter.inventory, TileEntityStoneCutter.POWER_SLOT, 143, 19));
        this.addSlotToContainer(new SlotItemHandler(stoneCutter.inventory, TileEntityStoneCutter.INPUT_SLOT, 35, 19));
        this.addSlotToContainer(new SlotItemHandler(stoneCutter.inventory, TileEntityStoneCutter.TOOL_SLOT, 35 + 18, 19));

        this.addSlotToContainer(new SlotItemHandler(stoneCutter.inventory, TileEntityStoneCutter.SPECIAL_SLOT, 35 + 9, 19 + 18));

        //Add player inventory
        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k)
        {
            this.addSlotToContainer(new Slot(player.inventory, k, 8 + k * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return playerIn.getDistanceSqToCenter(stoneCutter.getPos()) <= 100;
    }

    protected boolean isRecipeInput(ItemStack stack)
    {
        for(StoneCutterRecipe recipe : TileEntityStoneCutter.recipes)
        {
            if(recipe != null && recipe.isMatchingItem(stack, recipe.input))
            {
                return true;
            }
        }
        return false;
    }

    protected boolean isRecipeSpecialInput(ItemStack stack)
    {
        for(StoneCutterRecipe recipe : TileEntityStoneCutter.recipes)
        {
            if(recipe != null
                    && !recipe.specialityInput.isEmpty()
                    && recipe.isMatchingItem(stack, recipe.specialityInput))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        final int invStart = 0;
        final int invEnd = TileEntityStoneCutter.INVENTORY_SIZE;

        final int playerStart = invEnd;
        final int playerHotbar = invEnd + 27;
        final int playerEnd = playerHotbar + 9;

        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < invEnd)
            {
                if (!this.mergeItemStack(itemstack1, playerStart, playerEnd, true))
                {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (index >= playerStart)
            {
                if (itemstack1.getItem() == CraftingBlocks.itemStoneChisel)
                {
                    if (!this.mergeItemStack(itemstack1, TileEntityStoneCutter.TOOL_SLOT, TileEntityStoneCutter.TOOL_SLOT + 1, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (itemstack1.getItem() instanceof ItemCraftingPower)
                {
                    if (!this.mergeItemStack(itemstack1, TileEntityStoneCutter.POWER_SLOT, TileEntityStoneCutter.POWER_SLOT + 1, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (isRecipeInput(itemstack1))
                {
                    if (!this.mergeItemStack(itemstack1, TileEntityStoneCutter.INPUT_SLOT, TileEntityStoneCutter.INPUT_SLOT + 1, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if(isRecipeSpecialInput(itemstack1))
                {
                    if (!this.mergeItemStack(itemstack1, TileEntityStoneCutter.SPECIAL_SLOT, TileEntityStoneCutter.SPECIAL_SLOT + 1, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (index >= playerStart && index < playerHotbar)
                {
                    if (!this.mergeItemStack(itemstack1, playerHotbar, playerEnd, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                else if (index >= playerHotbar && index < playerEnd && !this.mergeItemStack(itemstack1, playerStart, playerHotbar, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, playerStart, playerEnd, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount())
            {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }
}
