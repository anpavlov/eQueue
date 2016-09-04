package com.sudo.equeueadmin.adapters;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sudo.equeueadmin.R;
import com.sudo.equeueadmin.activities.BTDeviceListActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by vitaly on 03.09.16.
 */
public class BTListRVAdapter extends RecyclerView.Adapter<BTListRVAdapter.ViewHolder> {

    private ArrayList<BluetoothDevice> deviceArrayList;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket mbtSocket = null;
    private BTDeviceListActivity activity;

    public BTListRVAdapter(ArrayList<BluetoothDevice> deviceArrayList, BluetoothAdapter mBluetoothAdapter, BluetoothSocket mbtSocket, BTDeviceListActivity activity) {
        this.deviceArrayList = deviceArrayList;
        this.mBluetoothAdapter = mBluetoothAdapter;
        this.mbtSocket = mbtSocket;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.devices_rv_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.deviceInfoTV.setText(deviceArrayList.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("adapter", "clicked");
//                Toast.makeText(v.getContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();

                Toast.makeText(
                        v.getContext(),
                        "Подключение к " + deviceArrayList.get(position).getName() + ","
                                + deviceArrayList.get(position).getAddress(),
                        Toast.LENGTH_SHORT).show();

                activity.connect(deviceArrayList.get(position));

            }
        });

    }

    @Override
    public int getItemCount() {
        return deviceArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView deviceInfoTV;

        public ViewHolder(View itemView) {
            super(itemView);
            deviceInfoTV = (TextView) itemView.findViewById(R.id.rv_item_device_tv);
        }
    }
}
