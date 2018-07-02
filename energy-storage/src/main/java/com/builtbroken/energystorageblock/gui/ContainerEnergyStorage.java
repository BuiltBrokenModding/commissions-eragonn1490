package com.builtbroken.energystorageblock.gui;

import com.builtbroken.energystorageblock.block.TileEntityEnergyStorage;
import com.builtbroken.energystorageblock.inventory.InventoryEnergyStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/1/2018.
 */
public class ContainerEnergyStorage extends Container
{
    private final TileEntityEnergyStorage energyStorage;

    public ContainerEnergyStorage(EntityPlayer player, TileEntityEnergyStorage energyStorage)
    {
        this.energyStorage = energyStorage;
        this.addSlotToContainer(new SlotItemHandler(energyStorage.inventory, TileEntityEnergyStorage.SLOT_BATTERY_DISCHARGE, 26, 37));
        this.addSlotToContainer(new SlotItemHandler(energyStorage.inventory, TileEntityEnergyStorage.SLOT_BATTERY_CHARGE, 134, 37));

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
        return playerIn.getDistanceSqToCenter(energyStorage.getPos()) <= 100;
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
                if (InventoryEnergyStorage.isBattery(itemstack1))
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
