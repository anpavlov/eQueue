package com.sudo.equeue.fragments.myqueues;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sudo.equeue.NetBaseFragment;
import com.sudo.equeue.NetService;
import com.sudo.equeue.R;
import com.sudo.equeue.activities.QueueAdminActivity;
import com.sudo.equeue.activities.QueueViewerActivity;
import com.sudo.equeue.models.Queue;
import com.sudo.equeue.models.QueueList;
import com.sudo.equeue.utils.QueueApplication;

import java.util.ArrayList;
import java.util.List;

//import com.sudo.equeue.activities.MainActivity;


public abstract class QueueListAbstractFragment extends NetBaseFragment {

//    public static final String TAG = QueueApplication.prefix + ".fragments.QueueListAbstractFragment";
    public static final String ARGS_IS_MY = QueueApplication.prefix + ".fragments.args.is_my";

    protected int getQueueRequestId = -1;

    private ArrayAdapter<String> adapter;
    protected List<String> queueNamesList = new ArrayList<>();
    protected List<Queue> queueList;
//    private boolean isMy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_queue_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        isMy = getArguments().getBoolean(ARGS_IS_MY, false);

        ListView listView = (ListView) view.findViewById(R.id.queue_list);

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, queueNamesList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view1, position, id) -> onQueueCLick(position));

        getQueueRequestId = makeRequest();

//        if (isMy) {
//            getQueueRequestId = getServiceHelper().myQueues();
//        } else {
//            getQueueRequestId = getServiceHelper().findQueue();
//        }

//        if (isMy) {
//            ((SaveIdListener) getActivity()).saveFindRequestId(((NetBaseActivity) getActivity()).getServiceHelper().myQueues());
//        } else {
//            ((SaveIdListener) getActivity()).saveFindRequestId(((NetBaseActivity) getActivity()).getServiceHelper().findQueue());
//        }
    }

//    private void openQueue(int position) {
//        if (isMy) {
//            Intent intent = new Intent(getActivity(), QueueAdminActivity.class);
//            intent.putExtra(QueueAdminActivity.EXTRA_IS_NEW_QUEUE, false);
//            intent.putExtra(QueueAdminActivity.EXTRA_QUEUE_ID, queueList.get(position).getQid());
//            startActivity(intent);
//        } else {
//            Intent intent = new Intent(getActivity(), QueueViewerActivity.class);
//            intent.putExtra(QueueViewerActivity.EXTRA_QUEUE_ID, queueList.get(position).getQid());
//            startActivity(intent);
//        }
//    }

    public void updateQueueList(QueueList queues) {
        queueList = queues.getQueues();
        queueNamesList.clear();
        for (Queue queue : queueList) {
            queueNamesList.add(queue.getName());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
        if (requestId == getQueueRequestId) {
            getServiceHelper().handleResponse(getActivity(), resultCode, data, obj -> updateQueueList((QueueList) obj), NetService.RETURN_QUEUE_LIST);
        }

    }

    protected abstract int makeRequest();

    protected abstract void onQueueCLick(int position);
}
