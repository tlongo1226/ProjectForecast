package com.example.projectforcast;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class PrevDeviceListAdapter extends RecyclerView.Adapter{
    LinkedList<BluetoothDevice> devices;

    public PrevDeviceListAdapter(@NonNull Context context, LinkedList<BluetoothDevice> deviceList){
        devices = deviceList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return devices.size();
    }
}
