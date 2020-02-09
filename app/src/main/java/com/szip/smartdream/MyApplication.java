package com.szip.smartdream;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.inuker.bluetooth.library.BluetoothContext;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.szip.smartdream.Bean.DeviceClockIsUpdataBean;
import com.szip.smartdream.Bean.HttpBean.BaseApi;
import com.szip.smartdream.Bean.HttpBean.ClockData;
import com.szip.smartdream.Bean.HttpBean.ClockDataBean;
import com.szip.smartdream.Bean.HttpBean.UserInfoBean;
import com.szip.smartdream.Bean.UpdataReportBean;
import com.szip.smartdream.Bean.UserInfo;
import com.szip.smartdream.Broadcat.UtilBroadcat;
import com.szip.smartdream.Interface.HttpCallbackWithClockData;
import com.szip.smartdream.Interface.HttpCallbackWithReport;
import com.szip.smartdream.Interface.HttpCallbackWithUserInfo;
import com.szip.smartdream.Model.ProgressHudModel;
import com.szip.smartdream.Service.BleService;
import com.szip.smartdream.Util.ClockUtil;
import com.szip.smartdream.Util.DateUtil;
import com.szip.smartdream.Util.HttpMessgeUtil;
import com.szip.smartdream.Util.JsonGenericsSerializator;
import com.szip.smartdream.Util.MathUitl;
import com.szip.smartdream.Util.TopExceptionHandler;
import com.zhuoting.health.write.ProtocolWriter;
import com.zhy.http.okhttp.callback.GenericsCallback;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import no.nordicsemi.android.dfu.DfuServiceInitiator;
import okhttp3.Call;

import static com.szip.smartdream.Util.HttpMessgeUtil.DOWNLOADDATA_FLAG;
import static com.szip.smartdream.Util.HttpMessgeUtil.GETALARM_FLAG;
import static com.szip.smartdream.Util.HttpMessgeUtil.UPDOWNDATA_FLAG;

/**
 * Created by Administrator on 2019/1/22.
 */

public class MyApplication extends Application implements HttpCallbackWithUserInfo,HttpCallbackWithClockData,HttpCallbackWithReport {


    /**
     * 总闹钟列表、服务器端的睡眠提醒闹钟列表、手环端的本地闹钟列表
     * */
    private ArrayList<ClockData> clockList;
    private ArrayList<ClockData> clockList1;


    private UserInfo userInfo;

    private static final String TAG = "MyApplication";

    private static MyApplication instance;
    public static boolean isBackground = false;

    public static boolean isSyncing = false;

    public static MyApplication getInstance() {
        return instance;
    }

    private int mFinalCount;

    /**
     * 报告的日期
     * */
    private int reportDate;

    /**
     * 今天的日期
     * */
    private int todayTime;

    /**
     * 是否开始睡眠
     * */
    private boolean startSleep = false;

    /**
     * 上次上传数据到服务器的时间
     * */
    private long upLoadTime;


    private SharedPreferences sharedPreferences;
    public static String FILE = "sleepEE";
    private boolean isGetData = true;

    private String hardV;
    private String softV;

    private boolean isRun = true;//拿个人信息的线程是否跑

    /**
     * 启动状态 0：登录状态 1：未登录状态 2：登录过期状态
     * */
    private int startState = 0;

    public static String fileName = Environment.getExternalStorageDirectory() + "/MecareMsg.txt";

    private boolean isSleepDataStandby = false,isSleepDataIndayStandby = false
            ,isHeartDataStandby = false,isHeartDataIndayStandby = false
            ,isBreathDataStandby = false,isBreathDataIndayStandby = false
            ,isTurnOverDataStanby = false,isTurnOverDataIndayStanby = false;

    private boolean isUpdating = false;//判断当前是否在同步中
    private boolean isFirst = true;


    public static final String CONNECTED_DEVICE_CHANNEL = "connected_device_channel";
    public static final String FILE_SAVED_CHANNEL = "file_saved_channel";
    public static final String PROXIMITY_WARNINGS_CHANNEL = "proximity_warnings_channel";
    private boolean updownAble = false;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 200:
                    try {
                        HttpMessgeUtil.getInstance(getApplicationContext()).getForGetClockList(GETALARM_FLAG);
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
                        HttpMessgeUtil.getInstance(getApplicationContext()).getForDownloadReportData(""+(calendar.getTimeInMillis()/1000-30*24*60*60),
                                "30",DOWNLOADDATA_FLAG);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("Heal","onCreate");

        instance = this;
        BluetoothContext.set(this);
        java.io.File file = new File(fileName);

        FlowManager.init(this);




        /**
         * 把log上传到云端
         * */
        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(this));



        /**
         * Android8.0之后DFU升级需要获取部分弹框权限
         * */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DfuServiceInitiator.createDfuNotificationChannel(this);

            final NotificationChannel channel = new NotificationChannel(CONNECTED_DEVICE_CHANNEL, getString(R.string.channel_connected_devices_title), NotificationManager.IMPORTANCE_LOW);
            channel.setDescription(getString(R.string.channel_connected_devices_description));
            channel.setShowBadge(false);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            final NotificationChannel fileChannel = new NotificationChannel(FILE_SAVED_CHANNEL, getString(R.string.channel_files_title), NotificationManager.IMPORTANCE_LOW);
            fileChannel.setDescription(getString(R.string.channel_files_description));
            fileChannel.setShowBadge(false);
            fileChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            final NotificationChannel proximityChannel = new NotificationChannel(PROXIMITY_WARNINGS_CHANNEL, getString(R.string.channel_proximity_warnings_title), NotificationManager.IMPORTANCE_LOW);
            proximityChannel.setDescription(getString(R.string.channel_proximity_warnings_description));
            proximityChannel.setShowBadge(false);
            proximityChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
            notificationManager.createNotificationChannel(fileChannel);
            notificationManager.createNotificationChannel(proximityChannel);
        }


        /**
         * 注册广播
         * */
        UtilBroadcat broadcat = new UtilBroadcat(getApplicationContext());
        broadcat.onRegister();

        //注册网络回调
        HttpMessgeUtil.getInstance(this).setHttpCallbackWithUserInfo(this);
        HttpMessgeUtil.getInstance(this).setHttpCallbackWithClockData(this);
        HttpMessgeUtil.getInstance(this).setHttpCallbackWithReport(this);

        /**
         * 启动后台
         * */
        startService(new Intent(this,BleService.class));

        //取缓存用户信息
        SharedPreferences  sharedPreferences = getSharedPreferences(FILE,MODE_PRIVATE);

        upLoadTime = sharedPreferences.getLong("lastLoadTime",0);
        //判断登录状态
        String token = sharedPreferences.getString("token",null);
        if (token==null){//未登录
            startState = 1;
        }else {//已登录
            startState = 0;
            HttpMessgeUtil.getInstance(this).setToken(token);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isRun){
                        try {
                            HttpMessgeUtil.getInstance(MyApplication.this).getForGetInfo();
                            Thread.sleep(2000);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        }

        this.reportDate = DateUtil.getStringToDate("today");
        this.todayTime = DateUtil.getStringToDate("today");

        instance = this;

        registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                mFinalCount++;
                //如果mFinalCount ==1，说明是从后台到前台
                Log.e("onActivityStarted", mFinalCount + "");
                if (mFinalCount == 1) {
                    //说明从后台回到了前台
                    Log.i(TAG, " 返回到了 前台");
                    MyApplication.isBackground = false;
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                mFinalCount--;
                //如果mFinalCount ==0，说明是前台到后台

                Log.i("onActivityStopped", mFinalCount + "");
                if (mFinalCount == 0) {
                    //说明从前台回到了后台
                    Log.i(TAG, " 切换到了 后台");
                    MyApplication.isBackground = true;
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });

    }

    public void setVersion(String hardV, String softV) {
        this.hardV = hardV;
        this.softV = softV;
    }


    public int getReportDate() {
        return reportDate;
    }

    public void setReportDate(int reportDate) {
        this.reportDate = reportDate;
    }

    public int getTodayTime() {
        return todayTime;
    }

    public String getUserId() {
        return "";
    }

    public boolean isGetData() {
        return isGetData;
    }

    public void setGetData(boolean getData) {
        isGetData = getData;
    }

    public String getHardV() {
        return hardV;
    }

    public String getSoftV() {
        return softV;
    }

    public void setStartSleep(boolean sleep){
        startSleep = sleep;
    }

    public boolean isStartSleep() {
        return startSleep;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        MathUitl.saveInfoData(getApplicationContext(),userInfo);
    }

    @Override
    public void onTerminate() {
        // TODO Auto-generated method stub
        super.onTerminate();
    }

    public ArrayList<ClockData> getClockList() {
        if (clockList!=null){
            return clockList;
        }else
            return new ArrayList<>();

    }

    public void clearClockList(){
        this.clockList = null;
        this.clockList1 = null;
    }

    public void addClockList(ClockData clockData) {
        if (this.clockList == null)
            this.clockList = new ArrayList<>();
        if(this.clockList1 == null)
            this.clockList1 = new ArrayList<>();

        this.clockList.add(clockData);
        this.clockList1.add(clockData);

        Log.d("clock******","clockList = "+clockList.size()+";clockList1 = "+clockList1.size());
        Collections.sort(this.clockList);
        ClockUtil.getInstance(this).setClockDataList(this.clockList);
    }

    public void removeClock(int position){
        ClockData clockData = clockList.get(position);
        this.clockList.remove(position);
        for (int i = 0;i<clockList1.size();i++){
            if (clockData.getId() == clockList1.get(i).getId()){
                clockList1.remove(i);
                break;
            }
        }
        Log.d("clock******","clockList = "+clockList.size()+";clockList1 = "+clockList1.size());
        Collections.sort(this.clockList);
        ClockUtil.getInstance(this).setClockDataList(this.clockList);
    }

    public void changeClockList(int pos,ClockData clockData) {
        this.clockList.set(pos,clockData);
        for (int i = 0;i<clockList1.size();i++){
            if (clockData.getId()==clockList1.get(i).getId()){
                clockList1.set(i,clockData);
            }
        }
        Collections.sort(this.clockList);
        ClockUtil.getInstance(this).setClockDataList(this.clockList);
    }

    /**
     * 读取到服务器的闹钟列表，刷新闹钟列表
     * */
    public void setClockList1(ArrayList<ClockData> clockList) {
        this.clockList = clockList;
        this.clockList1 = clockList;
        Collections.sort(this.clockList);
        ClockUtil.getInstance(this).setClockDataList(this.clockList);
        EventBus.getDefault().post(new DeviceClockIsUpdataBean());
    }

    /**
     * 读取到睡眠带的闹钟列表，刷新闹钟列表
     * */
    public void setClockList2(ArrayList<ClockData> clockList) {
        this.clockList = new ArrayList<>();
        if (clockList1!=null){
            for (int i = 0;i<clockList1.size();i++){
                if (clockList1.get(i).getType()!=2){//如果不是起床闹钟，直接添加进闹钟列表
                    this.clockList.add(clockList1.get(i));
                }else {//如果是起床闹钟，与底层闹钟列表校验之后才能加入闹钟列表
                    int flag  = 1;//0:闹钟在底层存在，且与云端的不一致 1:底层闹钟不存在 else:底层闹钟存在，且与云端的一致
                    int index = 0;
                    for (int a = 0;a<clockList.size();a++){
                        if (clockList1.get(i).getIndex()==clockList.get(a).getIndex()) {
                            index = a;
                            clockList.get(a).setId(clockList1.get(i).getId());
                            if (clockList.get(a).getHour() == clockList1.get(i).getHour() && clockList.get(a).getMinute() == clockList1.get(i).getMinute()){
                                flag = 2;
                                Log.d("CLOCK******","clock is right:"+String.format("at:%d,%d:%d",clockList.get(a).getId(),
                                        clockList.get(a).getHour(),clockList.get(a).getMinute()));

                            } else{
                                Log.d("CLOCK******","clock is wrong:"+String.format("at:%d,%d:%d",clockList.get(a).getId(),
                                        clockList.get(a).getHour(),clockList.get(a).getMinute()));
                                flag = 0;
                            }
                            break;
                        }
                    }
                    if (flag == 0){
                        try {
                            HttpMessgeUtil.getInstance(this).postForChangeClock(clockList.get(index).getId()+"",clockList.get(index).getType()+"",
                                    clockList.get(index).getHour()+"",clockList.get(index).getMinute()+"",clockList.get(index).getIndex()+"",
                                    clockList.get(index).getIsPhone()+"",clockList.get(index).getIsOn()+"","","",
                                    "0","",-1);
                            Log.d("CLOCK******","wrong clock is changed:"+clockList.get(index).getId());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else if (flag == 1){
                        try {
                            HttpMessgeUtil.getInstance(this).getForDeleteClock(clockList1.get(i).getId()+"",callback,0);
                            Log.d("CLOCK******","wrong clock is delete:"+clockList1.get(i).getId());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            for (int i = 0;i<clockList.size();i++){//把底层存在，云端不存在的闹钟删掉
                if (clockList.get(i).getId() == -1){
                    BleService.getInstance().write(ProtocolWriter.writeForDeleteClock((byte)clockList.get(i).getIndex()));
                    clockList.remove(i);
                    Log.d("CLOCK******","ERROR CLOCK IS DELETE"+clockList.get(i).getIndex());
                }
            }
            this.clockList.addAll(clockList);
        }else{
            this.clockList = clockList;
        }
        Collections.sort(this.clockList);
        ClockUtil.getInstance(this).setClockDataList(this.clockList);
        EventBus.getDefault().post(new DeviceClockIsUpdataBean());
    }

    public boolean isUpdating() {
        return isUpdating;
    }

    public void setUpdating(boolean updating) {
        /**
         * 表更新状态，为true的时候表示当前数据正在同步
         * */
        isUpdating = updating;
    }

    public int getStartState() {
        return startState;
    }

    public void setUpdownAble(boolean updownAble) {
        this.updownAble = updownAble;
    }

    /**
     * 判断数据是否全部同步完毕
     * */
    public void setDataUpdateState(int flag){
        switch (flag){
            case 0:
                isSleepDataStandby = true;
                break;
            case 1:
                isSleepDataIndayStandby = true;
                break;
            case 2:
                isHeartDataStandby = true;
                break;
            case 3:
                isHeartDataIndayStandby = true;
                break;
            case 4:
                isBreathDataStandby = true;
                break;
            case 5:
                isBreathDataIndayStandby = true;
                break;

            case 6:
                isTurnOverDataStanby = true;
                break;

            case 7:
                isTurnOverDataIndayStanby = true;
                break;
        }

        if (isSleepDataStandby&&isSleepDataIndayStandby&&isHeartDataIndayStandby&&isHeartDataStandby&&
                isBreathDataIndayStandby&&isBreathDataStandby&&isTurnOverDataStanby&&isTurnOverDataIndayStanby){
            //通知前端刷新界面
            ProgressHudModel.newInstance().diss();
            EventBus.getDefault().post(new UpdataReportBean(false));
            isSleepDataStandby = false;
            isSleepDataIndayStandby = false;
            isHeartDataStandby = false;
            isHeartDataIndayStandby = false;
            isBreathDataStandby = false;
            isBreathDataIndayStandby = false;
            isTurnOverDataStanby = false;
            isTurnOverDataIndayStanby = false;
            isUpdating = false;
            if(updownAble){
                Toast.makeText(this,getString(R.string.syncOk),Toast.LENGTH_SHORT).show();
                String json = MathUitl.getStringWithJson(upLoadTime);
                if (!json.equals("{\"data\":[]}")){
                    try {
                        HttpMessgeUtil.getInstance(this).postForUpdownReportData(json, UPDOWNDATA_FLAG);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                updownAble = false;
                BleService.getInstance().write(ProtocolWriter.writeForDeleteData());//同步完成删除底层的数据
            }
        }
    }

    private GenericsCallback<BaseApi> callback = new GenericsCallback<BaseApi>(new JsonGenericsSerializator()) {
        @Override
        public void onError(Call call, Exception e, int id) {

        }

        @Override
        public void onResponse(BaseApi response, int id) {

        }
    };

    @Override
    public void onUserInfo(UserInfoBean userInfoBean) {
        if (userInfoBean.getCode() == 401){//登录过期
            startState = 2;
        }else {//保存用户信息
            setUserInfo(userInfoBean.getData());
            handler.sendEmptyMessage(200);//获取信息成功，开始获取闹钟数据
        }
    }

    /**
     * 闹钟网络回调
     * */
    @Override
    public void onClockData(ClockDataBean clockDataBean) {
        if(clockDataBean.getData().getArray()!=null){
            setClockList1(clockDataBean.getData().getArray());
        }
        handler.sendEmptyMessage(300);//获取闹钟数据成功，开始获取报告数据
    }


    /**
     * 向服务器获取数据的回调
     * */
    @Override
    public void onReport(boolean isNewData) {
        HttpMessgeUtil.getInstance(this).setHttpCallbackWithUserInfo(null);
        HttpMessgeUtil.getInstance(this).setHttpCallbackWithClockData(null);
        HttpMessgeUtil.getInstance(this).setHttpCallbackWithReport(null);
        isRun = false;
    }
}