package com.szip.smartdream.Controller.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.szip.smartdream.Controller.ForgetPasswordActivity;
import com.szip.smartdream.Interface.OnClickForLogin;
import com.szip.smartdream.R;

import static com.szip.smartdream.MyApplication.FILE;


public class LoginForMailFragment extends BaseFragment {

    private OnClickForLogin clickForLogin;

    /**
     * 忘记密码
     * */
    private TextView forgetTv;
    /**
     *邮箱输入框以及相关控件
     * */
    private EditText mailEt;
    private ImageView mailClearIv;
    /**
     *密码输入框以及相关控件
     * */
    private EditText passwordEt;
    private ImageView passwordClearIv;
    private CheckBox rememberCb;
    private CheckBox lawsCb;
    /**
     *登录按钮
     * */
    private LinearLayout loginLl;

    /**
     * 轻量级文件
     * */
    private SharedPreferences sharedPreferencesp;
    ;

    /**
     *用于确定下一步按键是否可点击
     * */
    private int flagForEt;



    /**
     * 返回一个fragment实例，Activity中调用
     * */
    public static LoginForMailFragment newInstance(String param){
        Bundle bundle = new Bundle();
        bundle.putString("param",param);
        LoginForMailFragment fragment = new LoginForMailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login_for_mail;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {
        initView();
        initEvent();
    }

    public void setClickForLogin(OnClickForLogin clickForLogin){
        this.clickForLogin = clickForLogin;
    }

    /**
     *初始化界面
     * */
    private void initView() {
        lawsCb = getView().findViewById(R.id.lawsCb);
        forgetTv = getView().findViewById(R.id.forgetTv);
        mailEt = getView().findViewById(R.id.mailEt);
        mailClearIv = getView().findViewById(R.id.mailClearIv);
        passwordEt = getView().findViewById(R.id.passwordEt);
        passwordClearIv = getView().findViewById(R.id.passwordClearIv);
        rememberCb = getView().findViewById(R.id.rememberCb);
        loginLl = getView().findViewById(R.id.loginLl);

        if (sharedPreferencesp==null)
            sharedPreferencesp = getActivity().getSharedPreferences(FILE,Context.MODE_PRIVATE);
        mailEt.setText(sharedPreferencesp.getString("mail",""));
        passwordEt.setText(sharedPreferencesp.getString("password",""));
    }

    /**
     *初始化监听
     * */
    private void initEvent() {
        forgetTv.setOnClickListener(onClickListener);
        mailClearIv.setOnClickListener(onClickListener);
        passwordClearIv.setOnClickListener(onClickListener);
        loginLl.setOnClickListener(onClickListener);

        lawsCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String psd = passwordEt.getText().toString();
                if (isChecked){
                    passwordEt.setInputType(0x90);
                }else {
                    passwordEt.setInputType(0x81);
                }
                passwordEt.setSelection(psd.length());
            }
        });
        mailEt.addTextChangedListener(watcher);
        passwordEt.addTextChangedListener(watcher);
        mailEt.setOnFocusChangeListener(focusChangeListener);
        passwordEt.setOnFocusChangeListener(focusChangeListener);
    }

    /**
     *事件监听
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
                case R.id.loginLl:
                    if (mailEt.getText().toString().equals("")){
                        showToast(getString(R.string.inputMail));
                    }else if (passwordEt.getText().toString().equals("")){
                        showToast(getString(R.string.password));
                    }else if (clickForLogin!=null)
                        clickForLogin.onLogin("",mailEt.getText().toString(),passwordEt.getText().toString(),rememberCb.isChecked());
                    break;
                case R.id.forgetTv:
                    Intent intent = new Intent();
                    intent.setClass(getActivity(),ForgetPasswordActivity.class);
                    intent.putExtra("flag",1);
                    startActivity(intent);
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
                        mailClearIv.setVisibility(View.GONE);
                    }else {
                        mailClearIv.setVisibility(View.VISIBLE);
                    }
                    break;
                case 1:
                    if (TextUtils.isEmpty(data)){
                        passwordClearIv.setVisibility(View.GONE);
                    }else {
                        passwordClearIv.setVisibility(View.VISIBLE);
                    }
                    break;
            }
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
                case R.id.passwordEt:
                    if (!hasFocus)
                        passwordClearIv.setVisibility(View.GONE);
                    else {
                        flagForEt = 1;
                        if (!passwordEt.getText().toString().equals(""))
                            passwordClearIv.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    };

}
