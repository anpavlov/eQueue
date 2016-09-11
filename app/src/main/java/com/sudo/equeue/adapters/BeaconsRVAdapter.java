package com.sudo.equeue.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sudo.equeue.R;
import com.sudo.equeue.activities.FindBeaconsActivity;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import java.util.ArrayList;

/**
 * Created by vitaly on 11.09.16.
 */
public class BeaconsRVAdapter extends RecyclerView.Adapter<BeaconsRVAdapter.ViewHolder> {

    private ArrayList<Beacon> beacons;
    private FindBeaconsActivity activity;

    public BeaconsRVAdapter(ArrayList<Beacon> beacons, FindBeaconsActivity activity) {
        this.beacons = beacons;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.beacons_rv_item, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String url = UrlBeaconUrlCompressor.uncompress(beacons.get(position).getId1().toByteArray());


        String number = url.substring(url.lastIndexOf('/') + 1);
        holder.queueTVName.setText("id " + number);

        holder.itemView.setOnClickListener(v -> {
            activity.searchForQueue(number);
        });
    }

    @Override
    public int getItemCount() {
        return beacons.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView queueTVName;

        public ViewHolder(View itemView) {
            super(itemView);
            queueTVName = (TextView) itemView.findViewById(R.id.beacon_queue_name_tv);
        }
    }
}
