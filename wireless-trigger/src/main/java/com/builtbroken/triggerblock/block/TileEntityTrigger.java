package com.builtbroken.triggerblock.block;

import com.builtbroken.triggerblock.cap.CapabilityTriggerHz;
import com.builtbroken.triggerblock.cap.ITriggerHz;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/25/2018.
 */
public class TileEntityTrigger extends TileEntity
{
    public static final String NBT_HZ = "triggerHz";
    public static final String NBT_TRIGGER = "isTriggered";

    private boolean isTriggered = false;

    private ITriggerHz capabilityTriggerHz = new CapabilityTriggerHzTile(this);

    public boolean toggleTrigger()
    {
        if (hasFrequency())
        {
            isTriggered = !isTriggered;
            getWorld().setBlockState(getPos(), getBlockType().getDefaultState().withProperty(BlockTrigger.POWERED, isTriggered));
            return isTriggered;
        }
        return false;
    }

    public boolean isTriggered()
    {
        return isTriggered;
    }

    public boolean hasFrequency()
    {
        return capabilityTriggerHz.getTriggerHz() != 0;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        capabilityTriggerHz.setTriggerHz(compound.getInteger(NBT_HZ));
        isTriggered = compound.getBoolean(NBT_TRIGGER);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setInteger(NBT_HZ, capabilityTriggerHz.getTriggerHz());
        compound.setBoolean(NBT_TRIGGER, isTriggered);
        return super.writeToNBT(compound);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityTriggerHz.CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    @Nullable
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityTriggerHz.CAPABILITY)
        {
            return (T) capabilityTriggerHz;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidate()
    {
        super.invalidate();
        if (!world.isRemote)
        {
            TriggerHzHandler.get(getWorld()).remove(this);
        }
    }

    @Override
    public void validate()
    {
        super.validate();
        if (!world.isRemote && hasFrequency())
        {
            TriggerHzHandler.get(getWorld()).add(this);
        }
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return oldState.getBlock() != newSate.getBlock();
    }

    public static class CapabilityTriggerHzTile extends CapabilityTriggerHz
    {
        private final TileEntityTrigger host;

        public CapabilityTriggerHzTile(TileEntityTrigger host)
        {
            this.host = host;
        }

        @Override
        public void setTriggerHz(int hz)
        {
            int prev = getTriggerHz();
            super.setTriggerHz(hz);
            if (hz != prev && host.getWorld() != null)
            {
                if (hz != 0)
                {
                    TriggerHzHandler.get(host.getWorld()).add(host);
                }
                else
                {
                    TriggerHzHandler.get(host.getWorld()).remove(host);
                    host.isTriggered = false;
                }
            }
        }
    }
}
