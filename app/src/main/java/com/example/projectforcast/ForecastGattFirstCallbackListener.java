package com.example.projectforcast;

public interface ForecastGattFirstCallbackListener {
    void onDeviceConnected();
    void onDeviceDisconnected();
    void onConnectInit(ForecastScanner desiredDev, int position);
    void onConnectConfirm(ForecastScanner scanner);
}
