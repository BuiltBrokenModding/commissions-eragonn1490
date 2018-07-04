package com.builtbroken.energystorageblock.content.wireless.controller.prop;

import com.google.common.collect.Lists;
import net.minecraft.block.properties.PropertyEnum;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/4/2018.
 */
public class PropWirelessState extends PropertyEnum<EnumWirelessState>
{
    public PropWirelessState()
    {
        super("wireless", EnumWirelessState.class, Lists.newArrayList(EnumWirelessState.values()));
    }
}
