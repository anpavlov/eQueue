package com.sudo.equeueadmin.activities;//package com.sudo.equeue.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.sudo.equeueadmin.NetBaseActivity;
import com.sudo.equeueadmin.NetService;
import com.sudo.equeueadmin.R;
import com.sudo.equeueadmin.models.Queue;
import com.sudo.equeueadmin.models.User;
import com.sudo.equeueadmin.utils.QueueApplication;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

public class QueueTerminalActivity extends NetBaseActivity {

    public static final String EXTRA_QUEUE_ID = QueueApplication.prefix + ".extra.queue_id";

    private int getQueueRequestId;
    private int getRefreshQueueRequestId;
    private int createUserRequestId;
    private int joinRequestId;

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF291545;
    private ImageView bar_code;

    private Queue queueInfo;
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_terminal);

        findViewById(R.id.lbl_terminal).setVisibility(View.INVISIBLE);
        findViewById(R.id.description_lbl).setVisibility(View.INVISIBLE);
        findViewById(R.id.lbl_lost).setVisibility(View.INVISIBLE);
        findViewById(R.id.btn_join).setVisibility(View.INVISIBLE);


        findViewById(R.id.number_lbl).setVisibility(View.INVISIBLE);
        findViewById(R.id.number_field).setVisibility(View.INVISIBLE);
        findViewById(R.id.code_lbl).setVisibility(View.INVISIBLE);
        findViewById(R.id.code_field).setVisibility(View.INVISIBLE);
        findViewById(R.id.btn_hide).setVisibility(View.INVISIBLE);

        findViewById(R.id.bar_code).setVisibility(View.INVISIBLE);

        if (savedInstanceState == null) {
            getQueueRequestId = getServiceHelper().getQueue(getIntent().getIntExtra(EXTRA_QUEUE_ID, -1));
        }

        findViewById(R.id.btn_join).setOnClickListener(v -> join());
        findViewById(R.id.btn_hide).setOnClickListener(v -> hide());
        bar_code = (ImageView) findViewById(R.id.bar_code);

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

        findViewById(R.id.bar_code).setVisibility(View.VISIBLE);
    }

    private void updateQueueView() {

        findViewById(R.id.lbl_terminal).setVisibility(View.VISIBLE);
        findViewById(R.id.lbl_lost).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_join).setVisibility(View.VISIBLE);
        findViewById(R.id.bar_code).setVisibility(View.VISIBLE);

        ((TextView) findViewById(R.id.name_field)).setText(queueInfo.getName());

        if (queueInfo.getDescription().isEmpty()) {
            findViewById(R.id.description_lbl).setVisibility(View.GONE);
            findViewById(R.id.description_field).setVisibility(View.GONE);
        } else {
            findViewById(R.id.description_lbl).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.description_field)).setText(queueInfo.getDescription());
        }

        generateCodeImage("http://equeue/" + queueInfo.getQid());


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

                findViewById(R.id.bar_code).setVisibility(View.GONE);

                findViewById(R.id.number_lbl).setVisibility(View.VISIBLE);
                findViewById(R.id.number_field).setVisibility(View.VISIBLE);
                findViewById(R.id.code_lbl).setVisibility(View.VISIBLE);
                findViewById(R.id.code_field).setVisibility(View.VISIBLE);
                findViewById(R.id.btn_hide).setVisibility(View.VISIBLE);

                getQueueRequestId = getServiceHelper().getQueue(getIntent().getIntExtra(EXTRA_QUEUE_ID, -1));

            }, NetService.RETURN_QUEUE);

        }
    }


    private void generateCodeImage(String text) {
        try {
            Bitmap bitmap = encodeAsBitmap(text, BarcodeFormat.QR_CODE, 150, 150);
            bar_code.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private Bitmap encodeAsBitmap(String code, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        if (code == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(code);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(code, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

}
