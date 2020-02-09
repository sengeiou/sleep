package com.szip.smartdream.Controller;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.szip.smartdream.Bean.HttpBean.BaseApi;
import com.szip.smartdream.Interface.HttpCallbackWithBase;
import com.szip.smartdream.Model.ProgressHudModel;
import com.szip.smartdream.R;
import com.szip.smartdream.Util.HttpMessgeUtil;
import com.szip.smartdream.Util.StatusBarCompat;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static com.szip.smartdream.Util.HttpMessgeUtil.BINDMAIL_FLAG;
import static com.szip.smartdream.Util.HttpMessgeUtil.BINDPHONE_FLAG;
import static com.szip.smartdream.Util.HttpMessgeUtil.GETVERIFICATION_FLAG;
import static com.szip.smartdream.Util.HttpMessgeUtil.UNBINDMAIL_FLAG;
import static com.szip.smartdream.Util.HttpMessgeUtil.UNBINDPHONE_FLAG;

public class BindPhoneOrMailActivity extends BaseActivity implements HttpCallbackWithBase{

    private Context mContext;

    private LinearLayout countryLl;
    private TextView countryTv;
    private TextView codeTv;

    private ImageView image;
    private EditText userEt;
    private ImageView userClearIv;



    private EditText verificationCodeEt;
    private TextView sendTv;
    private Timer timer;
    private int time;

    private LinearLayout saveLl;

    private ImageView backIv;

    /**
     * 初始化页面的标志，0绑定邮箱，1绑定手机，2解绑邮箱，3解绑手机
     * */
    private int flag;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 100:
                    String getCodeAgain = getString(R.string.send);
                    time--;
                    if (time <= 0){
                        timer.cancel();
                        sendTv.setEnabled(true);
                        sendTv.setText(getCodeAgain);
                    }else {
                        sendTv.setText(time+"s");
                    }
                    break;
                case 255:
                    showToast(getString(R.string.bindSeccuss));
                    finish();
                    break;
                case 256:
                    showToast(getString(R.string.unBindSeccuss));
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithBase(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithBase(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_bind_phone_or_mail);
        StatusBarCompat.translucentStatusBar(BindPhoneOrMailActivity.this,true);
        mContext = getApplicationContext();
        Intent intent = getIntent();
        initView();
        initEvent();
        initData(intent);
    }

    private void initView() {
        countryLl = findViewById(R.id.layout);
        countryTv = findViewById(R.id.countryTv);
        codeTv = findViewById(R.id.codeTv);
        image = findViewById(R.id.image);
        userEt = findViewById(R.id.userEt);
        userClearIv = findViewById(R.id.userClearIv);
        verificationCodeEt = findViewById(R.id.verificationCodeEt);
        sendTv = findViewById(R.id.sendCodeTv);
        backIv = findViewById(R.id.backIv);
        saveLl = findViewById(R.id.saveLl);
    }

    private void initEvent(){
        countryLl.setOnClickListener(onClickListener);
        backIv.setOnClickListener(onClickListener);
        sendTv.setOnClickListener(onClickListener);
        saveLl.setOnClickListener(onClickListener);
    }

    private void initData(Intent intent) {
        flag = intent.getIntExtra("flag",0);
        if (flag==0||flag==2){//邮箱布局
            if (flag == 0)
                ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.reMail));
            else
                ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.unbind));

            countryLl.setVisibility(View.GONE);
            image.setImageResource(R.mipmap.signup_icon_email);
            userEt.setHint(getString(R.string.email));
            codeTv.setVisibility(View.GONE);

        }else {//电话布局
            if (flag == 1)
                ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.rePhone));
            else
                ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.unbind));
        }
    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.sendCodeTv:
                    startTimer();
                    break;
                case R.id.backIv:
                    finish();
                    break;
                case R.id.saveLl:
                    if (userEt.getText().toString().equals("")){
                        showToast(getString(R.string.inputUser));
                    }else if (verificationCodeEt.getText().toString().equals("")){
                        showToast(getString(R.string.inputVerification));
                    }else {
                        ProgressHudModel.newInstance().show(BindPhoneOrMailActivity.this,getString(R.string.waitting),
                                getString(R.string.httpError),10000);
                        if (flag==0){
                            try {
                                HttpMessgeUtil.getInstance(mContext).postForBindEmail(userEt.getText().toString(),
                                        verificationCodeEt.getText().toString(),BINDMAIL_FLAG);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else if (flag == 1){
                            try {
                                HttpMessgeUtil.getInstance(mContext).postForBindPhone(codeTv.getText().toString(),userEt.getText().toString(),
                                        verificationCodeEt.getText().toString(),BINDPHONE_FLAG);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else if (flag == 2){
                            try {
                                HttpMessgeUtil.getInstance(mContext).getForUnbindEmail(UNBINDMAIL_FLAG);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else if (flag == 3){
                            try {
                                HttpMessgeUtil.getInstance(mContext).getForUnbindPhone(UNBINDPHONE_FLAG);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                case R.id.layout:
                    break;
            }
        }
    };

    /**
     * 开始倒计时
     * */
    private void startTimer(){
        try {
            if (flag == 0||flag == 2)
                HttpMessgeUtil.getInstance(mContext).getVerificationCode("2","","",userEt.getText().toString(),GETVERIFICATION_FLAG);
            else
                HttpMessgeUtil.getInstance(mContext).getVerificationCode("1",
                        codeTv.toString(),userEt.getText().toString(),"",GETVERIFICATION_FLAG);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendTv.setEnabled(false);
        time = 60;
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(100);
            }
        };
        timer = new Timer();
        timer.schedule(timerTask,1000,1000);

    }


    @Override
    public void onCallback(BaseApi baseApi,int id) {
        if (id != GETVERIFICATION_FLAG){
            ProgressHudModel.newInstance().diss();
            if (flag == 0||flag == 1){
                handler.sendEmptyMessage(255);
            } else if (flag == 2||flag == 3){
                handler.sendEmptyMessage(256);
            }
        }
    }
}
