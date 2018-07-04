package com.builtbroken.energystorageblock.content.wireless.controller.prop;

import net.minecraft.util.IStringSerializable;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/4/2018.
 */
public enum EnumWirelessState implements IStringSerializable
{
    /** Multi-block is not setup */
    INCOMPLETE,
    /** Multi-block is setup and hz is set */
    CONNECTED,
    /** Multi-block is setup and hz is not set */
    DISCONNECTED,
    /** Multi-block is setup, hz is set, and has at least 1 connection */
    LINKED;

    public String getName()
    {
        return name().toLowerCase();
    }

    public static EnumWirelessState get(int i)
    {
        if (i >= 0 && i < values().length)
        {
            return values()[i];
        }
        return INCOMPLETE;
    }
}
