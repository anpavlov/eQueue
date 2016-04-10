package com.sudo.equeue.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sudo.equeue.NetBaseActivity;
import com.sudo.equeue.NetService;
import com.sudo.equeue.R;
import com.sudo.equeue.models.Queue;

public class FindQueueActivity extends NetBaseActivity {

    private int searchQueueRequestId = -1;
    
    private ProgressBar buttonProgressBar;
    private Button buttonSearch;
    private EditText hashField;
//    private FrameLayout progressBarHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_queue);
        overridePendingTransition(R.anim.open_slide_in, R.anim.open_slide_out);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Поиск очереди");
        }

        hashField = (EditText) findViewById(R.id.queue_hash_field);
        buttonSearch = (Button) findViewById(R.id.btn_find_queue);
        buttonSearch.setOnClickListener(v -> searchForQueue());
        
        buttonProgressBar = (ProgressBar) findViewById(R.id.button_loader);
//        buttonProgressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

    }

    private void searchForQueue() {
        buttonSearch.setEnabled(false);
        buttonSearch.setText("");
        buttonProgressBar.setVisibility(View.VISIBLE);

        try {
            Integer qid = Integer.parseInt(hashField.getText().toString());
            searchQueueRequestId = getServiceHelper().getQueue(qid);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "It's not a queue id", Toast.LENGTH_LONG).show();
        }

    }

    private void gotQueue(Queue queue) {
        if (queue != null) {
            Intent intent = new Intent(this, QueueActivity.class);
            intent.putExtra(QueueActivity.EXTRA_QUEUE, queue);
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
            getServiceHelper().handleResponse(this, resultCode, data, NetService.RETURN_QUEUE, obj -> gotQueue((Queue) obj), () -> {
                buttonSearch.setEnabled(true);
                buttonSearch.setText("Открыть");
                buttonProgressBar.setVisibility(View.GONE);
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.close_slide_in, R.anim.close_slide_out);
    }
}
