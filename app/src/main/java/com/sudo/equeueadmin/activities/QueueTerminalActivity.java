package com.sudo.equeueadmin.activities;//package com.sudo.equeue.activities;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.sudo.equeueadmin.utils.AlertDialogHelper;
import com.sudo.equeueadmin.utils.PrinterDriver;
import com.sudo.equeueadmin.utils.QRGenerator;
import com.sudo.equeueadmin.utils.QueueApplication;

import java.io.IOException;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

public class QueueTerminalActivity extends NetBaseActivity implements MediaPlayer.OnCompletionListener {

    public static final String EXTRA_QUEUE_ID = QueueApplication.prefix + ".extra.queue_id";

    private int getQueueRequestId;
    private int getRefreshQueueRequestId;
    private int createUserRequestId;
    private int joinRequestId;


    private ImageView bar_code;

    private Queue queueInfo;
    Handler mHandler = new Handler();

    LinkedList<Integer> tracks = new LinkedList<>();
    private MediaPlayer mediaPlayer = null;
    public int current_number = 0;

    int PrinterConnectId = 1;
    private static BluetoothSocket btsocket;

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
        findViewById(R.id.current_lbl).setVisibility(View.INVISIBLE);
        findViewById(R.id.current_field).setVisibility(View.INVISIBLE);
        findViewById(R.id.code_lbl).setVisibility(View.INVISIBLE);
        findViewById(R.id.code_field).setVisibility(View.INVISIBLE);
        findViewById(R.id.btn_hide).setVisibility(View.INVISIBLE);

        if (savedInstanceState == null) {
            getQueueRequestId = getServiceHelper().getQueue(getIntent().getIntExtra(EXTRA_QUEUE_ID, -1));
        }

        findViewById(R.id.btn_join).setOnClickListener(v -> join());
        findViewById(R.id.btn_hide).setOnClickListener(v -> hide());
        bar_code = (ImageView) findViewById(R.id.bar_code);

        mHandler.postDelayed(new Runnable(){
            public void run(){
                //do something
                getRefreshQueueRequestId = getServiceHelper().getQueue(getIntent().getIntExtra(EXTRA_QUEUE_ID, -1));
                mHandler.postDelayed(this, 1000);
            }
        }, 1000);

//        period_task = new Thread(() -> {
//            // TODO Auto-generated method stub
//            while (running) {
//                try {
//                    Thread.sleep(2000);
//                    mHandler.post(() -> {
//                        // TODO Auto-generated method stub
//                        getRefreshQueueRequestId = getServiceHelper().getQueue(getIntent().getIntExtra(EXTRA_QUEUE_ID, -1));
//                    });
//                } catch (Exception e) {
//                    // TODO: handle exception
//                }
//            }
//        });
//        period_task.start();
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (period_task != null) running = false;
//    }

    public void say_number(int number) {
        switch (number / 100) {
            case 0:  break;
            case 1:  tracks.addLast(R.raw.num100); break;
            case 2:  tracks.addLast(R.raw.num200); break;
            case 3:  tracks.addLast(R.raw.num300); break;
            case 4:  tracks.addLast(R.raw.num400); break;
            case 5:  tracks.addLast(R.raw.num500); break;
            case 6:  tracks.addLast(R.raw.num600); break;
            case 7:  tracks.addLast(R.raw.num700); break;
            case 8:  tracks.addLast(R.raw.num800); break;
            case 9:  tracks.addLast(R.raw.num900); break;
            default: break;
        }
        number %= 100;

        switch (number/10) {
            case 0:  break;
            case 1:
                switch (number) {
                    case 10: tracks.addLast(R.raw.num10); break;
                    case 11: tracks.addLast(R.raw.num11); break;
                    case 12: tracks.addLast(R.raw.num12); break;
                    case 13: tracks.addLast(R.raw.num13); break;
                    case 14: tracks.addLast(R.raw.num14); break;
                    case 15: tracks.addLast(R.raw.num15); break;
                    case 16: tracks.addLast(R.raw.num16); break;
                    case 17: tracks.addLast(R.raw.num17); break;
                    case 18: tracks.addLast(R.raw.num18); break;
                    case 19: tracks.addLast(R.raw.num19); break;
                }
                break;
            case 2:  tracks.addLast(R.raw.num20); break;
            case 3:  tracks.addLast(R.raw.num30); break;
            case 4:  tracks.addLast(R.raw.num40); break;
            case 5:  tracks.addLast(R.raw.num50); break;
            case 6:  tracks.addLast(R.raw.num60); break;
            case 7:  tracks.addLast(R.raw.num70); break;
            case 8:  tracks.addLast(R.raw.num80); break;
            case 9:  tracks.addLast(R.raw.num90); break;
            default: break;
        }
        if (number < 10 || number > 19) {
            number %= 10;
            switch (number) {
                case 0:break;
                case 1: tracks.addLast(R.raw.num1); break;
                case 2: tracks.addLast(R.raw.num2); break;
                case 3: tracks.addLast(R.raw.num3); break;
                case 4: tracks.addLast(R.raw.num4); break;
                case 5: tracks.addLast(R.raw.num5); break;
                case 6: tracks.addLast(R.raw.num6); break;
                case 7: tracks.addLast(R.raw.num7); break;
                case 8: tracks.addLast(R.raw.num8); break;
                case 9: tracks.addLast(R.raw.num9); break;
            }
        }

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.number);
        mediaPlayer.setOnCompletionListener(QueueTerminalActivity.this);
        mediaPlayer.start();
    }

    public void onCompletion(MediaPlayer mp) {
        mediaPlayer.release();
        if (tracks.size() > 0) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), tracks.pop());
            mediaPlayer.setOnCompletionListener(QueueTerminalActivity.this);
            mediaPlayer.start();
        }
    }

    private void join() {
        createUserRequestId = getServiceHelper().createUser(null, null, null, false);
    }

    private void hide() {
        findViewById(R.id.number_lbl).setVisibility(View.INVISIBLE);
        findViewById(R.id.number_field).setVisibility(View.INVISIBLE);
//        findViewById(R.id.code_lbl).setVisibility(View.INVISIBLE);
//        findViewById(R.id.code_field).setVisibility(View.INVISIBLE);
        findViewById(R.id.btn_hide).setVisibility(View.INVISIBLE);

        findViewById(R.id.bar_code).setVisibility(View.VISIBLE);
    }

    private void updateQueueView() {

        findViewById(R.id.lbl_terminal).setVisibility(View.VISIBLE);
        findViewById(R.id.lbl_lost).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_join).setVisibility(View.VISIBLE);
        findViewById(R.id.current_lbl).setVisibility(View.VISIBLE);
        findViewById(R.id.current_field).setVisibility(View.VISIBLE);

        ((TextView) findViewById(R.id.name_field)).setText(queueInfo.getName());

        if (queueInfo.getDescription().isEmpty()) {
            findViewById(R.id.description_lbl).setVisibility(View.GONE);
            findViewById(R.id.description_field).setVisibility(View.GONE);
        } else {
            findViewById(R.id.description_lbl).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.description_field)).setText(queueInfo.getDescription());
        }

        generateCodeImage("http://equeue/" + queueInfo.getQid());
        int current = queueInfo.getPassed();
        ((TextView) findViewById(R.id.count_field))
                .setText(String.valueOf(queueInfo.getUsersQuantity()));
        ((TextView) findViewById(R.id.current_field)).setText(String.valueOf(current));

        if (current_number != 0 && current_number != current ) {
            say_number(current);
        }
        current_number = current;

    }

    //    TODO: вынести инициализацию юзера в Application
    private void joinQueue(User user) {
        if (user != null && user.getToken() != null && !user.getToken().equals("")) {
            joinRequestId = getServiceHelper().joinQueueAnonym(queueInfo.getQid(), user.getToken());
//            Random rnd = new Random();
//            int code = 100 + rnd.nextInt(900);
            ((TextView) findViewById(R.id.number_field))
                    .setText(String.valueOf(queueInfo.getPassed() + queueInfo.getUsersQuantity() + 1));
//            ((TextView) findViewById(R.id.code_field)).setText(String.valueOf(code));
        } else {
            AlertDialogHelper.show(this, "Ошибка при запросе");
        }
    }

    private void print_ticket(Integer i) {
        try {
            PrinterDriver printer = new PrinterDriver(btsocket);
            printer.printLogo();

            printer.setAlignCenter();
            printer.printDoubleLine();
            printer.printNameQueue(queueInfo.getName());
            printer.printDescQueue(queueInfo.getDescription());
            printer.printDoubleLine();

            printer.printNumber(i);
            printer.printNewLine();
            printer.printNewLine();

//            btoutputstream.write(0x0D);
//            btoutputstream.write(0x0D);
//            btoutputstream.write(0x0D);
//            btoutputstream.flush();
        } catch (IOException e) {
            e.printStackTrace();
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
                findViewById(R.id.btn_hide).setVisibility(View.VISIBLE);

                getQueueRequestId = getServiceHelper()
                                .getQueue(getIntent()
                                .getIntExtra(EXTRA_QUEUE_ID, -1));

                if (btsocket != null) {
                    this.print_ticket(queueInfo.getPassed() + queueInfo.getUsersQuantity() + 1);
                }

            }, null);

        }
    }


    private void generateCodeImage(String text) {
        try {
            Bitmap bitmap = QRGenerator.encodeAsBitmap(text, 150, 150);
            bar_code.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, PrinterConnectId, 1, "Подлючить принтер");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                if(btsocket == null){
                    Intent BTIntent = new Intent(getApplicationContext(), BTDeviceList.class);
                    this.startActivityForResult(BTIntent, BTDeviceList.REQUEST_CONNECT_BT);
                } else {
                    Toast.makeText(QueueTerminalActivity.this,
                            "Принтер уже подключен",
                            Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            btsocket = BTDeviceList.getSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
