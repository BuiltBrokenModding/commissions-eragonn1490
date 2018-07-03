package com.builtbroken.energystorageblock.config;

import com.builtbroken.energystorageblock.EnergyStorageBlockMod;
import net.minecraftforge.common.config.Config;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/30/2018.
 */
@Config(modid = EnergyStorageBlockMod.DOMAIN, name = "energy_storage_mod/energy_storage_block")
@Config.LangKey("config.energystorageblock:energy.storage.title")
public class ConfigEnergyStorage
{
    @Config.Name("forge_energy_capacity")
    @Config.Comment("Amount of energy the energy block can store, in Forge Energy")
    @Config.RangeInt(min = 1)
    public static int CAPACITY = 100000;

    @Config.Name("forge_energy_input_limit")
    @Config.Comment("Transfer limit into the energy block, in Forge Energy")
    @Config.RangeInt(min = 1)
    public static int INPUT_LIMIT = 10000;

    @Config.Name("forge_energy_output_limit")
    @Config.Comment("Transfer limit out of the energy block, in Forge Energy")
    @Config.RangeInt(min = 1)
    public static int OUTPUT_LIMIT = 10000;

    @Config.Name("buildcraft_input_limit")
    @Config.Comment("Transfer limit into the energy block in MJ. Helps smooth out handling for the mod. Still works with existing FE limits.")
    @Config.RangeInt(min = 1)
    public static int INPUT_LIMIT_BC = 200;

    @Config.Name("buildcraft_output_limit")
    @Config.Comment("Transfer limit out of the energy block in MJ. Helps smooth out handling for the mod. Still works with existing FE limits.")
    @Config.RangeInt(min = 1)
    public static int OUTPUT_LIMIT_BC = 200;
}
