package com.sudo.equeue.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sudo.equeue.NetBaseActivity;
import com.sudo.equeue.NetService;
import com.sudo.equeue.R;
//import com.sudo.equeue.fragments.FindQueueFragment;
//import com.sudo.equeue.fragments.LoginFragment;
//import com.sudo.equeue.fragments.MyQueuesFragment;
//import com.sudo.equeue.fragments.ProfileFragment;
import com.sudo.equeue.models.Queue;
import com.sudo.equeue.models.QueueList;
import com.sudo.equeue.models.User;
import com.sudo.equeue.utils.QueueApplication;
import com.sudo.equeue.utils.RVAdapter;

import java.util.ArrayList;
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

    private QueueList queueList;
    private List<Queue> queues;
    private RVAdapter adapter;
    private FrameLayout progressBarHolder;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("eQueue");
        }
//        prefs = getSharedPreferences(QueueApplication.APP_PREFS, Context.MODE_PRIVATE);

        if (savedInstanceState == null) {
            queueList = (QueueList) getIntent().getSerializableExtra(EXTRA_QUEUE_LIST);
        } else {
            queueList = (QueueList) savedInstanceState.getSerializable(SAVED_STATE_QUEUE_LIST);
        }
        queues = new ArrayList<>(queueList.getQueues());

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        adapter = new RVAdapter(queueList.getQueues(), this::onItemClick);
        rv.setAdapter(adapter);

        updateView();

        Button addQueue = (Button) findViewById(R.id.btn_add_queue);
        if(addQueue != null) {
            addQueue.setOnClickListener(v -> openBottomSheet());
        }

        progressBarHolder = (FrameLayout) findViewById(R.id.progress_overlay);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_queue_list);
        swipeRefreshLayout.setOnRefreshListener(() -> getMyQueuesRequestId = getServiceHelper().meInQueues());
    }

    private void onItemClick(Queue queue) {
//        getQueueRequestId = getServiceHelper().getQueue(queue.getQid());
//        loadingStart();
        Intent intent = new Intent(this, QueueActivity.class);
        intent.putExtra(QueueActivity.EXTRA_QUEUE, queue);
        startActivity(intent);
    }

    private void updateView() {
        View noQueuesView = findViewById(R.id.no_queues_view);
        View queueListView = findViewById(R.id.queue_list_view);
        if (queueList.getQueues().size() == 0) {
            noQueuesView.setVisibility(View.VISIBLE);
            queueListView.setVisibility(View.GONE);
        } else {
            noQueuesView.setVisibility(View.GONE);
            queueListView.setVisibility(View.VISIBLE);
        }
    }

    private void updateQueueList(QueueList queueList) {
        this.queueList = queueList;
        queues.clear();
        queues.addAll(queueList.getQueues());
        swipeRefreshLayout.setRefreshing(false);
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
            // search nearby queues
            mBottomSheetDialog.dismiss();
        });


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVED_STATE_QUEUE_LIST, queueList);
    }

    private void loadingStart() {
        AlphaAnimation inAnimation;
        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);
    }

    private void loadingStop() {
        AlphaAnimation outAnimation;
        outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);
        progressBarHolder.setAnimation(outAnimation);
        progressBarHolder.setVisibility(View.GONE);
    }

    @Override
    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
        if (requestId == getMyQueuesRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, obj -> updateQueueList((QueueList) obj), NetService.RETURN_QUEUE_LIST);
        }
    }

//    private void onAddCLick() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("What to do")
//                .setItems(new String[]{"QR", "жжж"}, (dialog, which) -> {
//
//                });
//        builder.create().show();
//    }
}
