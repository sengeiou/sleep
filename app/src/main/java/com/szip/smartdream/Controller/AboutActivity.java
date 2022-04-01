package com.szip.smartdream.Controller;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.szip.smartdream.Bean.HttpBean.BaseApi;
import com.szip.smartdream.R;
import com.szip.smartdream.Util.HttpMessgeUtil;
import com.szip.smartdream.Util.JsonGenericsSerializator;
import com.szip.smartdream.Util.StatusBarCompat;
import com.szip.smartdream.View.MyAlerDialog;
import com.zhy.http.okhttp.callback.GenericsCallback;

import java.io.IOException;

import okhttp3.Call;

public class AboutActivity extends BaseActivity {

    private LinearLayout protocolLl;
    private LinearLayout deleteLl;
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
        deleteLl = findViewById(R.id.deleteLl);
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
                Intent intent = new Intent();
                intent.setClass(AboutActivity.this, PrivacyActivity.class);
                startActivity(intent);
            }
        });
        deleteLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAlerDialog.getSingle().showAlerDialog(getString(R.string.tip), getString(R.string.deleteTip), getString(R.string.confirm),
                        getString(R.string.cancel), false, new MyAlerDialog.AlerDialogOnclickListener() {
                            @Override
                            public void onDialogTouch(boolean flag) {
                                if (flag){
                                    try {
                                        HttpMessgeUtil.getInstance(AboutActivity.this).deleteAccount(new GenericsCallback<BaseApi>(new JsonGenericsSerializator()) {
                                            @Override
                                            public void onError(Call call, Exception e, int id) {

                                            }

                                            @Override
                                            public void onResponse(BaseApi response, int id) {
                                                if (response.getCode()==200){
                                                    showToast(getString(R.string.deleteSuccess));
                                                }else {
                                                    showToast(getString(R.string.deleteNow));
                                                }
                                            }
                                        });
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        },AboutActivity.this);
            }
        });
    }
}
