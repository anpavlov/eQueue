//package com.sudo.equeue.fragments.myqueues;
//
//import android.content.Intent;
//
//import com.sudo.equeue.activities.QueueViewerActivity;
//import com.sudo.equeue.utils.QueueApplication;
//
//public class MyStandingQueueListFragment extends RefreshableQueueListAbstractFragment {
//
//    public static final String TAG = QueueApplication.prefix + ".fragments.MyStandingQueueListFragment";
//    public static final String TAB_NAME = "Standings";
//
//    @Override
//    protected int makeRequest() {
//        return getServiceHelper().meInQueues();
//    }
//
//    @Override
//    protected void onQueueCLick(int position) {
//        Intent intent = new Intent(getActivity(), QueueViewerActivity.class);
//        intent.putExtra(QueueViewerActivity.EXTRA_QUEUE_ID, queueList.get(position).getQid());
//        startActivity(intent);
//    }
//}
