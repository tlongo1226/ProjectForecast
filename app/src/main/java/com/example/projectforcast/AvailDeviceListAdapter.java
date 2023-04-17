package com.example.projectforcast;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectforcast.databinding.AvailDeviceRowBinding;

import java.io.IOException;
import java.util.LinkedList;

public class AvailDeviceListAdapter extends RecyclerView.Adapter {
    public LinkedList<ForecastScanner> availDevices = new LinkedList<>();
    FirstFragment parent;
    private Handler availDevHandler;

    public AvailDeviceListAdapter(Context context, FirstFragment parentFragment){
        this.parent = parentFragment;
        availDevHandler = new Handler();
    }

    public void connectDevice(ForecastScanner scanner){
        int position = availDevices.indexOf(scanner);
        if (position >= 0) {
            availDevices.get(position).setConnected(true);
        }

        availDevHandler.post(() -> notifyItemChanged(position));

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AvailDeviceRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.avail_device_row, parent,false);
        return new AvailDeviceListHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ForecastScanner temp = availDevices.get(position);
        ((AvailDeviceListHolder)holder).bind(temp);
        ((AvailDeviceListHolder)holder).binding.connectAvailDev.setOnClickListener(view -> {
            try {
                if(temp.isConnected()){
                    (parent).disconnect();
                    temp.setConnected(false);
                    int count =0;
                    while(count<availDevices.size()){
                        if(count!=position){
                            availDevices.get(count).setDisabled(false);
                        }
                        int finalCount = count;
                        availDevHandler.post(() -> notifyItemChanged(finalCount));
                        count++;
                    }
                }else{
                    (parent).establishConn(temp);
                    ((AvailDeviceListHolder) holder).binding.connectAvailDev.setText("Connecting...");
                    int count = 0;
                    while(count<availDevices.size()){
                        if(count!=position){
                            availDevices.get(count).setDisabled(true);
                        }
                        int finalCount = count;
                        availDevHandler.post(() -> notifyItemChanged(finalCount));
                        count++;
                    }
                }
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
