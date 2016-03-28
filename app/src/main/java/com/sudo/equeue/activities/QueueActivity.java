package com.sudo.equeue.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sudo.equeue.NetBaseActivity;
import com.sudo.equeue.NetService;
import com.sudo.equeue.R;
import com.sudo.equeue.models.Queue;
import com.sudo.equeue.utils.QueueApplication;

public class QueueActivity extends NetBaseActivity {

    public static final String EXTRA_QUEUE = QueueApplication.prefix + ".extra.queue";

    private int joinQueueRequestId = -1;
    private int getQueueRequestId = -1;

    private Queue queue;
    private Button joinButton;
    private FrameLayout progressBarHolder;
    private RelativeLayout queueInfo;
    private ViewGroup hiddenPanel;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        if (savedInstanceState == null) {
            queue = (Queue) getIntent().getSerializableExtra(EXTRA_QUEUE);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(queue.getName());
        }

        hiddenPanel = (ViewGroup) findViewById(R.id.ticket);
        queueInfo = (RelativeLayout) findViewById(R.id.queue_stats);
        joinButton = (Button) findViewById(R.id.btn_join_queue);
        joinButton.setOnClickListener(v -> joinQueue());

        if (queue.isIn()) {
            joinButton.setVisibility(View.GONE);
            queueInfo.setVisibility(View.GONE);
            hiddenPanel.setVisibility(View.VISIBLE);
        }

        refreshQueueData();

        progressBarHolder = (FrameLayout) findViewById(R.id.progress_overlay);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_queue_list);
        swipeRefreshLayout.setOnRefreshListener(() -> getQueueRequestId = getServiceHelper().getQueue(queue.getQid()));
    }

    private void getQueueSuccess(Queue queue) {
        this.queue = queue;
        swipeRefreshLayout.setRefreshing(false);
        refreshQueueData();
    }

    private void refreshQueueData() {
        if (queue.isIn()) {
            ((TextView) findViewById(R.id.ticket_num)).setText(Integer.toString(2));
            ((TextView) findViewById(R.id.ticket_time)).setText("28.03.2016 18:06");
        } else {
            ((TextView) findViewById(R.id.num_in_queue)).setText(Integer.toString(queue.getUsersQuantity()));
            ((TextView) findViewById(R.id.num_passed)).setText(Integer.toString(2));
            ((TextView) findViewById(R.id.time_wait)).setText(Integer.toString(15) + " минут");
        }
    }

    private void joinQueue() {
        joinQueueRequestId = getServiceHelper().joinQueue(queue.getQid());
        loadingStart();
    }

    private void joinSuccess() {
        loadingStop();
        joinButton.setVisibility(View.GONE);
        queueInfo.setVisibility(View.GONE);
        Animation bottomUp = AnimationUtils.loadAnimation(QueueActivity.this, R.anim.bottom_up);
        hiddenPanel.startAnimation(bottomUp);
        hiddenPanel.setVisibility(View.VISIBLE);
        queue.setIsIn(true);
        refreshQueueData();
    }

    @Override
    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
        if (requestId == joinQueueRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, obj -> joinSuccess(), null);
        } else if (requestId == getQueueRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, obj -> getQueueSuccess((Queue) obj), NetService.RETURN_QUEUE);
        }
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
}
