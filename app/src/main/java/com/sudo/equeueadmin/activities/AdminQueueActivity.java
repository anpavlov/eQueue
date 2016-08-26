package com.sudo.equeueadmin.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.printservice.PrintDocument;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.sudo.equeueadmin.NetBaseActivity;
import com.sudo.equeueadmin.NetService;
import com.sudo.equeueadmin.R;
import com.sudo.equeueadmin.models.Queue;
import com.sudo.equeueadmin.utils.AlertDialogHelper;
import com.sudo.equeueadmin.utils.QueueApplication;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AdminQueueActivity extends NetBaseActivity {

    public static final String EXTRA_QUEUE = QueueApplication.prefix + ".extra.queue";

    private static final String SAVED_STATE_QUEUE = QueueApplication.prefix + ".QueueAdminActivity.saved.queue";
//    private static final String SAVED_STATE_ID_CREATE = QueueApplication.prefix + ".QueueAdminActivity.saved.id_create";
    private static final String SAVED_STATE_ID_GET_QUEUE = QueueApplication.prefix + ".QueueAdminActivity.saved.id_get_queue";
//    private static final String SAVED_STATE_ID_SAVE = QueueApplication.prefix + ".QueueAdminActivity.saved.id_save";
    private static final String SAVED_STATE_ID_CALL = QueueApplication.prefix + ".QueueAdminActivity.saved.id_call";

    private static final int EDIT_QUEUE_REQ_ID = 5135;

    private int getQueueRequestId = -1;
    private int callRequestId = -1;
    private int deleteRequestId = -1;
//    private int pdfRequestId = -1;

    private Queue queueInfo;
    private SwipeRefreshLayout swipeRefreshLayout;

    Handler mHandler = new Handler();
//    Thread period_task;
//    boolean running = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Управление очередью");
        }

        if (savedInstanceState == null) {
            queueInfo = (Queue) getIntent().getSerializableExtra(EXTRA_QUEUE);
        } else {
            queueInfo = (Queue) savedInstanceState.getSerializable(SAVED_STATE_QUEUE);
            callRequestId = savedInstanceState.getInt(SAVED_STATE_ID_CALL, -1);
            getQueueRequestId = savedInstanceState.getInt(SAVED_STATE_ID_GET_QUEUE, -1);
        }

        if (queueInfo == null) {
           throw new AssertionError("Queue is null");
        }

        findViewById(R.id.btn_next).setOnClickListener(v -> callNext());

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_queue_view);
        swipeRefreshLayout.setOnRefreshListener(this::refresh);
        updateQueueView();



//        period_task = new Thread(() -> {
//            // TODO Auto-generated method stub
//            while (running) {
//                try {
//                    Thread.sleep(1000);
//                    mHandler.post(() -> {
//                        // TODO Auto-generated method stub
//                        getQueueRequestId = getServiceHelper().getQueue(queueInfo.getQid());
//                    });
//                } catch (Exception e) {
//                    Log.e("equeue", "exception in thread sleep");
//                    // TODO: handle exception
//                }
//            }
//        });
//        period_task.start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(new Runnable(){
            public void run(){
                //do something
                getQueueRequestId = getServiceHelper().getQueue(queueInfo.getQid());
                mHandler.postDelayed(this, 2000);
            }
        }, 2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacksAndMessages(null);
    }

    private void refresh() {
        getQueueRequestId = getServiceHelper().getQueue(queueInfo.getQid());
    }

    private void callNext() {
        if (queueInfo != null) {
            callRequestId = getServiceHelper().callNext(queueInfo.getQid());
        }
    }

    private void deleteQueue() {
        if (queueInfo != null) {
            deleteRequestId = getServiceHelper().deleteQueue(queueInfo.getQid());
        }
    }

    private void openTerminal() {
        if (queueInfo != null) {
            Intent intent = new Intent(this, QueueTerminalActivity.class);
            intent.putExtra(QueueTerminalActivity.EXTRA_QUEUE_ID, queueInfo.getQid());
            startActivity(intent);
        }
    }

    private void updateQueueView() {
        ((TextView) findViewById(R.id.name)).setText(queueInfo.getName());
//        ((TextView) findViewById(R.id.description)).setText(queueInfo.getDescription());
        ((TextView) findViewById(R.id.inqueue)).setText("в очереди\n" + Integer.toString(queueInfo.getUsersQuantity()));
        ((TextView) findViewById(R.id.out_quantity)).setText("прошло\n" + Integer.toString(queueInfo.getPassed()));
//        ((TextView) findViewById(R.id.total_time)).setText(Integer.toString(queueInfo.getWaitTime()) + " минут");
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_admin, menu);
        return true;
    }

//    private void printClicked() {
//        pdfRequestId = getServiceHelper().getPdf(queueInfo.getQid());
//    }
//
//    private void gotPdf(byte[] pdfBytes) {
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//            PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
//            String jobName = "eQueue QR";
//            PrintDocumentAdapter pda = new PrintDocumentAdapter() {
//                @TargetApi(Build.VERSION_CODES.KITKAT)
//                @Override
//                public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
//                    if (cancellationSignal.isCanceled()) {
//                        callback.onLayoutCancelled();
//                        return;
//                    }
//                    PrintDocumentInfo pdi = new PrintDocumentInfo.Builder("File file!").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();
//                    callback.onLayoutFinished(pdi, true);
//                }
//
//                @TargetApi(Build.VERSION_CODES.KITKAT)
//                @Override
//                public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
//                    OutputStream output = null;
//                    try {
//                        output = new FileOutputStream(destination.getFileDescriptor());
//                        output.write(pdfBytes);
//
//                        callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
//                    } catch (Exception e) {
//                        //Catch exception
//                    } finally {
//                        try {
//                            output.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            };
//            printManager.print(jobName, pda, null);
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_edit:
                editQueue();
                return true;
            case R.id.menu_terminal:
                openTerminal();
                return true;
            case R.id.menu_delete:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Удалить очередь?");
                alertDialogBuilder.setPositiveButton("OK", (dialog, id) -> deleteQueue());
                alertDialogBuilder.setNegativeButton("Отмена", (dialog, id) -> dialog.dismiss());
                alertDialogBuilder.create().show();
                return true;
//            case R.id.menu_print:
//                printClicked();
//                return true;
            case R.id.menu_qr:
                if (queueInfo != null) {
                    String url = "http://p30280.lab1.stud.tech-mail.ru/api/queue/getpdf/?qid=" + Integer.toString(queueInfo.getQid());
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void editQueue() {
        Intent intent = new Intent(this, EditQueueActivity.class);
        intent.putExtra(EXTRA_QUEUE, queueInfo);
        startActivityForResult(intent, EDIT_QUEUE_REQ_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (data == null) {return;}
//        String name = data.getStringExtra("name");
//        tvName.setText("Your name is " + name);
        if (requestCode == EDIT_QUEUE_REQ_ID) {
            if (data == null || resultCode != RESULT_OK) {
//                Toast.makeText(this, "Error while editing", Toast.LENGTH_SHORT).show();
            } else {
                Queue queue = (Queue) data.getSerializableExtra(EXTRA_QUEUE);
                if (queue == null) {
//                    Toast.makeText(this, "Error while editing", Toast.LENGTH_SHORT).show();
                } else {
                    queueInfo = queue;
                    updateQueueView();
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVED_STATE_QUEUE, queueInfo);
        outState.putInt(SAVED_STATE_ID_CALL, callRequestId);
        outState.putInt(SAVED_STATE_ID_GET_QUEUE, getQueueRequestId);
    }

    @Override
    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
        if (requestId == getQueueRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, NetService.RETURN_QUEUE, obj -> {
                queueInfo = (Queue) obj;
                updateQueueView();
            }, null);
        } else
        if (requestId == callRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, null,
                    obj -> getQueueRequestId = getServiceHelper().getQueue(queueInfo.getQid()),
                    null);
        } else
        if (requestId == deleteRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, null,
                    obj -> finish(),
                    null);
        }
//        if (requestId == pdfRequestId) {
//            if (resultCode == NetService.CODE_OK) {
//                if (data.getInt(NetService.RETURN_CODE) == NetService.CODE_OK) {
//                    byte[] pdfBytes = data.getByteArray(NetService.RETURN_PDF);
//                    gotPdf(pdfBytes);
//                } else {
//                    String errMessage = data.getString(NetService.ERROR_MSG, getString(R.string.error_msg_unknown));
//                    AlertDialogHelper.show(this, errMessage);
//                }
//            } else {
////            throw new AssertionError("Error in arguments"); //TODO: wtf
////            Toast.makeText(context, "Error in arguments", Toast.LENGTH_LONG).show();
//            }

    }
}
