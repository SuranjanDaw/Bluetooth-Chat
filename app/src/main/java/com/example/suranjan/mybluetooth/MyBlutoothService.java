package com.example.suranjan.mybluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

class MyBlutoothService {

    MyBlutoothService(BluetoothDevice btdevice, UUID uuid)
    {
        acceptThread = new AcceptThread();
        connectThread = new ConnectThread(btdevice,uuid);
    }
    private static final String TAG = "MY_APP_DEBUG_TAG";
    private Handler mHandler;
    private BluetoothSocket btsocket;
    private BluetoothDevice btdevice;
    private BluetoothServerSocket mmServerSocket;
    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private String imcomingMessage = "No values sent yet.";
    private interface MessageConstants {
        int MESSAGE_READ = 0;
        int MESSAGE_WRITE = 1;
        int MESSAGE_TOAST = 2;
    }

    class AcceptThread extends Thread {
        //private final BluetoothServerSocket mmServerSocket;
        //BluetoothSocket socket;
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
            //BluetoothSocket socket;
            Log.d("aa", "in run()");
            while (true) {
                Log.d("aa", "in while()");
                try {
                    Log.d("aa","Searching for devices to Connect....");
                    btsocket = mmServerSocket.accept();

                } catch (IOException e) {

                    Log.d("aa", "Socket's accept() method failed");
                    break;
                }

                if (btsocket != null) {

                    Log.d("aa","Connected");
                    manageMyConnectedSocket(btsocket);

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

    }
    public class ConnectThread extends Thread {

        private UUID  MY_UUID;
        public ConnectThread(BluetoothDevice device, UUID MY_UUID){
            BluetoothSocket bluetoothSocket = null;
            btdevice = device;
            //MY_UUID = UUID.fromString("34B1CF4D-1069-4AD6-89B6-E161D79BE4D9");
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
                manageMyConnectedSocket(btsocket);
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

    public class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

         ConnectedThread(BluetoothSocket socket) {
             Log.d("aa","constructor of connectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            mmBuffer = new byte[1024];
            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            //mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    imcomingMessage = new String(mmBuffer,Charset.defaultCharset());
                    Log.d("aa",imcomingMessage);
                    /*Message readMsg = mHandler.obtainMessage(MessageConstants.MESSAGE_READ, numBytes, -1, mmBuffer);
                    readMsg.sendToTarget();*/
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
             Log.d("aa","in write");
            try {
                mmOutStream.write(bytes);
                String outMessge = new String(bytes, Charset.defaultCharset());
                Log.d("aa",outMessge);
                // Share the sent message with the UI activity.
                /*Message writtenMsg = mHandler.obtainMessage(MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
                writtenMsg.sendToTarget();*/
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg = mHandler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast", "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                mHandler.sendMessage(writeErrorMsg);
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

    public void manageMyConnectedSocket(BluetoothSocket mmSocket) {
        Log.d("","");
        if (mmSocket.isConnected()) {
            connectedThread = new ConnectedThread(mmSocket);
            connectedThread.start();
        }

    }
    public void write(byte[] b)
    {
        connectedThread.write(b);
    }
    public void clientConnect()
    {
        connectThread.start();
    }
    public void serverConnect()
    {
        acceptThread.start();
    }

    public String getImcomingMessage() {
        return imcomingMessage;
    }
}
