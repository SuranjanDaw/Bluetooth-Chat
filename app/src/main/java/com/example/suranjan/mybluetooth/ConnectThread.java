package com.example.suranjan.mybluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

public class ConnectThread extends Thread {
    private final BluetoothSocket btsocket;
    private final BluetoothDevice btdevice;
    private UUID  MY_UUID;
    public ConnectThread(BluetoothDevice device){
        BluetoothSocket bluetoothSocket = null;
        btdevice = device;
        MY_UUID = UUID.fromString("34B1CF4D-1069-4AD6-89B6-E161D79BE4D9");
        try {
            bluetoothSocket = btdevice.createRfcommSocketToServiceRecord(MY_UUID);

        } catch (IOException e) {
            e.printStackTrace();
        }
        btsocket = bluetoothSocket;
    }
    public void run()
    {
        BluetoothAdapter bluetoothAdapter =BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.cancelDiscovery();
        try {
            btsocket.connect();
        } catch (IOException e) {
            try {
                btsocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }
    public void cancel()
    {
        try {
            btsocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
