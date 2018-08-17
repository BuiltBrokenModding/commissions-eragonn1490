package com.builtbroken.craftblocks.paint.gui;

import com.builtbroken.craftblocks.paint.TileEntityPainter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/17/2018.
 */
public class ContainerPainter extends Container
{
    private final TileEntityPainter painter;

    public ContainerPainter(EntityPlayer player, TileEntityPainter painter)
    {
        this.painter = painter;
        this.addSlotToContainer(new SlotItemHandler(painter.inventory, TileEntityPainter.INPUT_SLOT, 35, 19));
        this.addSlotToContainer(new SlotItemHandler(painter.inventory, TileEntityPainter.BRUSH_SLOT, 35 + 18, 19));
        this.addSlotToContainer(new SlotItemHandler(painter.inventory, TileEntityPainter.OUTPUT_SLOT, 106, 19));
        this.addSlotToContainer(new SlotItemHandler(painter.inventory, TileEntityPainter.POWER_SLOT, 143, 19));

        for (int i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new SlotItemHandler(painter.inventory, TileEntityPainter.DYE_SLOT_START + i, 8 + i * 18, 46));
            if(i > 0 && i < 8)
            {
                this.addSlotToContainer(new SlotItemHandler(painter.inventory, TileEntityPainter.DYE_SLOT_START + 8 +  i, 8 + i * 18, 46 + 18));
            }
        }

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
        return playerIn.getDistanceSqToCenter(painter.getPos()) <= 100;
    }

    protected boolean isValidItemForInventory(ItemStack stack)
    {
        return false;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        final int invStart = 0;
        final int invEnd = TileEntityPainter.INVENTORY_SIZE;

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
                if (isValidItemForInventory(itemstack1))
                {
                    if (!this.mergeItemStack(itemstack1, invStart, invEnd, false))
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
