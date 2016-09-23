package com.sudo.equeueadmin.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.sudo.equeueadmin.NetBaseActivity;
import com.sudo.equeueadmin.NetService;
import com.sudo.equeueadmin.R;
import com.sudo.equeueadmin.utils.AlertDialogHelper;
import com.sudo.equeueadmin.utils.QRGenerator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class QRActivity extends NetBaseActivity {

    private int qid;
    private int pdfRequestId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("QR код");
        }

        ImageView qr_code = (ImageView) findViewById(R.id.qr_code);


        if (savedInstanceState == null) {
            Intent i = getIntent();
            qid = i.getIntExtra("qid", -1);
        } else {
            qid = savedInstanceState.getInt("qid", -1);
        }
        if (qid != -1) {
            try {
                Bitmap bitmap = QRGenerator.encodeAsBitmap("http://equeue.org/client/queue/" + Integer.toString(qid), 300, 300);
                qr_code.setImageBitmap(bitmap);
            } catch (Exception e) {

            }
        }
    }

    private void printClicked() {
        pdfRequestId = getServiceHelper().getPdf(qid);
    }

    private void gotPdf(byte[] pdfBytes) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
            String jobName = "eQueue QR";
            PrintDocumentAdapter pda = new PrintDocumentAdapter() {
                @TargetApi(Build.VERSION_CODES.KITKAT)
                @Override
                public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
                    if (cancellationSignal.isCanceled()) {
                        callback.onLayoutCancelled();
                        return;
                    }
                    PrintDocumentInfo pdi = new PrintDocumentInfo.Builder("File file!").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();
                    callback.onLayoutFinished(pdi, true);
                }

                @TargetApi(Build.VERSION_CODES.KITKAT)
                @Override
                public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
                    OutputStream output = null;
                    try {
                        output = new FileOutputStream(destination.getFileDescriptor());
                        output.write(pdfBytes);

                        callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
                    } catch (Exception e) {
                        //Catch exception
                    } finally {
                        try {
                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            printManager.print(jobName, pda, null);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_print:
                printClicked();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_qr, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("qid", qid);
    }

    @Override
    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
        if (requestId == pdfRequestId) {
            if (resultCode == NetService.CODE_OK) {
                if (data.getInt(NetService.RETURN_CODE) == NetService.CODE_OK) {
                    byte[] pdfBytes = data.getByteArray(NetService.RETURN_PDF);
                    gotPdf(pdfBytes);
                } else {
                    String errMessage = data.getString(NetService.ERROR_MSG, getString(R.string.error_msg_unknown));
                    AlertDialogHelper.show(this, errMessage);
                }
            } else {
//            throw new AssertionError("Error in arguments"); //TODO: wtf
//            Toast.makeText(context, "Error in arguments", Toast.LENGTH_LONG).show();
            }
        }
    }
}
