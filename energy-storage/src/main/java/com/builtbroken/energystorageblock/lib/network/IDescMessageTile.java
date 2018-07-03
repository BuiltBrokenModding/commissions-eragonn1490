package com.builtbroken.energystorageblock.lib.network;

import net.minecraft.nbt.NBTTagCompound;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/1/2018.
 */
public interface IDescMessageTile
{
    NBTTagCompound writeDescMessage(NBTTagCompound tagCompound);

    void readDescMessage(NBTTagCompound tagCompound);
}
