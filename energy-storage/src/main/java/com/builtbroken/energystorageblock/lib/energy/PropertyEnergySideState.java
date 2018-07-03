package com.builtbroken.energystorageblock.lib.energy;

import com.google.common.collect.Lists;
import net.minecraft.block.properties.PropertyEnum;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/1/2018.
 */
public class PropertyEnergySideState extends PropertyEnum<EnergySideState>
{
    public PropertyEnergySideState(String name)
    {
        super(name, EnergySideState.class, Lists.newArrayList(EnergySideState.values()));
    }
}
