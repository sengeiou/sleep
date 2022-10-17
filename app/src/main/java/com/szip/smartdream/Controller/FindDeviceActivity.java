package com.szip.smartdream.Controller;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.szip.smartdream.Adapter.DeviceListAdapter;
import com.szip.smartdream.Bean.HttpBean.BaseApi;
import com.szip.smartdream.Bean.HttpBean.ClockDataBean;
import com.szip.smartdream.Interface.HttpCallbackWithBase;
import com.szip.smartdream.Interface.HttpCallbackWithClockData;
import com.szip.smartdream.Interface.HttpCallbackWithReport;
import com.szip.smartdream.Model.ProgressHudModel;
import com.szip.smartdream.MyApplication;
import com.szip.smartdream.R;
import com.szip.smartdream.Service.BleService;
import com.szip.smartdream.Util.ClientManager;
import com.szip.smartdream.Util.HttpMessgeUtil;
import com.szip.smartdream.Util.StatusBarCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.szip.smartdream.MyApplication.FILE;
import static com.szip.smartdream.Util.HttpMessgeUtil.BINDDEVICE_FLAG;
import static com.szip.smartdream.Util.HttpMessgeUtil.DOWNLOADDATA_FLAG;
import static com.szip.smartdream.Util.HttpMessgeUtil.GETALARM_FLAG;

public class FindDeviceActivity extends BaseActivity implements HttpCallbackWithBase,HttpCallbackWithReport,HttpCallbackWithClockData {

    private Context mContext;

    private ObjectAnimator anim1,anim2,anim3,anim4,anim5,anim6;
    private AnimatorSet set = new AnimatorSet();
    private AnimatorSet set1 = new AnimatorSet();

    private ImageView animIv1,animIv2;

    private ListView listView;
    private DeviceListAdapter adapter;
    private List<SearchResult> mDevices;

    private ImageView backIv;
    private int pos;
    private boolean isSearch = false;
    private MyApplication app;
    private SharedPreferences sharedPreferences;
    ;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 200:
                    BleService.getInstance().setmMac(mDevices.get(pos).getAddress());
                    BleService.getInstance().setmName(mDevices.get(pos).getName());
                    app.getUserInfo().setDeviceCode(mDevices.get(pos).getAddress());
                    app.setUserInfo(app.getUserInfo());
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

                case 400:
                    break;
                default:
                    showToast(getString(R.string.error)+msg.what);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_find_device);
        StatusBarCompat.translucentStatusBar(FindDeviceActivity.this,true);
        mContext = getApplicationContext();
        app = (MyApplication) getApplicationContext();
        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithBase(this);
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithClockData(this);
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithReport(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        ClientManager.getClient().stopSearch();
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithBase(null);
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithClockData(null);
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithReport(null);
    }
    /**
     * 初始化界面
     * */
    private void initView() {

        ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.find));

        backIv = findViewById(R.id.backIv);
        backIv.setVisibility(View.VISIBLE);
        findViewById(R.id.addIv).setVisibility(View.VISIBLE);
        ((ImageView)findViewById(R.id.addIv)).setImageResource(R.mipmap.searchdevice_refresh_new);

        animIv1 = findViewById(R.id.animIv1);
        animIv2 = findViewById(R.id.animIv2);
        mDevices = new ArrayList<SearchResult>();
        listView = findViewById(R.id.deviceList);
        adapter = new DeviceListAdapter(this);
        listView.setAdapter(adapter);
        initAnimator();

        searchDevice();

        /**
         * 获取蓝牙客户端单例，监听手机蓝牙开关状态
         * */
        ClientManager.getClient().registerBluetoothStateListener(new BluetoothStateListener() {
            @Override
            public void onBluetoothStateChanged(boolean openOrClosed) {
                BluetoothLog.v(String.format("onBluetoothStateChanged %b", openOrClosed));
            }
        });


    }

    private void initAnimator() {
        anim1 = ObjectAnimator.ofFloat(animIv1,"alpha",0f,1f,0f);
        anim2 = ObjectAnimator.ofFloat(animIv1,"scaleX",0.5f,2f);
        anim3 = ObjectAnimator.ofFloat(animIv1,"scaleY",0.5f,2f);
        anim1.setInterpolator(new LinearInterpolator());
        anim1.setRepeatCount(-1);
        anim2.setInterpolator(new LinearInterpolator());
        anim2.setRepeatCount(-1);
        anim3.setInterpolator(new LinearInterpolator());
        anim3.setRepeatCount(-1);

        set.setDuration(4000);
        set.play(anim1).with(anim2).with(anim3);

        anim4 = ObjectAnimator.ofFloat(animIv2,"alpha",0f,1f,0f);
        anim5 = ObjectAnimator.ofFloat(animIv2,"scaleX",0.5f,2f);
        anim6= ObjectAnimator.ofFloat(animIv2,"scaleY",0.5f,2f);
        anim4.setInterpolator(new LinearInterpolator());
        anim4.setRepeatCount(-1);
        anim5.setInterpolator(new LinearInterpolator());
        anim5.setRepeatCount(-1);
        anim6.setInterpolator(new LinearInterpolator());
        anim6.setRepeatCount(-1);

        set1.setDuration(4000);
        set1.play(anim4).with(anim5).with(anim6);

    }

    /**
     *初始化事件监听
     * */
    private void initEvent() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
                ProgressHudModel.newInstance().show(FindDeviceActivity.this,getString(R.string.binding),getString(R.string.httpError),10000);
                try {
                    HttpMessgeUtil.getInstance(mContext).getBindDevice(mDevices.get(position).getAddress(),BINDDEVICE_FLAG);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        findViewById(R.id.addIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDevice();
            }
        });

        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferences == null)
                    sharedPreferences = getSharedPreferences(FILE,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLogin",false);
                editor.commit();
                startActivity(new Intent(mContext,LoginActivity.class));
                finish();
            }
        });
    }


    /**
     *搜索设备
     * */
    private void searchDevice() {
        BluetoothAdapter blueadapter = BluetoothAdapter.getDefaultAdapter();
        if (blueadapter.isEnabled()){
            if (!isSearch){
                set.start();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animIv2.setVisibility(View.VISIBLE);
                        set1.start();
                    }
                },2000);

                SearchRequest request = new SearchRequest.Builder()
                        .searchBluetoothLeDevice(20000, 1).build();
                ClientManager.getClient().search(request, mSearchResponse);
            }
        }else {
            Intent bleIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(bleIntent);
        }
    }


    /**
     * 绑定回调
     * */
    @Override
    public void onCallback(BaseApi baseApi, int id) {
        handler.sendEmptyMessage(200);
    }

    /**
     * 获取数据回调
     * */
    @Override
    public void onReport(boolean isNewData) {
        ProgressHudModel.newInstance().diss();
        BleService.getInstance().startConnectDevice();
        startActivity(new Intent(mContext,MainActivity.class));
        finish();
    }

    /**
     * 获取闹钟列表回调
     * */
    @Override
    public void onClockData(ClockDataBean clockDataBean) {
        if(clockDataBean.getData().getArray()!=null){
            ((MyApplication)getApplicationContext()).setClockList1(clockDataBean.getData().getArray());
        }
        handler.sendEmptyMessage(300);
    }

    /**
     * 扫描函数回调
     * */
    private final SearchResponse mSearchResponse = new SearchResponse() {
        @Override
        public void onSearchStarted() {
            BluetoothLog.w("MainActivity.onSearchStarted");
            ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.finding));
            mDevices.clear();
            isSearch = true;
        }

        @Override
        public void onDeviceFounded(SearchResult device) {
            if (!mDevices.contains(device)) {
                mDevices.add(device);
                adapter.setDataList(mDevices);
            }
        }

        @Override
        public void onSearchStopped() {
            BluetoothLog.w("MainActivity.onSearchStopped");
            ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.find));
            isSearch = false;
            set.end();
            set1.end();
        }

        @Override
        public void onSearchCanceled() {
            BluetoothLog.w("MainActivity.onSearchCanceled");
            ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.find));
            set.end();
            set1.end();
            isSearch = false;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (sharedPreferences == null)
                sharedPreferences = getSharedPreferences(FILE,MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("token",null);
            editor.commit();
            startActivity(new Intent(mContext,LoginActivity.class));
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }



}
