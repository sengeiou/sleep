package com.szip.smartdream.Controller;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.szip.smartdream.R;
import com.szip.smartdream.Util.StatusBarCompat;

public class AboutActivity extends BaseActivity {

    private LinearLayout protocolLl;
    private ImageView backIv;
    private TextView versionTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_about);
        StatusBarCompat.translucentStatusBar(AboutActivity.this,true);
        initView();
        initEvent();
    }

    private void initView() {
        ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.about));
        backIv = findViewById(R.id.backIv);
        protocolLl = findViewById(R.id.protocolLl);
        versionTv = findViewById(R.id.versionTv);
        String ver;
        try {
            ver = getPackageManager().getPackageInfo("com.szip.sleepee",
                    0).versionName;
            versionTv.setText("v" + ver);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initEvent() {
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        protocolLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getResources().getConfiguration().locale.getCountry().equals("CN")){
                    Uri uri = Uri.parse("https://cloud.znsdkj.com:8443/sleep/comm/statement?lang=en-US");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }else {
                    Uri uri = Uri.parse("https://cloud.znsdkj.com:8443/sleep/comm/statement");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        });
    }
}
