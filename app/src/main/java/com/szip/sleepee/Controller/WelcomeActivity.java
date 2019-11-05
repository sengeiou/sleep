package com.szip.sleepee.Controller;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.szip.sleepee.MyApplication;
import com.szip.sleepee.R;

public class WelcomeActivity extends BaseActivity implements Runnable{

    /**
     * 延时线程
     * */
    private Thread thread;
    private int time = 3;

    private int SleepEECode = 100;

    /**
     * 轻量级文件
     * */
    private SharedPreferences sharedPreferencesp;
    private String FILE = "sleepEE";

    private boolean isLogin = true;
    private boolean isBind = true;
    private boolean isFirst;
    private MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        app = (MyApplication)getApplicationContext();
        if(sharedPreferencesp == null){
            sharedPreferencesp = getSharedPreferences(FILE,MODE_PRIVATE);
        }

        isFirst = sharedPreferencesp.getBoolean("isFirst",true);
        isLogin = sharedPreferencesp.getBoolean("isLogin",false);
        isBind = sharedPreferencesp.getBoolean("isBind",false);

        /**
         * 获取权限·
         * */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, SleepEECode);
            }else {
                initData();
            }
        }else {
            initData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SleepEECode){
            int code = grantResults[0];
            int code1 = grantResults[1];
            int code2 = grantResults[2];
            if (code == PackageManager.PERMISSION_GRANTED&&code1 == PackageManager.PERMISSION_GRANTED&&code2 == PackageManager.PERMISSION_GRANTED){
                initData();
            }else {
                WelcomeActivity.this.finish();
            }
        }
    }

    private void initData() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            while (time != 0){
                Thread.sleep(1000);
                time = time -1;
            }
            if(isFirst){
//                Intent guiIntent = new Intent();
//                guiIntent.setClass(WelcomeActivity.this, GuideActivity.class);
//                startActivity(guiIntent);
//                SharedPreferences.Editor editor = sharedPreferencesp.edit();
//                editor.putBoolean("isFirst",false);
//                editor.commit();
//                finish();
                if(isLogin&&isBind){
                    Intent guiIntent = new Intent();
                    guiIntent.setClass(WelcomeActivity.this, MainActivity.class);
                    startActivity(guiIntent);
                    finish();
                }else if (isLogin){
                    Intent in = new Intent();
                    in.setClass(WelcomeActivity.this, FindDeviceActivity.class);
                    startActivity(in);
                    finish();
                }else{
                    Intent in = new Intent();
                    in.setClass(WelcomeActivity.this, LoginActivity.class);
                    startActivity(in);
                    finish();
                }
            }else{
                if(isLogin&&isBind){
                    Intent guiIntent = new Intent();
                    guiIntent.setClass(WelcomeActivity.this, MainActivity.class);
                    startActivity(guiIntent);
                    finish();
                }else if (isLogin){
                    Intent in = new Intent();
                    in.setClass(WelcomeActivity.this, FindDeviceActivity.class);
                    startActivity(in);
                    finish();
                }else{
                    Intent in = new Intent();
                    in.setClass(WelcomeActivity.this, LoginActivity.class);
                    startActivity(in);
                    finish();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
