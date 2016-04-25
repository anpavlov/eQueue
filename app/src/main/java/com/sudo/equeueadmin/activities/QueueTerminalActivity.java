package com.sudo.equeueadmin.activities;//package com.sudo.equeue.activities;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
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

public class QueueTerminalActivity extends NetBaseActivity implements MediaPlayer.OnCompletionListener {

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

    int[] tracks = new int[4];
    int currentTrack = 0;
    int currentSize = 0;
    private MediaPlayer mediaPlayer = null;

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

        View sound = findViewById(R.id.btn_sound);
        assert sound != null;
        sound.setOnClickListener(v -> {
            Random ran = new Random();
            int number = ran.nextInt(1000);
            Toast.makeText(QueueTerminalActivity.this, String.valueOf(number), Toast.LENGTH_SHORT).show();

            tracks[0] = R.raw.number;
            currentTrack = 0;

            if (number / 100 > 0) {
                currentSize = 3;
                if (number/100 == 1) tracks[1] = R.raw.num100;
                if (number/100 == 2) tracks[1] = R.raw.num200;
                if (number/100 == 3) tracks[1] = R.raw.num300;
                if (number/100 == 4) tracks[1] = R.raw.num400;
                if (number/100 == 5) tracks[1] = R.raw.num500;
                if (number/100 == 6) tracks[1] = R.raw.num600;
                if (number/100 == 7) tracks[1] = R.raw.num700;
                if (number/100 == 8) tracks[1] = R.raw.num800;
                if (number/100 == 9) tracks[1] = R.raw.num900;
                number = number % 100;
                if (number < 20 && number > 9) {
                    currentSize = 2;
                    if (number == 10) tracks[2] = R.raw.num10;
                    if (number == 11) tracks[2] = R.raw.num11;
                    if (number == 12) tracks[2] = R.raw.num12;
                    if (number == 13) tracks[2] = R.raw.num13;
                    if (number == 14) tracks[2] = R.raw.num14;
                    if (number == 15) tracks[2] = R.raw.num15;
                    if (number == 16) tracks[2] = R.raw.num16;
                    if (number == 17) tracks[2] = R.raw.num17;
                    if (number == 18) tracks[2] = R.raw.num18;
                    if (number == 19) tracks[2] = R.raw.num19;
                } else {
                    if (number/10 > 0) {
                        if (number / 10 == 2) tracks[2] = R.raw.num20;
                        if (number / 10 == 3) tracks[2] = R.raw.num30;
                        if (number / 10 == 4) tracks[2] = R.raw.num40;
                        if (number / 10 == 5) tracks[2] = R.raw.num50;
                        if (number / 10 == 6) tracks[2] = R.raw.num60;
                        if (number / 10 == 7) tracks[2] = R.raw.num70;
                        if (number / 10 == 8) tracks[2] = R.raw.num80;
                        if (number / 10 == 9) tracks[2] = R.raw.num90;
                        number = number % 10;
                        if (number == 0) {
                            currentSize = 2;
                        } else {
                            if (number == 1) tracks[3] = R.raw.num1;
                            if (number == 2) tracks[3] = R.raw.num2;
                            if (number == 3) tracks[3] = R.raw.num3;
                            if (number == 4) tracks[3] = R.raw.num4;
                            if (number == 5) tracks[3] = R.raw.num5;
                            if (number == 6) tracks[3] = R.raw.num6;
                            if (number == 7) tracks[3] = R.raw.num7;
                            if (number == 8) tracks[3] = R.raw.num8;
                            if (number == 9) tracks[3] = R.raw.num9;
                        }
                    } else {
                        currentSize = 2;
                        number = number % 10;
                        if (number == 1) tracks[2] = R.raw.num1;
                        if (number == 2) tracks[2] = R.raw.num2;
                        if (number == 3) tracks[2] = R.raw.num3;
                        if (number == 4) tracks[2] = R.raw.num4;
                        if (number == 5) tracks[2] = R.raw.num5;
                        if (number == 6) tracks[2] = R.raw.num6;
                        if (number == 7) tracks[2] = R.raw.num7;
                        if (number == 8) tracks[2] = R.raw.num8;
                        if (number == 9) tracks[2] = R.raw.num9;
                    }
                }
            } else
            if (number / 10 > 0) {
                currentSize = 2;
                if (number < 20) {
                    currentSize = 1;
                    if (number == 10) tracks[1] = R.raw.num10;
                    if (number == 11) tracks[1] = R.raw.num11;
                    if (number == 12) tracks[1] = R.raw.num12;
                    if (number == 13) tracks[1] = R.raw.num13;
                    if (number == 14) tracks[1] = R.raw.num14;
                    if (number == 15) tracks[1] = R.raw.num15;
                    if (number == 16) tracks[1] = R.raw.num16;
                    if (number == 17) tracks[1] = R.raw.num17;
                    if (number == 18) tracks[1] = R.raw.num18;
                    if (number == 19) tracks[1] = R.raw.num19;
                } else {
                    if (number / 10 == 1) tracks[1] = R.raw.num10;
                    if (number / 10 == 2) tracks[1] = R.raw.num20;
                    if (number / 10 == 3) tracks[1] = R.raw.num30;
                    if (number / 10 == 4) tracks[1] = R.raw.num40;
                    if (number / 10 == 5) tracks[1] = R.raw.num50;
                    if (number / 10 == 6) tracks[1] = R.raw.num60;
                    if (number / 10 == 7) tracks[1] = R.raw.num70;
                    if (number / 10 == 8) tracks[1] = R.raw.num80;
                    if (number / 10 == 9) tracks[1] = R.raw.num90;
                    number = number % 10;
                    if (number == 1) tracks[2] = R.raw.num1;
                    if (number == 2) tracks[2] = R.raw.num2;
                    if (number == 3) tracks[2] = R.raw.num3;
                    if (number == 4) tracks[2] = R.raw.num4;
                    if (number == 5) tracks[2] = R.raw.num5;
                    if (number == 6) tracks[2] = R.raw.num6;
                    if (number == 7) tracks[2] = R.raw.num7;
                    if (number == 8) tracks[2] = R.raw.num8;
                    if (number == 9) tracks[2] = R.raw.num9;
                }
            } else {
                currentSize = 1;
                if (number == 1) tracks[1] = R.raw.num1;
                if (number == 2) tracks[1] = R.raw.num2;
                if (number == 3) tracks[1] = R.raw.num3;
                if (number == 4) tracks[1] = R.raw.num4;
                if (number == 5) tracks[1] = R.raw.num5;
                if (number == 6) tracks[1] = R.raw.num6;
                if (number == 7) tracks[1] = R.raw.num7;
                if (number == 8) tracks[1] = R.raw.num8;
                if (number == 9) tracks[1] = R.raw.num9;
            }
            mediaPlayer = MediaPlayer.create(getApplicationContext(), tracks[currentTrack]);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.start();
        });


    }

    public void onCompletion(MediaPlayer arg0) {
        arg0.release();
        if (currentTrack < currentSize) {
            currentTrack++;
            arg0 = MediaPlayer.create(getApplicationContext(), tracks[currentTrack]);
            arg0.setOnCompletionListener(this);
            arg0.start();
        }
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

        ((TextView) findViewById(R.id.name_field)).setText(queueInfo.getName());

        if (queueInfo.getDescription().isEmpty()) {
            findViewById(R.id.description_lbl).setVisibility(View.GONE);
            findViewById(R.id.description_field).setVisibility(View.GONE);
        } else {
            findViewById(R.id.description_lbl).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.description_field)).setText(queueInfo.getDescription());
        }

        generateCodeImage("http://equeue/" + queueInfo.getQid());


//        if (queueInfo. != null && !queueInfo.getUserlist().isEmpty()) {
        int count = queueInfo.getUsersQuantity();
//        int current = queueInfo.getUserlist().get(0);
        ((TextView) findViewById(R.id.count_field)).setText(String.valueOf(count));

//        findViewById(R.id.current_lbl).setVisibility(View.VISIBLE);
//        findViewById(R.id.current_field).setVisibility(View.VISIBLE);
//            ((TextView) findViewById(R.id.current_field)).setText(String.valueOf(current));
//        } else {
        findViewById(R.id.current_lbl).setVisibility(View.INVISIBLE);
        findViewById(R.id.current_field).setVisibility(View.INVISIBLE);
//        ((TextView) findViewById(R.id.count_field)).setText("0");
//        }
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
            getServiceHelper().handleResponse(this, resultCode, data, NetService.RETURN_QUEUE, obj -> {
                queueInfo = (Queue) obj;
                updateQueueView();
            }, null);
        } else if (requestId == createUserRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, NetService.RETURN_USER, obj -> joinQueue((User) obj), null);
        } if (requestId == joinRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, NetService.RETURN_QUEUE, obj -> {

                findViewById(R.id.bar_code).setVisibility(View.GONE);

                findViewById(R.id.number_lbl).setVisibility(View.VISIBLE);
                findViewById(R.id.number_field).setVisibility(View.VISIBLE);
                findViewById(R.id.code_lbl).setVisibility(View.VISIBLE);
                findViewById(R.id.code_field).setVisibility(View.VISIBLE);
                findViewById(R.id.btn_hide).setVisibility(View.VISIBLE);

                getQueueRequestId = getServiceHelper().getQueue(getIntent().getIntExtra(EXTRA_QUEUE_ID, -1));

            }, null);

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
