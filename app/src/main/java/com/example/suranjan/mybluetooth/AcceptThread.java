package com.example.suranjan.mybluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.UUID;


class AcceptThread extends Thread {
    private final BluetoothServerSocket mmServerSocket;
    private TextView t;
    private static  final String NAME ="abc";
    public AcceptThread() {
        UUID MY_UUID = UUID.fromString("34B1CF4D-1069-4AD6-89B6-E161D79BE4D9");


        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothServerSocket tmp = null;
        try {

            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
        } catch (IOException e) {
            Log.d("aa", "Socket's listen() method failed");
        }
        mmServerSocket = tmp;
    }

    public void run() {
        BluetoothSocket socket = null;
        Log.d("aa", "in run()");
        while (true) {
            Log.d("aa", "in while()");
            try {
                Log.d("aa","Searching for devices to Connect....");
                socket = mmServerSocket.accept();
            } catch (IOException e) {

                Log.d("aa", "Socket's accept() method failed");
                break;
            }

            if (socket != null) {

                Log.d("aa","Connected");

                try {
                    mmServerSocket.close();
                } catch (IOException e) {

                    e.printStackTrace();
                }
                break;
            }
            else
                Log.d("aa","NOT Connected");

        }
    }
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) {
            Log.d("aa", "Could not close the connect socket");
        }
    }
    /*private void manageMyConnectedSocket(BluetoothSocket mmSocket) {
        if (mmSocket.isConnected()) {
            ConnectedThread mConnectedThread = new ConnectedThread(mmSocket);
            mConnectedThread.start();
        }
    }*/
}
