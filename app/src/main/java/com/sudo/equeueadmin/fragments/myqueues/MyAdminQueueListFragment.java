package com.sudo.equeueadmin.fragments.myqueues;//package com.sudo.equeue.fragments.myqueues;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.sudo.equeue.R;
//import com.sudo.equeue.activities.QueueAdminActivity;
//import com.sudo.equeue.utils.QueueApplication;
//
//public class MyAdminQueueListFragment extends RefreshableQueueListAbstractFragment {
//
//    public static final String TAG = QueueApplication.prefix + ".fragments.MyAdminQueueListFragment";
//    public static final String TAB_NAME = "Admining";
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_refreshable_queue_list, container, false);
//    }
//
//    @Override
//    protected int makeRequest() {
//        return getServiceHelper().myQueues();
//    }
//
//    @Override
//    protected void onQueueCLick(int position) {
//        Intent intent = new Intent(getActivity(), QueueAdminActivity.class);
//        intent.putExtra(QueueAdminActivity.EXTRA_IS_NEW_QUEUE, false);
//        intent.putExtra(QueueAdminActivity.EXTRA_QUEUE_ID, queueList.get(position).getQid());
//        startActivity(intent);
//    }
//}
