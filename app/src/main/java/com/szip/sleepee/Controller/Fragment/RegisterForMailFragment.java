package com.szip.sleepee.Controller.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;

import com.szip.sleepee.Bean.HttpBean.BaseApi;
import com.szip.sleepee.Interface.HttpCallbackWithBase;
import com.szip.sleepee.Interface.OnClickForRegister;
import com.szip.sleepee.MyApplication;
import com.szip.sleepee.R;
import com.szip.sleepee.Util.HttpMessgeUtil;
import com.szip.sleepee.Util.JsonGenericsSerializator;
import com.zhy.http.okhttp.callback.GenericsCallback;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;

import static com.szip.sleepee.Util.HttpMessgeUtil.GETVERIFICATION_FLAG;
import static com.szip.sleepee.Util.MathUitl.isEmail;

public class RegisterForMailFragment extends BaseFragment implements HttpCallbackWithBase{

    private Context mContext;

    private OnClickForRegister onClickForRegister;

    /**
     * 隐私条款
     * */
    private CheckBox checkBox;
    private TextView privacyTv;
    /**
     * 邮箱输入框以及相关控件
     * */
    private EditText mailEt;
    private ImageView mailClearIv;
    /**
     *验证码输入框已经获取按钮
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
     *注册按钮
     * */
    private LinearLayout signLl;

    /**
     *用于确定下一步按键是否可点击
     * */
    private int flagForEt;

    private KProgressHUD progressHUD;

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

    /**
     * 返回一个fragment实例，Activity中调用
     * */
    public static RegisterForMailFragment newInstance(String param){
        Bundle bundle = new Bundle();
        bundle.putString("param",param);
        RegisterForMailFragment fragment = new RegisterForMailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setOnClickForRegister(OnClickForRegister onClickForRegister){
        this.onClickForRegister = onClickForRegister;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_register_for_mail;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {

        mContext = getActivity().getApplicationContext();
        initView();
        initEvent();
    }

    @Override
    public void onResume() {
        super.onResume();
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithBase(this);
    }

    @Override
    public void onPause() {
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
        checkBox = getView().findViewById(R.id.checkbox);
        privacyTv = getView().findViewById(R.id.privacyTv);
        privacyTv.setMovementMethod(LinkMovementMethod.getInstance());
        passwordEt = getView().findViewById(R.id.passwordEt);
        passwordClearIv = getView().findViewById(R.id.passwordClearIv);
        confirmPasswordEt = getView().findViewById(R.id.confirmPasswordEt);
        confirmPasswordClearIv = getView().findViewById(R.id.confirmPasswordClearIv);
        mailEt = getView().findViewById(R.id.mailEt);
        mailClearIv = getView().findViewById(R.id.mailClearIv);
        verificationCodeEt = getView().findViewById(R.id.verificationCodeEt);
        sendTv = getView().findViewById(R.id.sendCodeTv);
        signLl = getView().findViewById(R.id.signLl);

    }

    /**
     * 初始化事件监听
     * */
    private void initEvent() {
        sendTv.setOnClickListener(onClickListener);
        signLl.setOnClickListener(onClickListener);
        mailClearIv.setOnClickListener(onClickListener);
        passwordClearIv.setOnClickListener(onClickListener);
        confirmPasswordClearIv.setOnClickListener(onClickListener);

        passwordEt.addTextChangedListener(watcher);
        passwordEt.setOnFocusChangeListener(focusChangeListener);
        confirmPasswordEt.addTextChangedListener(watcher);
        confirmPasswordEt.setOnFocusChangeListener(focusChangeListener);
        mailEt.addTextChangedListener(watcher);
        verificationCodeEt.addTextChangedListener(watcher);
        mailEt.setOnFocusChangeListener(focusChangeListener);
        verificationCodeEt.setOnFocusChangeListener(focusChangeListener);
    }

    /**
     * 事件监听
     * */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.mailClearIv:
                    mailEt.setText("");
                    break;
                case R.id.passwordClearIv:
                    passwordEt.setText("");
                    break;
                case R.id.confirmPasswordClearIv:
                    confirmPasswordEt.setText("");
                    break;
                case R.id.sendCodeTv:
                    if (mailEt.getText().toString().equals("")){
                        showToast(getString(R.string.inputMail));
                    }else if (!isEmail(mailEt.getText().toString())){
                        showToast(getString(R.string.emailError));
                    }else
                        startTimer();
                    break;
                case R.id.signLl:
                    if (mailEt.getText().toString().equals("")){
                        showToast(getString(R.string.inputMail));
                    }else if (passwordEt.getText().toString().equals("")){
                        showToast(getString(R.string.inputPassword));
                    }else if (confirmPasswordEt.getText().toString().equals("")){
                        showToast(getString(R.string.inoutConfirmPassword));
                    }else if (verificationCodeEt.getText().toString().equals("")){
                        showToast(getString(R.string.inputVerification));
                    }else if (!passwordEt.getText().toString().equals(confirmPasswordEt.getText().toString())){
                        showToast(getString(R.string.confirmPasswordError));
                    }else if (!checkBox.isChecked()){
                        showToast(getString(R.string.checkPrivacy));
                    }else
                        onClickForRegister.onRegisterForMail(mailEt.getText().toString(),passwordEt.getText().toString(),
                                verificationCodeEt.getText().toString());
                    break;
            }
        }
    };

    /**
     * 开始倒计时
     * */
    private void startTimer(){
        try {
            HttpMessgeUtil.getInstance(mContext).getVerificationCode("2","","",mailEt.getText().toString(),GETVERIFICATION_FLAG);
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
                        mailClearIv.setVisibility(View.GONE);
                    }else {
                        mailClearIv.setVisibility(View.VISIBLE);
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
                case R.id.mailEt:
                    if (!hasFocus)
                        mailClearIv.setVisibility(View.GONE);
                    else {
                        flagForEt = 0;
                        if (!mailEt.getText().toString().equals(""))
                            mailClearIv.setVisibility(View.VISIBLE);
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

//    /**
//     * 获取验证码回调
//     * */
//    private GenericsCallback<BaseApi> callback = new GenericsCallback<BaseApi>(new JsonGenericsSerializator()) {
//        @Override
//        public void onError(Call call, Exception e, int id) {
//
//        }
//
//        @Override
//        public void onResponse(BaseApi response, int id) {
//
//            if (response.getCode() != 200){
//                showToast(response.getMessage());
//            }
//
//
//        }
//    };


    @Override
    public void onCallback(BaseApi baseApi, int id) {
        if (progressHUD!=null){
            progressHUD.dismiss();
            progressHUD = null;
        }
    }
}
