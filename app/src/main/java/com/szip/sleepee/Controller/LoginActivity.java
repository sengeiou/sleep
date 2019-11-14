package com.szip.sleepee.Controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.szip.sleepee.Adapter.MyPagerAdapter;
import com.szip.sleepee.Bean.HttpBean.ClockDataBean;
import com.szip.sleepee.Bean.HttpBean.LoginBean;
import com.szip.sleepee.Controller.Fragment.LoginForMailFragment;
import com.szip.sleepee.Controller.Fragment.LoginForPhoneFragment;
import com.szip.sleepee.Interface.HttpCallbackWithClockData;
import com.szip.sleepee.Interface.HttpCallbackWithLogin;
import com.szip.sleepee.Interface.HttpCallbackWithReport;
import com.szip.sleepee.Interface.OnClickForLogin;
import com.szip.sleepee.Model.ProgressHudModel;
import com.szip.sleepee.MyApplication;
import com.szip.sleepee.R;
import com.szip.sleepee.Service.BleService;
import com.szip.sleepee.Util.HttpMessgeUtil;
import com.szip.sleepee.Util.JsonGenericsSerializator;
import com.szip.sleepee.Util.StatusBarCompat;
import com.zhy.http.okhttp.callback.GenericsCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.Call;

import static com.szip.sleepee.Util.HttpMessgeUtil.DOWNLOADDATA_FLAG;
import static com.szip.sleepee.Util.HttpMessgeUtil.GETALARM_FLAG;
import static com.szip.sleepee.Util.HttpMessgeUtil.LOGIN_FLAG;

public class LoginActivity extends BaseActivity implements HttpCallbackWithLogin,HttpCallbackWithReport,HttpCallbackWithClockData{

    private Context mContext;

    private TabLayout mTab;
    private ViewPager mPager;

    private TextView registerTv;
//    private TextView forgetTv;

    private KProgressHUD progressHUD;

    private boolean rememberPassword;
    private String passwordL;

    /**
     * 轻量级文件
     * */
    private SharedPreferences sharedPreferencesp;
    private String FILE = "sleepEE";

    /**
     * 回调标识
     * */
    private final static int REQUEST_CODE = 10;
    private final static int RESULT_CODE= 10;


    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        mContext = getApplicationContext();
        initView();
        initEvent();
        initPager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithLogin(this);
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithReport(this);
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithClockData(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithLogin(null);
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithReport(null);
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithClockData(null);
    }

    /**
     * 初始化界面
     * */
    private void initView() {
        StatusBarCompat.translucentStatusBar(LoginActivity.this,true);

        mTab = findViewById(R.id.tvtablayout);
//        MathUitl.reflex(mTab);
        mPager = findViewById(R.id.tvviewpager);

        registerTv = findViewById(R.id.registerTv);
    }

    /**
     * 初始化滑动页面
     * */
    private void initPager() {
        // 创建一个集合,装填Fragment
        ArrayList<Fragment> fragments = new ArrayList<>();
        // 装填
        fragments.add(LoginForPhoneFragment.newInstance("szip"));
        fragments.add(LoginForMailFragment.newInstance("szip"));
        ((LoginForPhoneFragment)fragments.get(0)).setClickForLogin(clickForLogin);
        ((LoginForMailFragment)fragments.get(1)).setClickForLogin(clickForLogin);
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

    /**
     * 初始化事件监听
     * */
    private void initEvent() {
        registerTv.setOnClickListener(onClickListener);
//        forgetTv.setOnClickListener(onClickListener);
    }

    /**
     * 点击事件监听
     * */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.registerTv:{
                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this,RegisterActivity.class);
                    startActivityForResult(intent,REQUEST_CODE);
                }
                break;
            }
        }
    };

    /**
     * 登录
     * */
    private OnClickForLogin clickForLogin = new OnClickForLogin() {
        @Override
        public void onLogin(String code,String user, String password,boolean remember) {
            passwordL = password;
            rememberPassword = remember;
            ProgressHudModel.newInstance().show(LoginActivity.this,getString(R.string.logging),getString(R.string.httpError),10000);
            try {
                if (code.equals(""))
                    HttpMessgeUtil.getInstance(mContext).postLogin("2","","",user,password,LOGIN_FLAG);//邮箱
                else
                    HttpMessgeUtil.getInstance(mContext).postLogin("1",code,user,"",password,LOGIN_FLAG);//手机
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };


    /**
     * 登录接口回调
     * */
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
        if (rememberPassword)
            editor.putString("password",passwordL);
        else
            editor.putString("password","");
        if (loginBean.getData().getUserInfo().getSleepDeviceCode()== null){//如果未绑定手环，跳到绑定页面
            startActivity(new Intent(mContext, FindDeviceActivity.class));
        }else {//如果已绑定睡眠带，获取闹钟列表
            BleService.getInstance().setmMac(loginBean.getData().getUserInfo().getSleepDeviceCode());
            editor.putBoolean("isBind",true);
            try {
                HttpMessgeUtil.getInstance(mContext).getForGetClockList(GETALARM_FLAG);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        editor.commit();
    }

    /**
     * 获取数据接口回调
     * */
    @Override
    public void onReport(boolean isNewData) {
        startActivity(new Intent(mContext,MainActivity.class));
        finish();
    }

    /**
     * 获取闹钟接口回调
     * */
    @Override
    public void onClockData(ClockDataBean clockDataBean) {
        if(clockDataBean.getData().getArray()!=null){
            ((MyApplication)getApplicationContext()).setClockList1(clockDataBean.getData().getArray());
        }
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY,5);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            calendar.set(Calendar.MILLISECOND,0);
            HttpMessgeUtil.getInstance(mContext).getForDownloadReportData(""+(calendar.getTimeInMillis()/1000-30*24*60*60),
                    "30",DOWNLOADDATA_FLAG);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Activity回调函数
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == 10) {
            final String string = data.getStringExtra("STRING");
            if (string!=null&&string.equals("exit")){
                finish();
            }
        }
    }
}
