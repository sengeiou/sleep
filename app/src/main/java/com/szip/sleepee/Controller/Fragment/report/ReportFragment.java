package com.szip.sleepee.Controller.Fragment.report;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.szip.sleepee.Adapter.MyPagerAdapter;
import com.szip.sleepee.Controller.Fragment.BaseFragment;
import com.szip.sleepee.Controller.MainActivity;
import com.szip.sleepee.Model.ProgressHudModel;
import com.szip.sleepee.MyApplication;
import com.szip.sleepee.R;
import com.szip.sleepee.Service.BleService;
import com.szip.sleepee.View.NoScrollViewPager;
import com.zhuoting.health.write.ProtocolWriter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 2019/1/23.
 */

public class ReportFragment extends BaseFragment {

    private String[] tabs =new String[]{"day","week","month"};
    private TabLayout mTab;
    private NoScrollViewPager mPager;
    private MyApplication app;
    private KProgressHUD progressHUD;

    private MyPagerAdapter myPagerAdapter;
    private ArrayList<Fragment> fragments = new ArrayList<>();

    private MainActivity activity;
    /**
     * 返回一个fragment实例，Activity中调用
     * */
    public static ReportFragment newInstance(String param){
        Bundle bundle = new Bundle();
        bundle.putString("param",param);
        ReportFragment fragment = new ReportFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_report;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {
        app = (MyApplication) getActivity().getApplicationContext();
        if (mPager == null && mTab == null){
            initView();
            initPager();
        }
    }

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

    /**
     * 初始化界面
     * */
    private void initView() {
        mTab = getView().findViewById(R.id.reportTl);
        mPager = getView().findViewById(R.id.reportVp);
    }

    /**
     * 初始化滑动页面
     * */
    private void initPager() {
        // 创建一个集合,装填Fragment
        ReportDayFragment dayFragment =  ReportDayFragment.newInstance("szip");
        dayFragment.setMainActivity(activity);
        ReportWeekFragment weekFragment =  ReportWeekFragment.newInstance("szip");
        ReportMonthFragment monthFragment =  ReportMonthFragment.newInstance("szip");

        // 装填
        fragments.add(dayFragment);
        fragments.add(weekFragment);
        fragments.add(monthFragment);
        // 创建ViewPager适配器
        myPagerAdapter = new MyPagerAdapter(getActivity().getSupportFragmentManager());
        myPagerAdapter.setFragmentArrayList(fragments);
        // 给ViewPager设置适配器
        mPager.setAdapter(myPagerAdapter);

        // 使用 TabLayout 和 ViewPager 相关联
        mTab.setupWithViewPager(mPager);

        // TabLayout 指示器 (记得自己手动创建3个Fragment,注意是 app包下的Fragment 还是 V4包下的 Fragment)
        for (int i = 0; i < myPagerAdapter.getCount(); i++) {
            TabLayout.Tab tab = mTab.getTabAt(i);//获得每一个tab
            tab.setCustomView(R.layout.main_top_layout);//给每一个tab设置view
            if (i == 0) {
                // 设置第一个tab的TextView是被选择的样式
                tab.getCustomView().findViewById(R.id.main_tv).setSelected(true);//第一个tab被选中
            }
            TextView textView = (TextView) tab.getCustomView().findViewById(R.id.main_tv);
            textView.setText(tabs[i]);//设置tab上的文字
        }
        mTab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getCustomView().findViewById(R.id.main_tv).setSelected(true);
                mPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getCustomView().findViewById(R.id.main_tv).setSelected(false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
}
