package com.sudo.equeue.activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sudo.equeue.NetBaseActivity;
import com.sudo.equeue.NetService;
import com.sudo.equeue.R;
//import com.sudo.equeue.fragments.FindQueueFragment;
//import com.sudo.equeue.fragments.LoginFragment;
//import com.sudo.equeue.fragments.MyQueuesFragment;
//import com.sudo.equeue.fragments.ProfileFragment;
import com.sudo.equeue.WebSocketService;
import com.sudo.equeue.models.Queue;
import com.sudo.equeue.models.QueueList;
import com.sudo.equeue.push.MyGcmListenerService;
import com.sudo.equeue.utils.MultiSwipeRefreshLayout;
import com.sudo.equeue.utils.QueueApplication;
import com.sudo.equeue.utils.QueueListAdapter;
import com.sudo.equeue.utils.QueueListWrapper;

import java.util.List;

//import com.sudo.equeue.fragments.AboutFragment;
//import com.sudo.equeue.fragments.PrefsFragment;
//import com.sudo.equeue.fragments.SearchFormFragment;
//import com.sudo.equeue.fragments.SearchResultsFragment;
//import com.sudo.equeue.fragments.StartFragment;
//import com.sudo.equeue.utils.ThemeUtils;


public class MainActivity extends NetBaseActivity {

//    private SharedPreferences prefs;
//    private Button addButton;

    //    private int createUserRequestId = -1;
    public static final String EXTRA_QUEUE_LIST = QueueApplication.prefix + ".extra.queue_list";
    private static final String SAVED_STATE_QUEUE_LIST = QueueApplication.prefix + ".QueueAdminActivity.saved.queue_list";

    private int getMyQueuesRequestId = -1;
//    private int isInQueueRequestId = -1;
//    private Queue savedQueue;

//    private QueueList queueList;
//    private List<Queue> queues;
    private QueueListWrapper queues;
    private QueueListAdapter adapter;
    private List<Integer> passedQueues;
//    private FrameLayout progressBarHolder;
    private MultiSwipeRefreshLayout swipeRefreshLayout;
    private PushBroadcastReceiver pushBroadcastReceiver;
    private boolean isReceiverRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ========== Toolbar ============
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Очереди");
        }

        Intent i = getIntent();

//        ========== Queue List ============
        QueueList queueList;
        if (savedInstanceState == null) {
            queueList = (QueueList) i.getSerializableExtra(EXTRA_QUEUE_LIST);
        } else {
            queueList = (QueueList) savedInstanceState.getSerializable(SAVED_STATE_QUEUE_LIST);
        }
        if (queueList != null) {
            queues = new QueueListWrapper();
            queues.setQueueList(queueList);
//            queues = new ArrayList<>(queueList.getQueues());
        } else {
            throw new AssertionError("queulist is null");
//            queues = new ArrayList<>();
//            getMyQueuesRequestId = getServiceHelper().meInQueues();
        }

//        ========== Recycler View ============
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        adapter = new QueueListAdapter(queues, this::onItemClick);
        rv.setAdapter(adapter);

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                if (queues.getQueueList().getQueues().get(pos).isPassed()) {
                    queues.getQueueList().getQueues().remove(pos);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int pos = viewHolder.getAdapterPosition();
                if (!queues.getQueueList().getQueues().get(pos).isPassed()) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }
        });

        itemTouchHelper.attachToRecyclerView(rv);


//        ========== Add button ============
        Button addQueue = (Button) findViewById(R.id.btn_add_queue);
        if (addQueue != null) {
            addQueue.setOnClickListener(v -> openBottomSheet());
        }

//        ========== Progress overlay ============
//        progressBarHolder = (FrameLayout) findViewById(R.id.progress_overlay);

//        ========== Swipe refresh layout ============
        swipeRefreshLayout = (MultiSwipeRefreshLayout) findViewById(R.id.refresh_queue_list);
        swipeRefreshLayout.setSwipeableChildren(R.id.queue_list_view, R.id.no_queues_view);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(() -> getMyQueuesRequestId = getServiceHelper().meInQueues());
        updateView();

//        ========== Broadcast receiver ============
        pushBroadcastReceiver = new PushBroadcastReceiver();
        registerCustomReceiver();

//        ========== WebSocket ============
//        ((QueueApplication) getApplication()).startWebSocketService();
//
//        Intent intent = new Intent(this, WebSocketService.class);
//        intent.setAction(WebSocketService.ACTION_BIND);
//        startService(intent);

//        === notif
        boolean isNotif = i.getBooleanExtra("isNot", false);
        if (isNotif) {
            Queue q = (Queue) i.getSerializableExtra("notQueue");
            if (q != null) {
                Intent intent = new Intent(this, QueueActivity.class);
                intent.putExtra("isNot", true);
                intent.putExtra(QueueActivity.EXTRA_QUEUE, q);
                startActivity(intent);
            }
        }
    }

    private void updateView() {
        View noQueuesView = findViewById(R.id.no_queues_view);
        View queueListView = findViewById(R.id.queue_list_view);
        if (queues.getQueueList().getQueues() == null || queues.getQueueList().getQueues().size() == 0) {
            noQueuesView.setVisibility(View.VISIBLE);
            queueListView.setVisibility(View.GONE);
        } else {
            noQueuesView.setVisibility(View.GONE);
            queueListView.setVisibility(View.VISIBLE);
        }
    }

    private void onItemClick(Queue queue) {
        Intent intent = new Intent(this, QueueActivity.class);
        intent.putExtra(QueueActivity.EXTRA_QUEUE, queue);
        startActivity(intent);

//        startService(new Intent(this, WebSocketService.class));
    }

    private void updateQueueList(QueueList queueList) {
        queues.setQueueList(queueList);
        swipeRefreshLayout.setRefreshing(false);
        updateView();
        adapter.notifyDataSetChanged();
    }

    private void updatePassedQueues(int qid) {
        for (int i = 0; i < queues.getQueueList().getQueues().size(); ++i) {
            if (queues.getQueueList().getQueues().get(i).getQid() == qid) {
                Queue tempQueue = queues.getQueueList().getQueues().get(i);
                queues.getQueueList().getQueues().remove(i);
                queues.getQueueList().getQueues().add(0, tempQueue);
                tempQueue.setPassed(true);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                openBottomSheet();
//                stopService(new Intent(this, WebSocketService.class));
//                Intent intent = new Intent(this, WebSocketService.class);
//                intent.setAction(WebSocketService.ACTION_MES);
//                startService(intent);
                break;
            default:
                break;
        }

        return true;
    }

    public void openBottomSheet() {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        TextView txtQR = (TextView)view.findViewById(R.id.qr_code);
        TextView txtID = (TextView)view.findViewById(R.id.enter_id);
        TextView txtNearby = (TextView)view.findViewById(R.id.findNearby);

        final Dialog mBottomSheetDialog = new Dialog (this,
                R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();


        txtQR.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, QRCodeActivity.class);
            startActivity(intent);
            mBottomSheetDialog.dismiss();
        });

        txtID.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FindQueueActivity.class);
            startActivity(intent);
            mBottomSheetDialog.dismiss();
        });

        txtNearby.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FindNearActivity.class);
            startActivity(intent);
            mBottomSheetDialog.dismiss();
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        registerCustomReceiver();
        getMyQueuesRequestId = getServiceHelper().meInQueues();
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(pushBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        QueueList queueList = new QueueList();
        queueList.setQueues(queues.getQueueList().getQueues());
        outState.putSerializable(SAVED_STATE_QUEUE_LIST, queueList);
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Intent intent = new Intent(this, WebSocketService.class);
//        intent.setAction(WebSocketService.ACTION_UNBIND);
//        startService(intent);
//    }

    private void registerCustomReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(pushBroadcastReceiver,
                    new IntentFilter(MyGcmListenerService.ACTION_PUSH_INCOMING));
            isReceiverRegistered = true;
        }
    }

    //    private void loadingStart() {
//        AlphaAnimation inAnimation;
//        inAnimation = new AlphaAnimation(0f, 1f);
//        inAnimation.setDuration(200);
//        progressBarHolder.setAnimation(inAnimation);
//        progressBarHolder.setVisibility(View.VISIBLE);
//    }

//    private void loadingStop() {
//        AlphaAnimation outAnimation;
//        outAnimation = new AlphaAnimation(1f, 0f);
//        outAnimation.setDuration(200);
//        progressBarHolder.setAnimation(outAnimation);
//        progressBarHolder.setVisibility(View.GONE);
//    }

    @Override
    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
        if (requestId == getMyQueuesRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, NetService.RETURN_QUEUE_LIST, obj -> updateQueueList((QueueList) obj), null);
        }
//        else if (requestId == isInQueueRequestId) {
//            getServiceHelper().handleResponse(this, resultCode, data, obj -> openQueue((IsInModel) obj), NetService.RETURN_IS_IN);
//        }
    }

    public class PushBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Push receiver", "Got it");
//            String type = intent.getStringExtra(WebSocketService.EXTRA_QUEUE_CHANGE_TYPE);
//            String action = intent.getStringExtra(WebSocketService.EXTRA_QUEUE_CHANGE_ACTION);
            int queueId = intent.getIntExtra(MyGcmListenerService.EXTRA_QUEUE_ID, -1);
            if (queueId != -1) {
                updatePassedQueues(queueId);
            }

        }
    }
}
