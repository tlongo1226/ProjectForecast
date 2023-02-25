package com.example.projectforcast;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectforcast.databinding.FragmentFirstBinding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

public class FirstFragment extends Fragment {
    private FragmentFirstBinding binding;
    PrevDeviceListAdapter prevAdapter;

    private LinkedList<BluetoothDevice> pairedDeviceList = new LinkedList<>(MainActivity.getPairedDeviceList());
    private BluetoothLeScanner forecastScanner;
    private AvailDeviceListAdapter availDeviceAdapter;
    private BluetoothGatt forecastGatt;
    BluetoothSocket forecastSocket;
    OutputStream outputStream;
    InputStream inputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    private static final UUID SERVICE_UUID =
            UUID.fromString("4fafc201-1fb5-459e-8fcc-c5c9c331914b");
    private static final UUID CHARACTERISTIC_TX_UUID =
            UUID.fromString("beb5483e-36e1-4688-b7f5-ea07361b26a8");
    private static final UUID CHARACTERISTIC_RX_UUID =
            UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");


    private Queue<Runnable> commandQueue;
    private boolean commandQueueBusy;
    int counter;
    private boolean scanning = false;
    private boolean devConnected = false;
    volatile boolean stopWorker;

    private static final long SCAN_PERIOD = 10000;
    private ScanCallback forecastCallback = new ScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            //TODO remove scans for duplicates
            if(result.getDevice().getName() != null) {
                ForecastScanner newScanner = new ForecastScanner(result.getDevice(), result.getRssi());
                availDeviceAdapter.addForecastDevice(newScanner);
                System.out.println("New Dev discovered: " + result.getDevice().getName());
                availDeviceAdapter.notifyDataSetChanged();
            }
        }
    };

    private BluetoothGattCallback forecastGattCallback = new BluetoothGattCallback() {
        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status);
            System.out.println("Top of phyUpdate");
            System.out.println("TxPhy: " + txPhy);
            System.out.println("rxPhy: " + rxPhy);
            System.out.println("Status: " + status);
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState){
            System.out.println("Status: "+status);
            System.out.println("newState: "+newState);
            if(newState == BluetoothProfile.STATE_CONNECTED){
                System.out.println("Connected");
                forecastGatt = gatt;

                gatt.discoverServices();
            }else{
                System.out.println("Forecast scanner Disconnected");
            }

        }

        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            List<BluetoothGattService> services= gatt.getServices();
            if(services.isEmpty()){
                System.out.println("EMPTY Service");
            }else{
//                System.out.println("Length of services: "+services.size());
                for(int i =0; i<services.size(); i++){
                    System.out.print("UUID OF Service "+(i+1)+": ");
                    BluetoothGattService service = services.get(i);
                    String charID = String.valueOf(service.getUuid());
                    System.out.println(charID);
                    List<BluetoothGattCharacteristic> characters = service.getCharacteristics();
                    if(characters.isEmpty()){
                        System.out.println("EMPTY Characteristics");
                    }else {
//                        System.out.println("Length of characteristics: " + characters.size());
                        for (int j = 0; j < characters.size(); j++) {
                            BluetoothGattCharacteristic currChar = characters.get(j);
                            String charUUID = String.valueOf(currChar.getUuid());
                            System.out.println("\tUUID of Char "+(j+1)+": " + charUUID);

                            List<BluetoothGattDescriptor> descriptors = currChar.getDescriptors();
                            for (int k =0; k<descriptors.size(); k++){

                                BluetoothGattDescriptor currDescrip = descriptors.get(k);
                                System.out.println("\t\tUUID of Descr " + (k + 1) +": "+ currDescrip.getUuid());

                            }
                        }
                    }

                }
            }
            BluetoothGattService service = gatt.getService(SERVICE_UUID);
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(CHARACTERISTIC_TX_UUID);

            // Enable notifications for the characteristic

            gatt.setCharacteristicNotification(characteristic, true);
            System.out.println("Char Notifications Enabled");
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
//            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            gatt.writeDescriptor(descriptor);
            System.out.println("Descriptor enable value written");
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }



        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            System.out.println("INSIDE DESCRIPTOR WRITE");
        }

        @Override
        public void onCharacteristicRead(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value, int status) {
            super.onCharacteristicRead(gatt, characteristic, value, status);
            System.out.println("Inside characterisitc read");
            if(status==BluetoothGatt.GATT_SUCCESS){
                System.out.println("Characteristic Successful");
                System.out.println("Value received: " + Arrays.toString(value));
            }else{
                System.out.println("Characteristic not successful");
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            System.out.println("Inside the characteristic write");
        }

        @Override
        public void onCharacteristicChanged(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value) {
            super.onCharacteristicChanged(gatt, characteristic, value);
            String newVal = characteristic.getStringValue(0);
            System.out.println("Char value in value param: "+ Arrays.toString(value));
            System.out.println("Char value changed: "+ newVal);
            Log.i("BLE CHAR", "Char value changed: "+ newVal);
            Log.i("BLE CHAR", "Char value changed: "+ Arrays.toString(value));
        }
    };
    private Handler scanHandler = new Handler();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        availDeviceAdapter = new AvailDeviceListAdapter(binding.getRoot().getContext(), this);
        prevAdapter = new PrevDeviceListAdapter(binding.getRoot().getContext(), pairedDeviceList, this);
        forecastScanner = ((MainActivity) getActivity()).getBleScanner();

        binding.availDeviceRecycler.setAdapter(availDeviceAdapter);
        binding.prevDeviceRecycler.setAdapter(prevAdapter);
        binding.scanStateButton.setOnClickListener(view -> {
            scanDevice();
        });
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.availDeviceRecycler.getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this.getContext(), R.drawable.avail_device_spacer));
        binding.prevDeviceRecycler.addItemDecoration(dividerItemDecoration);
        binding.availDeviceRecycler.addItemDecoration(dividerItemDecoration);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
            availDeviceAdapter = new AvailDeviceListAdapter(binding.getRoot().getContext(), this);
            binding.availDeviceRecycler.setAdapter(availDeviceAdapter);
            forecastScanner.startScan(forecastCallback);
        } else {
            scanning = false;
            binding.scanStateButton.setText("Start\nScan");
            forecastScanner.stopScan(forecastCallback);
        }
    }

    @SuppressLint("MissingPermission")
    public void establishConn(BluetoothDevice desiredDev) throws IOException {
        System.out.println("Inside the establishConn");

        //ATTEMPT AT SERIAL COMM
//        System.out.println("AFTER permission check");
//        UUID uuid = UUID.fromString("4fafc201-1fb5-459e-8fcc-c5c9c331914b"); //Standard SerialPortService ID
//        forecastSocket = desiredDev.createRfcommSocketToServiceRecord(uuid);
//        System.out.println("AFTER socket creation");
//        forecastSocket.connect();
//        System.out.println("AFTER socket connection");
//        outputStream = forecastSocket.getOutputStream();
//        inputStream = forecastSocket.getInputStream();
//        System.out.println("AFTER getting streams");
//        System.out.println("After socketing");
//        listenForData();
        forecastGatt = desiredDev.connectGatt(this.getContext(), false, forecastGattCallback);

    }


}
