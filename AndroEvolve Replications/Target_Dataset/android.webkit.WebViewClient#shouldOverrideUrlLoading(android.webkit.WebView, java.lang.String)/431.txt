package com.pupupon.russian_alphabet;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class PrivacyPolicyActivity extends Activity {
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_policy_activity);

        webView = findViewById(R.id.privacyWebView);

        WebSettings webSetting = webView.getSettings();
        webSetting.setBuiltInZoomControls(true);

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("file:///android_asset/privacy_policy.html");
    }

    private class WebViewClient extends android.webkit.WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }
}
