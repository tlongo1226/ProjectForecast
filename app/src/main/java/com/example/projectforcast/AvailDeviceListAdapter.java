package com.example.projectforcast;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectforcast.databinding.AvailDeviceRowBinding;

import java.util.LinkedList;

public class AvailDeviceListAdapter extends RecyclerView.Adapter {
    LinkedList<BluetoothDevice> availDevices = new LinkedList<>();
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AvailDeviceRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.avail_device_row, parent,false);
        return new AvailDeviceListHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BluetoothDevice devToBind = availDevices.get(position);
        ((AvailDeviceListHolder)holder).bind(devToBind);
    }

    public void addForecastDevice(BluetoothDevice scanner){
        availDevices.add(scanner);
    }

    @Override
    public int getItemCount() {
        return availDevices.size();
    }
}
