package com.sudo.equeue.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sudo.equeue.NetBaseActivity;
import com.sudo.equeue.R;
import com.sudo.equeue.activities.QueueAdminActivity;
import com.sudo.equeue.activities.QueueViewerActivity;
import com.sudo.equeue.models.basic.Queue;
import com.sudo.equeue.models.basic.QueueList;
import com.sudo.equeue.utils.QueueApplication;

import java.util.ArrayList;
import java.util.List;

//import com.sudo.equeue.activities.MainActivity;


public class QueueListFragment extends Fragment {

    public interface SaveIdListener {
        void saveFindRequestId(int id);
    }

    public static final String TAG = QueueApplication.prefix + ".fragments.QueueListFragment";
    public static final String ARGS_IS_MY = QueueApplication.prefix + ".fragments.args.is_my";

//    private int findQueuesRequestId;

    private ArrayAdapter<String> adapter;
    private List<String> queueNamesList = new ArrayList<>();
    private List<Queue> queueList;
    private boolean isMy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_queue_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        isMy = getArguments().getBoolean(ARGS_IS_MY, false);

        ListView listView = (ListView) view.findViewById(R.id.queue_list);

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, queueNamesList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view1, position, id) -> openQueue(position));

        if (isMy) {
            ((SaveIdListener) getActivity()).saveFindRequestId(((NetBaseActivity) getActivity()).getServiceHelper().myQueues());
        } else {
            ((SaveIdListener) getActivity()).saveFindRequestId(((NetBaseActivity) getActivity()).getServiceHelper().findQueue());
        }
    }

    private void openQueue(int position) {
        if (isMy) {
            Intent intent = new Intent(getActivity(), QueueAdminActivity.class);
            intent.putExtra(QueueAdminActivity.EXTRA_IS_NEW_QUEUE, false);
            intent.putExtra(QueueAdminActivity.EXTRA_QUEUE_ID, queueList.get(position).getQid());
            startActivity(intent);
        } else {
            Intent intent = new Intent(getActivity(), QueueViewerActivity.class);
            intent.putExtra(QueueViewerActivity.EXTRA_QUEUE_ID, queueList.get(position).getQid());
            startActivity(intent);
        }
    }

    public void updateQueueList(QueueList queues) {
        queueList = queues.getQueues();
        for (Queue queue : queueList) {
            queueNamesList.add(queue.getName());
        }
        adapter.notifyDataSetChanged();
    }
}
