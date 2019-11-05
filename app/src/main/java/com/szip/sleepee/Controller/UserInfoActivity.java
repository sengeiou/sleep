package com.szip.sleepee.Controller;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.szip.sleepee.Bean.HttpBean.UserInfoBean;
import com.szip.sleepee.DB.SaveDataUtil;
import com.szip.sleepee.Interface.HttpCallbackWithUserInfo;
import com.szip.sleepee.Model.ProgressHudModel;
import com.szip.sleepee.MyApplication;
import com.szip.sleepee.R;
import com.szip.sleepee.Service.BleService;
import com.szip.sleepee.Util.DateUtil;
import com.szip.sleepee.Util.HttpMessgeUtil;
import com.szip.sleepee.Util.JsonGenericsSerializator;
import com.szip.sleepee.Util.StatusBarCompat;
import com.szip.sleepee.View.DialogBottom;
import com.zhy.http.okhttp.callback.GenericsCallback;

import java.io.IOException;

import okhttp3.Call;

import static com.szip.sleepee.Util.HttpMessgeUtil.GETINFO_FLAG;

public class UserInfoActivity extends BaseActivity implements HttpCallbackWithUserInfo{

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
    private String FILE = "sleepEE";

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 200:
                    UserInfoBean infoBean = (UserInfoBean) msg.obj;
                    if (infoBean.getData().getEmail()!=null){
                        mailTv.setText(infoBean.getData().getEmail());
                    }else
                        mailTv.setText(getString(R.string.noMail));
                    if (infoBean.getData().getPhoneNumber()!=null){
                        phoneTv.setText(infoBean.getData().getPhoneNumber());
                    }else
                        phoneTv.setText(getString(R.string.noPhone));
                    break;
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithUserInfo(this);
        ProgressHudModel.newInstance().show(UserInfoActivity.this,getString(R.string.waitting),getString(R.string.httpError),10000);
        try {
            HttpMessgeUtil.getInstance(mContext).getForGetInfo(GETINFO_FLAG);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithUserInfo(null);
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
                        Log.d("SZIP******","邮箱解除绑定");
                        Intent intent = new Intent();
                        intent.setClass(UserInfoActivity.this,BindPhoneOrMailActivity.class);
                        intent.putExtra("flag",2);
                        startActivity(intent);
                    }else {
                        Log.d("SZIP******","电话解除绑定");
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
                    BleService.getInstance().disConnect();
                    BleService.getInstance().setmMac(null);
                    app.setUserInfo(null);
                    app.clearClockList();
                    app.setReportDate(DateUtil.getStringToDate("today"));
                    SaveDataUtil.newInstance(UserInfoActivity.this).clearDB();
                    if (sharedPreferences==null)
                        sharedPreferences = getSharedPreferences(FILE,MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLogin",false);
                    editor.putBoolean("isBind",false);
                    editor.commit();
                    Intent intent = new Intent();
                    intent.setClass(UserInfoActivity.this,LoginActivity.class);
                    Intent in=new Intent();
                    setResult(101, in);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };


    @Override
    public void onUserInfo(UserInfoBean userInfoBean) {
        ProgressHudModel.newInstance().diss();
        Message msg = new Message();
        msg.what = 200;
        msg.obj = userInfoBean;
        handler.sendMessage(msg);
    }
}
