package com.example.projectforcast;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.projectforcast.databinding.FragmentSecondBinding;

import java.util.UUID;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        ((MainActivity) getActivity()).getFirstFragment().getForecastGattCallback().setSecondBinding(binding);
        binding.setScanner(((MainActivity) getActivity()).getFirstFragment().getForecastDevice());
        binding.ambientCheck.setOnCheckedChangeListener((compoundButton, b) -> {
            System.out.println("Ambient Check");
            binding.ambientLayout.setSelected(compoundButton.isChecked());
            System.out.println("layoutSelected: "+binding.ambientLayout.isSelected());
           ((MainActivity) getActivity()).getFirstFragment().writeParams(setParams());
        });
        binding.humidityCheck.setOnCheckedChangeListener((compoundButton, b) -> {
            binding.humidityLayout.setSelected(compoundButton.isChecked());
            ((MainActivity) getActivity()).getFirstFragment().writeParams(setParams());
        });
        binding.skinCheck.setOnCheckedChangeListener((compoundButton, b) -> {
            binding.skinLayout.setSelected(compoundButton.isChecked());
            ((MainActivity) getActivity()).getFirstFragment().writeParams(setParams());
        });
        binding.pressureCheck.setOnCheckedChangeListener((compoundButton, b) -> {
            binding.pressureLayout.setSelected(compoundButton.isChecked());
            ((MainActivity) getActivity()).getFirstFragment().writeParams(setParams());
        });

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public String setParams(){
        String params ="";
        if(binding.skinCheck.isChecked()){
            params= params+ "1,";
        }else{
            params = params+"0,";
        }
        if(binding.ambientCheck.isChecked()){
            params= params+ "1,";
        }else{
            params = params+"0,";
        }
        if(binding.humidityCheck.isChecked()){
            params= params+ "1,";
        }else{
            params = params+"0,";
        }
        if(binding.pressureCheck.isChecked()){
            params= params+ "1";
        }else{
            params = params+"0";
        }
        return params;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}