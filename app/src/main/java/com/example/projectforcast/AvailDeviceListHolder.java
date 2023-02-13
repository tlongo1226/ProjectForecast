package com.example.projectforcast;

import android.bluetooth.BluetoothDevice;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectforcast.databinding.AvailDeviceRowBinding;

public class AvailDeviceListHolder extends RecyclerView.ViewHolder{
    AvailDeviceRowBinding binding;
    public AvailDeviceListHolder(AvailDeviceRowBinding rowBinding) {
        super(rowBinding.getRoot());
        this.binding = rowBinding;
    }

    public void bind(BluetoothDevice bleDev){
        binding.setForecastScanner(bleDev);
    }
}
