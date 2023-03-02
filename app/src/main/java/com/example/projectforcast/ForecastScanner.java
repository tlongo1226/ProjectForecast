package com.example.projectforcast;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class ForecastScanner extends BaseObservable {
    private int rssi=-1;
    private String address = "";
    private String name = "";
    private BluetoothDevice bleDev;
    private boolean connected = false;
    private boolean disabled= false;

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

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }


    public String getButtonText(){
        if(connected){
            return "Disconnect";
        }else{
            return "Connect";
        }
    }
}
