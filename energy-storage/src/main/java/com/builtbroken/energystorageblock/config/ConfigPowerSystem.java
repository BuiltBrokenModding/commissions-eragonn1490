package com.builtbroken.energystorageblock.config;

import com.builtbroken.energystorageblock.EnergyStorageBlockMod;
import net.minecraftforge.common.config.Config;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 2/22/2018.
 */
@Config(modid = EnergyStorageBlockMod.DOMAIN, name = "energy_storage_mod/power_systems")
@Config.LangKey("config.icbmclassic:ic2.title")
public class ConfigPowerSystem
{
    @Config.Name("from_ic2")
    @Config.Comment("How much (EU) IC2 energy to turn into (FE) Forge energy")
    @Config.RangeInt(min = 0)
    public static double FROM_IC2 = 4D; //Matched with Mekanism

    @Config.Name("from_buildcraft")
    @Config.Comment("How much (MJ) builcraft energy to turn into (FE) Forge energy")
    @Config.RangeInt(min = 0)
    public static double FROM_BUILDCRAFT = 200D;

    @Config.Name("enable_ic2")
    @Config.Comment("Set to true to enable IC2 (EU) power support. Requires restart to take full effect.")
    public static boolean ENABLE_IC2 = true;

    @Config.Name("enable_buildcraft")
    @Config.Comment("Set to true to enable buildcraft (MJ) power support. Requires restart to take full effect.")
    public static boolean ENABLE_BUILDCRAFT = true;
}
