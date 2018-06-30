package com.builtbroken.triggerblock.reprogrammer;

import com.builtbroken.triggerblock.TriggerBlockMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/25/2018.
 */
public class ContainerReprogrammer extends Container
{
    protected final TileEntityReprogrammer reprogrammer;

    public ContainerReprogrammer(EntityPlayer playerIn, TileEntityReprogrammer reprogrammer)
    {
        this.reprogrammer = reprogrammer;
        this.addSlotToContainer(new SlotItemHandler(reprogrammer.inventory, 0, 44, 37));
        this.addSlotToContainer(new SlotItemHandler(reprogrammer.inventory, 1, 116, 37));

        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(playerIn.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k)
        {
            this.addSlotToContainer(new Slot(playerIn.inventory, k, 8 + k * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return playerIn.getDistanceSqToCenter(reprogrammer.getPos()) < 10;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        final int invStart = 0;
        final int invEnd = 2;

        final int playerStart = 2;
        final int playerHotbar = 29;
        final int playerEnd = 38;

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
                if (itemstack.getItem() == TriggerBlockMod.itemTrigger)
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
