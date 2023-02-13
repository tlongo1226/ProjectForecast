package com.example.projectforcast;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectforcast.databinding.FragmentFirstBinding;

import java.util.LinkedList;

public class FirstFragment extends Fragment {
    private FragmentFirstBinding binding;
    PrevDeviceListAdapter prevAdapter;

    private LinkedList<BluetoothDevice> pairedDeviceList = new LinkedList<>(MainActivity.getPairedDeviceList());
    private BluetoothLeScanner forecastScanner;
    private AvailDeviceListAdapter availDeviceAdapter = new AvailDeviceListAdapter();
    private boolean scanning = false;
    private boolean devConnected=false;
    private static final long SCAN_PERIOD = 10000;
    private ScanCallback forecastCallback = new ScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            availDeviceAdapter.addForecastDevice(result.getDevice());
            System.out.println("New Dev discovered: "+result.getDevice().getName());
            availDeviceAdapter.notifyDataSetChanged();
        }


    };
    private Handler scanHandler = new Handler();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        prevAdapter = new PrevDeviceListAdapter(binding.getRoot().getContext(), pairedDeviceList);
        forecastScanner = ((MainActivity) getActivity()).getBleScanner();
        binding.availDeviceRecycler.setAdapter(availDeviceAdapter);
        binding.prevDeviceRecycler.setAdapter(prevAdapter);
        binding.scanStateButton.setOnClickListener(view -> {
            scanDevice();
        });
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.availDeviceRecycler.getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this.getContext(), R.drawable.avail_device_spacer));
        binding.availDeviceRecycler.addItemDecoration(dividerItemDecoration);

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

    private void scanDevice() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            getActivity().requestPermissions(new String[]{Manifest.permission.BLUETOOTH_SCAN}, 1);
        }
        if (!scanning) {
            binding.scanStateButton.setText("Scan\nIn Progress");
            scanHandler.postDelayed(() -> {
                scanning = false;
                forecastScanner.stopScan(forecastCallback);
                binding.scanStateButton.setText("Start\nScan");
            }, SCAN_PERIOD);
            scanning = true;

            forecastScanner.startScan(forecastCallback);
        }else{
            forecastScanner.stopScan(forecastCallback);
        }
    }


}