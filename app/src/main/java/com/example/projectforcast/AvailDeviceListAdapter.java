package com.example.projectforcast;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectforcast.databinding.AvailDeviceRowBinding;

import java.io.IOException;
import java.util.LinkedList;

public class AvailDeviceListAdapter extends RecyclerView.Adapter {
    public LinkedList<ForecastScanner> availDevices = new LinkedList<>();
    FirstFragment parent;

    public AvailDeviceListAdapter(Context context, FirstFragment parentFragment){
        this.parent = parentFragment;
    }

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
        ((AvailDeviceListHolder)holder).binding.connectAvailDev.setOnClickListener(view -> {
            try {
                ((FirstFragment)parent).establishConn(availDevices.get(position).getBleDev());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void addForecastDevice(ForecastScanner scanner){
        availDevices.add(scanner);
    }

    @Override
    public int getItemCount() {
        return availDevices.size();
    }
}
