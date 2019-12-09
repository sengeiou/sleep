package com.szip.sleepee.Controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.szip.sleepee.Adapter.MyPagerAdapter;
import com.szip.sleepee.Bean.HttpBean.LoginBean;
import com.szip.sleepee.Bean.HttpBean.RegisterBean;
import com.szip.sleepee.Controller.Fragment.RegisterForMailFragment;
import com.szip.sleepee.Controller.Fragment.RegisterForPhoneFragment;
import com.szip.sleepee.Interface.HttpCallbackWithLogin;
import com.szip.sleepee.Interface.HttpCallbackWithRegist;
import com.szip.sleepee.Interface.OnClickForRegister;
import com.szip.sleepee.Model.ProgressHudModel;
import com.szip.sleepee.MyApplication;
import com.szip.sleepee.R;
import com.szip.sleepee.Service.BleService;
import com.szip.sleepee.Util.HttpMessgeUtil;
import com.szip.sleepee.Util.JsonGenericsSerializator;
import com.szip.sleepee.Util.StatusBarCompat;
import com.zhy.http.okhttp.callback.GenericsCallback;
import com.zhy.http.okhttp.https.HttpsUtils;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;

import static com.szip.sleepee.Util.HttpMessgeUtil.LOGIN_FLAG;
import static com.szip.sleepee.Util.HttpMessgeUtil.REGIST_FLAG;

public class RegisterActivity extends BaseActivity implements HttpCallbackWithRegist,HttpCallbackWithLogin{

    private Context mContext;

    private TabLayout mTab;
    private ViewPager mPager;
    private String mailR,phoneR,passwordR,codeR;

    /**
     * 轻量级文件
     * */
    private SharedPreferences sharedPreferencesp;
    private String FILE = "sleepEE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);
        mContext = getApplicationContext();
        initView();
        initPager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithRegist(this);
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithLogin(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithRegist(null);
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithLogin(null);
    }

    /**
     * 初始化界面
     * */
    private void initView() {
        StatusBarCompat.translucentStatusBar(RegisterActivity.this,true);
        ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.sign));
        findViewById(R.id.backIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTab = findViewById(R.id.tvtablayout);
//        MathUitl.reflex(mTab);
        mPager = findViewById(R.id.tvviewpager);

    }

    /**
     * 初始化滑动页面
     * */
    private void initPager() {
        // 创建一个集合,装填Fragment
        ArrayList<Fragment> fragments = new ArrayList<>();
        // 装填
        fragments.add(RegisterForPhoneFragment.newInstance("szip"));
        fragments.add(RegisterForMailFragment.newInstance("szip"));
        ((RegisterForPhoneFragment)fragments.get(0)).setOnClickForRegister(clickForRegister);
        ((RegisterForMailFragment)fragments.get(1)).setOnClickForRegister(clickForRegister);
        // 创建ViewPager适配器
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        myPagerAdapter.setFragmentArrayList(fragments);
        // 给ViewPager设置适配器
        mPager.setAdapter(myPagerAdapter);
        // TabLayout 指示器 (记得自己手动创建4个Fragment,注意是 app包下的Fragment 还是 V4包下的 Fragment)
        mTab.addTab(mTab.newTab());
        mTab.addTab(mTab.newTab());
        // 使用 TabLayout 和 ViewPager 相关联
        mTab.setupWithViewPager(mPager);
        // TabLayout指示器添加文本
        mTab.getTabAt(0).setText(getString(R.string.phone));
        mTab.getTabAt(1).setText(getString(R.string.email));
    }


    private OnClickForRegister clickForRegister = new OnClickForRegister() {
        @Override
        public void onRegisterForPhone(String country, String code, String phone,String password, String verificationCode) {
            ProgressHudModel.newInstance().show(RegisterActivity.this,
                    getString(R.string.waitting),getString(R.string.httpError),10000);
            codeR = code;
            phoneR = phone;
            passwordR = password;
            try {
                HttpMessgeUtil.getInstance(mContext).postRegister("1",code,phone,"",verificationCode,
                        password,REGIST_FLAG);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onRegisterForMail(String mail, String password,String verificationCode) {
            ProgressHudModel.newInstance().show(RegisterActivity.this,
                    getString(R.string.waitting),getString(R.string.httpError),10000);
            mailR = mail;
            passwordR = password;
            try {
                HttpMessgeUtil.getInstance(mContext).postRegister("2","","",mail,verificationCode,
                        password,REGIST_FLAG);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    public void onLogin(LoginBean loginBean) {
        ProgressHudModel.newInstance().diss();
        HttpMessgeUtil.getInstance(mContext).setToken(loginBean.getData().getToken());
        if (sharedPreferencesp == null)
            sharedPreferencesp = getSharedPreferences(FILE,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesp.edit();
        editor.putString("token",loginBean.getData().getToken());
        editor.putBoolean("isLogin",true);
        editor.putString("phone",loginBean.getData().getUserInfo().getPhoneNumber());
        editor.putString("mail",loginBean.getData().getUserInfo().getEmail());
        ((MyApplication)getApplicationContext()).setUserInfo(loginBean.getData().getUserInfo());
        editor.commit();

        startActivity(new Intent(mContext, FindDeviceActivity.class));

        Intent in = new Intent();
        in.putExtra("STRING","exit");
        setResult(10,in);
        finish();
    }

    @Override
    public void onRegist(RegisterBean registerBean) {
        try {
            if (mailR==null){
                HttpMessgeUtil.getInstance(mContext).postLogin("1",codeR,phoneR,"",passwordR,LOGIN_FLAG);
            }else {
                HttpMessgeUtil.getInstance(mContext).postLogin("2","","",mailR,passwordR,LOGIN_FLAG);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
