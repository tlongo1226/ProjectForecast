package com.example.projectforcast;

public interface ForecastGattSecondCallbackListener {
    void onDeviceConnected();
    void onDeviceDisconnected();
    void onDataReceived();
}
