package com.szip.sleepee.Controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.szip.sleepee.Bean.ConnectBean;
import com.szip.sleepee.R;
import com.szip.sleepee.Service.BleService;
import com.szip.sleepee.Service.DfuService;
import com.szip.sleepee.Util.FileUtil;
import com.szip.sleepee.Util.StatusBarCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.UUID;

import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

public class UpdataFirmwareActivity extends BaseActivity {

    private ImageButton updataBtn;
    private ImageView backIv;

    private TextView currentTv;
    private TextView versionTv;

    private boolean updataAble;
    private String url;
    private String version;
    private String current;
    private FileUtil fileUtil;

    private KProgressHUD progressHUD;
    private KProgressHUD progressHUD1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_updata_firmware);
        StatusBarCompat.translucentStatusBar(UpdataFirmwareActivity.this,true);
        Intent intent = getIntent();
        updataAble = intent.getBooleanExtra("updataAble",false);
        url = intent.getStringExtra("url");
        version = intent.getStringExtra("version");
        current = intent.getStringExtra("current");
        fileUtil = new FileUtil(UpdataFirmwareActivity.this);
        DfuServiceListenerHelper.registerProgressListener(this, mDfuProgressListener);
        initView();
    }

    private void initView() {
        ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.newVersion));
        backIv =findViewById(R.id.backIv);
        backIv.setVisibility(View.VISIBLE);
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        versionTv = findViewById(R.id.versionTv);
        currentTv = findViewById(R.id.currentTv);
        currentTv.setText(getString(R.string.current)+current);
        if (updataAble){

            versionTv.setText(getString(R.string.touchToUpdata)+version);
            updataBtn = findViewById(R.id.updataBtn);
            updataBtn.setImageResource(R.mipmap.me_version_btn_update);
            updataBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (progressHUD==null){
                        progressHUD  = KProgressHUD.create(UpdataFirmwareActivity.this)
                                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                .setLabel(getString(R.string.waitting))
                                .setCancellable(true)
                                .setAnimationSpeed(2)
                                .setDimAmount(0.5f);
                        progressHUD.show();
                    }

                    if (fileUtil.downloadAble(version)){
                        BleService.getInstance().downloadFirmsoft(url,version+".zip",fileUtil.getPath());
                    }else {
                        BleService.getInstance().disConnect();
                        onUploadClicked();
                    }

                }
            });
        }else{
            versionTv.setText(getString(R.string.touchToUpdata)+version);
            updataBtn = findViewById(R.id.updataBtn);
            updataBtn.setEnabled(false);
        }
    }


    /**
     * 开始烧录
     */
    public void onUploadClicked() {

        final DfuServiceInitiator starter = new DfuServiceInitiator(BleService.getInstance().getmMac())
                .setDeviceName(BleService.getInstance().getName())
                .setKeepBond(false)
                .setForceDfu(false)
                .setPacketsReceiptNotificationsEnabled(false)
                .setPacketsReceiptNotificationsValue(12)
                .setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true);

        starter.setZip(null, fileUtil.getPath()+"/"+version+".zip");
//        starter.setZip(null, fileUtil.getPath()+"/"+"v1.0_2.3.zip");

        starter.start(this, DfuService.class);
    }

    /**
     * 更新固件
     * */
    private final DfuProgressListener mDfuProgressListener = new DfuProgressListenerAdapter() {
        @Override
        public void onDeviceConnecting(final String deviceAddress) {
            if (progressHUD!=null)
                progressHUD.setLabel(getString(R.string.connectting));
            Log.d("SZIP******","正在连接");
        }

        @Override
        public void onDfuProcessStarting(final String deviceAddress) {
            if (progressHUD!=null){
                progressHUD.dismiss();
                progressHUD  = KProgressHUD.create(UpdataFirmwareActivity.this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel(getString(R.string.realStart))
                        .setCancellable(true)
                        .setAnimationSpeed(2)
                        .setDimAmount(0.5f);
                progressHUD.show();
            }


            Log.d("SZIP******","准备开始");
        }

        @Override
        public void onEnablingDfuMode(final String deviceAddress) {

            if (progressHUD!=null){
                progressHUD.dismiss();
                progressHUD  = KProgressHUD.create(UpdataFirmwareActivity.this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel(getString(R.string.enableDfu))
                        .setCancellable(true)
                        .setAnimationSpeed(2)
                        .setDimAmount(0.5f);
                progressHUD.show();
            }
            Log.d("SZIP******","使能Dfu");
        }

        @Override
        public void onFirmwareValidating(final String deviceAddress) {

            if (progressHUD!=null){
                progressHUD.dismiss();
                progressHUD  = KProgressHUD.create(UpdataFirmwareActivity.this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel(getString(R.string.reallyFirm))
                        .setCancellable(true)
                        .setAnimationSpeed(2)
                        .setDimAmount(0.5f);
                progressHUD.show();
            }
            Log.d("SZIP******","确认固件");
        }

        @Override
        public void onDeviceDisconnecting(final String deviceAddress) {

            Log.d("SZIP******","设备断开连接");
            if (progressHUD!=null){
                progressHUD.dismiss();
                progressHUD  = KProgressHUD.create(UpdataFirmwareActivity.this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel(getString(R.string.waitting))
                        .setCancellable(true)
                        .setAnimationSpeed(2)
                        .setDimAmount(0.5f);
                progressHUD.show();
            }
        }

        @Override
        public void onDfuCompleted(final String deviceAddress) {

            if (progressHUD!=null)
                progressHUD.dismiss();
            Toast.makeText(UpdataFirmwareActivity.this,"烧录完成",Toast.LENGTH_SHORT).show();
            Log.d("SZIP******","烧录完成");
        }

        @Override
        public void onDfuAborted(final String deviceAddress) {

            Log.d("SZIP******","烧录终止");
        }

        @Override
        public void onProgressChanged(final String deviceAddress, final int percent, final float speed, final float avgSpeed, final int currentPart, final int partsTotal) {

            if (progressHUD!=null){
                progressHUD.dismiss();

            }

            if (progressHUD1==null){
                progressHUD1  = KProgressHUD.create(UpdataFirmwareActivity.this)
                        .setMaxProgress(100)
                        .setStyle(KProgressHUD.Style.PIE_DETERMINATE)
                        .setLabel(getString(R.string.firming))
                        .setCancellable(true)
                        .setAnimationSpeed(2)
                        .setDimAmount(0.5f);
                progressHUD1.show();
            }else {
                progressHUD1.setProgress(percent);
            }


            Log.d("SZIP******","烧录完成度"+percent);
        }

        @Override
        public void onError(final String deviceAddress, final int error, final int errorType, final String message) {
            Log.d("SZIP******","烧录错误"+message);
        }
    };


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartDFU(ConnectBean connectBean){
        if (connectBean.isConnect){
            BleService.getInstance().disConnect();
            onUploadClicked();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DfuServiceListenerHelper.unregisterProgressListener(this,mDfuProgressListener);
    }
}
