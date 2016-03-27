package com.sudo.equeue.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;

import com.sudo.equeue.NetBaseActivity;
import com.sudo.equeue.R;
import com.sudo.equeue.models.Queue;
import com.sudo.equeue.utils.QueueApplication;

public class QueueActivity extends NetBaseActivity {

    public static final String EXTRA_QUEUE = QueueApplication.prefix + ".extra.queue";

    private int joinQueueRequestId = -1;

    private Queue queue;
    private Button joinButton;
    private FrameLayout progressBarHolder;

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

        joinButton = (Button) findViewById(R.id.btn_join_queue);
        if(joinButton != null) {
            joinButton.setOnClickListener(v -> joinQueue());
        }

        progressBarHolder = (FrameLayout) findViewById(R.id.progress_overlay);
    }

    private void joinQueue() {
        joinButton.setVisibility(View.GONE);
        joinQueueRequestId = getServiceHelper().joinQueue(queue.getQid());
        loadingStart();
    }

    private void joinSuccess() {
        loadingStop();
        Animation bottomUp = AnimationUtils.loadAnimation(QueueActivity.this,
                R.anim.bottom_up);
        ViewGroup hiddenPanel = (ViewGroup) findViewById(R.id.ticket);
        if (hiddenPanel != null) {
            hiddenPanel.startAnimation(bottomUp);
            hiddenPanel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
        if (requestId == joinQueueRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, obj -> joinSuccess(), null);
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
