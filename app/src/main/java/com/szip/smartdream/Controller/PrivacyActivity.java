package com.szip.smartdream.Controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.szip.smartdream.R;
import com.szip.smartdream.Util.StatusBarCompat;

public class PrivacyActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_privicy);
        StatusBarCompat.translucentStatusBar(PrivacyActivity.this,true);
        initView();
    }

    private void initView() {
        ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.privacy1));
        findViewById(R.id.backIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        webView = findViewById(R.id.webview);
        if(getResources().getConfiguration().locale.getLanguage().equals("zh"))
            webView.loadUrl("https://cloud.znsdkj.com:8443/file/contract/sleep/statement.html");
        else
            webView.loadUrl("https://cloud.znsdkj.com:8443/file/contract/sleep/statement-eng.html");
        webView.getSettings().setJavaScriptEnabled(true);
    }
}