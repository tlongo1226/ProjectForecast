package com.example.projectforcast;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;

public class ForecastScanner {
    private int rssi=-1;
    private String address = "";
    private String name = "";
    private BluetoothDevice bleDev;

    @SuppressLint("MissingPermission")
    public ForecastScanner(BluetoothDevice newDev, int rssi){
        this.bleDev = newDev;
        this.address = bleDev.getAddress();
        this.name = bleDev.getName();
        this.rssi = rssi;
    }
    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        if(name!=null){
            if(!name.isEmpty()) {
                return name;
            }else{
                return "N\\A";
            }
        }else{
            return "N\\A";
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public BluetoothDevice getBleDev() {
        return bleDev;
    }

    public void setBleDev(BluetoothDevice bleDev) {
        this.bleDev = bleDev;
    }
}
