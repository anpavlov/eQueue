package com.sudo.equeue.activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sudo.equeue.NetBaseActivity;
import com.sudo.equeue.NetService;
import com.sudo.equeue.R;
import com.sudo.equeue.models.Queue;
import com.sudo.equeue.models.User;
import com.sudo.equeue.utils.QueueApplication;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class QueueTerminalActivity extends NetBaseActivity {

    public static final String EXTRA_QUEUE_ID = QueueApplication.prefix + ".extra.queue_id";

    private int getQueueRequestId;
    private int getRefreshQueueRequestId;
    private int createUserRequestId;
    private int joinRequestId;

    private Queue queueInfo;
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_terminal);

        if (savedInstanceState == null) {
            getQueueRequestId = getServiceHelper().getQueue(getIntent().getIntExtra(EXTRA_QUEUE_ID, -1));
        }

        findViewById(R.id.btn_join).setOnClickListener(v -> join());
        findViewById(R.id.btn_hide).setOnClickListener(v -> hide());

        new Thread(() -> {
            // TODO Auto-generated method stub
            while (true) {
                try {
                    Thread.sleep(3000);
                    mHandler.post(() -> {
                        // TODO Auto-generated method stub
                        getRefreshQueueRequestId = getServiceHelper().getQueue(getIntent().getIntExtra(EXTRA_QUEUE_ID, -1));
                    });
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        }).start();

    }

    private void join() {
        createUserRequestId = getServiceHelper().createUser(null, null, null, false);
    }

    private void hide() {
        findViewById(R.id.number_lbl).setVisibility(View.INVISIBLE);
        findViewById(R.id.number_field).setVisibility(View.INVISIBLE);
        findViewById(R.id.code_lbl).setVisibility(View.INVISIBLE);
        findViewById(R.id.code_field).setVisibility(View.INVISIBLE);
        findViewById(R.id.btn_hide).setVisibility(View.INVISIBLE);
    }

    private void updateQueueView() {
        ((TextView) findViewById(R.id.name_field)).setText(queueInfo.getName());
        ((TextView) findViewById(R.id.description_field)).setText(queueInfo.getDescription());


        if (queueInfo.getUserlist() != null && !queueInfo.getUserlist().isEmpty()) {
            int count = queueInfo.getUserlist().size();
            int current = queueInfo.getUserlist().get(0);
            ((TextView) findViewById(R.id.count_field)).setText(String.valueOf(count));

            findViewById(R.id.current_lbl).setVisibility(View.VISIBLE);
            findViewById(R.id.current_field).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.current_field)).setText(String.valueOf(current));
        } else {
            findViewById(R.id.current_lbl).setVisibility(View.INVISIBLE);
            findViewById(R.id.current_field).setVisibility(View.INVISIBLE);
            ((TextView) findViewById(R.id.count_field)).setText("0");
        }
    }

    //    TODO: вынести инициализацию юзера в Application
    private void joinQueue(User user) {
        if (user != null && user.getToken() != null && !user.getToken().equals("")) {
            joinRequestId = getServiceHelper().joinQueueAnonym(queueInfo.getQid(), user.getToken());
            Random rnd = new Random();
            int code = 100 + rnd.nextInt(900);
            ((TextView) findViewById(R.id.number_field)).setText(String.valueOf(user.getUid()));
            ((TextView) findViewById(R.id.code_field)).setText(String.valueOf(code));
        } else {
            Toast.makeText(this, "Error in request", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
        if (requestId == getQueueRequestId || requestId == getRefreshQueueRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, obj -> {
                queueInfo = (Queue) obj;
                updateQueueView();
            }, NetService.RETURN_QUEUE);
        } else if (requestId == createUserRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, obj -> joinQueue((User) obj), NetService.RETURN_USER);
        } if (requestId == joinRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, obj -> {

                findViewById(R.id.number_lbl).setVisibility(View.VISIBLE);
                findViewById(R.id.number_field).setVisibility(View.VISIBLE);
                findViewById(R.id.code_lbl).setVisibility(View.VISIBLE);
                findViewById(R.id.code_field).setVisibility(View.VISIBLE);
                findViewById(R.id.btn_hide).setVisibility(View.VISIBLE);

                getQueueRequestId = getServiceHelper().getQueue(getIntent().getIntExtra(EXTRA_QUEUE_ID, -1));

            }, NetService.RETURN_QUEUE);

        }
    }

}
