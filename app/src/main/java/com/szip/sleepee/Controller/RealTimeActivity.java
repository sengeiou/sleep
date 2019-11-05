package com.szip.sleepee.Controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.szip.sleepee.Bean.HealthAdcDataBean;
import com.szip.sleepee.Bean.HealthBean;
import com.szip.sleepee.R;
import com.szip.sleepee.Service.BleService;
import com.szip.sleepee.Util.StatusBarCompat;
import com.szip.sleepee.View.DrawGradView;
import com.szip.sleepee.View.WH_ECGView;
import com.zhuoting.health.write.ProtocolWriter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class RealTimeActivity extends BaseActivity {

    private TextView heartTv;
    private TextView breathTv;
    private ImageView backIv;
    private WH_ECGView heartView;
    private WH_ECGView breathView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_real_time);
        StatusBarCompat.translucentStatusBar(RealTimeActivity.this,true);
        initView();
    }

    private void initView() {
        ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.monitor));
        heartTv = findViewById(R.id.heartForTableTv);
        breathTv = findViewById(R.id.breathForTableTv);
        backIv = findViewById(R.id.backIv);
        backIv.setVisibility(View.VISIBLE);
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        heartView = findViewById(R.id.heartView);
        heartView.setColor(R.color.brown);
        ((DrawGradView)findViewById(R.id.draw)).setColor(R.color.brown);
        breathView = findViewById(R.id.breathView);
        heartView.setHeart(true);

        int data[] = new int[]{1024};

        breathView.addData(data);
        heartView.addData(data);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateHealth(HealthBean healthBean){
        heartTv.setText(healthBean.getHeart()+getString(R.string.heartUnit));
        breathTv.setText(healthBean.getBreath()+getString(R.string.heartUnit));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDrawView(HealthAdcDataBean healthAdcDataBean){
        heartView.addData(healthAdcDataBean.getHeartDatas());
        breathView.addData(healthAdcDataBean.getBreathDatas());
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        BleService.getInstance().write(ProtocolWriter.writeForReadHealth((byte) 0x01));
        BleService.getInstance().write(ProtocolWriter.writeForReadAdcData((byte) 0x01));
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        BleService.getInstance().write(ProtocolWriter.writeForReadHealth((byte) 0x00));
        BleService.getInstance().write(ProtocolWriter.writeForReadAdcData((byte) 0x00));
    }
}
