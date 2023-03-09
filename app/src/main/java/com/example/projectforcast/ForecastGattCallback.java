package com.example.projectforcast;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.graphics.Color;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.example.projectforcast.databinding.FragmentFirstBinding;
import com.example.projectforcast.databinding.FragmentSecondBinding;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ForecastGattCallback extends BluetoothGattCallback {
    private FragmentFirstBinding firstBinding;
    private FragmentSecondBinding secondBinding;
    private Handler callBackHandler = new Handler();
    private ForecastScanner forecastDevice;

    public ForecastGattCallback(FragmentFirstBinding fBinding, ForecastScanner fScanner){
        firstBinding = fBinding;
        forecastDevice = fScanner;
    }

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

            ((AvailDeviceListAdapter)firstBinding.availDeviceRecycler.getAdapter()).connectDevice(forecastDevice);
            callBackHandler.post(()->{
                //TODO set the gatt in first fragment

                firstBinding.continueButton.setBackgroundColor(Color.parseColor("#EC782A"));
                firstBinding.continueButton.setEnabled(true);
            });
            gatt.discoverServices();
        }else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            forecastDevice=null;
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

                        if(charUUID.equalsIgnoreCase("6E400003-B5A3-F393-E0A9-E50E24DCCA9E")){
                            System.out.println("Found the char and enabling/reading it");
                            gatt.setCharacteristicNotification(currChar,true);

//                            List<BluetoothGattDescriptor> descriptors = currChar.getDescriptors();
//                            for (int k =0; k<descriptors.size(); k++) {
//                                System.out.println(descriptors.get(k));
//                            }

                            if (gatt.setCharacteristicNotification(currChar, true)) {
                                System.out.println("Enables char notifications ");
                            }

//
//                                    BluetoothGattDescriptor currDescrip = currChar.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                            BluetoothGattDescriptor currDescrip = currChar.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")); //find the descriptors on the characteristic
                            currDescrip.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            if (gatt.writeDescriptor(currDescrip)){
                                System.out.println("NOTIFICATIONS ENABLED");
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
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);
        System.out.println("INSIDE DESCRIPTOR WRITE");
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
        if (status == BluetoothGatt.GATT_SUCCESS) {

            System.out.println("Write successful");
            // Characteristic write successful
        } else {
            // Characteristic write unsuccessful
        }
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
        System.out.print("New value: ");
//        System.out.println(Arrays.toString(newValue));
        StringBuilder builder = new StringBuilder();
        for (byte b : newValue) {
            builder.append((char) (b & 0xFF));
        }

        String message = builder.toString();
        String[] newValues = message.split(",");
        forecastDevice.setSkinVal(newValues[0]);
        forecastDevice.setAmbientVal(newValues[1]);
        forecastDevice.setHumidityVal(newValues[2]);
        forecastDevice.setPressureVal(newValues[3]);
        System.out.println(message);



//            String newData = bytesToHex(newValue);
//            System.out.println("ORIGINAL: "+newData);
    }

    public void setSecondBinding(FragmentSecondBinding secondBinding) {
        this.secondBinding = secondBinding;
    }
}
