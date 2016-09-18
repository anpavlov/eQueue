package com.sudo.equeue.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sudo.equeue.R;
import com.sudo.equeue.activities.FindBeaconsActivity;
import com.sudo.equeue.activities.QueueActivity;
import com.sudo.equeue.models.Queue;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import java.util.ArrayList;

/**
 * Created by vitaly on 11.09.16.
 */
public class BeaconsRVAdapter extends RecyclerView.Adapter<BeaconsRVAdapter.ViewHolder> {

    private ArrayList<Queue> queues;
    private FindBeaconsActivity activity;

    public BeaconsRVAdapter(ArrayList<Queue> queues, FindBeaconsActivity activity) {
        this.queues = queues;
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

        holder.queueTVName.setText(queues.get(position).getName());
        holder.queueTVDescription.setText(queues.get(position).getDescription());
        holder.queueTVAddress.setText(queues.get(position).getAddress());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, QueueActivity.class);
            intent.putExtra(QueueActivity.EXTRA_QUEUE, queues.get(position));
            activity.startActivity(intent);
            activity.finish();
        });
    }

    @Override
    public int getItemCount() {
        return queues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView queueTVName;
        TextView queueTVDescription;
        TextView queueTVAddress;

        public ViewHolder(View itemView) {
            super(itemView);
            queueTVName = (TextView) itemView.findViewById(R.id.beacon_queue_name_tv);
            queueTVDescription = (TextView) itemView.findViewById(R.id.beacon_queue_descr_tv);
            queueTVAddress = (TextView) itemView.findViewById(R.id.beacon_queue_address_tv);
        }
    }
}
