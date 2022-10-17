package com.szip.smartdream.Service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;

import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.model.BleGattCharacter;
import com.inuker.bluetooth.library.model.BleGattProfile;

import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.szip.smartdream.Bean.ClockBeanForBluetooch;
import com.szip.smartdream.Bean.ConnectBean;
import com.szip.smartdream.Bean.DevicePowerBean;
import com.szip.smartdream.Bean.HealthAdcDataBean;
import com.szip.smartdream.Bean.HealthBean;
import com.szip.smartdream.Bean.HttpBean.ClockData;
import com.szip.smartdream.Bean.UserInfoWriteBean;
import com.szip.smartdream.Controller.ClockRunningActivity;
import com.szip.smartdream.Controller.MyDeviceActivity;
import com.szip.smartdream.MyApplication;
import com.szip.smartdream.R;
import com.szip.smartdream.Util.ClientManager;
import com.szip.smartdream.Util.DateUtil;
import com.szip.smartdream.Util.MathUitl;
import com.szip.smartdream.View.MyAlerDialog;
import com.zhuoting.health.Config;
import com.zhuoting.health.notify.IDataResponse;
import com.zhuoting.health.notify.IErrorCommand;
import com.zhuoting.health.notify.IRequestResponse;
import com.zhuoting.health.parser.DataParser;
import com.zhuoting.health.parser.IOperation;
import com.zhuoting.health.util.DataUtil;
import com.zhuoting.health.util.Tools;
import com.zhuoting.health.write.ProtocolWriter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static android.media.AudioManager.FLAG_PLAY_SOUND;
import static android.media.AudioManager.STREAM_MUSIC;

public class BleService extends Service {

    private MediaPlayer mediaPlayer;
    private int volume = 0;

    UUID serviceUUID;
    private String mMac = "";
    private String mName;
    public static BleService myBleService ;


    /**
     * 蓝牙连接状态 0:未连接 1：正在连接 2：已经连接 3：连接失败
     * */
    private int connectState = 0;
    private boolean firstConnect = true;//第一次连接成功的时候，获取数据，后面再连接成功，则不再获取数据

    private Thread bleConnect;//连接蓝牙的线程
    private boolean threadRun = true;
    private boolean deviceIsHere;//设备是否在附近存在

    private DownloadManager downloadManager;
    private long mTaskId;

    public BleService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        myBleService = this ;
        ClientManager.getClient().registerBluetoothStateListener(bluetoothStateListener);
        DataParser.newInstance().setDataResponseListener(iDataResponse);
        DataParser.newInstance().setRequestResponseListener(iRequestResponse);
        DataParser.newInstance().setErrorCommandListener(iErrorCommand);
        DataParser.newInstance().setOperation(iOperation);
    }

    public static BleService getInstance(){
        return myBleService ;
    }

    public String getmMac(){
        return mMac;
    }

    public String getName(){
        return mName;
    }

    public void setmMac(String mMac){
        firstConnect = true;//设置mac地址说明已经更换了设备，再次连接成功需要获取数据
        this.mMac = mMac;
    }

    public void setmName(String nName){
        this.mName = nName;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private BluetoothStateListener bluetoothStateListener = new BluetoothStateListener() {
        @Override
        public void onBluetoothStateChanged(boolean openOrClosed) {

        }
    };

    /**
     * 开始蓝牙连接线程
     * */
    public void startConnectDevice(){
        threadRun = true;
        bleConnect = new Thread(new Runnable() {//6秒检查一下连接状态，如果断了就重新连接
            @Override
            public void run() {
                while (threadRun){
                    if(connectState == 0){
                        Log.d("SZIP******","开始搜索蓝牙设备");
                        searchDevice();
                    }
                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        bleConnect.start();
    }

    public void stopConnectDevice(){
        threadRun = false;
        disConnect();
        mMac = null;
    }

    /**
     * 蓝牙连接
     *
     */
    public void connect(){
        if (connectState == 0){
            Log.d("SZIP******","开始连接蓝牙设备mac = "+mMac);
            connectState = 1;
            ClientManager.getClient().connect(mMac,bleConnectResponse);
            ClientManager.getClient().registerConnectStatusListener(mMac,connectStatusListener);
        }
    }

    public void disConnect(){
        Log.d("SZIP******","断开蓝牙设备mac = "+mMac);
        connectState = 0;
        ClientManager.getClient().disconnect(mMac);
    }

    private BleConnectResponse bleConnectResponse = new BleConnectResponse() {
        @Override
        public void onResponse(int code, BleGattProfile data) {
            Log.e("connectRes","code = "+code);
            if( code == 0 ){        // 0 成功
                setGattProfile(data);
            }else{
                Log.d("SZIP******","连接蓝牙失败");
                connectState = 0;
            }
        }
    };


    /**
     * 蓝牙状态的回调
     * */
    private BleConnectStatusListener connectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {
            Log.d("connectStatus",status+"");
            if( status == 0x10){
                connectState = 2;
                Log.d("SZIP******","连接");
                TimerTask timerTask= new TimerTask() {
                    @Override
                    public void run() {
                        updateTime();
                    }
                };
                Timer timer = new Timer();
                timer.schedule(timerTask,500);
            }else{
                Log.d("SZIP******","断开");
                connectState = 0;
            }
            EventBus.getDefault().post(new ConnectBean(connectState));
        }
    };

    /**
     * 连接成功同步睡眠带时间
     * */
    private void updateTime() {
        Calendar calendar = Calendar.getInstance();
        long timeNow = calendar.getTimeInMillis()/1000;
        byte date1,date2,date3,date4;
        date1 = (byte) (timeNow & 0x0ff);
        date2 = (byte) ((timeNow>>8)&0x0ff);
        date3 = (byte) ((timeNow>>16)&0x0ff);
        date4 = (byte) ((timeNow>>24)&0x0ff);
        int gmt[] = DateUtil.getGMT();

        //同步时间
        write(ProtocolWriter.writeForWriteTime(date1,date2,date3,date4,(byte)(gmt[0]&0x0ff),(byte)(gmt[1]&0x0ff)));
        //获取固件版本（为避免通道堵塞，延时100毫秒发送）
        TimerTask timerTask= new TimerTask() {
            @Override
            public void run() {
                write(ProtocolWriter.writeForGetVersion());
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask,100);

        //获取闹钟列表（为避免通道堵塞，延时200毫秒发送）
        TimerTask timerTask1= new TimerTask() {
            @Override
            public void run() {
                write(ProtocolWriter.writeForReadClock());
            }
        };
        Timer timer1 = new Timer();
        timer1.schedule(timerTask1,200);

        //获取电量（为避免通道堵塞，延时300毫秒发送）
        TimerTask timerTask2= new TimerTask() {
            @Override
            public void run() {
                write(ProtocolWriter.writeForGetPower());
            }
        };
        Timer timer2 = new Timer();
        timer2.schedule(timerTask2,300);

        if (firstConnect){
            //同步数据（为避免通道堵塞，延时400毫秒发送）
            TimerTask timerTask3= new TimerTask() {
                @Override
                public void run() {
                    ((MyApplication)getApplicationContext()).setUpdating(true);
                    BleService.getInstance().write(ProtocolWriter.writeForReadSleepState());
                }
            };
            Timer timer3 = new Timer();
            timer3.schedule(timerTask3,400);
            firstConnect = false;
        }
    }

    /**
     * 配置特征值以及接受特征值的通知
     * */
    public void setGattProfile(BleGattProfile profile) {
        List<String> items = new ArrayList<String>();

        List<com.inuker.bluetooth.library.model.BleGattService> services = profile.getServices();

        for (com.inuker.bluetooth.library.model.BleGattService service : services) {
            if(Config.char0.equalsIgnoreCase(service.getUUID().toString())){
                serviceUUID = service.getUUID();
                List<BleGattCharacter> characters = service.getCharacters();
                for(BleGattCharacter character : characters){
                    String uuidCharacteristic = character.getUuid().toString();
                    Log.d("uuid","characteristic : "+uuidCharacteristic);
                    if( character.getUuid().toString().equalsIgnoreCase(Config.char2)){     // 主要用于回复等操作
                        openid(serviceUUID,character.getUuid());
                    }else if(character.getUuid().toString().equalsIgnoreCase(Config.char3)){    // 主要用于实时数据、批量数据上传
                        openid(serviceUUID,character.getUuid());
                    }else if(character.getUuid().toString().equalsIgnoreCase(Config.char4)){    // 主要用于同步历史数据以及ADC数据
                        openid(serviceUUID,character.getUuid());
                    }
                }
            }
        }
    }

    public void openid(UUID serviceUUID, UUID characterUUID) {
        ClientManager.getClient().notify(mMac,serviceUUID,characterUUID,bleNotifyResponse);
    }

    private BleNotifyResponse bleNotifyResponse = new BleNotifyResponse() {
        @Override
        public void onNotify(UUID service, UUID character, byte[] value) {
            String data = DataUtil.byteToHexString(value);
            Log.d("SZIP******","收到蓝牙信息:"+data+",UUID = "+character.toString());
            DataParser.newInstance().parseData(value);
        }

        @Override
        public void onResponse(int code) {

        }
    };

    IRequestResponse iRequestResponse = new IRequestResponse() {

        @Override
        public void onSetUserInfo(byte isSeccuss) {
            if (isSeccuss==0x01)
                EventBus.getDefault().post(new UserInfoWriteBean(true));
        }

        @Override
        public void onDeleteOverTimeData(byte isSeccuss) {
        }
    };

    IDataResponse iDataResponse = new IDataResponse() {

        @Override
        public void onGetDeviceVersion(String hardV, String softV) {
            ((MyApplication)getApplicationContext()).setVersion(hardV,softV);
        }

        @Override
        public void onUpdateHealthData(byte heart, byte breath) {
            EventBus.getDefault().post(new HealthBean(heart,breath));
        }

        @Override
        public void onDrawTheView(int[] heartDatas, int[] breathDatas) {
            EventBus.getDefault().post(new HealthAdcDataBean(heartDatas,breathDatas));
        }

        @Override
        public void onUpdataPower(byte power, boolean isPower) {
            if(power<15)
                MathUitl.showToast(BleService.this,getString(R.string.bettery));
            EventBus.getDefault().post(new DevicePowerBean(power,isPower));
        }

        @Override
        public void onUpdataUserInfo(boolean gender, byte age, byte stature, byte weight) {

        }

        /**
         * 接收完睡眠数据
         * */
        @Override
        public void onReadSleepData(byte[] sleepDatas) {
            ((MyApplication)getApplicationContext()).setUpdownAble(true);
            if (sleepDatas.length!=0) {
                MathUitl.bytesToArray(sleepDatas,0,BleService.this);
            }else {
                ((MyApplication)getApplicationContext()).setDataUpdateState(0);
                ((MyApplication)getApplicationContext()).setDataUpdateState(1);
            }
            //开始读取心率呼吸率数据
            write(ProtocolWriter.writeForReadHistoryHealth());
        }
        /**
         * 接收完心率呼吸率数据
         * */
        @Override
        public void onReadHealthData(byte[] healthDatas) {
            if (healthDatas.length!=0){
                MathUitl.bytesToArray(healthDatas,1,BleService.this);
            }else {
                ((MyApplication)getApplicationContext()).setDataUpdateState(2);
                ((MyApplication)getApplicationContext()).setDataUpdateState(3);
                ((MyApplication)getApplicationContext()).setDataUpdateState(4);
                ((MyApplication)getApplicationContext()).setDataUpdateState(5);
            }
            //开始读取翻身数据
            write(ProtocolWriter.writeForReadHistoryTurnNum());
        }
        /**
         * 接收完翻身数据
         * */
        @Override
        public void onReadTurnOverData(byte[] turnOverDatas) {
            if (turnOverDatas.length!=0){
                MathUitl.bytesToArray(turnOverDatas,2,BleService.this);
            }else{
                ((MyApplication)getApplicationContext()).setDataUpdateState(6);
                ((MyApplication)getApplicationContext()).setDataUpdateState(7);
            }

        }

        @Override
        public void onAddClock(byte index, byte flag) {
            EventBus.getDefault().post(new ClockBeanForBluetooch(index,flag,1));
        }

        @Override
        public void onChangeClock(byte index, byte flag) {
            EventBus.getDefault().post(new ClockBeanForBluetooch(index,flag,2));
        }

        @Override
        public void onCheckClock(byte[] data) {
            ArrayList<ClockData> clockDataList = new ArrayList<>();
            if (data[0]!=0xff){
                int size = data.length/10;
                for (int i = 0;i<size;i++){
                    //Log.d("SZIP******","添加一个新闹钟");
                    ClockData clockData = new ClockData();
                    clockData.setId(-1);
                    clockData.setIndex(data[0+i*10]&0xff);
                    clockData.setIsPhone((data[1+i*10]&0xff)==1?0:1);
                    clockData.setIsOn(data[2+i*10]&0xff);
                    clockData.setHour(data[6+i*10]&0xff);
                    clockData.setMinute(data[7+i*10]&0xff);
                    clockData.setIsIntelligentWake(data[8+i*10]&0xff);
                    clockData.setType(data[9+i*10]&0xff);
                    clockData.setMusic("");
                    clockData.setRepeatState((byte) (data[5+i*10]&0xff));
                    clockData.setRemark("");
                    clockDataList.add(clockData);
                }
                MyApplication app = (MyApplication) getApplicationContext();
                app.setClockList2(clockDataList);
            }
            //Log.d("SZIP******","完成闹钟同步");
        }

        @Override
        public void onAlarm(byte type, int heartData, int breathData) {
            MyApplication app = (MyApplication) getApplicationContext();
            app.setAlarm(type,heartData,breathData);
        }

        @Override
        public void onWakeup() {

            Intent intent1=new Intent(BleService.this, ClockRunningActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.putExtra("pos",-1);
            BleService.this.startActivity(intent1);

//            final AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
//            Log.d("SZIP******","起床啦");
//            volume  = am.getStreamVolume(STREAM_MUSIC);//保存手机原来的音量
//            am.setStreamVolume (STREAM_MUSIC, am.getStreamMaxVolume(STREAM_MUSIC), FLAG_PLAY_SOUND);//设置系统音乐最大
//            if (mediaPlayer==null){
//                mediaPlayer = MediaPlayer.create(BleService.this, R.raw.bugu);
//                mediaPlayer.start();
//                mediaPlayer.setVolume(1f,1f);
//                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                    @Override
//                    public void onCompletion(MediaPlayer mp) {
//                        am.setStreamVolume (STREAM_MUSIC, volume, FLAG_PLAY_SOUND);//播放完毕，设置回之前的音量
//                        mediaPlayer = null;
//                    }
//                });
//            }
//
//            AlertDialog alertDialog = MyAlerDialog.getSingle().showAlerDialog(getString(R.string.tip),getString(R.string.wakeupNow),
//                    getString(R.string.ok),null,true,null, BleService.this);
//            alertDialog.show();
        }
    };


    IErrorCommand iErrorCommand = new IErrorCommand() {
        @Override
        public void onErrorCommand(String commandIdAndKey, int errorType) {
            Log.d("errorCommand","commandIdAndKey : "+commandIdAndKey+" errorType : "+errorType);
        }
    };

    IOperation iOperation = new IOperation() {
        @Override
        public void onDoSynchronizedHistorySport() {
            byte[] smsg = {0x05, 0x02, 0x01};
            smsg = Tools.makeSend(smsg);
            write(smsg);
        }

        @Override
        public void onDoSynchronizedHistorySleep() {
            byte[] smsg = {0x05, 0x04, 0x01};
            smsg = Tools.makeSend(smsg);
            write(smsg);
        }

        @Override
        public void onDoSynchronizedHistoryHeartRate() {
            byte[] smsg = {0x05, 0x06, 0x01};
            smsg = Tools.makeSend(smsg);
            write(smsg);
        }

        @Override
        public void onDoSynchronizedHistoryBloodPressure() {
            byte[] smsg = {0x05, 0x08, 0x01};
            smsg = Tools.makeSend(smsg);
            write(smsg);
        }

        @Override
        public void onDeleteSport() {
            byte[] smsg = {0x05, 0x40, 0x02};
            smsg = Tools.makeSend(smsg);
            write(smsg);
        }

        @Override
        public void onDeleteSleep() {
            byte[] smsg = {0x05, 0x41, 0x02};
            smsg = Tools.makeSend(smsg);
            write(smsg);
        }

        @Override
        public void onDeleteHeartRate() {
            byte[] smsg = {0x05, 0x42, 0x02};
            smsg = Tools.makeSend(smsg);
            write(smsg);
        }

        @Override
        public void onDeleteBloodPressure() {
            byte[] smsg = {0x05, 0x43, 0x02};
            smsg = Tools.makeSend(smsg);
            write(smsg);
        }

        @Override
        public void onDoReceiveAllComplete() {
            byte[] smsg = {0x05, (byte) 0x80, 0x01};
            smsg = Tools.makeSend(smsg);
            write(smsg);
        }
    };

    /**
     *  写数据
     * @param data      写入蓝牙的数据
     */
    public void write(byte[] data){
        //Log.d("SZIP******","发送的蓝牙数据:"+ DataUtil.byteToHexString(data));
        ClientManager.getClient().write(mMac,serviceUUID,UUID.fromString(Config.char1),data,writeResponse);
    }

    private BleWriteResponse writeResponse = new BleWriteResponse() {
        @Override
        public void onResponse(int code) {
            Log.d("writeResp","code = "+code);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     *搜索设备
     * */
    private void searchDevice() {
        SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(5000, 1).build();
        ClientManager.getClient().search(request, mSearchResponse);
    }

    /**
     * 扫描函数回调
     * */
    private final SearchResponse mSearchResponse = new SearchResponse() {
        @Override
        public void onSearchStarted() {
            BluetoothLog.w("MainActivity.onSearchStarted");
            deviceIsHere = false;
        }

        @Override
        public void onDeviceFounded(SearchResult device) {
            if(device.getAddress().equals(mMac)){
                Log.d("SZIP******","搜索到蓝牙设备");
                deviceIsHere = true;
                mName = device.getName();
                ClientManager.getClient().stopSearch();
                connect();
            }
        }

        @Override
        public void onSearchStopped() {
            Log.d("SZIP******","停止搜索");
            if (deviceIsHere == false)
                EventBus.getDefault().post(new ConnectBean(3));
        }

        @Override
        public void onSearchCanceled() {
        }
    };

    public int getConnectState() {
        return connectState;
    }


    /**
     * 下载固件
     * */
    public void downloadFirmsoft(String versionUrl, String versionName,String path) {
        //创建下载任务
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(versionUrl));
//        request.addRequestHeader("token",HttpMessgeUtil.getInstance(BleService.this).getToken());
        request.setAllowedOverRoaming(false);//漫游网络是否可以下载

        //设置文件类型，可以在下载结束后自动打开该文件
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(versionUrl));
        request.setMimeType(mimeString);

        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(true);

        //sdcard的目录下的download文件夹，必须设置
        request.setDestinationInExternalPublicDir("/SleepEE/", versionName);
//        request.setDestinationInExternalFilesDir(BleService.this,path,versionName);
        //Log.d("SZIP******","path = "+path+";version = "+versionName+";uri = " + versionUrl);

        //将下载请求加入下载队列
        downloadManager = (DownloadManager) BleService.this.getSystemService(Context.DOWNLOAD_SERVICE);
        //加入下载队列后会给该任务返回一个long型的id，
        //通过该id可以取消任务，重启任务等等
        mTaskId = downloadManager.enqueue(request);

        //注册广播接收者，监听下载状态
        BleService.this.registerReceiver(receiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    //广播接受者，接收下载状态
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkDownloadStatus();//检查下载状态
        }
    };

    private void checkDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mTaskId);//筛选下载任务，传入任务ID，可变参数
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    //Log.d("SZIP******",">>>下载暂停");
                case DownloadManager.STATUS_PENDING:
                    //Log.d("SZIP******",">>>下载延迟");
                case DownloadManager.STATUS_RUNNING:
                    //Log.d("SZIP******",">>>正在下载");
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    //Log.d("SZIP******",">>>下载完成");
                    EventBus.getDefault().post(new ConnectBean(1));
                    break;
                case DownloadManager.STATUS_FAILED:
                    //Log.d("SZIP******",">>>下载失败");
                    break;
            }
        }
    }
}
