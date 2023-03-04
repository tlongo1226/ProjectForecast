package com.example.projectforcast;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
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
    private Handler availDevHandler;

    public AvailDeviceListAdapter(Context context, FirstFragment parentFragment){
        this.parent = parentFragment;
        availDevHandler = new Handler();
    }

    public void updateConnection(ForecastScanner scanner){
        int position = availDevices.indexOf(scanner);
        if (position >= 0) {
            System.out.println("Inside the clicked button");
            availDevices.get(position);
            System.out.println("After position");
            availDevices.get(position).setConnected(true);
            System.out.println("After the boolean set");
            System.out.println("after the notify");

        }
        int count=0;
        while(count<availDevices.size()){
            if(count!=position){
                System.out.println("Inside the updateConn for non-clicked");
                availDevices.get(count).setDisabled(true);
            }
            count++;
        }
        availDevHandler.post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });

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
        ForecastScanner temp = availDevices.get(position);
        ((AvailDeviceListHolder)holder).bind(temp);
        ((AvailDeviceListHolder)holder).binding.connectAvailDev.setOnClickListener(view -> {
            try {
                if(temp.isConnected()){
                    ((FirstFragment)parent).disconnect();
                    temp.setConnected(false);

                }else{
                    ((FirstFragment)parent).establishConn(temp);
                    ((AvailDeviceListHolder) holder).binding.connectAvailDev.setText("Connecting...");

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
