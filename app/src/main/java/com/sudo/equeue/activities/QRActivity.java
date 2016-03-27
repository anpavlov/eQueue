package com.sudo.equeue.activities;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sudo.equeue.R;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;


public class QRActivity extends Activity {
    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    private FrameLayout preview;
    private TextView scanText;
    private ImageScanner scanner;
    private boolean previewing = true;
    private String lastScannedCode;
    private Image codeImage;

    static {
        System.loadLibrary("iconv");
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        autoFocusHandler = new Handler();

        preview = (FrameLayout) findViewById(R.id.cameraPreview);

        /* Instance barcode scanner */
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        scanText = (TextView) findViewById(R.id.scanText);
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
            Size size = parameters.getPreviewSize();
            codeImage = new Image(size.width, size.height, "Y800");
            previewing = true;
            mPreview.refreshDrawableState();
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing && mCamera != null) {
                mCamera.autoFocus(autoFocusCB);
            }
        }
    };

    PreviewCallback previewCb = new PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            codeImage.setData(data);
            int result = scanner.scanImage(codeImage);
            if (result != 0) {
                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {
                    lastScannedCode = sym.getData();
                    if (lastScannedCode != null) {
                        Toast.makeText(QRActivity.this, lastScannedCode, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            camera.addCallbackBuffer(data);
        }
    };

    // Mimic continuous auto-focusing
    final AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

}
