package com.bu.ubru.ubru_ui_test1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ArduinoTestActivity extends Activity {

    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice = null;
    public TextView myLabel;
    public Button mBtn1;
    public Button mBtn2;

    final byte delimiter = 33; // delimiter is an exclamation point (!)
    int readBufferPosition = 0;


    public int sendBtMsg(String msg2send){
        //UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
        UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e492929"); //Standard SerialPortService ID
        try {
            if(mmDevice != null) {
                mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
                if (!mmSocket.isConnected()) {
                    mmSocket.connect();
                }

                OutputStream mmOutputStream = mmSocket.getOutputStream();
                mmOutputStream.write(msg2send.getBytes());
                return 0;
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 1;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arduino_test);

        final Handler handler = new Handler();

        myLabel = (TextView) this.findViewById(R.id.results);
        mBtn1 = (Button) this.findViewById(R.id.arduino_test_button1);
        mBtn2 = (Button) this.findViewById(R.id.arduino_test_button2);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //Log.e("Ubru","Vars initialized!!");

        final class workerThread implements Runnable {

            private String btMsg;

            public workerThread(String msg) {
                btMsg = msg;
            }

            public void run()
            {
                if(sendBtMsg(btMsg) == 0) {
                    while (!Thread.currentThread().isInterrupted()) {
                        int bytesAvailable;
                        boolean workDone = false;

                        try {


                            final InputStream mmInputStream;
                            mmInputStream = mmSocket.getInputStream();
                            bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {

                                byte[] packetBytes = new byte[bytesAvailable];
                                Log.e("Ubru", "bytes available");
                                byte[] readBuffer = new byte[1024];
                                mmInputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        Log.e("Ubru", "msg: " + data);

                                        //The variable data now contains our full command
                                        handler.post(new Runnable() {
                                            public void run() {
                                                myLabel.setText(data);
                                            }
                                        });

                                        workDone = true;
                                        break;


                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }

                                if (workDone) {
                                    Log.e("Ubru", "socket closed");
                                    mmSocket.close();
                                    break;
                                }

                            }
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                }

            }
        };


        // start mBtn1 handler

        mBtn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on temp button click

                (new Thread(new workerThread("test1"))).start();

            }
        });

        //end mBtn1 handler

        // start mBtn2 handler

        mBtn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on temp button click

                (new Thread(new workerThread("test2"))).start();

            }
        });

        //end mBtn2 handler



        if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                //Log.e("Ubru","Got in here.... with name: "+device.getName());
                if(device.getName().equals("BlueZ 5.47"))
                {
                    Log.e("Ubru",device.getName());
                    mmDevice = device;
                    break;
                }
            }
        }


    }

    public void backMessage(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

}