package com.szip.sleepee.Controller;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.zhy.http.okhttp.callback.GenericsCallback;

import java.io.IOException;

import okhttp3.Call;

import static com.szip.sleepee.Util.HttpMessgeUtil.CHANGEPASSWORD_FLAG;

public class ChangePasswordActivity extends BaseActivity implements HttpCallbackWithBase{

    private Context mContext;

    /**
     * 旧密码输入框以及相关控件
     * */
    private EditText oldPasswordEt;
    private ImageView oldPasswordClearIv;

    /**
     * 密码输入框以及相关控件
     * */
    private EditText passwordEt;
    private ImageView passwordClearIv;
    /**
     * 重复密码输入框以及相关控件
     * */
    private EditText confirmPasswordEt;
    private ImageView confirmPasswordClearIv;

    private CheckBox lawsCb,lawsCb1,lawsCb2;

    /**
     * 完成
     * */
    private LinearLayout completeLl;

    /**
     *用于确定下一步按键是否可点击
     * */
    private int flagForEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_set_password);
        mContext = getApplicationContext();
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

    /**
     * 初始化界面
     * */
    private void initView() {
        findViewById(R.id.backIv).setOnClickListener(onClickListener);

        StatusBarCompat.translucentStatusBar(ChangePasswordActivity.this,true);
        ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.resetPassword));

        lawsCb = findViewById(R.id.lawsCb);
        lawsCb1 = findViewById(R.id.lawsCb1);
        lawsCb2 = findViewById(R.id.lawsCb2);
        oldPasswordEt = findViewById(R.id.oldPasswordEt);
        oldPasswordClearIv = findViewById(R.id.oldPasswordClearIv);
        passwordEt = findViewById(R.id.passwordEt);
        passwordClearIv = findViewById(R.id.passwordClearIv);
        confirmPasswordEt = findViewById(R.id.confirmPasswordEt);
        confirmPasswordClearIv = findViewById(R.id.confirmPasswordClearIv);
        completeLl = findViewById(R.id.completeLl);
    }

    /**
     * 初始化事件监听
     * */
    private void initEvent() {
        oldPasswordClearIv.setOnClickListener(onClickListener);
        passwordClearIv.setOnClickListener(onClickListener);
        confirmPasswordClearIv.setOnClickListener(onClickListener);

        oldPasswordEt.addTextChangedListener(watcher);
        passwordEt.addTextChangedListener(watcher);
        confirmPasswordEt.addTextChangedListener(watcher);
        oldPasswordEt.setOnFocusChangeListener(onFocusChangeListener);
        passwordEt.setOnFocusChangeListener(onFocusChangeListener);
        confirmPasswordEt.setOnFocusChangeListener(onFocusChangeListener);
        completeLl.setOnClickListener(onClickListener);

        lawsCb.setOnCheckedChangeListener(checkedChangeListener);

        lawsCb1.setOnCheckedChangeListener(checkedChangeListener);

        lawsCb2.setOnCheckedChangeListener(checkedChangeListener);
    }

    private CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()){
                case R.id.lawsCb:
                    String psd = oldPasswordEt.getText().toString();
                    if (isChecked){
                        oldPasswordEt.setInputType(0x90);
                    }else {
                        oldPasswordEt.setInputType(0x81);
                    }
                    oldPasswordEt.setSelection(psd.length());
                    break;
                case R.id.lawsCb1:
                    String psd1 = passwordEt.getText().toString();
                    if (isChecked){
                        passwordEt.setInputType(0x90);
                    }else {
                        passwordEt.setInputType(0x81);
                    }
                    passwordEt.setSelection(psd1.length());
                    break;
                case R.id.lawsCb2:
                    String psd2 = confirmPasswordEt.getText().toString();
                    if (isChecked){
                        confirmPasswordEt.setInputType(0x90);
                    }else {
                        confirmPasswordEt.setInputType(0x81);
                    }
                    confirmPasswordEt.setSelection(psd2.length());
                    break;
            }
        }
    };

    /**
     * 事件监听
     * */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.oldPasswordClearIv:
                    oldPasswordEt.setText("");
                    break;
                case R.id.passwordClearIv:
                    passwordEt.setText("");
                    break;
                case R.id.confirmPasswordClearIv:
                    confirmPasswordEt.setText("");
                    break;
                case R.id.completeLl:
                    if (passwordEt.getText().toString().equals(confirmPasswordEt.getText().toString())){
                        ProgressHudModel.newInstance().show(ChangePasswordActivity.this,getString(R.string.waitting)
                                ,getString(R.string.httpError),10000);

                        try {
                            HttpMessgeUtil.getInstance(mContext).postForChangePassword(oldPasswordEt.getText().toString(),
                                    passwordEt.getText().toString(),CHANGEPASSWORD_FLAG);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }else {
                        showToast(getString(R.string.confirmPasswordError));
                    }

                    break;
                case R.id.backIv:
                    finish();
                    break;
            }
        }
    };

    /**
     * 输入框焦点监听
     * */
    private View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch (v.getId()){
                case R.id.passwordEt:
                    if (!hasFocus)
                        passwordClearIv.setVisibility(View.GONE);
                    else {
                        flagForEt = 0;
                        if (!passwordEt.getText().toString().equals(""))
                            passwordClearIv.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.confirmPasswordEt:
                    if (!hasFocus)
                        confirmPasswordClearIv.setVisibility(View.GONE);
                    else {
                        flagForEt = 1;
                        if (!passwordEt.getText().toString().equals(""))
                            confirmPasswordClearIv.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.oldPasswordEt:
                    if (!hasFocus)
                        oldPasswordClearIv.setVisibility(View.GONE);
                    else {
                        flagForEt = 2;
                        if (!oldPasswordEt.getText().toString().equals(""))
                            oldPasswordClearIv.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    };



    /**
     * 输入框键入监听器
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
                        passwordClearIv.setVisibility(View.GONE);
                    }else {
                        passwordClearIv.setVisibility(View.VISIBLE);
                    }
                    break;
                case 1:
                    if (TextUtils.isEmpty(data)){
                        confirmPasswordClearIv.setVisibility(View.GONE);
                    }else {
                        confirmPasswordClearIv.setVisibility(View.VISIBLE);
                    }
                    break;
                case 2:
                    if (TextUtils.isEmpty(data)){
                        oldPasswordClearIv.setVisibility(View.GONE);
                    }else {
                        oldPasswordClearIv.setVisibility(View.VISIBLE);
                    }
                    break;

            }
        }
    };

    @Override
    public void onCallback(BaseApi baseApi, int id) {
        ProgressHudModel.newInstance().diss();
        showToast(getString(R.string.changeSeccuss));
        finish();
    }
}
