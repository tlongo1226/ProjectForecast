package com.example.projectforcast;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.projectforcast.databinding.FragmentSecondBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SecondFragment extends Fragment implements OnBackPressedListener, ForecastGattSecondCallbackListener {

    private FragmentSecondBinding binding;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        ((MainActivity) getActivity()).getForecastGattCallback().setSecondFragmentListener(this);
        binding.setScanner(((MainActivity)getActivity()).getForecastScanner());
        binding.ambientCheck.setOnCheckedChangeListener((compoundButton, b) -> {
            binding.ambientLayout.setSelected(compoundButton.isChecked());
           ((MainActivity) getActivity()).writeParams(setParams());
        });
        binding.humidityCheck.setOnCheckedChangeListener((compoundButton, b) -> {
            binding.humidityLayout.setSelected(compoundButton.isChecked());
            ((MainActivity) getActivity()).writeParams(setParams());
        });
        binding.skinCheck.setOnCheckedChangeListener((compoundButton, b) -> {
            binding.skinLayout.setSelected(compoundButton.isChecked());
            ((MainActivity) getActivity()).writeParams(setParams());
        });
        binding.pressureCheck.setOnCheckedChangeListener((compoundButton, b) -> {
            binding.pressureLayout.setSelected(compoundButton.isChecked());
            ((MainActivity) getActivity()).writeParams(setParams());
        });



        binding.confirmButton.setOnClickListener(view -> {
            AsyncRequestThread asyncRequestThread = new AsyncRequestThread(this.getContext());
//            String roomID = binding.roomNameSpinner.getSelectedItem().toString(); //TODO this should just be hard coded
            String roomID = "FarrproTest";
            String sowID = "N/A";
            String amb = binding.currAmbVal.getText().toString();
            String skin = binding.currSkinVal.getText().toString();
            String press = binding.currPressureVal.getText().toString();
            String humid = binding.currHumidityVal.getText().toString();

            if(!binding.animalSpinner.getSelectedItem().toString().equals("Select an Animal") && (!(amb.equals("off") || amb.equals("N\\A"))|| !(skin.equals("off") || skin.equals("N\\A")) || !(press.equals("off") || press.equals("N\\A")) || !(humid.equals("off") || humid.equals("N\\A")))){
                sowID = binding.animalSpinner.getSelectedItem().toString();

                asyncRequestThread.updatePhpArgumentsAndRunThread("username|password|call|room|sow|a|s|p|h|", MainActivity.username + "|" + MainActivity.password + "|1|"+roomID+"|"+sowID+"|"+amb+"|"+skin+"|"+press+"|"+humid+"|");
            }else{
                Toast.makeText(this.getContext(), "Please Select an Animal or Record a Measurement", Toast.LENGTH_SHORT).show();
            }


        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(), R.array.sows, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.animalSpinner.setAdapter(adapter);
        binding.animalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("Selected item: "+adapterView.getSelectedItem().toString());
                System.out.println("Position at index: "+adapterView.getItemAtPosition(i));
                if(!adapterView.getSelectedItem().toString().equals("Select an Animal")){
                    System.out.println(adapterView.getSelectedItem().toString());
                    //Todo change the animal selected
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return binding.getRoot();
    }
    public void setValues(String roomID, String sowID, String a, String s, String p, String h){
      }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public String setParams(){
        String params ="";
        if(binding.skinCheck.isChecked()){
            params= params+ "1,";
            if(((MainActivity)getActivity()).getForecastScanner().getSkinVal().equals("off")) {
                ((MainActivity)getActivity()).getForecastScanner().setSkinVal("N\\A");
            }
        }else{

            ((MainActivity)getActivity()).getForecastScanner().setSkinVal("off");
            params = params+"0,";
        }
        if(binding.ambientCheck.isChecked()){
            params= params+ "1,";
            if(((MainActivity)getActivity()).getForecastScanner().getAmbientVal().equals("off")) {
                    ((MainActivity)getActivity()).getForecastScanner().setAmbientVal("N\\A");
                }
        }else{
            ((MainActivity)getActivity()).getForecastScanner().setAmbientVal("off");
            params = params+"0,";
        }
        if(binding.humidityCheck.isChecked()){
            params= params+ "1,";
            if(((MainActivity)getActivity()).getForecastScanner().getHumidityVal().equals("off")) {
                ((MainActivity)getActivity()).getForecastScanner().setHumidityVal("N\\A");
            }
        }else{
            ((MainActivity)getActivity()).getForecastScanner().setHumidityVal("off");
            params = params+"0,";
        }
        if(binding.pressureCheck.isChecked()){
            params= params+ "1";
            if(((MainActivity)getActivity()).getForecastScanner().getPressureVal().equals("off")) {
                ((MainActivity)getActivity()).getForecastScanner().setPressureVal("N\\A");
            }
        }else{
            ((MainActivity)getActivity()).getForecastScanner().setPressureVal("off");
            params = params+"0";
        }
        return params;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public boolean onBackPressed() {

        return true;
    }

    @Override
    public void onDeviceConnected() {

    }

    @Override
    public void onDeviceDisconnected() {

    }

    @Override
    public void onDataReceived() {

    }
}