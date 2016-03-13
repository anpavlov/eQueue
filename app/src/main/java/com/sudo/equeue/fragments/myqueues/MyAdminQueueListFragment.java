package com.sudo.equeue.fragments.myqueues;

import android.content.Intent;

import com.sudo.equeue.activities.QueueAdminActivity;
import com.sudo.equeue.utils.QueueApplication;

public class MyAdminQueueListFragment extends QueueListAbstractFragment {

    public static final String TAG = QueueApplication.prefix + ".fragments.MyAdminQueueListFragment";
    public static final String TAB_NAME = "Admining";

    @Override
    protected int makeRequest() {
        return getServiceHelper().myQueues();
    }

    @Override
    protected void onQueueCLick(int position) {
        Intent intent = new Intent(getActivity(), QueueAdminActivity.class);
        intent.putExtra(QueueAdminActivity.EXTRA_IS_NEW_QUEUE, false);
        intent.putExtra(QueueAdminActivity.EXTRA_QUEUE_ID, queueList.get(position).getQid());
        startActivity(intent);
    }
}
