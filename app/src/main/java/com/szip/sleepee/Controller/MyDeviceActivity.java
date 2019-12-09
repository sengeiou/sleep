package com.szip.sleepee.Controller;

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

import com.szip.sleepee.Bean.DevicePowerBean;
import com.szip.sleepee.Bean.HttpBean.BaseApi;
import com.szip.sleepee.Bean.HttpBean.UpdataBean;
import com.szip.sleepee.DB.SaveDataUtil;
import com.szip.sleepee.Interface.HttpCallbackWithBase;
import com.szip.sleepee.Interface.HttpCallbackWithUpdata;
import com.szip.sleepee.Model.ProgressHudModel;
import com.szip.sleepee.MyApplication;
import com.szip.sleepee.R;
import com.szip.sleepee.Service.BleService;
import com.szip.sleepee.Util.DateUtil;
import com.szip.sleepee.Util.HttpMessgeUtil;
import com.szip.sleepee.Util.StatusBarCompat;
import com.szip.sleepee.View.MyAlerDialog;
import com.zhuoting.health.write.ProtocolWriter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import static com.szip.sleepee.Util.HttpMessgeUtil.SOFT_FLAG;
import static com.szip.sleepee.Util.HttpMessgeUtil.UNBINDDEVICE_FLAG;

public class MyDeviceActivity extends BaseActivity implements HttpCallbackWithUpdata,HttpCallbackWithBase{

    private Context mContext;

    private TextView powerTv;
    private LinearLayout softLl;
    private TextView softTv;
    private TextView deviceIdTv;
    private TextView macTv;
    private LinearLayout unbindLl;
    private ImageView backIv;
    private String FILE = "sleepEE";

    /**
     * 判断是否可以升级
     * */
    private boolean updataAble = false;

    private MyApplication app;

    private String version;

    private UpdataBean updataBean;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 200:
                    updataBean = (UpdataBean) msg.obj;
                    updataAble = updataBean.getData().isHasNewVersion();
                    if (updataAble){
                        MyAlerDialog.getSingle().showAlerDialog(getString(R.string.newVersion),getString(R.string.versionMsg),
                                getString(R.string.ok),getString(R.string.cancel),true,listener,MyDeviceActivity.this).show();
                    }
                    break;
                case 100:
                    BleService.getInstance().disConnect();
                    BleService.getInstance().setmMac(null);
                    SaveDataUtil.newInstance(MyDeviceActivity.this).clearDB();
                    startActivity(new Intent(mContext, FindDeviceActivity.class));
                    setResult(10);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_my_device);
        StatusBarCompat.translucentStatusBar(MyDeviceActivity.this,true);
        mContext = getApplicationContext();
        app = (MyApplication) getApplicationContext();
        version = "v"+app.getHardV()+"_"+app.getSoftV();
        initView();
        initEvent();
        checkUpdata();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        BleService.getInstance().write(ProtocolWriter.writeForGetPower());
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithBase(this);
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithUpdata(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithBase(null);
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithUpdata(null);
    }

    /**
     * 初始化界面
     * */
    private void initView() {
        ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.myDevice));
        powerTv = findViewById(R.id.powerTv);
        softLl = findViewById(R.id.softLl);
        softTv = findViewById(R.id.softTv);
        softTv.setText(version);
        deviceIdTv = findViewById(R.id.deviceIDTv);
        deviceIdTv.setText(BleService.getInstance().getName());
        macTv = findViewById(R.id.macTv);
        macTv.setText(BleService.getInstance().getmMac());
        unbindLl = findViewById(R.id.unbindLl);
        backIv = findViewById(R.id.backIv);
        backIv.setVisibility(View.VISIBLE);
    }

    /**
     * 初始化事件监听
     * */
    private void initEvent(){
        softLl.setOnClickListener(onClickListener);
        unbindLl.setOnClickListener(onClickListener);
        backIv.setOnClickListener(onClickListener);
    }

    /**
     * 事件监听
     * */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.softLl:{
                    if(BleService.getInstance().isConnect()){
                        if (updataBean!=null){
                            Intent intent = new Intent();
                            intent.setClass(MyDeviceActivity.this,UpdataFirmwareActivity.class);
                            intent.putExtra("updataAble",updataAble);
                            intent.putExtra("url",updataBean.getData().getUrl());
                            intent.putExtra("version",updataBean.getData().getVersionNumber());
                            intent.putExtra("current",version);
                            startActivity(intent);
                        }
                    }else
                        showToast(getString(R.string.bluetoochError));
                }
                break;
                case R.id.unbindLl:
                    try {
                        ProgressHudModel.newInstance().show(MyDeviceActivity.this,getString(R.string.waitting),getString(R.string.httpError),10000);
                        HttpMessgeUtil.getInstance(mContext).getUnbindDevice(UNBINDDEVICE_FLAG);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.backIv:
                    finish();
                    break;
            }
        }
    };

    /**
     * 获取服务器上的固件版本
     * */
    private void checkUpdata() {
        ProgressHudModel.newInstance().show(MyDeviceActivity.this,getString(R.string.waitting),getString(R.string.httpError),10000);
        try {
            HttpMessgeUtil.getInstance(mContext).getForUpdata("v"+app.getHardV()+"_"+app.getSoftV(),SOFT_FLAG);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    /**
//     * 判断是否可以更新
//     * */
//    private boolean isUpdataAble(String version){
//        if(version !=null){
//            int index = version.indexOf('_');
//            int serverV = 0;
//            int nativeV;
//
//            nativeV = Integer.valueOf(app.getSoftV().substring(0,1))*10+Integer.valueOf(app.getSoftV().substring(2));
//            if (index>=0){
//                serverV =Integer.valueOf(version.substring(index+1,index+2))*10+Integer.valueOf(version.substring(index+3));
//            }
//            if (nativeV>=serverV)
//                return false;
//            else
//                return true;
//        }else
//            return false;
//    }

    /**
     * 更新电量
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdataPower(DevicePowerBean devicePowerBean){
        powerTv.setText(devicePowerBean.getPower().equals("")?getString(R.string.charging):devicePowerBean.getPower());
    }

    private MyAlerDialog.AlerDialogOnclickListener listener = new MyAlerDialog.AlerDialogOnclickListener() {
        @Override
        public void onDialogTouch(boolean flag) {
            Intent intent = new Intent();
            intent.setClass(MyDeviceActivity.this,UpdataFirmwareActivity.class);
            intent.putExtra("updataAble",updataAble);
            intent.putExtra("url",updataBean.getData().getUrl());
            intent.putExtra("version",updataBean.getData().getVersionNumber());
            intent.putExtra("current",version);
            startActivity(intent);
        }
    };

    @Override
    public void onCallback(BaseApi baseApi, int id) {
        ProgressHudModel.newInstance().diss();
        if (id == UNBINDDEVICE_FLAG){
            app.clearClockList();
            app.setReportDate(DateUtil.getStringToDate("today"));
            SharedPreferences sharedPreferencesp = getSharedPreferences(FILE,MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferencesp.edit();
            editor.putBoolean("isBind",false);
            editor.commit();
            handler.sendEmptyMessage(100);
        }
    }

    @Override
    public void onUpdata(UpdataBean updataBean) {
        ProgressHudModel.newInstance().diss();
        Message msg = new Message();
        msg.what = 200;
        msg.obj = updataBean;
        handler.sendMessage(msg);
    }
}
