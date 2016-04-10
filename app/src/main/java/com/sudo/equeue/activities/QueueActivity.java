package com.sudo.equeue.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sudo.equeue.NetBaseActivity;
import com.sudo.equeue.NetService;
import com.sudo.equeue.R;
import com.sudo.equeue.models.Queue;
import com.sudo.equeue.utils.QueueApplication;
import com.sudo.equeue.utils.StaticSwipeRefreshLayout;

import java.util.Random;

public class QueueActivity extends NetBaseActivity implements OnMapReadyCallback {

    public static final String EXTRA_QUEUE = QueueApplication.prefix + ".extra.queue";

    private int joinQueueRequestId = -1;
    private int getQueueRequestId = -1;
    private int leaveQueueRequestId = -1;

    private Queue queue;
    private Button joinButton;
    private ViewGroup ticketView;
    private StaticSwipeRefreshLayout swipeRefreshLayout;
    private GoogleMap mMap;
    private Marker mMapMaker;

//    private ProgressBar toolbarProgressBar;
//    private ProgressBar statsInQueueProgressbar;
//    private ProgressBar statsBeforeProgressbar;
//    private ProgressBar statsTimeProgressbar;
    private ProgressBar buttonProgressbar;

    private TextView ticketNum;
    private TextView statsInQueue;
    private TextView statsBefore;
    private TextView statsTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);
//        overridePendingTransition(R.anim.open_slide_in, R.anim.open_slide_out);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowTitleEnabled(false);
//        }

//        toolbarProgressBar = (ProgressBar) toolbar.findViewById(R.id.toolbar_loader);
//        toolbarProgressBar.setVisibility(View.VISIBLE);

        buttonProgressbar = (ProgressBar) findViewById(R.id.button_loader);

//        statsInQueueProgressbar = (ProgressBar) findViewById(R.id.stats_in_queue_loader);
//        statsBeforeProgressbar = (ProgressBar) findViewById(R.id.stats_before_loader);
//        statsTimeProgressbar = (ProgressBar) findViewById(R.id.stats_time_loader);

        if (savedInstanceState == null) {
            queue = (Queue) getIntent().getSerializableExtra(EXTRA_QUEUE);
            if (queue == null) {
                throw new AssertionError("No queue in intent");
            }
            getQueueRequestId = getServiceHelper().getQueue(queue.getQid());
        } else {
//            TODO: restore from savedInstanceState
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(queue.getName());
        }

//        joinButton.setEnabled(false);
//        joinButton.setText("");

        swipeRefreshLayout = (StaticSwipeRefreshLayout) findViewById(R.id.refresh_queue_list);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (queue != null) {
                getQueueRequestId = getServiceHelper().getQueue(queue.getQid());
            }
        });

        ticketView = (ViewGroup) findViewById(R.id.ticket);
        ticketNum = (TextView) findViewById(R.id.ticket_num);
        statsInQueue = (TextView) findViewById(R.id.stats_in_queue);
        statsBefore = (TextView) findViewById(R.id.stats_before);
        statsTime = (TextView) findViewById(R.id.stats_time_left);

        statsInQueue.setText(Integer.toString(queue.getUsersQuantity()));
        statsBefore.setText(Integer.toString(queue.getInFront()));
        statsTime.setText(Integer.toString(queue.getWaitTime()));
        ticketNum.setText(Integer.toString(queue.getInFront() + 1));

        joinButton = (Button) findViewById(R.id.btn_join_queue);
        if (queue.IsIn()) {
            joinButton.setText("Покинуть");
            joinButton.setOnClickListener((v) -> leaveQueue());

            ticketView.setVisibility(View.VISIBLE);
        } else {
            joinButton.setOnClickListener((v) -> joinQueue());
        }

        MapView mapView = (MapView) findViewById(R.id.lite_map);
        mapView.onCreate(null);
        mapView.getMapAsync(this);
        mapView.setVisibility(View.GONE);

//        LatLng place = queue.getLatLng();
//        if (place != null) {
//            LatLng move_place = new LatLng(place.latitude + 0.0001, place.longitude);
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(move_place, 15f));
//            mMapMaker = mMap.addMarker(new MarkerOptions().position(place));
//            findViewById(R.id.lite_map).setVisibility(View.VISIBLE);
//        }
    }

    private void getQueueSuccess(Queue newQueue) {
        this.queue = newQueue;
        swipeRefreshLayout.setRefreshing(false);

//        toolbarProgressBar.setVisibility(View.GONE);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayShowTitleEnabled(true);
//            getSupportActionBar().setTitle(queue.getName());
//        }

//        statsInQueueProgressbar.setVisibility(View.GONE);
//        statsInQueue.setVisibility(View.VISIBLE);
        statsInQueue.setText(Integer.toString(queue.getUsersQuantity()));

//        statsBeforeProgressbar.setVisibility(View.GONE);
//        statsBefore.setVisibility(View.VISIBLE);
        statsBefore.setText(Integer.toString(queue.getInFront()));
        ticketNum.setText(Integer.toString(queue.getInFront() + 1));

//        statsTimeProgressbar.setVisibility(View.GONE);
//        statsTime.setVisibility(View.VISIBLE);
        statsTime.setText(Integer.toString(queue.getWaitTime()));

//        buttonProgressbar.setVisibility(View.GONE);
//        joinButton.setEnabled(true);
//        joinButton.setText("Присоединиться");
//        joinButton.setOnClickListener((v) -> joinQueue());

        LatLng place = this.queue.getLatLng();
        if (place != null) {
            LatLng move_place = new LatLng(place.latitude + 0.0001, place.longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(move_place, 15f));
            mMapMaker = mMap.addMarker(new MarkerOptions().position(place));
            findViewById(R.id.lite_map).setVisibility(View.VISIBLE);
        }

    }

    private void joinQueue() {
        joinButton.setEnabled(false);
        joinButton.setText("");
        buttonProgressbar.setVisibility(View.VISIBLE);

        joinQueueRequestId = getServiceHelper().joinQueue(queue.getQid());
    }

    private void joinSuccess() {
        buttonProgressbar.setVisibility(View.GONE);
        joinButton.setEnabled(true);
        joinButton.setText("Покинуть");
        joinButton.setOnClickListener((v) -> leaveQueue());

        Animation bottomUp = AnimationUtils.loadAnimation(QueueActivity.this, R.anim.bottom_up);
        ticketView.startAnimation(bottomUp);
        ticketView.setVisibility(View.VISIBLE);
    }

    private void joinFail() {
        buttonProgressbar.setVisibility(View.GONE);
        joinButton.setEnabled(true);
        joinButton.setText("Присоединиться");
    }

    private void leaveQueue() {
        joinButton.setEnabled(false);
        joinButton.setText("");
        buttonProgressbar.setVisibility(View.VISIBLE);

        leaveQueueRequestId = getServiceHelper().leaveQueue(queue.getQid());
    }

    private void leaveSuccess() {
        AlphaAnimation fade_out = new AlphaAnimation(1.0f, 0.0f);
        fade_out.setDuration(500);
        fade_out.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation arg0) {
            }

            public void onAnimationRepeat(Animation arg0) {
            }

            public void onAnimationEnd(Animation arg0) {
                ticketView.setVisibility(View.GONE);
            }
        });
        ticketView.startAnimation(fade_out);

        buttonProgressbar.setVisibility(View.GONE);
        joinButton.setEnabled(true);
        joinButton.setText("Присоединиться");
        joinButton.setOnClickListener((v) -> joinQueue());
    }

    private void leaveFail() {
        buttonProgressbar.setVisibility(View.GONE);
        joinButton.setEnabled(true);
        joinButton.setText("Покинуть");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
//            case R.id.show_map:
////                TODO: start map activity
//                break;
        }

        return true;
    }

    @Override
    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
        if (requestId == joinQueueRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, null, obj -> joinSuccess(), this::joinFail);
        } else if (requestId == getQueueRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, NetService.RETURN_QUEUE, obj -> getQueueSuccess((Queue) obj), null);
        } else if (requestId == leaveQueueRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, null, obj -> leaveSuccess(), this::leaveFail);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.close_slide_in, R.anim.close_slide_out);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.queue_page_menu, menu);
//        return true;
//    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setMapToolbarEnabled(false);
        mUiSettings.setAllGesturesEnabled(false);
    }
}
