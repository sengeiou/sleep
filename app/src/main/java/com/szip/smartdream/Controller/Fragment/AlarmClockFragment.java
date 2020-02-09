package com.szip.smartdream.Controller.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.roamer.slidelistview.SlideListView;
import com.szip.smartdream.Adapter.AlarmClockAdapter;
import com.szip.smartdream.Bean.DeviceClockIsUpdataBean;
import com.szip.smartdream.Bean.HttpBean.ClockData;
import com.szip.smartdream.Controller.ClockSettingActivity;
import com.szip.smartdream.MyApplication;
import com.szip.smartdream.R;
import com.szip.smartdream.Service.BleService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * Created by Administrator on 2019/1/23.
 */

public class AlarmClockFragment extends BaseFragment {

    private MyApplication app;
    private KProgressHUD progressHUD;

    private SlideListView listView;
    private AlarmClockAdapter alarmClockAdapter;
    private ArrayList<ClockData> list;

    private Handler handler = new Handler(){};

    private LinearLayout addLl;

    /**
     * 上下文
     * */
    private Context mContext;


    /**
     * 返回一个fragment实例，Activity中调用
     * */
    public static AlarmClockFragment newInstance(String param){
        Bundle bundle = new Bundle();
        bundle.putString("param",param);
        AlarmClockFragment fragment = new AlarmClockFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_alarm_clock;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {
        mContext = getContext();
        app = (MyApplication) getActivity().getApplicationContext();
        initData();
        initView();
    }

    private void initData() {
        list = app.getClockList();
    }

    /**
     * 初始化界面
     * */
    private void initView() {

        addLl = getView().findViewById(R.id.addLl);
        addLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setClass(mContext,ClockSettingActivity.class);
                            intent.putExtra("add",true);
                            startActivity(intent);
            }
        });

        listView = getView().findViewById(R.id.slideList);
        alarmClockAdapter = new AlarmClockAdapter(getContext(),list);
        listView.setAdapter(alarmClockAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (list.get(position).getType()!=2 || BleService.getInstance().getConnectState()==2){
                    Intent intent = new Intent();
                    intent.setClass(getActivity(),ClockSettingActivity.class);
                    intent.putExtra("flag",list.get(position).getType());
                    intent.putExtra("add",false);
                    intent.putExtra("pos",position);
                    startActivity(intent);
                }else
                    Toast.makeText(getActivity(),getString(R.string.blueUnline),Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (alarmClockAdapter!=null){
            list = app.getClockList();
            alarmClockAdapter.setList(list);
            alarmClockAdapter.notifyDataSetChanged();
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateClock(DeviceClockIsUpdataBean connectBean){
        if (alarmClockAdapter!=null){
            list = app.getClockList();
            alarmClockAdapter.setList(list);
            alarmClockAdapter.notifyDataSetChanged();
        }
    }
}
