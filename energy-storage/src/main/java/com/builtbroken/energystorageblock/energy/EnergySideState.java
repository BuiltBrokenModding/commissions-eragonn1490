package com.builtbroken.energystorageblock.energy;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/30/2018.
 */
public enum EnergySideState
{
    INPUT,
    OUTPUT,
    NONE;

    public EnergySideState next()
    {
        int i = ordinal();
        i++;
        if (i >= values().length)
        {
            i = 0;
        }
        return values()[i];
    }
}
