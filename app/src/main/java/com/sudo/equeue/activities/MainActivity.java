package com.sudo.equeue.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.sudo.equeue.models.IsInModel;
import com.sudo.equeue.models.Queue;
import com.sudo.equeue.models.QueueList;
import com.sudo.equeue.utils.MultiSwipeRefreshLayout;
import com.sudo.equeue.utils.QueueApplication;
import com.sudo.equeue.utils.QueueListAdapter;

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
//    private int isInQueueRequestId = -1;
//    private Queue savedQueue;

//    private QueueList queueList;
    private List<Queue> queues;
    private QueueListAdapter adapter;
//    private FrameLayout progressBarHolder;
    private MultiSwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ========== Toolbar ============
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("eQueue");
        }

//        ========== Queue List ============
        QueueList queueList;
        if (savedInstanceState == null) {
            queueList = (QueueList) getIntent().getSerializableExtra(EXTRA_QUEUE_LIST);
        } else {
            queueList = (QueueList) savedInstanceState.getSerializable(SAVED_STATE_QUEUE_LIST);
        }
        if (queueList != null) {
            queues = new ArrayList<>(queueList.getQueues());
        } else {
            queues = new ArrayList<>();
            getMyQueuesRequestId = getServiceHelper().meInQueues();
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

//        ========== Location ============
        int minTime = 5000;
        float minDistance = 5;
        MyLocationListener myLocListener = new MyLocationListener();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setSpeedRequired(false);

        String bestProvider = locationManager.getBestProvider(criteria, false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }

        locationManager.requestLocationUpdates(bestProvider, minTime, minDistance, myLocListener);
//        ===============================

        updateView();
    }

    private void updateView() {
        View noQueuesView = findViewById(R.id.no_queues_view);
        View queueListView = findViewById(R.id.queue_list_view);
        if (queues == null || queues.size() == 0) {
            noQueuesView.setVisibility(View.VISIBLE);
            queueListView.setVisibility(View.GONE);
        } else {
            noQueuesView.setVisibility(View.GONE);
            queueListView.setVisibility(View.VISIBLE);
        }
    }

    private void onItemClick(int qid) {
        Intent intent = new Intent(this, QueueActivity.class);
        intent.putExtra(QueueActivity.EXTRA_QUEUE_ID, qid);
        startActivity(intent);
//        loadingStart();
//        isInQueueRequestId = getServiceHelper().isIn(qid);
//        savedQueue = queue;
//        Intent intent = new Intent(this, QueueActivity.class);
//        intent.putExtra(QueueActivity.EXTRA_QUEUE_ID, queue);
//        startActivity(intent);
    }

//    private void openQueue(IsInModel isIn) {
//        savedQueue.setIsIn(isIn.getStatus());
//        loadingStop();
//        Intent intent = new Intent(this, QueueActivity.class);
//        intent.putExtra(QueueActivity.EXTRA_QUEUE_ID, savedQueue);
//        startActivity(intent);
//    }



    private void updateQueueList(QueueList queueList) {
        queues.clear();
        queues.addAll(queueList.getQueues());
        swipeRefreshLayout.setRefreshing(false);
        updateView();
        adapter.notifyDataSetChanged();
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
    protected void onResume() {
        super.onResume();
        getMyQueuesRequestId = getServiceHelper().meInQueues();
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        QueueList queueList = new QueueList();
        queueList.setQueues(queues);
        outState.putSerializable(SAVED_STATE_QUEUE_LIST, queueList);
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
            getServiceHelper().handleResponse(this, resultCode, data, obj -> updateQueueList((QueueList) obj), NetService.RETURN_QUEUE_LIST);
        }
//        else if (requestId == isInQueueRequestId) {
//            getServiceHelper().handleResponse(this, resultCode, data, obj -> openQueue((IsInModel) obj), NetService.RETURN_IS_IN);
//        }
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            if (loc != null) {
//                Toast.makeText(MainActivity.this,
//                        "lat: " + String.valueOf(loc.getLatitude()) + ", long: " + String.valueOf(loc.getLongitude()),
//                        Toast.LENGTH_LONG).show();
//                TODO: save coords somewhere to get when needed
            }
        }

        @Override
        public void onProviderDisabled(String arg0) {
            Toast.makeText(MainActivity.this, "Вы отключили систему навигации", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onProviderEnabled(String arg0) {
            // Do something here if you would like to know when the provider is enabled by the user
        }

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
            // Do something here if you would like to know when the provider status changes
        }
    }
}
