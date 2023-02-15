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
    public LinkedList<ForecastScanner> availDevices = new LinkedList<>();

    public LinkedList<ForecastScanner> getAvailDevices(){
        return availDevices;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AvailDeviceRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.avail_device_row, parent,false);
        return new AvailDeviceListHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ((AvailDeviceListHolder)holder).bind(availDevices.get(position));
    }

    public void addForecastDevice(ForecastScanner scanner){
        availDevices.add(scanner);
    }

    @Override
    public int getItemCount() {
        return availDevices.size();
    }
}
