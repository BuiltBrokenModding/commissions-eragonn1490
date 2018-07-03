package com.builtbroken.energystorageblock.content.wireless.controller;

import com.builtbroken.energystorageblock.content.wireless.connector.TileEntityWirelessConnector;
import com.builtbroken.energystorageblock.lib.network.MessageDesc;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/3/2018.
 */
public class TileEntityWirelessController extends TileEntity implements ITickable
{
    private TileEntityWirelessConnector connector;

    private boolean sendDescPacket = false;
    private boolean isMultiBlockFormed = false;
    private int hz = -1;

    private int ticks = 0;

    @Override
    public void update()
    {
        //Count ticks
        if (ticks++ >= Short.MAX_VALUE)
        {
            ticks = 0;
        }

        //Server logic
        if (!world.isRemote)
        {
            if (ticks % 20 == 0)
            {
                //Check structure
                boolean prevMultiBlockFormed = isMultiBlockFormed;
                isMultiBlockFormed = checkMultiBlock();

                //Play effect to act as user feedback
                if (prevMultiBlockFormed != isMultiBlockFormed)
                {
                    if (isMultiBlockFormed)
                    {
                        //TODO generate redstone effect and audio to note generated
                    }
                    else
                    {
                        //TODO generate smoke effect and audio to note broken
                    }
                }

                //Find connector
                if (connector == null || connector.isInvalid())
                {
                    TileEntity tile = world.getTileEntity(getPos().down());
                    if (tile instanceof TileEntityWirelessConnector)
                    {
                        connector = (TileEntityWirelessConnector) tile;
                        connector.setController(this);
                    }
                }
            }

            //Only run logic if we have a hz
            if (hz >= 0)
            {
                if (isOutputMode())
                {
                    //TODO pull power from connector and send to other towers
                }
            }

            //Send packet if needed
            if (sendDescPacket)
            {
                sendDescPacket = false;
                MessageDesc.send(this);
            }
        }
    }

    public boolean isMultiBlockFormed()
    {
        return isMultiBlockFormed;
    }

    protected boolean checkMultiBlock()
    {
        return true;
    }

    public boolean isOutputMode()
    {
        return connector != null && !connector.shouldOutputEnergyToTiles();
    }
}
