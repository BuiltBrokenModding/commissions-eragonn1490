package com.builtbroken.energystorageblock.mods;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/22/2018.
 */
public class ModProxy
{
    public void preInit()
    {

    }

    public void init()
    {

    }

    public void postInit()
    {

    }

    /**
     * Called to output power
     *
     * @param storage    - storage to drain power from
     * @param target     - tile to feed power
     * @param enumFacing - side of target
     * @return true to consume action
     */
    public boolean outputPower(IEnergyStorage storage, TileEntity target, EnumFacing enumFacing)
    {
        return false;
    }

    public void onTileValidate(TileEntity tile)
    {

    }

    public void onTileInvalidate(TileEntity tile)
    {

    }
}
