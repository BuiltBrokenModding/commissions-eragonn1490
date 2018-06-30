package com.builtbroken.triggerblock.reprogrammer;

import com.builtbroken.triggerblock.cap.CapabilityTriggerHz;
import com.builtbroken.triggerblock.cap.ITriggerHz;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.ItemStackHandler;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/25/2018.
 */
public class TileEntityReprogrammer extends TileEntity
{
    public static final String NBT_INVENTORY = "inventory";

    protected final ItemStackHandler inventory = new InventoryReprogrammer(this);

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        inventory.deserializeNBT(compound.getCompoundTag(NBT_INVENTORY));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setTag(NBT_INVENTORY, inventory.serializeNBT());
        return super.writeToNBT(compound);
    }

    private static class InventoryReprogrammer extends ItemStackHandler
    {
        private final TileEntityReprogrammer host;

        public InventoryReprogrammer(TileEntityReprogrammer tileEntityReprogrammer)
        {
            super(2);
            this.host = tileEntityReprogrammer;
        }

        @Override
        protected void onContentsChanged(int slot)
        {
            ItemStack inputStack = getStackInSlot(0);
            ItemStack outputStack = getStackInSlot(1);

            if (inputStack != null && inputStack.hasCapability(CapabilityTriggerHz.CAPABILITY, null)
                    && outputStack != null && outputStack.hasCapability(CapabilityTriggerHz.CAPABILITY, null))
            {
                ITriggerHz inputCap = inputStack.getCapability(CapabilityTriggerHz.CAPABILITY, null);
                ITriggerHz outputCap = outputStack.getCapability(CapabilityTriggerHz.CAPABILITY, null);

                outputCap.setTriggerHz(inputCap.getTriggerHz());
            }
        }
    }
}
