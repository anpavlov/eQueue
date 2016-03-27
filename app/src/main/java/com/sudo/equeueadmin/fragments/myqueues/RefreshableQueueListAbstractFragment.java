package com.sudo.equeueadmin.fragments.myqueues;//package com.sudo.equeue.fragments.myqueues;
//
//import android.os.Bundle;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.sudo.equeue.R;
//import com.sudo.equeue.models.QueueList;
//
//public abstract class RefreshableQueueListAbstractFragment extends QueueListAbstractFragment {
//
//    private SwipeRefreshLayout swipeRefreshLayout;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_refreshable_queue_list, container, false);
//    }
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_queue_list);
//        swipeRefreshLayout.setOnRefreshListener(() -> getQueueRequestId = makeRequest());
//    }
//
//    @Override
//    public void updateQueueList(QueueList queues) {
//        super.updateQueueList(queues);
//        swipeRefreshLayout.setRefreshing(false);
//    }
//}
