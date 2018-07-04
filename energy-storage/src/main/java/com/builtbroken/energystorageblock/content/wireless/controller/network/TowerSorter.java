package com.builtbroken.energystorageblock.content.wireless.controller.network;

import com.builtbroken.energystorageblock.content.wireless.controller.TileEntityWirelessController;
import net.minecraft.util.math.BlockPos;

import java.util.Comparator;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/4/2018.
 */
public class TowerSorter implements Comparator<TileEntityWirelessController>
{
    public final BlockPos center;

    public TowerSorter(BlockPos center)
    {
        this.center = center;
    }

    @Override
    public int compare(TileEntityWirelessController o1, TileEntityWirelessController o2)
    {
        if (!o1.isOutputMode() && o2.isOutputMode())
        {
            return 1;
        }
        else if (o1.isOutputMode() && !o2.isOutputMode())
        {
            return -1;
        }

        double distance1 = center.distanceSq(o1.getPos());
        double distance2 = center.distanceSq(o1.getPos());
        return Double.compare(distance1, distance2);
    }
}
