package com.sudo.equeue.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.sudo.equeue.NetBaseActivity;
import com.sudo.equeue.NetService;
import com.sudo.equeue.R;
import com.sudo.equeue.models.Queue;
import com.sudo.equeue.models.QueueList;
import com.sudo.equeue.utils.CustomSnackBar;
import com.sudo.equeue.utils.MultiSwipeRefreshLayout;
import com.sudo.equeue.utils.QueueApplication;
import com.sudo.equeue.utils.QueueListAdapter;
import com.sudo.equeue.utils.QueueListWrapper;

import java.util.ArrayList;
import java.util.List;


public class FindNearActivity extends NetBaseActivity {

    private static final String SAVED_STATE_QUEUE_LIST = QueueApplication.prefix + ".QueueAdminActivity.saved.queue_list";

    private int getNearQueuesRequestId = -1;
    private QueueListWrapper queues;
    private QueueListAdapter adapter;
    private MultiSwipeRefreshLayout swipeRefreshLayout;
    private String coords;
    private ProgressBar buttonProgressBar;
    private LocationManager locationManager;
    private MyLocationListener myLocListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_near);

        buttonProgressBar = (ProgressBar) findViewById(R.id.button_loader);
        findViewById(R.id.no_queues_view).setVisibility(View.GONE);

        //========== Toolbar ============
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Очереди рядом");
        }

        //========== Queue List ============
        QueueList queueList;
        if (savedInstanceState == null) {
            queueList = new QueueList();
            queueList.setQueues(new ArrayList<>());
        } else {
            queueList = (QueueList) savedInstanceState.getSerializable(SAVED_STATE_QUEUE_LIST);
            buttonProgressBar.setVisibility(View.GONE);
        }
        queues = new QueueListWrapper();
        queues.setQueueList(queueList);
        if (savedInstanceState != null) updateView();


        //========== Recycler View ============
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


        //========== Swipe refresh layout ============
        swipeRefreshLayout = (MultiSwipeRefreshLayout) findViewById(R.id.refresh_queue_list);
        swipeRefreshLayout.setSwipeableChildren(R.id.queue_list_view, R.id.no_queues_view);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(() ->
        {
            if (coords != null) getNearQueuesRequestId = getServiceHelper().getNearQueues(coords);
        });


        //========== Location ============
        myLocListener = new MyLocationListener();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        long minTime = System.currentTimeMillis() - 30 * 60 * 1000;
        float minDistance = 100;
        float bestAccuracy = 2000;
        Location bestResult = null;
        long bestTime = 0;

        List<String> matchingProviders = locationManager.getAllProviders();
        for (String provider: matchingProviders) {
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                float accuracy = location.getAccuracy();
                long time = location.getTime();

                if ((time > minTime && accuracy < bestAccuracy)) {
                    bestResult = location;
                    bestAccuracy = accuracy;
                    bestTime = time;
                }
                else if (time < minTime &&
                        bestAccuracy == Float.MAX_VALUE && time > bestTime){
                    bestResult = location;
                    bestTime = time;
                }
            }
        }

        if(bestResult != null) {
            coords = String.valueOf(bestResult.getLatitude()) + ',' + String.valueOf(bestResult.getLongitude());
            getNearQueuesRequestId = getServiceHelper().getNearQueues(coords);
            return;
        }




        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setSpeedRequired(true);

        String bestProvider = locationManager.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }

//        Location lastKnownLocation = locationManager.getLastKnownLocation(bestProvider);
//        if(lastKnownLocation != null) {
//            coords = String.valueOf(lastKnownLocation.getLatitude()) + ',' + String.valueOf(lastKnownLocation.getLongitude());
//            getNearQueuesRequestId = getServiceHelper().getNearQueues(coords);
//        }

        locationManager.requestLocationUpdates(bestProvider, minTime, minDistance, myLocListener);
    }

    private void updateView() {
        View noQueuesView = findViewById(R.id.no_queues_view);
        View queueListView = findViewById(R.id.queue_list_view);
        View loader = findViewById(R.id.button_loader);
        loader.setVisibility(View.GONE);
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return true;
    }

    private void updateQueueList(QueueList queueList) {
        queues.setQueueList(queueList);
        swipeRefreshLayout.setRefreshing(false);
        updateView();
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (coords != null) getNearQueuesRequestId = getServiceHelper().getNearQueues(coords);
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.locationManager = null;
        this.myLocListener = null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        QueueList queueList = new QueueList();
        queueList.setQueues(queues.getQueueList().getQueues());
        outState.putSerializable(SAVED_STATE_QUEUE_LIST, queueList);
    }

    @Override
    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
        if (requestId == getNearQueuesRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, NetService.RETURN_QUEUE_LIST, obj -> updateQueueList((QueueList) obj), null);
        }
    }

    public void removeLocationListener() {
        if(this.locationManager != null && this.myLocListener != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                this.locationManager.removeUpdates(this.myLocListener);
                this.myLocListener = null;
            }
        }
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            LinearLayout findNearLayout = (LinearLayout) findViewById(R.id.findNearLayout);
            if(findNearLayout != null) {
                CustomSnackBar.show(findNearLayout, "Получили координаты, ищем очереди");
            }
            if (loc != null) {
                coords = String.valueOf(loc.getLatitude()) + ',' + String.valueOf(loc.getLongitude());
                getNearQueuesRequestId = getServiceHelper().getNearQueues(coords);
                FindNearActivity.this.removeLocationListener();
            }
        }

        @Override
        public void onProviderDisabled(String arg0) {
            LinearLayout findNearLayout = (LinearLayout) findViewById(R.id.findNearLayout);
            if(findNearLayout != null) {
                CustomSnackBar.show(findNearLayout, "Вы отключили систему навигации");
            }
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
