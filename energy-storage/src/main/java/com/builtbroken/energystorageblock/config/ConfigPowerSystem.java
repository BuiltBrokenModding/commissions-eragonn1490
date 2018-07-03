package com.builtbroken.energystorageblock.config;

import com.builtbroken.energystorageblock.EnergyStorageBlockMod;
import net.minecraftforge.common.config.Config;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 2/22/2018.
 */
@Config(modid = EnergyStorageBlockMod.DOMAIN, name = "energy_storage_mod/power_systems")
@Config.LangKey("config.energystorageblock:power.system.title")
public class ConfigPowerSystem
{
    @Config.Name("fe_per_eu")
    @Config.Comment("How much (FE) Forge energy to exchange for (EU) IC2 energy")
    @Config.RangeInt(min = 0)
    public static double FE_PER_EU = 4; //Matched with Mekanism

    @Config.Name("mj_per_fe")
    @Config.Comment("How much (FE) Forge energy to exchange for (MJ) builcraft energy")
    @Config.RangeInt(min = 0)
    public static double FE_PER_MJ = 10;

    @Config.Name("enable_ic2")
    @Config.Comment("Set to true to enable IC2 (EU) power support. Requires restart to take full effect.")
    public static boolean ENABLE_IC2 = true;

    @Config.Name("enable_buildcraft")
    @Config.Comment("Set to true to enable buildcraft (MJ) power support. Requires restart to take full effect.")
    public static boolean ENABLE_BUILDCRAFT = true;
}
