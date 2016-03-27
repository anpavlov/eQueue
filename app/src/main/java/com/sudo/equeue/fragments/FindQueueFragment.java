//package com.sudo.equeue.fragments;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.app.AppCompatActivity;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.inputmethod.EditorInfo;
//import android.widget.AbsListView;
//import android.widget.EditText;
//
//import com.sudo.equeue.R;
//import com.sudo.equeue.activities.QueueViewerActivity;
//import com.sudo.equeue.fragments.myqueues.QueueListAbstractFragment;
//import com.sudo.equeue.models.QueueList;
//import com.sudo.equeue.utils.QueueApplication;
//
//public class FindQueueFragment extends QueueListAbstractFragment {
//
//    public static final String TAG = QueueApplication.prefix + ".fragments.QueueListAbstractFragment";
//
//    private EditText searchField;
//    private SwipeRefreshLayout swipeRefreshLayout;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_find_queue, container, false);
//    }
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) {
//            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Поиск");
//        }
//
//        searchField = (EditText) view.findViewById(R.id.field_search);
//        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_find);
//
//        searchField.setOnKeyListener((v, keyCode, event) -> {
//            if (keyCode == EditorInfo.IME_ACTION_SEARCH || keyCode == EditorInfo.IME_ACTION_DONE
//                    || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
//                sendQuery();
//                return true;
//            }
//            return false;
//        });
//
//        swipeRefreshLayout.setColorSchemeColors(R.color.colorAccent);
//        swipeRefreshLayout.setOnRefreshListener(() -> {
//            refresh();
//        });
//
//        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) { }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
//            {
//                int topRowVerticalPosition = (getListView() == null || getListView().getChildCount() == 0) ? 0 : getListView().getChildAt(0).getTop();
//                swipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
//            }
//        });
//    }
//
//    private void refresh() {
//        getQueueRequestId = getServiceHelper().findQueue(searchField.getText().toString());
//    }
//
//    private void sendQuery() {
//        getQueueRequestId = getServiceHelper().findQueue(searchField.getText().toString());
//    }
//
//    @Override
//    public void updateQueueList(QueueList queueList) {
//        super.updateQueueList(queueList);
//        swipeRefreshLayout.setRefreshing(false);
//    }
//
//    @Override
//    protected int makeRequest() {
//        return getServiceHelper().findQueue(null);
//    }
//
//    @Override
//    protected void onQueueCLick(int position) {
//        Intent intent = new Intent(getActivity(), QueueViewerActivity.class);
//        intent.putExtra(QueueViewerActivity.EXTRA_QUEUE_ID, queueList.get(position).getQid());
//        startActivity(intent);
//    }
//}
