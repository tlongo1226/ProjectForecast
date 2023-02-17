package com.example.projectforcast;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectforcast.databinding.PrevDeviceRowBinding;

import java.io.IOException;
import java.util.LinkedList;

public class PrevDeviceListAdapter extends RecyclerView.Adapter{
    LinkedList<BluetoothDevice> devices;
    //TODO this could be changed to instead be a list of ForecastScanner for the paired devs
    //  We could leave the avail devices as a list of Bluetooth Devices
    //  would allow for increased customization in the displays, attr, etc
    FirstFragment parent;
    public PrevDeviceListAdapter(@NonNull Context context, LinkedList<BluetoothDevice> deviceList, FirstFragment parentFrag){
        devices = deviceList;
        parent = parentFrag;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PrevDeviceRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.prev_device_row, parent, false);
        return new PrevDeviceListHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BluetoothDevice deviceToBind = devices.get(position);
        ((PrevDeviceListHolder)holder).bind(deviceToBind);
        ((PrevDeviceListHolder)holder).binding.prevDevConnect.setOnClickListener(view -> {
            try {
                ((FirstFragment)parent).establishConn(devices.get(position));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }
}
