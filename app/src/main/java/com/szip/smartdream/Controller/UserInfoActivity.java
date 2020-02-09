package com.szip.smartdream.Controller;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.szip.smartdream.Bean.UserInfo;
import com.szip.smartdream.DB.SaveDataUtil;
import com.szip.smartdream.MyApplication;
import com.szip.smartdream.R;
import com.szip.smartdream.Util.DateUtil;
import com.szip.smartdream.Util.StatusBarCompat;
import com.szip.smartdream.View.DialogBottom;

import static com.szip.smartdream.MyApplication.FILE;

public class UserInfoActivity extends BaseActivity{

    private Context mContext;

    /**
     * 邮箱
     * */
    private LinearLayout mailLl;
    private TextView mailTv;
    private boolean isMail;
    /**
     *电话
     * */
    private LinearLayout phoneLl;
    private TextView phoneTv;
    /**
     *修改密码
     * */
    private LinearLayout resetPasswordLl;
    private DialogBottom dialogBottom;

    private LinearLayout logoutLl;

    private Dialog dialog;
    private ImageView backIv;
    private MyApplication app;

    private SharedPreferences sharedPreferences;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 200:

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_user_info);
        StatusBarCompat.translucentStatusBar(UserInfoActivity.this,true);
        mContext = getApplicationContext();
        app = (MyApplication) getApplicationContext();
        initView();
        initEvent();
        initData();
    }

    private void initData() {
        UserInfo userInfo = app.getUserInfo();
        if (userInfo.getEmail()!=null){
            mailTv.setText(userInfo.getEmail());
        }else
            mailTv.setText(getString(R.string.noMail));
        if (userInfo.getPhoneNumber()!=null){
            phoneTv.setText(userInfo.getPhoneNumber());
        }else
            phoneTv.setText(getString(R.string.noPhone));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    /**
     * 初始化界面
     * */
    private void initView() {
        ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.userInfo));
        backIv = findViewById(R.id.backIv);
        backIv.setVisibility(View.VISIBLE);
        mailLl = findViewById(R.id.mailLl);
        mailTv = findViewById(R.id.mailTv);
        phoneLl = findViewById(R.id.phoneLl);
        phoneTv = findViewById(R.id.phoneTv);
        resetPasswordLl = findViewById(R.id.resetPasswordLl);
        logoutLl = findViewById(R.id.logoutLl);

        dialogBottom = new DialogBottom(this,onClickListener);
    }

    /**
     * 初始化事件监听
     * */
    private void initEvent() {
        mailLl.setOnClickListener(onClickListener);
        phoneLl.setOnClickListener(onClickListener);
        resetPasswordLl.setOnClickListener(onClickListener);
        backIv.setOnClickListener(onClickListener);
        logoutLl.setOnClickListener(onClickListener);
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
                case R.id.mailLl:{
                    isMail = true;
                    if (phoneTv.getText().toString().equals(getString(R.string.noPhone))){
                        dialog = dialogBottom.show(getString(R.string.unbind),getString(R.string.reMail),false);
                    } else{
                        if (mailTv.getText().toString().equals(getString(R.string.noMail)))
                            dialog = dialogBottom.show(getString(R.string.unbind),getString(R.string.bindMail),false);
                        else
                            dialog = dialogBottom.show(getString(R.string.unbind),getString(R.string.reMail),true);
                    }

                }
                break;
                case R.id.phoneLl:{
                    isMail = false;
                    if (mailTv.getText().toString().equals(getString(R.string.noMail))){
                        dialog = dialogBottom.show(getString(R.string.unbind),getString(R.string.rePhone),false);
                    }
                    else{
                        if (phoneTv.getText().toString().equals(getString(R.string.noPhone)))
                            dialog = dialogBottom.show(getString(R.string.unbind),getString(R.string.bindPhone),false);
                        else
                            dialog = dialogBottom.show(getString(R.string.unbind),getString(R.string.rePhone),true);
                    }

                }
                break;
                case R.id.resetPasswordLl:{
                    Intent intent = new Intent();
                    intent.putExtra("flag","change");
                    intent.setClass(UserInfoActivity.this,ChangePasswordActivity.class);
                    startActivity(intent);
                }
                break;
                case R.id.button1:{
                    if (isMail){
                        Intent intent = new Intent();
                        intent.setClass(UserInfoActivity.this,BindPhoneOrMailActivity.class);
                        intent.putExtra("flag",2);
                        startActivity(intent);
                    }else {
                        Intent intent = new Intent();
                        intent.setClass(UserInfoActivity.this,BindPhoneOrMailActivity.class);
                        intent.putExtra("flag",3);
                        startActivity(intent);
                    }
                    dialog.dismiss();
                }
                break;
                case R.id.button2:{
                    if (isMail){
                        Intent intent = new Intent();
                        intent.setClass(UserInfoActivity.this,BindPhoneOrMailActivity.class);
                        intent.putExtra("flag",0);
                        startActivity(intent);
                    }else {
                        Intent intent = new Intent();
                        intent.setClass(UserInfoActivity.this,BindPhoneOrMailActivity.class);
                        intent.putExtra("flag",1);
                        startActivity(intent);
                    }
                    dialog.dismiss();
                }
                break;
                case R.id.btn_cancel:
                    dialog.dismiss();
                    break;
                case R.id.logoutLl:
                    app.clearClockList();
                    app.setReportDate(DateUtil.getStringToDate("today"));
                    SaveDataUtil.newInstance(UserInfoActivity.this).clearDB();
                    if (sharedPreferences==null)
                        sharedPreferences = getSharedPreferences(FILE,MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token",null);
                    editor.commit();
                    Intent intent = new Intent();
                    intent.setClass(UserInfoActivity.this,LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };
}
