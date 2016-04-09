package com.sudo.equeue.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sudo.equeue.NetBaseActivity;
import com.sudo.equeue.NetService;
import com.sudo.equeue.R;
import com.sudo.equeue.models.Queue;
import com.sudo.equeue.utils.QueueApplication;
import com.sudo.equeue.utils.StaticSwipeRefreshLayout;

import java.util.Random;

public class QueueActivity extends NetBaseActivity {

    public static final String EXTRA_QUEUE_ID = QueueApplication.prefix + ".extra.queue";

    private int joinQueueRequestId = -1;
    private int getQueueRequestId = -1;

    private Queue queue;
    private Button joinButton;
    private ViewGroup ticketView;
    private StaticSwipeRefreshLayout swipeRefreshLayout;

    private ProgressBar toolbarProgressBar;
    private ProgressBar statsInQueueProgressbar;
    private ProgressBar statsBeforeProgressbar;
    private ProgressBar statsTimeProgressbar;
    private ProgressBar buttonProgressbar;

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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


        toolbarProgressBar = (ProgressBar) toolbar.findViewById(R.id.toolbar_loader);
        toolbarProgressBar.setVisibility(View.VISIBLE);

        buttonProgressbar = (ProgressBar) findViewById(R.id.button_loader);

        statsInQueueProgressbar = (ProgressBar) findViewById(R.id.stats_in_queue_loader);
        statsBeforeProgressbar = (ProgressBar) findViewById(R.id.stats_before_loader);
        statsTimeProgressbar = (ProgressBar) findViewById(R.id.stats_time_loader);

        if (savedInstanceState == null) {
            int qid = getIntent().getIntExtra(EXTRA_QUEUE_ID, -1);
            if (qid == -1) {
                throw new AssertionError("No queue id in intent");
            }
            getQueueRequestId = getServiceHelper().getQueue(qid);
        }

        joinButton = (Button) findViewById(R.id.btn_join_queue);
        joinButton.setEnabled(false);
        joinButton.setText("");

        swipeRefreshLayout = (StaticSwipeRefreshLayout) findViewById(R.id.refresh_queue_list);
        swipeRefreshLayout.setOnRefreshListener(() -> getQueueRequestId = getServiceHelper().getQueue(queue.getQid()));

        ticketView = (ViewGroup) findViewById(R.id.ticket);
        statsInQueue = (TextView) findViewById(R.id.stats_in_queue);
        statsBefore = (TextView) findViewById(R.id.stats_before);
        statsTime = (TextView) findViewById(R.id.stats_time_left);
    }

    private void getQueueSuccess(Queue newQueue) {
        this.queue = newQueue;
        swipeRefreshLayout.setRefreshing(false);

        toolbarProgressBar.setVisibility(View.GONE);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(queue.getName());
        }

        statsInQueueProgressbar.setVisibility(View.GONE);
        statsInQueue.setVisibility(View.VISIBLE);
        statsInQueue.setText(Integer.toString(queue.getUsersQuantity()));

        statsBeforeProgressbar.setVisibility(View.GONE);
        statsBefore.setVisibility(View.VISIBLE);
        statsBefore.setText(Integer.toString((new Random()).nextInt(50)));

        statsTimeProgressbar.setVisibility(View.GONE);
        statsTime.setVisibility(View.VISIBLE);
        statsTime.setText(Integer.toString((new Random()).nextInt(59)));

        buttonProgressbar.setVisibility(View.GONE);
        joinButton.setEnabled(true);
        joinButton.setText("Присоединиться");
        joinButton.setOnClickListener((v) -> joinQueue());
    }

//    private void refreshQueueData() {
//        if (queue.isIn()) {
//            ((TextView) findViewById(R.id.ticket_num)).setText(Integer.toString(2));
//            ((TextView) findViewById(R.id.ticket_time)).setText("28.03.2016 18:06");
//        } else {
//            ((TextView) findViewById(R.id.stats_in_queue)).setText(Integer.toString(queue.getUsersQuantity()));
//            ((TextView) findViewById(R.id.stats_before)).setText(Integer.toString(2));
//            ((TextView) findViewById(R.id.stats_time_left)).setText(Integer.toString(15));
//        }
//    }

    private void joinQueue() {
        joinButton.setEnabled(false);
        joinButton.setText("");
        buttonProgressbar.setVisibility(View.VISIBLE);

        joinQueueRequestId = getServiceHelper().joinQueue(queue.getQid());
    }

    private void leaveQueue() {

//        joinButton.setEnabled(false);
//        joinButton.setText("");
//        buttonProgressbar.setVisibility(View.VISIBLE);
//
//        joinQueueRequestId = getServiceHelper().leaveQueue(queue.getQid());
    }

    private void leaveSuccess() {
        buttonProgressbar.setVisibility(View.GONE);
        joinButton.setEnabled(true);
        joinButton.setText("Присоединиться");
        joinButton.setOnClickListener((v) -> joinQueue());
    }

    private void joinSuccess() {
        ticketView.animate()
                .alpha(0f)
                .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }
                });

        buttonProgressbar.setVisibility(View.GONE);
        joinButton.setEnabled(true);
        joinButton.setText("Покинуть");
        joinButton.setOnClickListener((v) -> leaveQueue());



//        loadingStop();
//        joinButton.setVisibility(View.GONE);
////        queueInfo.setVisibility(View.GONE);
        Animation bottomUp = AnimationUtils.loadAnimation(QueueActivity.this, R.anim.bottom_up);
        ticketView.startAnimation(bottomUp);
        ticketView.setVisibility(View.VISIBLE);
//        queue.setIsIn(true);
//        refreshQueueData();
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

    @Override
    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
        if (requestId == joinQueueRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, obj -> joinSuccess(), null);
        } else if (requestId == getQueueRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, obj -> getQueueSuccess((Queue) obj), NetService.RETURN_QUEUE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.close_slide_in, R.anim.close_slide_out);
    }
}
