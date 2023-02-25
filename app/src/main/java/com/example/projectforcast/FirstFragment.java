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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
    UUID tempUuid = UUID.fromString("beb5483e-36e1-4688-b7f5-ea07361b26a8");


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
            if(newState == BluetoothProfile.STATE_CONNECTED){
                System.out.println("Connected");
                forecastGatt = gatt;

                forecastGatt.discoverServices();
            }else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                System.out.println("Disconnected");
            }
            else {
                if (status == 19) {
                    System.out.println("ERROR CODE 19: DEVICE DISCONNECTED ITSELF ON PURPOSE");
                } else if (status == 8) {
                    System.out.println("ERROR CODE 8: CONNECTION TIMED OUT");
                }
                else {
                    System.out.println("Error status: " + status + " specifics unknown - " + gatt.getDevice().getAddress());
                }
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
                System.out.println("Length of services: "+services.size());
                for(int i =0; i<services.size(); i++){
                    System.out.print("UUID OF Service "+(i+1)+": ");
                    BluetoothGattService service = services.get(i);
                    String charID = String.valueOf(service.getUuid());
                    System.out.println(charID);
                    List<BluetoothGattCharacteristic> characters = service.getCharacteristics();
                    if(characters.isEmpty()){
                        System.out.println("EMPTY Characteristics");
                    }else {
                        System.out.println("Length of characteristics: " + characters.size());
                        for (int j = 0; j < characters.size(); j++) {
                            BluetoothGattCharacteristic currChar = characters.get(j);
                            String charUUID = String.valueOf(currChar.getUuid());
                            System.out.println("\tUUID of Char "+(j+1)+": " + charUUID);

                            if(charUUID.equals("beb5483e-36e1-4688-b7f5-ea07361b26a8")){
                                System.out.println("Found the char and enabling/reading it");
//                                gatt.setCharacteristicNotification(currChar,true);
//                                currChar.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                if (gatt.setCharacteristicNotification(currChar, true)) {
                                    BluetoothGattDescriptor currDescrip = currChar.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")); //find the descriptors on the characteristic
                                    currDescrip.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                    if (gatt.writeDescriptor(currDescrip)){
                                        System.out.println("NOTIFICATIONS ENABLED");
                                        gatt.readCharacteristic(currChar);
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }

        @Override
        public void onCharacteristicRead(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value, int status) {
            super.onCharacteristicRead(gatt, characteristic, value, status);
            System.out.println("Inside characteristic read");
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

//        @Override
//        public void onCharacteristicChanged(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value) {
//            super.onCharacteristicChanged(gatt, characteristic, value);
//            System.out.println("Char value changed: "+ Arrays.toString(value));
//        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            byte[] newValue = characteristic.getValue();
            String newData = bytesToHex(newValue);
            System.out.println("ORIGINAL: "+newData);
        }
    };

    final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    final String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
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
        binding.disconnectButton.setOnClickListener(view -> {
            try {
                closeBT();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        binding.sendDataButton.setOnClickListener(view -> {
            try {
                sendData();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
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

    void listenForData(){
        final Handler handler = new Handler();
        final byte delimiter = 10;
        readBufferPosition = 0;
        stopWorker=false;
        readBuffer = new byte[1024];
        System.out.println("INSIDE THE LISTEN FOR DATA");
        workerThread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted() && !stopWorker){
                try
                {
                    int bytesAvailable = inputStream.available();
                    if(bytesAvailable > 0)
                    {
                        byte[] packetBytes = new byte[bytesAvailable];
                        inputStream.read(packetBytes);
                        for(int i=0;i<bytesAvailable;i++)
                        {
                            byte b = packetBytes[i];
                            if(b == delimiter)
                            {
                                byte[] encodedBytes = new byte[readBufferPosition];
                                System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                final String data = new String(encodedBytes, StandardCharsets.US_ASCII);
                                readBufferPosition = 0;

                                handler.post(new Runnable()
                                {
                                    public void run()
                                    {
                                        System.out.println("SENT DATA:\n\t" +data);

                                    }
                                });
                            }
                            else
                            {
                                readBuffer[readBufferPosition++] = b;
                            }
                        }
                    }
                }
                catch (IOException ex)
                {
                    stopWorker = true;
                }
            }
        });
        workerThread.start();
    }

    void sendData() throws IOException{


    }

    void closeBT() throws IOException{
        stopWorker = true;
        outputStream.close();
        inputStream.close();
        forecastSocket.close();
        System.out.println("BLUETOOTH CLOSED");
    }
}
