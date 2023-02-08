package com.example.projectforcast;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectforcast.databinding.FragmentFirstBinding;

import java.util.LinkedList;

public class FirstFragment extends Fragment {
    private FragmentFirstBinding binding;
    PrevDeviceListAdapter prevAdapter;
    private LinkedList<BluetoothDevice> pairedDeviceList = new LinkedList<>(MainActivity.getPairedDeviceList());

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        prevAdapter = new PrevDeviceListAdapter(binding.getRoot().getContext(), pairedDeviceList);
        binding.prevDeviceRecycler.setAdapter(prevAdapter);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.continueButton.setOnClickListener(view1 -> {

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}