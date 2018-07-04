package com.builtbroken.energystorageblock.content.wireless.controller.network;

import com.builtbroken.energystorageblock.content.wireless.controller.TileEntityWirelessController;
import com.builtbroken.triggerblock.cap.CapabilityTriggerHz;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/25/2018.
 */
public class WirelessTowerHzHandler
{
    protected final static HashMap<Integer, WirelessTowerHzHandler> worldToHandler = new HashMap();

    protected final HashMap<Integer, List<BlockPos>> hzToTile = new HashMap();
    protected final HashMap<BlockPos, Integer> tileToHz = new HashMap();

    public static WirelessTowerHzHandler get(World world)
    {
        return get(world.provider.getDimension());
    }

    public static WirelessTowerHzHandler get(int dim)
    {
        if (!worldToHandler.containsKey(dim))
        {
            worldToHandler.put(dim, new WirelessTowerHzHandler());
        }
        return worldToHandler.get(dim);
    }


    /**
     * Called to add a tile to the handler
     *
     * @param tileEntityTrigger
     */
    public void add(final TileEntity tileEntityTrigger)
    {
        if (tileEntityTrigger != null)
        {
            //Remove if already added
            if (tileToHz.containsKey(tileEntityTrigger.getPos()))
            {
                remove(tileEntityTrigger);
            }

            //Get hz
            int hz = tileEntityTrigger.getCapability(CapabilityTriggerHz.CAPABILITY, null).getTriggerHz();

            //Only add if hz is not zero
            if (hz != 0)
            {
                //Add to ile to hz  map
                tileToHz.put(tileEntityTrigger.getPos(), hz);

                //Add to hz to tiles map
                if (!hzToTile.containsKey(hz))
                {
                    hzToTile.put(hz, new ArrayList());
                }
                if (!hzToTile.get(hz).contains(tileEntityTrigger.getPos()))
                {
                    hzToTile.get(hz).add(tileEntityTrigger.getPos());
                }
            }
        }
    }

    /**
     * Called to remove a tile from the handler
     *
     * @param tileEntityTrigger
     */
    public void remove(final TileEntity tileEntityTrigger)
    {
        if (tileEntityTrigger != null && tileToHz.containsKey(tileEntityTrigger))
        {
            remove(tileEntityTrigger.getPos());
        }
    }

    public void remove(final BlockPos blockPos)
    {
        if (blockPos != null)
        {
            //Get hz
            int hz = tileToHz.get(blockPos);

            //Remove from tile to hz map
            tileToHz.remove(blockPos);

            //Remove from hz to tiles map
            if (hzToTile.containsKey(hz))
            {
                hzToTile.get(hz).remove(blockPos);
            }
        }
    }

    /**
     * Collects a list of positions that contain wireless tower controllers.
     *
     * @param hz     - frequency to get tiles
     * @param world  - world to look for tiles
     * @param center - point to distance check from
     * @param range  - range to get tiles inside
     * @return list of positions matching tiles
     */
    public static List<BlockPos> getTiles(final int hz, final World world, final BlockPos center, final double range)
    {
        WirelessTowerHzHandler hzHandler = get(world);
        final List<TileEntityWirelessController> tiles = new ArrayList();
        if (hzHandler.hzToTile.containsKey(hz))
        {
            final List<BlockPos> invalid = new ArrayList();

            //Loop tiles
            for (BlockPos blockPos : hzHandler.hzToTile.get(hz))
            {
                //Ensure area is loaded so not to load chunks
                if (world.isBlockLoaded(blockPos))
                {
                    //Get tile at location
                    TileEntity tileEntity = world.getTileEntity(blockPos);

                    //Validate tile is usable
                    if (tileEntity instanceof TileEntityWirelessController
                            && !tileEntity.isInvalid()
                            && tileEntity.getWorld() == world
                            && tileEntity.getPos() != null)
                    {
                        //Check that same world and is in range
                        //  range is box shaped, replace with [sqrt(x^2 + y^2 + z^2) < range] for sphere shaped
                        if (inRange(center.getX(), tileEntity.getPos().getX(), range)
                                && inRange(center.getY(), tileEntity.getPos().getY(), range)
                                && inRange(center.getZ(), tileEntity.getPos().getZ(), range))
                        {

                            //Collect tile to allow sorting, converted to block pos at end
                            tiles.add((TileEntityWirelessController) tileEntity);
                        }
                    }
                    //Mark bad tiles for removal
                    //  should not happen, but exists just
                    //  in case of other mods breaking logic
                    else
                    {
                        invalid.add(blockPos);
                    }
                }
                //Remove tiles that are not load
                //  Should not happen as tiles remove themselves on unload
                else
                {
                    invalid.add(blockPos);
                }
            }

            //Remove bad tiles
            invalid.forEach(blockPos -> hzHandler.remove(blockPos));

            //Sort data to improve energy transfer
            Collections.sort(tiles, new TowerSorter(center));

            //Convert data to block pos
            return tiles.stream().map(TileEntity::getPos).collect(Collectors.toList());
        }

        return new ArrayList();
    }

    //Simple method to check range
    private static boolean inRange(double a, double b, double range)
    {
        double delta = a - b;
        return delta >= 0 ? delta < range : delta > -range;
    }
}
