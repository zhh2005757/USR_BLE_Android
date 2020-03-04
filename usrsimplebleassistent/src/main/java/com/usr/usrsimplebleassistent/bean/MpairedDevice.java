package com.usr.usrsimplebleassistent.bean;

import android.bluetooth.BluetoothDevice;

/**
 * Created by shizhiyuan on 2017/5/24.
 */

public class MpairedDevice {
    private BluetoothDevice device;
    private int rssi;

    public MpairedDevice() {
    }

    public MpairedDevice(BluetoothDevice device, int rssi) {
        this.device = device;
        this.rssi = rssi;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MDevice) {
            return device.equals(((MDevice) o).getDevice());
        }
        return false;
    }
}
