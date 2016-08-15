package com.sudo.equeue.utils;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sudo.equeue.R;
import com.sudo.equeue.models.Queue;

public class QueueListGeoAdapter extends RecyclerView.Adapter<QueueListGeoAdapter.PersonViewHolder> {

    public interface ItemClickListener {
        void onItemClick(Queue queue);
    }

//    private List<Queue> queues;
    private QueueListWrapper queues;
    private ItemClickListener listener;

    public QueueListGeoAdapter(QueueListWrapper queues, ItemClickListener listener) {
        this.queues = queues;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return queues.getQueueList().getQueues().size();
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.queue_card_geo, viewGroup, false);
        return new PersonViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        personViewHolder.bind(queues.getQueueList().getQueues().get(i), listener);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder {

        private CardView cv;
        private TextView queueName;
        private TextView queueDescription;
        private TextView queueLocation;
        private TextView queueRemaining;
        private TextView queueTotal;

        public PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.queueCard);
            queueName = (TextView)itemView.findViewById(R.id.queue_name);
            queueDescription = (TextView)itemView.findViewById(R.id.queue_description);
            queueLocation = (TextView)itemView.findViewById(R.id.location);
            queueRemaining = (TextView)itemView.findViewById(R.id.remaining);
            queueTotal = (TextView)itemView.findViewById(R.id.peop_number);
        }

        public void bind(Queue queue, ItemClickListener listener) {
            queueName.setText(queue.getName());
            queueDescription.setText(queue.getDescription());
            queueLocation.setText(queue.getAddress());
            queueRemaining.setText(Integer.toString(queue.getWaitTime()) + " мин");
            queueTotal.setText(Integer.toString(queue.getUsersQuantity()) + " человек в очереди");
            cv.setOnClickListener(v -> listener.onItemClick(queue));
        }
    }

}