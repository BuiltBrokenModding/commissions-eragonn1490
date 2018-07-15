package com.builtbroken.energystorageblock.content.wireless.controller;

import com.builtbroken.energystorageblock.content.wireless.controller.network.WirelessTowerHzHandler;
import com.builtbroken.triggerblock.cap.ITriggerHz;

public class CapabilityWirelessHz implements ITriggerHz
{
    private int hz;
    private final TileEntityWirelessController host;

    public CapabilityWirelessHz(TileEntityWirelessController host)
    {
        this.host = host;
    }

    @Override
    public int getTriggerHz()
    {
        return hz;
    }

    @Override
    public void setTriggerHz(int hz)
    {
        int prev = getTriggerHz();
        this.hz = hz;
        if (hz != prev && host.getWorld() != null)
        {
            host.hzChanged = true;
            if (hz != 0)
            {
                WirelessTowerHzHandler.get(host.getWorld()).add(host);
            }
            else
            {
                WirelessTowerHzHandler.get(host.getWorld()).remove(host);
            }
        }
    }
}