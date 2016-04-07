package com.sudo.equeue.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.sudo.equeue.NetBaseActivity;
import com.sudo.equeue.NetService;
import com.sudo.equeue.R;
import com.sudo.equeue.models.Queue;

public class FindQueueActivity extends NetBaseActivity {

    private int searchQueueRequestId = -1;
    private FrameLayout progressBarHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_queue);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Поиск очереди");
        }

        Button buttonSearch = (Button) findViewById(R.id.btn_find_queue);
        if(buttonSearch != null) {
            buttonSearch.setOnClickListener(v -> searchForQueue());
        }

        progressBarHolder = (FrameLayout) findViewById(R.id.progress_overlay);
    }

//    private void loadingStart() {
//        AlphaAnimation inAnimation;
//        inAnimation = new AlphaAnimation(0f, 1f);
//        inAnimation.setDuration(200);
//        progressBarHolder.setAnimation(inAnimation);
//        progressBarHolder.setVisibility(View.VISIBLE);
//    }
//
//    private void loadingStop() {
//        AlphaAnimation outAnimation;
//        outAnimation = new AlphaAnimation(1f, 0f);
//        outAnimation.setDuration(200);
//        progressBarHolder.setAnimation(outAnimation);
//        progressBarHolder.setVisibility(View.GONE);
//    }

    private void searchForQueue() {
        String queueHash = ((EditText) findViewById(R.id.queue_hash_field)).getText().toString();
        Integer aa = Integer.parseInt(queueHash);
        searchQueueRequestId = getServiceHelper().getQueue(aa == null ? 1 : aa);
//        loadingStart();
    }

    private void gotQueue(Queue queue) {
//        loadingStop();

        if (queue != null) {
            Intent intent = new Intent(this, QueueActivity.class);
            intent.putExtra(QueueActivity.EXTRA_QUEUE_ID, queue.getQid());
            startActivity(intent);
            finish();
        }
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
        if (requestId == searchQueueRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, obj -> gotQueue((Queue) obj), NetService.RETURN_QUEUE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}
