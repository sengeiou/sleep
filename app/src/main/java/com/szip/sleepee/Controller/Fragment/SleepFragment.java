package com.szip.sleepee.Controller.Fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.szip.sleepee.Bean.DeviceClockIsUpdataBean;
import com.szip.sleepee.Bean.HealthBean;
import com.szip.sleepee.Controller.RealTimeActivity;
import com.szip.sleepee.MyApplication;
import com.szip.sleepee.R;
import com.szip.sleepee.Service.BleService;
import com.szip.sleepee.Util.MathUitl;
import com.zhuoting.health.write.ProtocolWriter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Administrator on 2019/1/23.
 */

public class SleepFragment extends BaseFragment {

    private ObjectAnimator anim1,anim2,anim3;
    private ImageView animatorIv1,animatorIv2,animatorIv3;
    private AnimatorSet set = new AnimatorSet();

    /**
     * 开始睡眠/闹钟控件
     * */
    private RelativeLayout sleepRl;
    private TextView sleepTv;
    private RelativeLayout clockRl;
    private TextView clockTv;
    /**
     *实时健康数据以及连接状态
     * */
    private LinearLayout breathLl,heartLl;
    private TextView heartTv;
    private TextView breathTv;

    private MyApplication app;

    private boolean isDisconnect;

    /**
     * 按钮出现的动画
     * */
    private ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 1f, 0f,
            1f, Animation.RELATIVE_TO_PARENT,0.5f,Animation.RELATIVE_TO_PARENT,1f);
    private ScaleAnimation scaleAnimation1 = new ScaleAnimation(1f, 1f, 0f,
            1f, Animation.RELATIVE_TO_PARENT,0.5f,Animation.RELATIVE_TO_PARENT,1f);

    /**
     * 返回一个fragment实例，Activity中调用
     * */
    public static SleepFragment newInstance(String param){
        Bundle bundle = new Bundle();
        bundle.putString("param",param);
        SleepFragment fragment = new SleepFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sleep;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {
        app = (MyApplication) getActivity().getApplicationContext();
        initView();
        initEvent();
        initAnimator();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        if (BleService.getInstance().isConnect()){
            if (app.isStartSleep())
                BleService.getInstance().write(ProtocolWriter.writeForReadHealth((byte) 0x01));
        }
        startAnimator();
        if (app.getClockList()!=null&&app.getClockList().size()!=0){
            Log.d("CLOCK******","update clock = "+MathUitl.getNearClock(app.getClockList()));
            clockTv.setText(MathUitl.getNearClock(app.getClockList()));
        }
        else {
            clockTv.setText("");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        if (app.isStartSleep())
        BleService.getInstance().write(ProtocolWriter.writeForReadHealth((byte) 0x00));
    }

    /**
     *初始化界面
     * */
    private void initView() {
        clockTv = getView().findViewById(R.id.clockTv);
        clockRl = getView().findViewById(R.id.clockRl);
        sleepRl = getView().findViewById(R.id.sleepRl);
        sleepTv = getView().findViewById(R.id.sleepTv);
        breathLl = getView().findViewById(R.id.breathLl);
        heartLl = getView().findViewById(R.id.heartLl);
        breathTv = getView().findViewById(R.id.breathTv);
        heartTv = getView().findViewById(R.id.heartTv);

        animatorIv1 = getView().findViewById(R.id.animIv1);
        animatorIv2 = getView().findViewById(R.id.animIv2);
        animatorIv3 = getView().findViewById(R.id.animIv3);

    }

    /**
     *初始化事件监听
     * */
    private void initEvent() {
        sleepRl.setOnClickListener(onClickListener);
        breathLl.setOnClickListener(onClickListener);
        heartLl.setOnClickListener(onClickListener);
    }

    /**
     * 初始化动画
     * */
    private void initAnimator() {

        anim1 = ObjectAnimator.ofFloat(animatorIv1,"alpha",0,1,1,1,0,0,0);
        anim2 = ObjectAnimator.ofFloat(animatorIv2,"alpha",0,0,1,1,1,0,0);
        anim3 = ObjectAnimator.ofFloat(animatorIv3,"alpha",0,0,0,1,1,1,0);
        anim1.setInterpolator(new LinearInterpolator());
        anim1.setRepeatCount(-1);
        anim2.setInterpolator(new LinearInterpolator());
        anim2.setRepeatCount(-1);
        anim3.setInterpolator(new LinearInterpolator());
        anim3.setRepeatCount(-1);

        set.setDuration(6000);
        set.play(anim1).with(anim2).with(anim3);

        scaleAnimation.setDuration(1000);//设置动画持续时间
        scaleAnimation.setRepeatCount(0);//设置重复次数
        scaleAnimation.setFillAfter(true);//动画执行完后是否停留在执行完的状态

        scaleAnimation1.setDuration(1500);//设置动画持续时间
        scaleAnimation1.setRepeatCount(0);//设置重复次数
        scaleAnimation1.setFillAfter(true);//动画执行完后是否停留在执行完的状态
    }


    /**
     * 开始起始动画
     * */
    private void startAnimator() {

        animatorIv1.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animatorIv2.setVisibility(View.VISIBLE);
            }
        },500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animatorIv3.setVisibility(View.VISIBLE);
            }
        },1000);

        sleepRl.startAnimation(scaleAnimation);
        clockRl.startAnimation(scaleAnimation1);
    }

    /**
     * 实时更新心率呼吸率
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateHealth(HealthBean healthBean){
        breathTv.setText(healthBean.getBreath());
        heartTv.setText(healthBean.getHeart());
    }

    /**
     * 显示最近一次的闹钟
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateClock(DeviceClockIsUpdataBean connectBean){
        Log.d("CLOCK******","update clock");
        if (app.getClockList()!=null&&app.getClockList().size()!=0)
            clockTv.setText(MathUitl.getNearClock(app.getClockList()));
        else
            clockTv.setText("");
    }

    /**
     *事件监听
     * */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.heartLl:
                case R.id.breathLl: {
                    if (BleService.getInstance().isConnect()) {//蓝牙连接着
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), RealTimeActivity.class);
                        startActivity(intent);
                    }
                    else {
                        showToast(getString(R.string.lineError));
                    }
                }
                break;
                case R.id.sleepRl:
                    if (BleService.getInstance().isConnect()){//蓝牙连接着
                        if (sleepTv.getText().toString().equals(getString(R.string.startSleep))){
                            sleepTv.setText(getString(R.string.stopSleep));
                            set.start();
                            app.setStartSleep(true);
                            BleService.getInstance().write(ProtocolWriter.writeForReadHealth((byte) 0x01));
                        }else {
                            sleepTv.setText(getString(R.string.startSleep));
                            app.setStartSleep(false);
                            BleService.getInstance().write(ProtocolWriter.writeForReadHealth((byte) 0x00));
                            set.end();
                        }
                    }else {
                        showToast(getString(R.string.lineError));
                    }
                    break;
            }
        }
    };
}
