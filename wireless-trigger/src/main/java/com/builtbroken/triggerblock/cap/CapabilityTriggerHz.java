package com.builtbroken.triggerblock.cap;

import com.builtbroken.triggerblock.TriggerBlockMod;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = TriggerBlockMod.DOMAIN)
public class CapabilityTriggerHz implements ITriggerHz, ICapabilityProvider, INBTSerializable<NBTTagInt>
{
    @CapabilityInject(ITriggerHz.class)
    public static Capability<ITriggerHz> CAPABILITY = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(ITriggerHz.class, new Capability.IStorage<ITriggerHz>()
                {
                    @Override
                    public NBTBase writeNBT(Capability<ITriggerHz> capability, ITriggerHz instance, EnumFacing side)
                    {
                        if (instance instanceof CapabilityTriggerHz)
                        {
                            return new NBTTagInt(instance.getTriggerHz());
                        }
                        return null;
                    }

                    @Override
                    public void readNBT(Capability<ITriggerHz> capability, ITriggerHz instance, EnumFacing side, NBTBase nbt)
                    {
                        if (nbt instanceof NBTTagInt)
                        {
                            instance.setTriggerHz(((NBTTagInt) nbt).getInt());
                        }
                    }
                },
                () -> new CapabilityTriggerHz());
    }

    //---------------------------------------

    private int hz;

    @Override
    public int getTriggerHz()
    {
        return hz;
    }

    @Override
    public void setTriggerHz(int hz)
    {
        this.hz = hz;
    }

    //---------------------------------------

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CAPABILITY)
        {
            return (T) this;
        }
        return null;
    }

    //---------------------------------------

    @Override
    public NBTTagInt serializeNBT()
    {
        return new NBTTagInt(getTriggerHz());
    }

    @Override
    public void deserializeNBT(NBTTagInt nbt)
    {
        setTriggerHz(nbt.getInt());
    }
}