package com.szip.sleepee.Controller;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jonas.jgraph.utils.MathHelper;
import com.szip.sleepee.Adapter.MyPagerAdapter;
import com.szip.sleepee.Controller.Fragment.LoginForPhoneFragment;
import com.szip.sleepee.Controller.Fragment.report.ReportDayFragment;
import com.szip.sleepee.Controller.Fragment.report.SleepReortInDayFragment;
import com.szip.sleepee.DB.DBModel.BreathData;
import com.szip.sleepee.DB.DBModel.HeartData;
import com.szip.sleepee.DB.DBModel.SleepData;
import com.szip.sleepee.DB.DBModel.TurnOverData;
import com.szip.sleepee.DB.LoadDataUtil;
import com.szip.sleepee.Interface.MyTouchListener;
import com.szip.sleepee.R;
import com.szip.sleepee.Util.StatusBarCompat;
import com.szip.sleepee.View.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

public class SleepReportInDayActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private NoScrollViewPager viewPager;

    private ArrayList<View> dots = new ArrayList<>();

    private MyPagerAdapter vpAdapter;

    private ArrayList<Fragment> views = new ArrayList<>();

    private LinearLayout dotLl;

    private int reportDay;//报告的时间

    private int oldPosition = 0;// 记录上一次点的位置
    private int currentItem; // 当前页面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_sleep_report_in_day);
        StatusBarCompat.translucentStatusBar(SleepReportInDayActivity.this,true);
        reportDay = getIntent().getIntExtra("date",0);

        initView();
        initData();
    }

    private void initView() {
        findViewById(R.id.backIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView)findViewById(R.id.titleTv)).setText(R.string.sleepReport);
        dotLl = findViewById(R.id.dotLl);
        viewPager = findViewById(R.id.viewpager);
    }

    /**
     * 初始化数据
     * */
    private void initData() {
        Gson gson = new Gson();
        //获取当天的所有睡眠报告段数
//        SleepData sleepData = LoadDataUtil.newInstance().loadSleepStateListInDayLast(reportDay);
        List<SleepData> sleepDataList = LoadDataUtil.newInstance().loadSleepStateListInDay(reportDay);
        List<HeartData> heartDataList = LoadDataUtil.newInstance().loadHeartDataListInDay(reportDay);
        List<BreathData> breathDataList = LoadDataUtil.newInstance().loadBreathDataListInDay(reportDay);
        List<TurnOverData> turnOverDataList = LoadDataUtil.newInstance().loadTurnOverDataListInDay(reportDay);

        //根据睡眠段数绘制报告
        for (int i = 0;i<sleepDataList.size();i++){
            SleepReortInDayFragment sleepReortInDayFragment = SleepReortInDayFragment.newInstance(reportDay,gson.toJson(sleepDataList.get(i)),
                    gson.toJson(heartDataList.get(i)),gson.toJson(breathDataList.get(i)),gson.toJson(turnOverDataList.get(i)));
            sleepReortInDayFragment.setSleepReportInDayActivity(this);
            views.add(sleepReortInDayFragment);
            View view = new View(this);
            LinearLayout.LayoutParams viewLayoutParams1 = new LinearLayout.LayoutParams(MathHelper.dip2px(this,5),
                    MathHelper.dip2px(this,5));
            int dpRight = MathHelper.dip2px(this, 2);
            int dpLeft= MathHelper.dip2px(this, 2);

            viewLayoutParams1.setMargins(dpLeft, 0, dpRight, 0);
            view.setLayoutParams(viewLayoutParams1);
            if (i == 0)
                view.setBackgroundResource(R.drawable.dot_focused);
            else
                view.setBackgroundResource(R.drawable.dot_normal);
            dots.add(view);
            dotLl.addView(view);
        }

        // 创建ViewPager适配器
        vpAdapter = new MyPagerAdapter(this.getSupportFragmentManager());
        vpAdapter.setFragmentArrayList(views);
        viewPager.setOnPageChangeListener(this);
        viewPager.setAdapter(vpAdapter);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        dots.get(oldPosition).setBackgroundResource(
                R.drawable.dot_normal);
        dots.get(position)
                .setBackgroundResource(R.drawable.dot_focused);

        oldPosition = position;
        currentItem = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

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


    public void setViewPagerScroll(boolean isScroll){
        viewPager.setScroll(isScroll);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (myTouchListener!=null)
            myTouchListener.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

}
