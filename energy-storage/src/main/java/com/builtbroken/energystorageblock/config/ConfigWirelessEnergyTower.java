package com.builtbroken.energystorageblock.config;

import com.builtbroken.energystorageblock.EnergyStorageBlockMod;
import net.minecraftforge.common.config.Config;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/30/2018.
 */
@Config(modid = EnergyStorageBlockMod.DOMAIN, name = "energy_storage_mod/wireless_tower_blocks")
@Config.LangKey("config.energystorageblock:energy.wireless.tower.title")
public class ConfigWirelessEnergyTower
{
    @Config.Name("multi_block_scan_rate")
    @Config.Comment("Amount of time in ticks (20 ticks a second) before scanning the multi-block structure")
    @Config.RangeInt(min = 20)
    public static int MULTI_BLOCK_SCAN_RATE = 20;

    @Config.Name("connection_scan_rate")
    @Config.Comment("Amount of time in ticks (20 ticks a second) before scanning for wireless connections")
    @Config.RangeInt(min = 20)
    public static int CONNECTION_CHECK_RATE = 100;

    @Config.Name("range")
    @Config.Comment("Distance in meters (blocks) to connect to another tower")
    @Config.RangeInt(min = 5)
    public static int RANGE = 10;

    @Config.Name("forge_energy_capacity")
    @Config.Comment("Amount of energy the energy block can store, in Forge Energy")
    @Config.RangeInt(min = 1)
    public static int CAPACITY = 100000;

    @Config.Name("transfer_limit")
    @Config.Comment("Transfer limit from one tower to the next in Forge Energy")
    @Config.RangeInt(min = 1)
    public static int TRANSFER_LIMIT = 10000;

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
