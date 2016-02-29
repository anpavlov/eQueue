package com.sudo.equeue.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends AppCompatActivity {

    public static final String EXTRA_VKUID = "asfdgdgafbsadf";

    private static final String APP_VK_KEY = "5324943";

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                handleUrlChange(url);
            }
        });
        setContentView(webView);
        webView.loadUrl("https://oauth.vk.com/authorize?client_id=" + APP_VK_KEY + "&display=mobile&response_type=token&redirect_uri=http://com.sudo.vk.done/cb");
    }

    void handleUrlChange(String url) {
        if (!url.startsWith("http://com.sudo.vk.done/cb")) {
            return;
        }
//        Log.d("handleUrlChange", "url =" + url);
        for (String arg : url.split("#")[1].split("&")) {
            if (arg.startsWith("user_id")) {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_VKUID, Integer.parseInt(arg.split("=")[1]));
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }
}
