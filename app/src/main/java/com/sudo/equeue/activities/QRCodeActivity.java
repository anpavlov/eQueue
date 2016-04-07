package com.sudo.equeue.activities;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

import com.sudo.equeue.NetBaseActivity;
import com.sudo.equeue.NetService;
import com.sudo.equeue.R;
import com.sudo.equeue.models.Queue;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

public class QRCodeActivity extends NetBaseActivity {

    private int getQueueRequestId = -1;

    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    private FrameLayout preview;
//    private TextView scanText;
    private ImageScanner scanner;
    private boolean previewing = true;
    private Image codeImage;
    private FrameLayout progressBarHolder;

    static {
        System.loadLibrary("iconv");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        //        ========== Toolbar ============
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Сканировать QR");
        }

        autoFocusHandler = new Handler();

        preview = (FrameLayout) findViewById(R.id.cameraPreview);

        /* Instance barcode scanner */
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        progressBarHolder = (FrameLayout) findViewById(R.id.progress_overlay);
//        scanText = (TextView) findViewById(R.id.scanText);
    }

    private void handleScannedText(String text) {
        if (text.startsWith("http://equeue/")) {
            int qid = Integer.parseInt(text.replace("http://equeue/", ""));
            getQueueRequestId = getServiceHelper().getQueue(qid);
            loadingStart();
        }
    }

    private void openQueue(Queue queue) {
        loadingStop();
        Intent intent = new Intent(this, QueueActivity.class);
        intent.putExtra(QueueActivity.EXTRA_QUEUE_ID, queue);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeCamera();
    }

    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    // A safe way to get an instance of the Camera object.
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) { }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.cancelAutoFocus();
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private void resumeCamera() {
        mCamera = getCameraInstance();
        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        preview.removeAllViews();
        preview.addView(mPreview);
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            Camera.Size size = parameters.getPreviewSize();
            codeImage = new Image(size.width, size.height, "Y800");
            previewing = true;
            mPreview.refreshDrawableState();
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

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing && mCamera != null) {
                mCamera.autoFocus(autoFocusCB);
            }
        }
    };

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            codeImage.setData(data);
            int result = scanner.scanImage(codeImage);
            if (result != 0) {
                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {
                    String lastScannedCode = sym.getData();
                    if (lastScannedCode != null) {
//                        Toast.makeText(QRCodeActivity.this, lastScannedCode, Toast.LENGTH_SHORT).show();
                        handleScannedText(lastScannedCode);
                    }
                }
            }
            camera.addCallbackBuffer(data);
        }
    };

    // Mimic continuous auto-focusing
    final Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

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

    @Override
    public void onServiceCallback(int requestId, int resultCode, Bundle data) {
        if (requestId == getQueueRequestId) {
            getServiceHelper().handleResponse(this, resultCode, data, obj -> openQueue((Queue) obj), NetService.RETURN_QUEUE);
        }
    }
}
