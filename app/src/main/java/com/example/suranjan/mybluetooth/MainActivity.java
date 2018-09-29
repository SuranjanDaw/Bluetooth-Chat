package com.example.suranjan.mybluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.UUID;

import static android.provider.Settings.NameValueTable.NAME;

public class MainActivity extends AppCompatActivity {
    BluetoothAdapter mBluetoothAdapter;
    private TextView text;
    private TextView text2;
    private TextView sample;
    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = findViewById(R.id.pairedDevices);
        text2 = findViewById(R.id.discoveredDevices);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void sendData(View view) {

        if (mBluetoothAdapter == null)
            Toast.makeText(this, "Device does not support Bluetooth!!!", Toast.LENGTH_SHORT).show();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }

    public void showList(View view) {
        StringBuilder deviceName = new StringBuilder("");
        StringBuilder deviceMACAdress = new StringBuilder("");
        Set<BluetoothDevice> pariedDevices = mBluetoothAdapter.getBondedDevices();
        if (pariedDevices.size() > 0) {
            for (BluetoothDevice devices : pariedDevices) {
                deviceName.append(devices.getName()).append("\n");
                deviceMACAdress.append(devices.getAddress()).append("\n");
            }
        }

        String devicesDetails = "Device name is:" + deviceName.toString() + "\nMAC ADDRESS:" + deviceMACAdress.toString();
        text.setText(devicesDetails);

    }

    public void startDiscovery1(View view) {
        if (mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery();
        boolean flag = mBluetoothAdapter.startDiscovery();
        Log.d("aa", flag + "");
        checkBTPermissions();
        IntentFilter discover = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, discover);

        //mBluetoothAdapter.cancelDiscovery();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            StringBuilder deviceName = new StringBuilder("");
            StringBuilder deviceMACAdress = new StringBuilder("");
            String action = intent.getAction();
            Log.d("aa", "hello");
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                deviceName.append(device.getName()).append("\n");
                deviceMACAdress.append(device.getAddress()).append("\n");
                Log.d("aa", deviceName.toString() + "++" + deviceMACAdress.toString());
            }
            String devicesDetails = "Device name is:" + deviceName.toString() + "\nMAC ADDRESS:" + deviceMACAdress.toString();
            text2.setText(devicesDetails);
        }
    };

    private void checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        } else {
            Log.d("aa", "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
            Toast.makeText(this, "Bluetooth Enabled", Toast.LENGTH_SHORT).show();
        else if (resultCode == RESULT_CANCELED)
            Toast.makeText(this, "Bluetooth NOT Enabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    public void makeDiscover(View view) {
        EditText time = findViewById(R.id.time);
        try{

            int time1 = Integer.parseInt(time.getText().toString());
            Intent discoverable = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverable.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,time1);
            startActivity(discoverable);
        }catch (Exception e)
        {
            Toast.makeText(this,"Set a time and then make it discoverable.",Toast.LENGTH_LONG).show();
        }

    }

    public void connectToOtherDevice(View view) {
        sample = findViewById(R.id.sample);
        AcceptThread acceptThread = new AcceptThread();
        acceptThread.start();
    }
}




