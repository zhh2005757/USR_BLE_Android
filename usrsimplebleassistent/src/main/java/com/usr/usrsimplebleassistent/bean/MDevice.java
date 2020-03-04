package com.usr.usrsimplebleassistent.bean;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Administrator on 2015-11-16.
 */
public class MDevice {
    private BluetoothDevice device;
    private int rssi;

    public MDevice(){

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

    public MDevice(BluetoothDevice device, int rssi) {

        this.device = device;
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
