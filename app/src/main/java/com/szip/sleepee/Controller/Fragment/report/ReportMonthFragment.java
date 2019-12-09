package com.szip.sleepee.Controller.Fragment.report;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jonas.jgraph.graph.JcoolGraph;
import com.jonas.jgraph.models.Jchart;
import com.jonas.jgraph.utils.MathHelper;
import com.szip.sleepee.Bean.UpdataReportBean;
import com.szip.sleepee.Controller.Fragment.BaseFragment;
import com.szip.sleepee.DB.DBModel.BreathInDayData;
import com.szip.sleepee.DB.DBModel.HeartInDayData;
import com.szip.sleepee.DB.DBModel.SleepInDayData;
import com.szip.sleepee.DB.DBModel.TurnOverInDayData;
import com.szip.sleepee.DB.LoadDataUtil;
import com.szip.sleepee.MyApplication;
import com.szip.sleepee.R;
import com.szip.sleepee.Util.DateUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.jonas.jgraph.graph.JcoolGraph.LINE_EVERYPOINT;

/**
 * Created by Administrator on 2019/3/3.
 */

public class ReportMonthFragment extends BaseFragment {
    private LinearLayout hintLl;

    /**
     * 六项菜单（平均心率、呼吸率等）
     */
    private TextView averageRealSleepTimeTv;
    private TextView averageDeepSleepTimeTv;
    private TextView averageLightSleepTimeTv;
    private TextView averageMidSleepTimeTv;
    private TextView averageHeartTv;
    private TextView averageBreathTv;
    private TextView averageDreamTimeTv;
    private ProgressBar averageRealSleepTimePb;
    private ProgressBar averageDeepSleepTimePb;
    private ProgressBar averageLightSleepTimePb;
    private ProgressBar averageMidSleepTimePb;
    private ProgressBar averageHeartPb;
    private ProgressBar averageBreathPb;
    private ProgressBar averageDreamTimePb;

    /**
     * 六项菜单里面的数据
     */
    private String averageAllTime;
    private String averageMidTime;
    private String averageLightTime;
    private String averageDeepTime;
    private String averageAwakeTime;
    private String averageHeartTime;
    private String averageBreadTime;
    private String averageAwakeTimes;


    private TextView menuOneTv;
    private TextView menuTwoTv;
    private TextView menuThreeTv;
    private TextView menuFourTv;


    /**
     * 四组数据的曲线
     */
    private JcoolGraph mLineChar;
    private JcoolGraph mLineCharforHeart;
    private JcoolGraph mLineCharforBreath;
    private JcoolGraph mLineCharforThird;

    private TextView heartForTableTv;
    private TextView breathForTableTv;
    private TextView awakaTimesForTableTv;


    /**
     * 四组数据
     */
    private List<Jchart> lines1 = new ArrayList<>();
    private List<Jchart> lines2 = new ArrayList<>();
    private List<Jchart> lines3 = new ArrayList<>();
    private List<Jchart> lines4 = new ArrayList<>();

    private TextView reportTimeTv;

    private MyApplication app;

    /**
     * 是否加调用过updateView  true：setUserVisibleHint已经调用过但是视图未加载所以无数据，false：setUserVisibleHint还未调用过
     * */
    private boolean isFirst = true;

    /**
     * 当月第一天的日期
     * */
    private int monthDate;
    private int monthSize;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
                    if (lines1.size()!=0&&lines2.size()!=0&&lines3.size()!=0&&lines4.size()!=0){
                        mLineChar.aniChangeData(lines1);
                        mLineCharforHeart.aniChangeData(lines2);
                        mLineCharforBreath.aniChangeData(lines3);
                        mLineCharforThird.aniChangeData(lines4);
                        averageRealSleepTimeTv.setText(DateUtil.initText(averageAllTime, true));
                        averageDeepSleepTimeTv.setText(DateUtil.initText(averageDeepTime, true));
                        averageMidSleepTimeTv.setText(DateUtil.initText(averageMidTime, true));
                        averageLightSleepTimeTv.setText(DateUtil.initText(averageLightTime, true));
                        averageDreamTimeTv.setText(DateUtil.initText(averageAwakeTime, true));
                        averageHeartTv.setText(DateUtil.initText(averageHeartTime, false));
                        averageBreathTv.setText(DateUtil.initText(averageBreadTime, false));
                        heartForTableTv.setText(averageHeartTime);
                        breathForTableTv.setText(averageBreadTime);
                        awakaTimesForTableTv.setText(averageAwakeTimes);
                    }
                    break;
            }
        }
    };

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            upDataView();
            EventBus.getDefault().register(this);
        }else
            EventBus.getDefault().unregister(this);
    }

    /**
     * 返回一个fragment实例，Activity中调用
     * */
    public static ReportMonthFragment newInstance(String param){
        Bundle bundle = new Bundle();
        bundle.putString("param",param);
        ReportMonthFragment fragment = new ReportMonthFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_report_data;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {
        if (isFirst){
            app = (MyApplication) getActivity().getApplicationContext();
            lines1.add(new Jchart(0, "", (float) 20 / (float) 100, (float) 40 / (float) 100, (float) 60 / (float) 100));
            lines2.add(new Jchart(0, "", 1));
            lines3.add(new Jchart(0, "", 1));
            lines4.add(new Jchart(0, "", 1));
            initView();
            upDataView();
            isFirst = false;
        }
    }

    /**
     * 初始化视图
     */
    @SuppressLint("WrongConstant")
    private void initView() {
        updataDate();
        hintLl = getView().findViewById(R.id.hintLl);
        hintLl.setVisibility(View.VISIBLE);

        reportTimeTv = getView().findViewById(R.id.reportTimeTv);

        //六项数据
        menuOneTv = getView().findViewById(R.id.menuOneTv);
        menuOneTv.setText(mActivity.getString(R.string.averageDeep));
        menuTwoTv = getView().findViewById(R.id.menuTwoTv);
        menuTwoTv.setText(mActivity.getString(R.string.averageMid));
        menuThreeTv = getView().findViewById(R.id.menuThreeTv);
        menuThreeTv.setText(mActivity.getString(R.string.averageLight));
        menuFourTv = getView().findViewById(R.id.menuFourTv);
        menuFourTv.setText(mActivity.getString(R.string.averageAwake));

        //每项数据的时间
        averageRealSleepTimeTv = getView().findViewById(R.id.realSleepTimeTv);
        averageDeepSleepTimeTv = getView().findViewById(R.id.deepSleepTimeTv);
        averageLightSleepTimeTv = getView().findViewById(R.id.lightSleepTimeTv);
        averageMidSleepTimeTv = getView().findViewById(R.id.midSleepTimeTv);
        averageDreamTimeTv = getView().findViewById(R.id.dreamTimeTv);
        averageHeartTv = getView().findViewById(R.id.heartTv);
        averageBreathTv = getView().findViewById(R.id.breathTv);

        //每项数据的进度条
        averageDeepSleepTimePb = getView().findViewById(R.id.deepPb);
        averageMidSleepTimePb = getView().findViewById(R.id.middlePb);
        averageLightSleepTimePb = getView().findViewById(R.id.lightPb);
        averageDreamTimePb = getView().findViewById(R.id.wakePb);
        averageHeartPb = getView().findViewById(R.id.heartPb);
        averageBreathPb = getView().findViewById(R.id.breathPb);

        heartForTableTv = getView().findViewById(R.id.heartForTableTv);
        breathForTableTv = getView().findViewById(R.id.breathForTableTv);
        awakaTimesForTableTv = getView().findViewById(R.id.thirdForTableTv);

        mLineChar = getView().findViewById(R.id.sug_recode_line);
        mLineChar.setSleepFlag(1);
        mLineChar.setInterval(MathHelper.dip2px(getActivity(),20));
        mLineChar.setXvelue(4,monthSize);
        mLineChar.setInterval(4);
        mLineChar.setYaxisValues(0,600,6);
        mLineChar.setGraphStyle(0);
        mLineChar.setLinePointRadio((int)mLineChar.getLineWidth());
        if (!mLineChar.isDetachFlag())
            mLineChar.feedData(lines1);


        mLineCharforHeart = getView().findViewById(R.id.tableForHeart);
        mLineCharforHeart.setXvelue(4,monthSize);
        mLineCharforHeart.setYaxisValues(0,150,5);
        mLineCharforHeart.setGraphStyle(1);
        mLineCharforHeart.setLineStyle(1);
        mLineCharforHeart.setLineMode(LINE_EVERYPOINT);
        mLineCharforHeart.setLinePointRadio((int)mLineCharforHeart.getLineWidth());

        mLineCharforHeart.setNormalColor(Color.parseColor("#d1b793"));
        if (!mLineCharforHeart.isDetachFlag())
            mLineCharforHeart.feedData(lines2);

        mLineCharforBreath = getView().findViewById(R.id.tableForBreath);
        mLineCharforBreath.setXvelue(4,monthSize);
        mLineCharforBreath.setYaxisValues(0,40,5);
        mLineCharforBreath.setGraphStyle(1);
        mLineCharforBreath.setLineStyle(1);
        mLineCharforBreath.setLineMode(LINE_EVERYPOINT);
        mLineCharforBreath.setLinePointRadio((int)mLineCharforBreath.getLineWidth());

        mLineCharforBreath.setNormalColor(Color.parseColor("#21a0bf"));
        if (!mLineCharforBreath.isDetachFlag())
            mLineCharforBreath.feedData(lines3);
//
        mLineCharforThird = getView().findViewById(R.id.anotherView);
        mLineCharforThird.setXvelue(7,7);
        mLineCharforThird.setYaxisValues(0,10,5);
        mLineCharforThird.setGraphStyle(1);
        mLineCharforThird.setLineStyle(1);
        mLineCharforThird.setLineMode(LINE_EVERYPOINT);
        mLineCharforThird.setLinePointRadio((int)mLineCharforThird.getLineWidth());

        mLineCharforThird.setNormalColor(Color.parseColor("#21a0bf"));
        if (!mLineCharforThird.isDetachFlag())
            mLineCharforThird.feedData(lines4);
    }

    /**
     * 更新数据
     */
    private void upDataView() {
        if (app!=null){
            updataDate();
            getData();
        }
    }

    /**
     * 更新时间
     */
    private void updataDate() {
        int []date;
        date = DateUtil.getMonth(app.getReportDate());
        monthDate = app.getReportDate()-date[0];
        monthSize = date[1];
        if (reportTimeTv!=null)
            reportTimeTv.setText(DateUtil.getDateToStringWithoutYear(monthDate)+" - "+DateUtil.getDateToStringWithoutYear(monthDate+monthSize-1));
    }

    /**
     * 获取数据
     */
    void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getDrawData();//获取页面数据用于显示
                handler.sendEmptyMessage(100);
            }
        }).start();
    }

    /**
     * 获取绘图数据
     */
    private void getDrawData() {
        lines1 = new ArrayList<>();
        lines2 = new ArrayList<>();
        lines3 = new ArrayList<>();
        lines4 = new ArrayList<>();

        List<SleepInDayData> sleepInDayDataArrayList = LoadDataUtil.newInstance().loadSleepStateListInMonth(monthDate,monthSize);
        List<HeartInDayData> heartInDayDataList = LoadDataUtil.newInstance().loadHeartListInMonth(monthDate,monthSize);
        List<BreathInDayData> breathInDayDataList = LoadDataUtil.newInstance().loadBreathListInMonth(monthDate,monthSize);
        List<TurnOverInDayData> turnOverInDayDataList = LoadDataUtil.newInstance().loadTurnOverListInMonth(monthDate,monthSize);


        for (int i = 0; i < sleepInDayDataArrayList.size(); i++) {
            /**
             * 拿周睡眠数据
             * */
            lines1.add(new Jchart(sleepInDayDataArrayList.get(i).getAllTime(), String.format("%d", i + 1),
                    (float) sleepInDayDataArrayList.get(i).deepSleepInDay / (float) sleepInDayDataArrayList.get(i).getAllTime(),
                    (float) (sleepInDayDataArrayList.get(i).deepSleepInDay + sleepInDayDataArrayList.get(i).middleSleepInDay) / (float)
                            sleepInDayDataArrayList.get(i).getAllTime(),
                    (float) (sleepInDayDataArrayList.get(i).deepSleepInDay + sleepInDayDataArrayList.get(i).middleSleepInDay +
                            sleepInDayDataArrayList.get(i).lightSleepInDay) / (float) sleepInDayDataArrayList.get(i).getAllTime()));

            /**
             * 拿周心率数据
             * */
            lines2.add(new Jchart(heartInDayDataList.get(i).heartInDay, String.format("%d", i + 1), 1));

            /**
             * 拿周呼吸率数据
             * */
            lines3.add(new Jchart(breathInDayDataList.get(i).breathInDay, String.format("%d", i + 1), 1));

            /**
             * 拿起床次数数据
             * */
            lines4.add(new Jchart(turnOverInDayDataList.get(i).turnOverInDay, String.format("%d", i + 1), 1));
        }

        getAverageData(sleepInDayDataArrayList, heartInDayDataList, breathInDayDataList,turnOverInDayDataList);
    }

    /**
     * 获取周平均数据
     */
    private void getAverageData(List<SleepInDayData> sleepInDayDataList, List<HeartInDayData> heartInDayDataList
            , List<BreathInDayData> breathInDayDataList,List<TurnOverInDayData> turnOverInDayDataList) {
        int allSum = 0;
        int deepSum = 0;
        int midSum = 0;
        int lightSum = 0;
        int awakeSum = 0;
        int heartSum = 0;
        int breathSum = 0;
        int turnSum = 0;
        int size = 0;

        for (int i = 0; i < sleepInDayDataList.size(); i++) {
            if (sleepInDayDataList.get(i).getAllTime() != 0) {
                allSum += sleepInDayDataList.get(i).getAllTime();
                deepSum += sleepInDayDataList.get(i).deepSleepInDay;
                midSum += sleepInDayDataList.get(i).middleSleepInDay;
                lightSum += sleepInDayDataList.get(i).lightSleepInDay;
                awakeSum += sleepInDayDataList.get(i).wakeSleepInDay;
                heartSum += heartInDayDataList.get(i).heartInDay;
                breathSum += breathInDayDataList.get(i).breathInDay;
                turnSum += turnOverInDayDataList.get(i).turnOverInDay;
                size++;
            }

        }

        if (size != 0) {
            //睡眠数据的进度，满进度为睡眠总时长
            averageDeepSleepTimePb.setMax(allSum);
            averageDeepSleepTimePb.setProgress(deepSum);

            averageMidSleepTimePb.setMax(allSum);
            averageMidSleepTimePb.setProgress(midSum);

            averageLightSleepTimePb.setMax(allSum);
            averageLightSleepTimePb.setProgress(lightSum);

            averageDreamTimePb.setMax(allSum);
            averageDreamTimePb.setProgress(awakeSum);

            averageHeartPb.setMax(180);
            averageHeartPb.setProgress(heartSum/size);

            averageBreathPb.setMax(25);
            averageBreathPb.setProgress(breathSum/size);

            averageAllTime = String.format("%dh%02dmin", allSum / 60, allSum % 60);
            averageAwakeTime = String.format("%dh%02dmin", awakeSum / 60, awakeSum % 60);
            averageLightTime = String.format("%dh%02dmin", lightSum / 60, lightSum % 60);
            averageMidTime = String.format("%dh%02dmin", midSum / 60, midSum % 60);
            averageDeepTime = String.format("%dh%02dmin", deepSum / 60, deepSum % 60);
            averageHeartTime = String.format("%02d", heartSum / size) +" "+ mActivity.getString(R.string.heartUnit);
            averageBreadTime = String.format("%02d", breathSum / size) +" "+ mActivity.getString(R.string.heartUnit);
            averageAwakeTimes = String.format("%02d",turnSum/size)+" "+mActivity.getString(R.string.turnOver);

        } else {
            averageAllTime = String.format("--h--min");
            averageAwakeTime = String.format("--h--min");
            averageLightTime = String.format("--h--min");
            averageMidTime = String.format("--h--min");
            averageDeepTime = String.format("--h--min");
            averageHeartTime = String.format("--") +" "+ mActivity.getString(R.string.heartUnit);
            averageBreadTime = String.format("--") +" "+ mActivity.getString(R.string.heartUnit);
            averageAwakeTimes = String.format("--")+" "+mActivity.getString(R.string.turnOver);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMsgFromDatabase(UpdataReportBean connectBean){
        upDataView();
    }
}
