package com.sudo.equeueadmin.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sudo.equeueadmin.R;
import com.sudo.equeueadmin.adapters.BTListRVAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BTDeviceListActivity extends AppCompatActivity {

    static public final int REQUEST_CONNECT_BT = 0x2300;
    final static int REQUEST_BLUETOOTH = 47;
    final static int REQUEST_CODE_LOCATION = 48;
    static private final int REQUEST_ENABLE_BT = 0x1000;

    private ProgressDialog dialog;
    private Menu menu;

    static private BluetoothAdapter mBluetoothAdapter = null;

    static private ArrayAdapter<String> mArrayAdapter = null;

    private ArrayList<BluetoothDevice> deviceArrayList;

    private BTListRVAdapter adapter;

//    static private ArrayAdapter<BluetoothDevice> btDevices = null;
    private RecyclerView devicesRV;

    private static final UUID SPP_UUID = UUID
            .fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
// UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    static private BluetoothSocket mbtSocket = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog = new ProgressDialog(BTDeviceListActivity.this);
        dialog.setTitle("Поиск устройств");
        dialog.setMessage("Идет поиск устройств");

        deviceArrayList = new ArrayList<>();
        setContentView(R.layout.activity_bluetooth_devices);
        devicesRV = (RecyclerView) findViewById(R.id.devices_rv);
        devicesRV.setLayoutManager(new LinearLayoutManager(BTDeviceListActivity.this));
        adapter = new BTListRVAdapter(deviceArrayList, mBluetoothAdapter, mbtSocket, this);
        devicesRV.setAdapter(adapter);


        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("Bluetooth-устройства");
        }

        try {
            if (initDevicesList() != 0) {
                this.finish();
                return;
            }

        } catch (Exception ex) {
            this.finish();
            return;
        }

        IntentFilter btIntentFilter = new IntentFilter();
        btIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        btIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        btIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        btIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mBTReceiver, btIntentFilter);
    }

    public static BluetoothSocket getSocket() {
        return mbtSocket;
    }

    private void flushData() {
        try {
            if (mbtSocket != null) {
                mbtSocket.close();
                mbtSocket = null;
            }

            if (mBluetoothAdapter != null) {
                mBluetoothAdapter.cancelDiscovery();
            }

            if (deviceArrayList != null) {
                deviceArrayList.clear();
            }

            if (mArrayAdapter != null) {
                mArrayAdapter.clear();
                mArrayAdapter.notifyDataSetChanged();
                mArrayAdapter.notifyDataSetInvalidated();
                mArrayAdapter = null;
            }

            finalize();

        } catch (Exception ex) {
        } catch (Throwable e) {
        }

    }
    private int initDevicesList() {

        flushData();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(),
                    "Bluetooth не поддерживается", Toast.LENGTH_LONG).show();
            return -1;
        }

        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
//
//        mArrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
//                android.R.layout.simple_list_item_1);
//
//        setListAdapter(mArrayAdapter);

        Intent enableBtIntent = new Intent(
                BluetoothAdapter.ACTION_REQUEST_ENABLE);
        try {
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } catch (Exception ex) {
            return -2;
        }

//        Toast.makeText(getApplicationContext(),
//                "Поиск Bluetooth-устройств", Toast.LENGTH_SHORT)
//                .show();

        return 0;

    }

    @Override
    protected void onResume() {
        super.onResume();
        int hasReadContactsPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH);

        if (hasReadContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.BLUETOOTH)) {
                showMessageOKCancel("Необходимо дать разрешение на использование сервиса Bluetooth",
                        (dialog, which) -> ActivityCompat.requestPermissions(BTDeviceListActivity.this,
                                new String[] {Manifest.permission.BLUETOOTH},
                                REQUEST_BLUETOOTH));
                return;
            }
            ActivityCompat.requestPermissions(BTDeviceListActivity.this,
                    new String[] {Manifest.permission.BLUETOOTH},
                    REQUEST_BLUETOOTH);
            return;
        }

        int hasLocationPermission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                showMessageOKCancel("Необходимо дать разрешение на использование сервиса LOCATION",
                        (dialog, which) -> ActivityCompat.requestPermissions(BTDeviceListActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_CODE_LOCATION));
                return;
            }
            ActivityCompat.requestPermissions(BTDeviceListActivity.this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION);
            return;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(BTDeviceListActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void connect(BluetoothDevice device){
        if (mBluetoothAdapter == null) {
            return;
        }

        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        Thread connectThread = new Thread(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                try {
                    Log.i("adapter", "runned");
                    boolean gotuuid = device
                            .fetchUuidsWithSdp();
                    UUID uuid = device.getUuids()[0]
                            .getUuid();
                    Thread.sleep(1000);
                    mbtSocket = device
                            .createRfcommSocketToServiceRecord(uuid);
                    Thread.sleep(1000);
                    mbtSocket.connect();
                    Log.i("adapter", "unregistered");
                } catch (IOException ex) {
                    setResult(RESULT_CANCELED, intent);
                    runOnUiThread(socketErrorRunnable);
                    try {
                        mbtSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mbtSocket = null;
                    return;
                } catch (InterruptedException ex){
                    ex.printStackTrace();
                } finally {
                    Log.i("adapter", "finally");
                    unregisterReceiver(mBTReceiver);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("adapter", "finish");
                            finish();

                        }
                    });
                }
            }
        });
        connectThread.start();
    }


    private Runnable socketErrorRunnable = new Runnable() {

        @Override
        public void run() {
            Toast.makeText(BTDeviceListActivity.this,
                    "Невозможно установить соединение", Toast.LENGTH_SHORT).show();
            mBluetoothAdapter.startDiscovery();

        }
    };

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent intent) {
        super.onActivityResult(reqCode, resultCode, intent);



        switch (reqCode) {
            case REQUEST_ENABLE_BT:

                if (resultCode == RESULT_OK) {
                    Set<BluetoothDevice> btDeviceList = mBluetoothAdapter
                            .getBondedDevices();
                    try {
                        if (btDeviceList.size() > 0) {

                            for (BluetoothDevice device : btDeviceList) {
                                if (btDeviceList.contains(device) == false) {
                                    deviceArrayList.add(device);
                                    adapter.notifyDataSetChanged();
//                                    btDevices.add(device);
//
//                                    mArrayAdapter.add(device.getName() + "\n"
//                                            + device.getAddress());
//                                    mArrayAdapter.notifyDataSetInvalidated();
                                }
                            }
                        }
                    } catch (Exception ex) {
                    }
                }

                break;
        }

        mBluetoothAdapter.startDiscovery();

    }

    private final BroadcastReceiver mBTReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Log.i("receive", action);

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                try {
                    deviceArrayList.add(device);
                    adapter.notifyDataSetChanged();
//                    if (btDevices == null) {
//                        btDevices = new ArrayAdapter<BluetoothDevice>(
//                                getApplicationContext(), android.R.id.text1);
//                    }
//
//                    if (btDevices.getPosition(device) < 0) {
//                        btDevices.add(device);
//                        mArrayAdapter.add(device.getName() + "\n"
//                                + device.getAddress() + "\n" );
//                        mArrayAdapter.notifyDataSetInvalidated();
//                    }

                } catch (Exception ex) {
// ex.fillInStackTrace();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.i("adapter", "discovery finished");
                dialog.dismiss();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                dialog.show();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        this.menu = menu;

        getMenuInflater().inflate(R.menu.menu_bluetooth, menu);
        return true;
    }

    private void hideOption(int id)
    {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    private void showOption(int id)
    {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.menu_bt_refresh:
                initDevicesList();
                Log.i("adapter", "refresh");
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return true;
    }
}
