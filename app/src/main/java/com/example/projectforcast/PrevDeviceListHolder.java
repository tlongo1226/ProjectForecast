package com.example.projectforcast;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectforcast.databinding.PrevDeviceRowBinding;

public class PrevDeviceListHolder extends RecyclerView.ViewHolder{
    PrevDeviceRowBinding binding;
    public PrevDeviceListHolder(PrevDeviceRowBinding rowBinding) {
        super(rowBinding.getRoot());
        this.binding = rowBinding;
    }

    public void bind(Object obj){

    }
}
