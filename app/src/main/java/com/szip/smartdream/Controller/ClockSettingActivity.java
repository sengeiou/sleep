package com.szip.smartdream.Controller;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.szip.smartdream.Bean.ClockBeanForBluetooch;
import com.szip.smartdream.Bean.HttpBean.AddClockBean;
import com.szip.smartdream.Bean.HttpBean.ClockData;
import com.szip.smartdream.Interface.HttpCallbackWithAddClock;
import com.szip.smartdream.Model.ProgressHudModel;
import com.szip.smartdream.MyApplication;
import com.szip.smartdream.R;
import com.szip.smartdream.Service.BleService;
import com.szip.smartdream.Util.HttpMessgeUtil;
import com.szip.smartdream.Util.MathUitl;
import com.szip.smartdream.Util.StatusBarCompat;
import com.szip.smartdream.View.MyAlerDialog;
import com.szip.smartdream.View.SelectTimeView;
import com.zhuoting.health.write.ProtocolWriter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static com.szip.smartdream.Util.HttpMessgeUtil.ADDALARM_FLAG;
import static com.szip.smartdream.Util.HttpMessgeUtil.CHANGEALARM_FLAG;

public class ClockSettingActivity extends BaseActivity implements HttpCallbackWithAddClock{


    private Context mContext;

    private int alarmType;//闹钟类型:0普通、1看护、2起床、3早睡
    private TextView typeTv;

    private ImageView backIv;

    private ClockData clockData;

    /**
     * 时间选择控件
     * */
    private SelectTimeView selectTimeView;

    /**
     * 下拉菜单实例
     * */
    private PopupWindow mPop;

    /**
     * 选项列表
     * */
    private ListView MenuItem;
    private ArrayAdapter<String> ItemAdapter;
    private List<String> ItemValue;

    /**
     * 隐藏的布局，不同类型的闹钟显示不同的布局
     * */
    private LinearLayout nurseLl,wakeLl;

    /**
     *通知类型，音乐，备注
     * */
    private TextView remindTv;
    private boolean isphone = true;
    private TextView remarkTv;

    /**
     *重复周期选择
     * */
    private CheckBox check1,check2,check3,check4,check5,check6,check7;
    private byte repeatState = 0;
    private ArrayList<String> repeat = new ArrayList<>();

    /**
     *稍后提醒，智能叫醒，智能叫醒时间
     * */
    private CheckBox intelligentCb;

    private boolean isAdd = false;
    private int pos;

    private MyApplication app;

    private int hour = 0;
    private int minute = 0;
    private int index = 0;
    private int clockId = 0;

    private ImageView okIv;

    private ArrayList<String> list2 = new ArrayList(Arrays.asList("00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15",
            "16","17","18","19","20","21","22","23"));
    private ArrayList<String> list3 = new ArrayList(Arrays.asList("00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15",
            "16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39",
            "40","41","42","43","44","45","46","47","48","49","50","51","52","53","54","55","56","57","58","59"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_clock_setting);
        StatusBarCompat.translucentStatusBar(ClockSettingActivity.this,true);
        mContext = getApplicationContext();
        Intent intent = getIntent();
        alarmType = intent.getIntExtra("flag",1);
        isAdd = intent.getBooleanExtra("add",false);
        pos = intent.getIntExtra("pos",0);
        app = (MyApplication) getApplicationContext();
        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithAddClock(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithAddClock(null);
    }

    /**
     * 初始化视图
     * */
    private void initView() {

        selectTimeView = findViewById(R.id.selectView);
        selectTimeView.setTime(String.format("%02d:%02d",0,0));
        backIv = findViewById(R.id.backIv);
        ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.addAlarm));
        Calendar calendar = Calendar.getInstance();
        findViewById(R.id.saveTv).setVisibility(View.VISIBLE);
        findViewById(R.id.saveTv).setOnClickListener(onClickListener);
        ((TextView)findViewById(R.id.saveTv)).setText(getString(R.string.save));

        okIv = findViewById(R.id.okIv);
        typeTv = findViewById(R.id.typeTv);
        nurseLl = findViewById(R.id.nurseLl);
        remarkTv = findViewById(R.id.remarkTv);
        wakeLl = findViewById(R.id.wakeLl);
        remindTv = findViewById(R.id.remindTv);
        intelligentCb = findViewById(R.id.intelligentCb);

        check1 = findViewById(R.id.check1);
        check2 = findViewById(R.id.check2);
        check3 = findViewById(R.id.check3);
        check4 = findViewById(R.id.check4);
        check5 = findViewById(R.id.check5);
        check6 = findViewById(R.id.check6);
        check7 = findViewById(R.id.check7);


        switch (alarmType){
            case 1:{
                typeTv.setText(getString(R.string.nurse));
                nurseLl.setVisibility(View.VISIBLE);
            }
            break;
            case 2:{
                typeTv.setText(getString(R.string.wake));
                wakeLl.setVisibility(View.VISIBLE);
            }
            break;
            case 3:
                typeTv.setText(getString(R.string.goodNight));
                break;
        }

        if (!isAdd){
            ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.changeAlarm));
            (findViewById(R.id.typeIv)).setVisibility(View.GONE);
            clockData = app.getClockList().get(pos);
            index = clockData.getIndex();
            clockId = clockData.getId();
            hour = clockData.getHour();
            minute = clockData.getMinute();
            repeat = clockData.getRepeat();
            if (repeat.contains("7")){
                repeatState = (byte) (repeatState|0x01);
            }
            if (repeat.contains("1")){
                repeatState = (byte) (repeatState|(0x01<<1));
            }
            if (repeat.contains("2")){
                repeatState = (byte) (repeatState|(0x01<<2));
            }
            if (repeat.contains("3")){
                repeatState = (byte) (repeatState|(0x01<<3));
            }
            if (repeat.contains("4")){
                repeatState = (byte) (repeatState|(0x01<<4));
            }
            if (repeat.contains("5")){
                repeatState = (byte) (repeatState|(0x01<<5));
            }
            if (repeat.contains("6")){
                repeatState = (byte) (repeatState|(0x01<<6));
            }
            intelligentCb.setChecked(clockData.getIsIntelligentWake()==1?true:false);
            updataView();

        }else {
            selectTimeView.setTime(String.format("%02d:%02d",calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE)));
        }

    }

    /**
     * 更新数据
     * */
    private void updataView() {

        check1.setChecked(repeat.contains("7")?true:false);
        check2.setChecked(repeat.contains("1")?true:false);
        check3.setChecked(repeat.contains("2")?true:false);
        check4.setChecked(repeat.contains("3")?true:false);
        check5.setChecked(repeat.contains("4")?true:false);
        check6.setChecked(repeat.contains("5")?true:false);
        check7.setChecked(repeat.contains("6")?true:false);

        switch (alarmType){
            case 1:
                remarkTv.setText(clockData.getRemark());
                break;
            case 2:
                if (clockData.getIsPhone()==1){
                    this.isphone = true;
                    remindTv.setText(getString(R.string.phoneNoty));
                }else {
                    this.isphone = false;
                    remindTv.setText(getString(R.string.deviceNoty));
                }
                intelligentCb.setChecked(clockData.getIsIntelligentWake()==1?true:false);
                break;
        }

        selectTimeView.setTime(String.format("%02d:%02d",hour,minute));
    }

    /**
     * 重置视图
     * */
    private void resetUI(){

        switch (alarmType){
            case 1:
                nurseLl.setVisibility(View.VISIBLE);
                wakeLl.setVisibility(View.GONE);
                break;
            case 2:
                nurseLl.setVisibility(View.GONE);
                wakeLl.setVisibility(View.VISIBLE);
                break;
            case 3:
                nurseLl.setVisibility(View.GONE);
                wakeLl.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 初始化点击事件
     * */
    private void initEvent() {
        okIv.setOnClickListener(onClickListener);
        backIv.setOnClickListener(onClickListener);
        (findViewById(R.id.typeIv)).setOnClickListener(onClickListener);
        (findViewById(R.id.typeLl)).setOnClickListener(onClickListener);
        (findViewById(R.id.remarkLl)).setOnClickListener(onClickListener);
        (findViewById(R.id.remindLl)).setOnClickListener(onClickListener);


        check1.setOnCheckedChangeListener(onCheckedChangeListener);
        check2.setOnCheckedChangeListener(onCheckedChangeListener);
        check3.setOnCheckedChangeListener(onCheckedChangeListener);
        check4.setOnCheckedChangeListener(onCheckedChangeListener);
        check5.setOnCheckedChangeListener(onCheckedChangeListener);
        check6.setOnCheckedChangeListener(onCheckedChangeListener);
        check7.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    /**
     * 初始化下拉菜单
     * */
    private void initSelectPopup() {
        ItemValue= new ArrayList<>(Arrays.asList(getString(R.string.nurse),getString(R.string.wake),getString(R.string.goodNight)));

        MenuItem = new ListView(ClockSettingActivity.this);
        ItemAdapter = new ArrayAdapter<>(this,R.layout.popwindow_layout2,ItemValue);
        MenuItem.setAdapter(ItemAdapter);
        MenuItem.setBackgroundResource(R.color.nullColor);



        MenuItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                alarmType = position+1;
                typeTv.setText(ItemValue.get(position));
                resetUI();
                mPop.dismiss();
            }
        });


        WindowManager wm = (WindowManager) ClockSettingActivity.this
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();

        mPop = new PopupWindow(MenuItem, width/2, ActionBar.LayoutParams.WRAP_CONTENT, true);

        Drawable drawable = ContextCompat.getDrawable(ClockSettingActivity.this, R.drawable.bg_corner);
        mPop.setBackgroundDrawable(drawable);
        mPop.setFocusable(true);
        mPop.setOutsideTouchable(true);
        mPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // 关闭popup窗口
                mPop.dismiss();
            }
        });
    }

    /**
     * 初始化选设备下拉菜单
     * */
    private void initSelectDevicePopup() {
        ItemValue= new ArrayList<>(Arrays.asList(getString(R.string.phoneNoty),getString(R.string.deviceNoty)));

        MenuItem = new ListView(ClockSettingActivity.this);
        ItemAdapter = new ArrayAdapter<>(this,R.layout.popwindow_layout2,ItemValue);
        MenuItem.setAdapter(ItemAdapter);
        MenuItem.setBackgroundResource(R.color.nullColor);



        MenuItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position){
                    case 0:{//手机
                        remindTv.setText(getString(R.string.phoneNoty));
                        isphone = true;
                    }
                    break;
                    case 1:{//睡眠带
                        remindTv.setText(getString(R.string.deviceNoty));
                        isphone = false;
                    }
                    break;

                }
                mPop.dismiss();
            }
        });


        WindowManager wm = (WindowManager) ClockSettingActivity.this
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();

        mPop = new PopupWindow(MenuItem, width/2, ActionBar.LayoutParams.WRAP_CONTENT, true);

        Drawable drawable = ContextCompat.getDrawable(ClockSettingActivity.this, R.drawable.bg_corner);
        mPop.setBackgroundDrawable(drawable);
        mPop.setFocusable(true);
        mPop.setOutsideTouchable(true);
        mPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // 关闭popup窗口
                mPop.dismiss();
            }
        });
    }


    /**
     * 事件监听
     * */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.typeIv:
                case R.id.typeLl:
                    if (isAdd){
                        initSelectPopup();
                        if (mPop != null && !mPop.isShowing()) {
                            mPop.showAsDropDown(typeTv, 10, 10);
                        }
                    }
                    break;
                case R.id.remindLl:
                    initSelectDevicePopup();
                    if (mPop != null && !mPop.isShowing()) {
                        mPop.showAsDropDown(remindTv, 10, 10);
                    }
                    break;

                case R.id.remarkLl:
                    MyAlerDialog.getSingle().showAlerDialogWithEdit(getString(R.string.remark),"",getString(R.string.inputRemark),getString(R.string.confirm),
                            getString(R.string.cancel),true,alerDialogEditOnclickListener,ClockSettingActivity.this);
                    break;
                case R.id.saveTv:{
                    int i = selectTimeView.getTime().indexOf(":");
                    hour = Integer.valueOf(selectTimeView.getTime().substring(0,i));
                    minute = Integer.valueOf(selectTimeView.getTime().substring(i+1,selectTimeView.getTime().length()));
                    if (repeat.size() !=0){
                        if (isAdd){
                            switch (alarmType){

                                case 1:{
                                    try {
                                        ProgressHudModel.newInstance().show(ClockSettingActivity.this,getString(R.string.waitting)
                                                ,getString(R.string.httpError),5000);

                                        HttpMessgeUtil.getInstance(mContext).postForAddClock(alarmType+"",
                                                hour+"",minute+"","-1","1","1", MathUitl.ArrayToString(repeat),"",
                                                "0",remarkTv.getText().toString(),ADDALARM_FLAG);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                break;
                                case 2:{//起床闹钟


                                    if (BleService.getInstance().getConnectState()==2){
                                        ProgressHudModel.newInstance().show(ClockSettingActivity.this,getString(R.string.waitting)
                                                ,getString(R.string.httpError),5000);
                                        BleService.getInstance().write(ProtocolWriter.writeForAddClock(isphone?(byte) 0x0:0x01,(byte)0x1,(byte)0x0,
                                                (byte)0x0,repeatState,(byte) hour,(byte) minute,intelligentCb.isChecked()?(byte)0x1:0x00,(byte)alarmType));
                                    } else
                                        Toast.makeText(ClockSettingActivity.this,getString(R.string.blueUnline),Toast.LENGTH_SHORT).show();
                                    break;
                                }
                                case 0:
                                case 3:{//早睡闹钟，不需要要在底层保存
                                    try {
                                        ProgressHudModel.newInstance().show(ClockSettingActivity.this,getString(R.string.waitting)
                                                ,getString(R.string.httpError),5000);

                                        HttpMessgeUtil.getInstance(mContext).postForAddClock(alarmType+"",
                                                hour+"",minute+"","-1","1","1",MathUitl.ArrayToString(repeat),"",
                                                "0","",ADDALARM_FLAG);
                                        Log.d("sudo",MathUitl.ArrayToString(repeat));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                break;
                            }
                        }else {
                            switch (alarmType){
                                case 1:{
                                    try {
                                        ProgressHudModel.newInstance().show(ClockSettingActivity.this,getString(R.string.waitting)
                                                ,getString(R.string.httpError),5000);

                                        HttpMessgeUtil.getInstance(mContext).postForChangeClock(clockId+"",alarmType+"",
                                                hour+"",minute+"","-1","1",clockData.getIsOn()+"",MathUitl.ArrayToString(repeat),"",
                                                "0",remarkTv.getText().toString(),CHANGEALARM_FLAG);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                break;

                                case 2:{
                                    ProgressHudModel.newInstance().show(ClockSettingActivity.this,getString(R.string.waitting)
                                            ,getString(R.string.httpError),5000);
                                    if (BleService.getInstance().getConnectState()==2)
                                        BleService.getInstance().write(ProtocolWriter.writeForChangeClock((byte) index,isphone?(byte) 0x0:01,(byte) clockData.getIsOn(),(byte)0x0,
                                                (byte)0x0,repeatState,(byte) hour,(byte) minute,intelligentCb.isChecked()?(byte)0x1:0x00,(byte)alarmType));
                                    else
                                        Toast.makeText(ClockSettingActivity.this,getString(R.string.blueUnline),Toast.LENGTH_SHORT).show();
                                }
                                break;

                                case 0:
                                case 3:{
                                    try {
                                        ProgressHudModel.newInstance().show(ClockSettingActivity.this,getString(R.string.waitting)
                                                ,getString(R.string.httpError),5000);
                                        HttpMessgeUtil.getInstance(mContext).postForChangeClock(clockId+"",alarmType+"",
                                                hour+"",minute+"","-1","1",clockData.getIsOn()+"",MathUitl.ArrayToString(repeat),"",
                                                "0","",CHANGEALARM_FLAG);
                                        Log.d("sudo",MathUitl.ArrayToString(repeat));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                break;
                            }
                        }
                    }else {
                        showToast(getString(R.string.noRepeat));
                    }
                }
                break;

                case R.id.backIv:
                    finish();
                    break;
            }
        }
    };


    /**
     * 监听循环周点击事件
     * */
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()){
                case R.id.check1:
                    if (isChecked){
                        repeat.add("7");
                        repeatState = (byte) (repeatState|0x01);
                    }
                    else{
                        repeat.remove("7");
                        repeatState = (byte) (repeatState&(~0x01));
                    }

                    break;
                case R.id.check2:
                    if (isChecked){
                        repeat.add("1");
                        repeatState = (byte) (repeatState|(0x01<<1));
                    }
                    else{
                        repeat.remove("1");
                        repeatState = (byte) (repeatState&(~(0x01<<1)));
                    }
                    break;
                case R.id.check3:
                    if (isChecked){
                        repeat.add("2");
                        repeatState = (byte) (repeatState|(0x01<<2));
                    }
                    else{
                        repeat.remove("2");
                        repeatState = (byte) (repeatState&(~(0x01<<2)));
                    }
                    break;
                case R.id.check4:
                    if (isChecked){
                        repeat.add("3");
                        repeatState = (byte) (repeatState|(0x01<<3));
                    }
                    else{
                        repeat.remove("3");
                        repeatState = (byte) (repeatState&(~(0x01<<3)));
                    }

                    break;
                case R.id.check5:
                    if (isChecked){
                        repeat.add("4");
                        repeatState = (byte) (repeatState|(0x01<<4));
                    }
                    else{
                        repeat.remove("4");
                        repeatState = (byte) (repeatState&(~(0x01<<4)));
                    }
                    break;
                case R.id.check6:
                    if (isChecked){
                        repeat.add("5");
                        repeatState = (byte) (repeatState|(0x01<<5));
                    }
                    else{
                        repeat.remove("5");
                        repeatState = (byte) (repeatState&(~(0x01<<5)));
                    }

                    break;
                case R.id.check7:
                    if (isChecked){
                        repeat.add("6");
                        repeatState = (byte) (repeatState|(0x01<<6));
                    }
                    else{
                        repeat.remove("6");
                        repeatState = (byte) (repeatState&(~(0x01<<6)));
                    }

                    break;
            }
        }
    };

    /**
     * 蓝牙添加/修改闹钟的返回数据
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void addClock(ClockBeanForBluetooch beanForBluetooch){
        ProgressHudModel.newInstance().diss();
        if (beanForBluetooch.getWordType() == 1){
            if (beanForBluetooch.isOK()){
                index = beanForBluetooch.getIndex();
                try {
                    HttpMessgeUtil.getInstance(mContext).postForAddClock(alarmType+"",
                            hour+"",minute+"",index+"",(isphone?1:0)+"","1",MathUitl.ArrayToString(repeat),"",
                            intelligentCb.isChecked()?"1":"0","",ADDALARM_FLAG);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(ClockSettingActivity.this,getString(R.string.addClockError),Toast.LENGTH_SHORT).show();
            }
        }else if (beanForBluetooch.getWordType() == 2){
            if (beanForBluetooch.isOK()){
                index = beanForBluetooch.getIndex();
                try {
                    HttpMessgeUtil.getInstance(mContext).postForChangeClock(clockId+"",alarmType+"",
                            hour+"",minute+"",index+"",(isphone?1:0)+"",clockData.getIsOn()+"",MathUitl.ArrayToString(repeat),"",
                            intelligentCb.isChecked()?"1":"0","",CHANGEALARM_FLAG);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(ClockSettingActivity.this,getString(R.string.addClockError),Toast.LENGTH_SHORT).show();
            }
        }

    }

    /**
     * 弹出框回调
     * */
    private MyAlerDialog.AlerDialogEditOnclickListener alerDialogEditOnclickListener = new MyAlerDialog.AlerDialogEditOnclickListener() {
        @Override
        public void onDialogEditTouch(String edit1) {
            remarkTv.setText(edit1);
        }

        @Override
        public void onDialogEditWithRadioTouch(String edit1, int flag, int position) {
        }
    };

    @Override
    public void onAddClock(AddClockBean addClockBean,int id) {
        ProgressHudModel.newInstance().diss();
        if (id == ADDALARM_FLAG){
            if (addClockBean.getData()!=null){
                app.addClockList(addClockBean.getData().getAlarmClock());
                Log.d("CLOCK******","clock is add for me:"+String.format("at:%d,%d:%d",addClockBean.getData().getAlarmClock().getId(),
                        addClockBean.getData().getAlarmClock().getHour(),addClockBean.getData().getAlarmClock().getMinute()));

            }
        }else if (id == CHANGEALARM_FLAG){
            if (addClockBean.getData()!=null){
                app.changeClockList(pos,addClockBean.getData().getAlarmClock());
                Log.d("CLOCK******","clock is change for me:"+String.format("at:%d,%d:%d",addClockBean.getData().getAlarmClock().getId(),
                        addClockBean.getData().getAlarmClock().getHour(),addClockBean.getData().getAlarmClock().getMinute()));

            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },500);
    }
}
