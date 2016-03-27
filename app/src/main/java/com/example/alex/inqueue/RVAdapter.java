package com.example.alex.inqueue;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder>{
    List<Queue> queues;

    RVAdapter(List<Queue> queues) {
        this.queues = queues;
    }

    @Override
    public int getItemCount() {
        return queues.size();
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.queue_card, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        personViewHolder.queueName.setText(queues.get(i).name);
        personViewHolder.queueDescription.setText(queues.get(i).description);
        personViewHolder.queueLocation.setText(queues.get(i).location);
        personViewHolder.queueRemaining.setText(String.format("%d", queues.get(i).remaining) + " мин");
        personViewHolder.queueTotal.setText(String.format("%d", queues.get(i).totalInQueue) + " человек в очереди");
        personViewHolder.queueFront.setText(String.format("%d", queues.get(i).frontOfYou) + " человек перед Вами");
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView queueName;
        TextView queueDescription;
        TextView queueLocation;
        TextView queueRemaining;
        TextView queueTotal;
        TextView queueFront;

        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.queueCard);
            queueName = (TextView)itemView.findViewById(R.id.queue_name);
            queueDescription = (TextView)itemView.findViewById(R.id.queue_description);
            queueLocation = (TextView)itemView.findViewById(R.id.location);
            queueRemaining = (TextView)itemView.findViewById(R.id.remaining);
            queueTotal = (TextView)itemView.findViewById(R.id.peop_number);
            queueFront = (TextView)itemView.findViewById(R.id.front_of_you);
        }
    }

}