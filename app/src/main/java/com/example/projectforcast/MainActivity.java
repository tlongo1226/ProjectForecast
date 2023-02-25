package com.example.projectforcast;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.projectforcast.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Set;
//<a href="https://www.flaticon.com/free-icons/information" title="information icons">Information icons created by Freepik - Flaticon</a>
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    protected BluetoothManager bluetoothManager;
    public BluetoothLeScanner bleScanner;
    BluetoothAdapter bluetoothAdapter;
    static Set<BluetoothDevice> pairedDeviceList;
    public FloatingActionButton infoButton;
    int REQUEST_ENABLE_BT = 0;
    ConnectInfoDialog connectInfoDialog;
    DataInfoDialog dataInfoDialog;
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bluetoothManager = getSystemService(BluetoothManager.class);
        bluetoothAdapter =bluetoothManager.getAdapter();
        if(bluetoothAdapter == null){
            System.out.println("Device does not support Bluetooth");
        }else {
            if (!bluetoothAdapter.isEnabled()) {
                System.out.println("Bluetooth is not enabled");
                if(ContextCompat.checkSelfPermission(
                        this, Manifest.permission.BLUETOOTH)== PackageManager.PERMISSION_GRANTED){
                    System.out.println("Bluetooth is already enabled");
                }else{
                    requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH);
                }

            } else {
                System.out.println("Bluetooth Enabled");
            }
        }
        pairedDeviceList = bluetoothAdapter.getBondedDevices();
        bleScanner = bluetoothAdapter.getBluetoothLeScanner();
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        dataInfoDialog = new DataInfoDialog(this);
        connectInfoDialog = new ConnectInfoDialog(this);
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        binding.getRoot().findViewById(R.id.informationButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment currFrag = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main).getChildFragmentManager().getFragments().get(0);
                if(currFrag instanceof FirstFragment){
                    connectInfoDialog.show();
                }else if(currFrag instanceof SecondFragment){
                    dataInfoDialog.show();
                }
            }
        });
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public BluetoothLeScanner getBleScanner() {
        return bleScanner;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public static Set<BluetoothDevice> getPairedDeviceList() {
        return pairedDeviceList;
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    System.out.println("Permission granted");
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    System.out.println("Permission denied");
                }
            });
}