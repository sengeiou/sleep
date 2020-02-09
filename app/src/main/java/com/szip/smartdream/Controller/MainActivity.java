package com.szip.smartdream.Controller;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.szip.smartdream.Adapter.MyMenuAdapter;
import com.szip.smartdream.Bean.ConnectBean;
import com.szip.smartdream.Bean.UpdataReportBean;
import com.szip.smartdream.Controller.Fragment.AlarmClockFragment;
import com.szip.smartdream.Controller.Fragment.PersonFragment;
import com.szip.smartdream.Controller.Fragment.report.ReportFragment;
import com.szip.smartdream.Controller.Fragment.SleepFragment;
import com.szip.smartdream.DB.LoadDataUtil;
import com.szip.smartdream.Interface.MyTouchListener;
import com.szip.smartdream.Interface.OnProgressTimeout;
import com.szip.smartdream.Model.ProgressHudModel;
import com.szip.smartdream.MyApplication;
import com.szip.smartdream.R;
import com.szip.smartdream.Service.BleService;
import com.szip.smartdream.Util.DateUtil;
import com.szip.smartdream.Util.HttpMessgeUtil;
import com.szip.smartdream.Util.StatusBarCompat;
import com.szip.smartdream.View.DateSelectView;
import com.szip.smartdream.View.MenuListView;
import com.zhuoting.health.write.ProtocolWriter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.Calendar;

import cn.aigestudio.datepicker.cons.DPMode;
import cn.aigestudio.datepicker.views.DatePicker;

import static com.szip.smartdream.Util.HttpMessgeUtil.DOWNLOADDATA_FLAG;
import static com.szip.smartdream.Util.HttpMessgeUtil.GETALARM_FLAG;


public class MainActivity extends BaseActivity {

    private Context mContext;

    private long firstime = 0;
    private DateSelectView dateSelectView;
    /**
     * Fragment操作相关
     * */
    private FragmentManager fm;
    private FragmentTransaction transaction;

    /**
     * 侧滑菜单控件
     * */
    private MenuListView menuListView;
    private MyMenuAdapter adapter;
    private ListView listView;

    /**
     * 顶栏控件
     * */
    private ImageView menuIv;
    private ImageView imageOne;
    private ImageView imageTwo;

    /**
     * 所在界面
     * */
    private int fragmentPos = 0;


    /**
     * 旋转出现的动画
     * */
    private RotateAnimation rotateLeft  = new RotateAnimation(90f, 0f, Animation.RELATIVE_TO_SELF,
            0f, Animation.RELATIVE_TO_SELF, 0f);
    /**
     * 旋转消失的动画
     * */
    private RotateAnimation rotateRight  = new RotateAnimation(0f, 90f, Animation.RELATIVE_TO_SELF,
            0f, Animation.RELATIVE_TO_SELF, 0f);

    private MyApplication app;

    SleepFragment sleepFragment = SleepFragment.newInstance("");
    ReportFragment reportFragment = ReportFragment.newInstance("");
    AlarmClockFragment alarmClockFragment = AlarmClockFragment.newInstance("");
    PersonFragment personFragment = PersonFragment.newInstance("");

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 200:
                    try {
                        HttpMessgeUtil.getInstance(mContext).getForGetClockList(GETALARM_FLAG);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case 300:
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
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        app = (MyApplication) getApplicationContext();
        StatusBarCompat.translucentStatusBar(MainActivity.this,true);
        mContext = getApplicationContext();
        initView();
        initEvent();
        intiAnimation();
        updateView(fragmentPos);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updataBleStateImage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("SZIP******","MAIN IS destroy");
        BleService.getInstance().stopConnectDevice();
    }

    /** 保存MyTouchListener接口的列表 */
    private MyTouchListener myTouchListener;

    /** 提供给Fragment通过getActivity()方法来注册自己的触摸事件的方法 */
    public void registerMyTouchListener(MyTouchListener listener) {
        this.myTouchListener = listener;
    }

    /** 提供给Fragment通过getActivity()方法来取消注册自己的触摸事件的方法 */
    public void unRegisterMyTouchListener() {
        myTouchListener = null;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (myTouchListener!=null)
//            myTouchListener.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 更新连接按钮
     * */
    private void updataBleStateImage(){
        if (fragmentPos!=1){
            if (!EventBus.getDefault().isRegistered(this))
                EventBus.getDefault().register(this);
            if (BleService.getInstance().getConnectState() == 2){
                imageOne.setImageResource(R.mipmap.sleep_icon_connect);
            }else if (BleService.getInstance().getConnectState() == 0){
                imageOne.setImageResource(R.mipmap.sleep_icon_ununited);
            }
        }
    }

    /**
     * 初始化界面
     * */
    private void initView() {

        BluetoothAdapter blueadapter = BluetoothAdapter.getDefaultAdapter();
        if (!blueadapter.isEnabled()) {
            Intent bleIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(bleIntent);
        }

        menuIv = findViewById(R.id.menuIv);
        imageOne = findViewById(R.id.imageOne);
        imageTwo = findViewById(R.id.imageTwo);
        menuListView = findViewById(R.id.menuListView);
        listView = findViewById(R.id.v4_listview);
        adapter = new MyMenuAdapter(this);
        listView.setAdapter(adapter);
        dateSelectView = findViewById(R.id.dateView);
        dateSelectView.setSelectListener(dateSelectListener);
    }

    /**
     * 初始化事件监听
     * */
    private void initEvent() {
        menuIv.setOnClickListener(onClickListener);
        imageOne.setOnClickListener(onClickListener);
        imageTwo.setOnClickListener(onClickListener);
        findViewById(R.id.backView).setOnClickListener(onClickListener);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position!=fragmentPos){
                    fragmentPos = position;
                    updateView(position);
                }
                startSector(false);
            }
        });
    }

    /**
     * 更新界面
     * */
    private void updateView(int pos) {
        switch (pos){
            case 0:
                ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.sleep));
                imageOne.setVisibility(View.VISIBLE);
                imageTwo.setVisibility(View.GONE);
                if (BleService.getInstance().getConnectState() == 2)
                    imageOne.setImageResource(R.mipmap.sleep_icon_connect);
                else
                    imageOne.setImageResource(R.mipmap.sleep_icon_ununited);

                fm = getSupportFragmentManager();
                transaction =  fm.beginTransaction();
                transaction.replace(R.id.fragment,sleepFragment);
                transaction.commit();
                break;
            case 1:
                ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.report));
                imageOne.setVisibility(View.VISIBLE);
                imageOne.setImageResource(R.mipmap.report_btn_refresh);
                imageTwo.setVisibility(View.VISIBLE);
                imageTwo.setImageResource(R.mipmap.report_btn_calenda);
                fm = getSupportFragmentManager();
                transaction =  fm.beginTransaction();
                transaction.replace(R.id.fragment,reportFragment);
                transaction.commit();
                break;
            case 2:
                ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.alarm));
                imageOne.setVisibility(View.VISIBLE);
                imageTwo.setVisibility(View.GONE);
                if (BleService.getInstance().getConnectState() == 2)
                    imageOne.setImageResource(R.mipmap.sleep_icon_connect);
                else
                    imageOne.setImageResource(R.mipmap.sleep_icon_ununited);

                fm = getSupportFragmentManager();
                transaction =  fm.beginTransaction();
                transaction.replace(R.id.fragment,alarmClockFragment);
                transaction.commit();
                break;
            case 3:
                ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.me));
                imageOne.setVisibility(View.VISIBLE);
                imageTwo.setVisibility(View.GONE);
                if (BleService.getInstance().getConnectState() == 2)
                    imageOne.setImageResource(R.mipmap.sleep_icon_connect);
                else
                    imageOne.setImageResource(R.mipmap.sleep_icon_ununited);

                fm = getSupportFragmentManager();
                transaction =  fm.beginTransaction();
                transaction.replace(R.id.fragment,personFragment);
                transaction.commit();
                break;
        }
    }

    /**
     * 初始化动画
     * */
    private void intiAnimation() {
        rotateLeft.setDuration(500);//设置动画持续时间
        rotateLeft.setRepeatCount(0);//设置重复次数
        rotateLeft.setFillAfter(true);//动画执行完后是否停留在执行完的状态

        rotateRight.setDuration(500);//设置动画持续时间
        rotateRight.setRepeatCount(0);//设置重复次数
        rotateRight.setFillAfter(true);//动画执行完后是否停留在执行完的状态
    }

    /**
     * 日期选择事件监听
     * */
    private DateSelectView.DateSelectListener dateSelectListener = new DateSelectView.DateSelectListener() {
        @Override
        public void onTouchOk() {
            dateSelectView.startAnimotion(false);
        }
    };

    /**
     * 事件监听
     * */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.menuIv:
                    if (menuListView.getVisibility() == View.GONE||menuListView.getVisibility() == View.INVISIBLE){
                        startSector(true);
                    }
                    else{
                        startSector(false);
                    }
                    break;

                case R.id.imageOne:
                    if (fragmentPos!=1){//如果不是在报告页面，则监听蓝牙断开/连接的按钮
//                        if (BleService.getInstance().isConnect()){
//                            MyAlerDialog.getSingle().showAlerDialog(getString(R.string.tip), getString(R.string.confirmDis),
//                                    getString(R.string.ok), getString(R.string.cancel), true, new MyAlerDialog.AlerDialogOnclickListener() {
//                                        @Override
//                                        public void onDialogTouch(boolean flag) {
//                                            BleService.getInstance().disConnect();
//                                        }
//                                    }, MainActivity.this).show();
//                        }else {
//                            connectBle();
//                        }
                    }else {
                        //TODO 刷新数据
                        ((MyApplication)getApplicationContext()).setUpdating(true);
                        ProgressHudModel.newInstance().show(MainActivity.this,getString(R.string.syncing)
                                ,null,10000);
                        BleService.getInstance().write(ProtocolWriter.writeForReadSleepState());
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                app.setUpdating(false);
                            }
                        },15000);
                    }
                    break;
                case R.id.imageTwo:

                    String date = DateUtil.getDateToString(app.getReportDate());
                    //TODO 选择时间
                    final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
                    dialog.show();
                    final DatePicker picker = new DatePicker(MainActivity.this);
                    picker.setDate(Integer.valueOf(date.substring(0,4)), Integer.valueOf(date.substring(5,7)),Integer.valueOf(date.substring(8,10)));
                    picker.setMode(DPMode.SINGLE);
                    picker.setFestivalDisplay(false);
                    picker.setHolidayDisplay(false);
                    picker.setTodayDisplay(true);
                    picker.setOnDatePickedListener(new DatePicker.OnDatePickedListener() {
                        @Override
                        public void onDatePicked(String date) {
                            if (LoadDataUtil.newInstance().dataCanGet(date)){
                                    app.setReportDate(DateUtil.getStringToDate(date));
                                    EventBus.getDefault().post(new UpdataReportBean(true));
                                    dialog.dismiss();
                            }else {
                                showToast(getString(R.string.future));
                            }
                        }
                    });
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setContentView(picker, params);
                    dialog.getWindow().setGravity(Gravity.CENTER);
                    break;

                case R.id.backView:
                    startSector(false);
                    break;
            }
        }
    };

    /**
     * 隐藏/显示菜单
     * */
    private void startSector(boolean visible){
        if (visible){
            menuListView.startAnimation(rotateLeft);
            menuListView.setVisibility(View.VISIBLE);
            menuListView.setClickable(true);
            listView.setVisibility(View.VISIBLE);
            findViewById(R.id.backView).setVisibility(View.VISIBLE);
        }else {
            menuListView.startAnimation(rotateRight);
            menuListView.setVisibility(View.GONE);
            menuListView.setClickable(false);
            listView.setVisibility(View.INVISIBLE);
            findViewById(R.id.backView).setVisibility(View.INVISIBLE);
        }
    }

    /**
     *接受后台返回的数据
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleConnectStateChange(ConnectBean connectBean){
        if (fragmentPos!=1){
            if (connectBean.isConnect == 2){
                imageOne.setImageResource(R.mipmap.sleep_icon_connect);
            }else if (connectBean.isConnect == 0){
                imageOne.setImageResource(R.mipmap.sleep_icon_ununited);
                showToast(getString(R.string.lineError));
            }else if (connectBean.isConnect == 3){
                imageOne.setImageResource(R.mipmap.sleep_icon_ununited);
                showToast(getString(R.string.deviceNoHere));
            }
        }
    }

    /**
     * 登录请求超时
     * */
    private OnProgressTimeout onProgressTimeout = new OnProgressTimeout() {
        @Override
        public void onTimeout() {
            handler.sendEmptyMessage(402);
        }
    };
    /**
     * 双击退出
     * */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long secondtime = System.currentTimeMillis();
            if (secondtime - firstime > 3000) {
                Toast.makeText(this, getString(R.string.touchAgain),
                        Toast.LENGTH_SHORT).show();
                firstime = System.currentTimeMillis();
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
