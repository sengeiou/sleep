package com.szip.sleepee.Controller;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.szip.sleepee.Bean.HttpBean.BaseApi;
import com.szip.sleepee.Interface.HttpCallbackWithBase;
import com.szip.sleepee.Model.ProgressHudModel;
import com.szip.sleepee.R;
import com.szip.sleepee.Util.HttpMessgeUtil;
import com.szip.sleepee.Util.JsonGenericsSerializator;
import com.szip.sleepee.Util.StatusBarCompat;
import com.zaaach.citypicker.CityPicker;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.model.City;
import com.zhy.http.okhttp.callback.GenericsCallback;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;

import static com.szip.sleepee.Util.HttpMessgeUtil.FOTGET_FLAG;
import static com.szip.sleepee.Util.HttpMessgeUtil.GETVERIFICATION_FLAG;
import static com.szip.sleepee.Util.MathUitl.isEmail;

public class ForgetPasswordActivity extends BaseActivity implements HttpCallbackWithBase{

    private Context mContext;

    /**
     * 选择国家的布局，用户使用邮箱找回密码的时候隐藏
     * */
    private LinearLayout layout;
    /**
     * 输入框图标，显示电话或者邮件
     * */
    private ImageView userIv;
    /**
     *选择国家以及国家代码
     * */
    private TextView countryTv;
    private TextView codeTv;
    /**
     *用户账号输入框（手机或者邮箱）以及相应控件
     * */
    private EditText userEt;
    private ImageView userClearIv;
    /**
     *验证码输入框以及相应控件
     * */
    private EditText verificationCodeEt;
    private TextView sendTv;
    private Timer timer;
    private int time;
    /**
     *输入密码以及确认密码相关控件
     * */
    private EditText passwordEt;
    private ImageView passwordClearIv;
    private EditText confirmPasswordEt;
    private ImageView confirmPasswordClearIv;
    /**
     *确定更改密码按钮
     * */
    private LinearLayout saveLl;
    /**
     *判断是否是用手机找回
     * */
    private boolean isPhone;

    private int flagForEt;

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
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_forget_password);
        mContext = getApplicationContext();
        Intent intent = getIntent();
        isPhone = intent.getIntExtra("flag",0) == 0?true:false;
        initView();
        initEvent();
    }

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
    public void onDestroy() {
        super.onDestroy();
        if (timer!=null)
            timer.cancel();
    }

    /**
     * 初始化界面
     * */
    private void initView() {
        StatusBarCompat.translucentStatusBar(ForgetPasswordActivity.this,true);
        ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.retrieve));
        if (isPhone){
            layout = findViewById(R.id.countryLl);
            layout.setVisibility(View.VISIBLE);
            userIv = findViewById(R.id.userIv);
            userIv.setImageResource(R.mipmap.signup_icon_phone);
            countryTv = findViewById(R.id.countryTv);
            codeTv = findViewById(R.id.codeTv);
            codeTv.setVisibility(View.VISIBLE);
            userEt = findViewById(R.id.userEt);
            userClearIv = findViewById(R.id.userClearIv);
            verificationCodeEt = findViewById(R.id.verificationCodeEt);
            passwordEt = findViewById(R.id.passwordEt);
            passwordClearIv = findViewById(R.id.passwordClearIv);
            confirmPasswordEt = findViewById(R.id.confirmPasswordEt);
            confirmPasswordClearIv = findViewById(R.id.confirmPasswordClearIv);
            sendTv = findViewById(R.id.sendCodeTv);
            saveLl = findViewById(R.id.saveLl);
        }else {
            userIv = findViewById(R.id.userIv);
            userIv.setImageResource(R.mipmap.signup_icon_email);
            userEt = findViewById(R.id.userEt);
            userEt.setHint(getString(R.string.email));
            userClearIv = findViewById(R.id.userClearIv);
            verificationCodeEt = findViewById(R.id.verificationCodeEt);
            passwordEt = findViewById(R.id.passwordEt);
            passwordClearIv = findViewById(R.id.passwordClearIv);
            confirmPasswordEt = findViewById(R.id.confirmPasswordEt);
            confirmPasswordClearIv = findViewById(R.id.confirmPasswordClearIv);
            sendTv = findViewById(R.id.sendCodeTv);
            saveLl = findViewById(R.id.saveLl);
        }
    }

    /**
     * 初始化事件监听
     * */
    private void initEvent() {
        findViewById(R.id.backIv).setOnClickListener(onClickListener);
        if (isPhone){
            layout.setOnClickListener(onClickListener);
            userClearIv.setOnClickListener(onClickListener);
            passwordClearIv.setOnClickListener(onClickListener);
            confirmPasswordClearIv.setOnClickListener(onClickListener);
            sendTv.setOnClickListener(onClickListener);
            saveLl.setOnClickListener(onClickListener);



            userEt.addTextChangedListener(watcher);
            userEt.setOnFocusChangeListener(focusChangeListener);
            verificationCodeEt.addTextChangedListener(watcher);
            verificationCodeEt.setOnFocusChangeListener(focusChangeListener);
            passwordEt.addTextChangedListener(watcher);
            passwordEt.setOnFocusChangeListener(focusChangeListener);
            confirmPasswordEt.addTextChangedListener(watcher);
            confirmPasswordEt.setOnFocusChangeListener(focusChangeListener);
        }else {
            userClearIv.setOnClickListener(onClickListener);
            passwordClearIv.setOnClickListener(onClickListener);
            confirmPasswordClearIv.setOnClickListener(onClickListener);
            sendTv.setOnClickListener(onClickListener);
            saveLl.setOnClickListener(onClickListener);


            userEt.addTextChangedListener(watcher);
            userEt.setOnFocusChangeListener(focusChangeListener);
            verificationCodeEt.addTextChangedListener(watcher);
            verificationCodeEt.setOnFocusChangeListener(focusChangeListener);
            passwordEt.addTextChangedListener(watcher);
            passwordEt.setOnFocusChangeListener(focusChangeListener);
            confirmPasswordEt.addTextChangedListener(watcher);
            confirmPasswordEt.setOnFocusChangeListener(focusChangeListener);
        }
    }

    /**
     * 事件监听
     * */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.backIv:
                    finish();
                    break;
                case R.id.countryLl:
                    CityPicker.getInstance()
                            .setFragmentManager(ForgetPasswordActivity.this.getSupportFragmentManager())
                            .enableAnimation(true)
                            .setAnimationStyle(R.style.CustomAnim)
                            .setLocatedCity(null)
                            .setHotCities(null)
                            .setOnPickListener(new OnPickListener() {
                                @Override
                                public void onPick(int position, City data) {
                                    countryTv.setText(data == null ? "" :  data.getName());
                                    codeTv.setText(data.getCode());

                                }
                                @Override
                                public void onLocate() {
                                }
                            }).show();
                    break;
                case R.id.userClearIv:
                    userEt.setText("");
                    break;
                case R.id.passwordClearIv:
                    passwordEt.setText("");
                    break;
                case R.id.confirmPasswordClearIv:
                    confirmPasswordEt.setText("");
                    break;
                case R.id.sendCodeTv:
                    if (userEt.getText().toString().equals("")){
                        if(isPhone)
                            showToast(getString(R.string.inputNum));
                        else
                            showToast(getString(R.string.inputMail));
                    }else {
                        if (!isPhone){
                            if (!isEmail(userEt.getText().toString()))
                                showToast(getString(R.string.emailError));
                            else
                                startTimer();
                        } else
                            startTimer();
                    }
                    break;
                case R.id.saveLl:

                    if (userEt.getText().toString().equals("")){
                        if (isPhone)
                            showToast(getString(R.string.inputNum));
                        else
                            showToast(getString(R.string.inputMail));
                    }else if (verificationCodeEt.getText().toString().equals("")){
                        showToast(getString(R.string.inputVerification));
                    }else if (passwordEt.getText().toString().equals("")){
                        showToast(getString(R.string.inputPassword));
                    }else if (confirmPasswordEt.getText().toString().equals("")){
                        showToast(getString(R.string.inoutConfirmPassword));
                    }else if (!passwordEt.getText().toString().equals(confirmPasswordEt.getText().toString())){
                        showToast(getString(R.string.confirmPasswordError));
                    }else {
                        ProgressHudModel.newInstance().show(ForgetPasswordActivity.this,getString(R.string.waitting)
                                ,getString(R.string.httpError),10000);
                        try {
                            if (codeTv.getVisibility()==View.VISIBLE)//手机
                            HttpMessgeUtil.getInstance(mContext).postForgotPassword("1",codeTv.getText().toString(),userEt.getText().toString()
                                    ,"", verificationCodeEt.getText().toString(),passwordEt.getText().toString(),FOTGET_FLAG);
                            else //邮箱
                                HttpMessgeUtil.getInstance(mContext).postForgotPassword("2","",""
                                        ,userEt.getText().toString(), verificationCodeEt.getText().toString(),passwordEt.getText().toString(),FOTGET_FLAG);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }


                    break;
            }
        }
    };

    /**
     * 开始倒计时
     * */
    private void startTimer(){
        try {
            if (isPhone)
                HttpMessgeUtil.getInstance(mContext).getVerificationCode("1",codeTv.getText().toString(),
                        userEt.getText().toString(),"",GETVERIFICATION_FLAG);
            else
                HttpMessgeUtil.getInstance(mContext).getVerificationCode("2","","",userEt.getText().toString(),GETVERIFICATION_FLAG);

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

    /**
     * 输入框键入监听
     * */
    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String data = s.toString();
            switch (flagForEt){
                case 0:
                    if (TextUtils.isEmpty(data)){
                        userClearIv.setVisibility(View.GONE);
                    }else {
                        userClearIv.setVisibility(View.VISIBLE);
                    }
                    break;
                case 2:
                    if (TextUtils.isEmpty(data)){
                        passwordClearIv.setVisibility(View.GONE);
                    }else {
                        passwordClearIv.setVisibility(View.VISIBLE);
                    }
                    break;
                case 3:
                    if (TextUtils.isEmpty(data)){
                        confirmPasswordClearIv.setVisibility(View.GONE);
                    }else {
                        confirmPasswordClearIv.setVisibility(View.VISIBLE);
                    }
                    break;
            }
//            nextBtn.setEnabled((userIsEmpty||verificationCodeIsEmpty)?false:true);
        }
    };

    /**
     * 输入框焦点监听
     * */
    private View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch (v.getId()){
                case R.id.userEt:
                    if (!hasFocus)
                        userClearIv.setVisibility(View.GONE);
                    else {
                        flagForEt = 0;
                        if (!userEt.getText().toString().equals(""))
                            userClearIv.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.verificationCodeEt:
                    if (hasFocus)
                        flagForEt = 1;
                    break;
                case R.id.passwordEt:
                    if (!hasFocus)
                        passwordClearIv.setVisibility(View.GONE);
                    else {
                        flagForEt = 2;
                        if (!passwordEt.getText().toString().equals(""))
                            passwordClearIv.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.confirmPasswordEt:
                    if (!hasFocus)
                        confirmPasswordClearIv.setVisibility(View.GONE);
                    else {
                        flagForEt = 3;
                        if (!confirmPasswordEt.getText().toString().equals(""))
                            confirmPasswordClearIv.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    };



    @Override
    public void onCallback(BaseApi baseApi, int id) {
        if (id != GETVERIFICATION_FLAG){
            ProgressHudModel.newInstance().diss();
            showToast(getString(R.string.resetSuccess));
            finish();
        }
    }
}
