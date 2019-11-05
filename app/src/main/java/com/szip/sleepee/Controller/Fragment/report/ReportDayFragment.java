package com.szip.sleepee.Controller.Fragment.report;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jonas.jgraph.graph.JcoolGraph;
import com.jonas.jgraph.models.Jchart;
import com.szip.sleepee.Bean.HealthDataBean;
import com.szip.sleepee.Bean.HttpBean.ReportDataBean;
import com.szip.sleepee.Bean.SleepStateBean;
import com.szip.sleepee.Bean.UpdataReportBean;
import com.szip.sleepee.Controller.Fragment.BaseFragment;
import com.szip.sleepee.Controller.SleepReportInDayActivity;
import com.szip.sleepee.DB.DBModel.BreathData;
import com.szip.sleepee.DB.DBModel.HeartData;
import com.szip.sleepee.DB.DBModel.SleepData;
import com.szip.sleepee.DB.DBModel.TurnOverData;
import com.szip.sleepee.DB.LoadDataUtil;
import com.szip.sleepee.Interface.HttpCallbackWithReport;
import com.szip.sleepee.MyApplication;
import com.szip.sleepee.R;
import com.szip.sleepee.Util.DateUtil;
import com.szip.sleepee.Util.HttpMessgeUtil;
import com.szip.sleepee.Util.MathUitl;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.jonas.jgraph.graph.JcoolGraph.LINE_DASH_0;
import static com.jonas.jgraph.graph.JcoolGraph.LINE_EVERYPOINT;
import static com.szip.sleepee.Util.HttpMessgeUtil.DOWNLOADDATA_FLAG;

/**
 * Created by Administrator on 2019/3/3.
 */

public class ReportDayFragment extends BaseFragment {

    private TextView moreTv;

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
    private ProgressBar deepSleepTimePb;
    private ProgressBar lightSleepTimePb;
    private ProgressBar midSleepTimePb;
    private ProgressBar dreamTimePb;
    private ProgressBar averageHeartPb;
    private ProgressBar averageBreathPb;


    /**
     * 六项菜单里面的数据
     */
    private int allSleepTime;
    private int deepSleepTime;
    private int middleSleepTime;
    private int lightSleepTime;
    private int awakeSleepTime;
    private int averageHeartData = 0;
    private int averageBreathData = 0;
    private int averageTurnOver;

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

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
                    if(lines1.size()!=0&&lines2.size()!=0&&lines3.size()!=0&&lines4.size()!=0){
                        mLineChar.aniChangeData(lines1);
                        mLineCharforHeart.aniChangeData(lines2);
                        mLineCharforBreath.aniChangeData(lines3);
                        mLineCharforThird.aniChangeData(lines4);
                        if (lines1.size()==1){//没数据
                            averageRealSleepTimeTv.setText(DateUtil.initText("--h--min", true));
                            averageDeepSleepTimeTv.setText(DateUtil.initText("--h--min", true));
                            averageMidSleepTimeTv.setText(DateUtil.initText("--h--min", true));
                            averageLightSleepTimeTv.setText(DateUtil.initText("--h--min", true));
                            averageDreamTimeTv.setText(DateUtil.initText("--h--min", true));
                            averageHeartTv.setText(DateUtil.initText("--"+mActivity.getString(R.string.heartUnit), false));
                            averageBreathTv.setText(DateUtil.initText("--" +mActivity.getString(R.string.heartUnit), false));
                            heartForTableTv.setText("--"+" "+mActivity.getString(R.string.heartUnit));
                            breathForTableTv.setText("--" +" "+mActivity.getString(R.string.heartUnit));
                            averageHeartPb.setProgress(0);
                            averageBreathPb.setProgress(0);
                            deepSleepTimePb.setProgress(0);
                            midSleepTimePb.setProgress(0);
                            lightSleepTimePb.setProgress(0);
                            dreamTimePb.setProgress(0);
                            awakaTimesForTableTv.setText("--"+" "+mActivity.getString(R.string.turnOver));
                        }else {
                            averageRealSleepTimeTv.setText(DateUtil.initText(String.format("%02dh%02dmin",allSleepTime/60,allSleepTime%60), true));
                            averageDeepSleepTimeTv.setText(DateUtil.initText(String.format("%02dh%02dmin",deepSleepTime/60,deepSleepTime%60), true));
                            averageMidSleepTimeTv.setText(DateUtil.initText(String.format("%02dh%02dmin",middleSleepTime/60,middleSleepTime%60), true));
                            averageLightSleepTimeTv.setText(DateUtil.initText(String.format("%02dh%02dmin",lightSleepTime/60,lightSleepTime%60), true));
                            averageDreamTimeTv.setText(DateUtil.initText(String.format("%02dh%02dmin",awakeSleepTime/60,awakeSleepTime%60), true));
                            averageHeartTv.setText(DateUtil.initText(String.format("%02d", averageHeartData)+mActivity.getString(R.string.heartUnit), false));
                            averageBreathTv.setText(DateUtil.initText(String.format("%02d",averageBreathData) +mActivity.getString(R.string.heartUnit), false));
                            heartForTableTv.setText(String.format("%02d", averageHeartData)+" "+mActivity.getString(R.string.heartUnit));
                            breathForTableTv.setText(String.format("%02d",averageBreathData) +" "+mActivity.getString(R.string.heartUnit));
                            averageHeartPb.setProgress(averageHeartData);
                            averageBreathPb.setProgress(averageBreathData);
                            deepSleepTimePb.setProgress(deepSleepTime);
                            midSleepTimePb.setProgress(middleSleepTime);
                            lightSleepTimePb.setProgress(lightSleepTime);
                            dreamTimePb.setProgress(awakeSleepTime);
                            awakaTimesForTableTv.setText(String.format("%02d", averageTurnOver)+" "+mActivity.getString(R.string.turnOver));
                        }
                    }
                    break;
            }
        }
    };

    /**
     * 返回一个fragment实例，Activity中调用
     * */
    public static ReportDayFragment newInstance(String param){
        Bundle bundle = new Bundle();
        bundle.putString("param",param);
        ReportDayFragment fragment = new ReportDayFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            upDataView();
            EventBus.getDefault().register(this);
        }else{
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_report_data;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {
        if (isFirst){
            app = (MyApplication) getActivity().getApplicationContext();
            lines1.add(new Jchart((float) 0,"",(float) 0));
            lines2.add(new Jchart(0, "",1));
            lines3.add(new Jchart(0, "",1));
            lines4.add(new Jchart(0, "",1));
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
        moreTv = getView().findViewById(R.id.moreTv);
        moreTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(app==null)
                    app = (MyApplication) getActivity().getApplicationContext();
                Intent intent = new Intent();
                intent.setClass(getActivity(), SleepReportInDayActivity.class);
                intent.putExtra("date",app.getReportDate());
                startActivity(intent);
            }
        });

        reportTimeTv = getView().findViewById(R.id.reportTimeTv);
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
        deepSleepTimePb = getView().findViewById(R.id.deepPb);
        midSleepTimePb = getView().findViewById(R.id.middlePb);
        lightSleepTimePb = getView().findViewById(R.id.lightPb);
        dreamTimePb = getView().findViewById(R.id.wakePb);
        averageHeartPb = getView().findViewById(R.id.heartPb);
        averageHeartPb.setMax(180);
        averageBreathPb = getView().findViewById(R.id.breathPb);
        averageBreathPb.setMax(25);

        heartForTableTv = getView().findViewById(R.id.heartForTableTv);
        breathForTableTv = getView().findViewById(R.id.breathForTableTv);
        awakaTimesForTableTv = getView().findViewById(R.id.thirdForTableTv);

        mLineChar = getView().findViewById(R.id.sug_recode_line);
        mLineChar.setXvelue(5,0,1560);
        mLineChar.setGraphStyle(1);
        mLineChar.setLineStyle(1);
        mLineChar.setLineMode(LINE_DASH_0);
        mLineChar.setSleepFlag(2);
//        mLineChar.drawPoint(true);
        mLineChar.setYaxisValues(0,255,5);
        mLineChar.setShaderAreaColors(Color.parseColor("#bb21a0bf"), Color.TRANSPARENT);
        mLineChar.setLinePointRadio((int)mLineChar.getLineWidth());
        mLineChar.setNormalColor(Color.parseColor("#21a0bf"));
        if (!mLineChar.isDetachFlag())
            mLineChar.feedData(lines1);

        mLineCharforHeart = getView().findViewById(R.id.tableForHeart);
        mLineCharforHeart.setXvelue(5,0,1560);
        mLineCharforHeart.setGraphStyle(1);
        mLineCharforHeart.setLineStyle(1);
        mLineCharforHeart.setYaxisValues(0,180,5);
        mLineCharforHeart.setLineMode(LINE_EVERYPOINT);
        mLineCharforHeart.setLinePointRadio((int)mLineCharforHeart.getLineWidth());
        mLineCharforHeart.setNormalColor(Color.parseColor("#d1b793"));
        if (!mLineCharforHeart.isDetachFlag())
            mLineCharforHeart.feedData(lines2);

        mLineCharforBreath = getView().findViewById(R.id.tableForBreath);
        mLineCharforBreath.setXvelue(5,0,1560);
        mLineCharforBreath.setGraphStyle(1);
        mLineCharforBreath.setLineStyle(1);
        mLineCharforBreath.setYaxisValues(0,40,5);
        mLineCharforBreath.setLineMode(LINE_EVERYPOINT);
        mLineCharforBreath.setLinePointRadio((int)mLineCharforBreath.getLineWidth());
        mLineCharforBreath.setNormalColor(Color.parseColor("#21a0bf"));
        if (!mLineCharforBreath.isDetachFlag())
            mLineCharforBreath.feedData(lines3);

        mLineCharforThird = getView().findViewById(R.id.anotherView);
        mLineCharforThird.setXvelue(5,0,1560);
        mLineCharforThird.setGraphStyle(1);
        mLineCharforThird.setLineStyle(1);
        mLineCharforThird.setYaxisValues(0,10,5);
        mLineCharforThird.setLineMode(LINE_EVERYPOINT);
        mLineCharforThird.setLinePointRadio((int)mLineCharforThird.getLineWidth());
        mLineCharforThird.setNormalColor(Color.parseColor("#d1b793"));
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
     * */
    private void updataDate(){
        if (app.getReportDate() == app.getTodayTime()){
            reportTimeTv.setText(mActivity.getString(R.string.today));
        }else if (app.getTodayTime()-app.getReportDate() == 1){
            reportTimeTv.setText(mActivity.getString(R.string.yesterday));
        }else {
            reportTimeTv.setText(DateUtil.getDateToString(app.getReportDate()));
        }
    }

    /**
     * 获取数据
     * */
    void getData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                getDrawData();
                handler.sendEmptyMessage(100);
            }
        }).start();
    }

    /**
     * 获取绘图数据
     */
    private void getDrawData() {
        lines1.clear();
        lines2.clear();
        lines3.clear();
        lines4.clear();

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<SleepStateBean>>(){}.getType();
        Type type1 = new TypeToken<ArrayList<HealthDataBean>>(){}.getType();


        if (LoadDataUtil.newInstance().loadSleepStateListInDay(app.getReportDate()).size()>1)//如果当天报告超过2个，显示多次报告的字样
        {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    moreTv.setVisibility(View.VISIBLE);
                }
            });
        }else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    moreTv.setVisibility(View.GONE);
                }
            });
        }

        SleepData sleepData = LoadDataUtil.newInstance().loadSleepStateListInDayLast(app.getReportDate());
        HeartData heartData = LoadDataUtil.newInstance().loadHeartDataListInDayLast(app.getReportDate());
        BreathData breathData = LoadDataUtil.newInstance().loadBreathDataListInDayLast(app.getReportDate());
        TurnOverData turnOverData = LoadDataUtil.newInstance().loadTurnOverDataListInDayLast(app.getReportDate());

        String outputarray;

        /**
         * 拿日睡眠数据
         * */
        if (sleepData!=null){
            outputarray = sleepData.getDataForSleep();
            ArrayList<SleepStateBean> sleepStateBeanArrayList = gson.fromJson(outputarray, type);
            if (sleepStateBeanArrayList.size()!=0){
                int [] value = MathUitl.getSleepStateValue(sleepData.getTime(),sleepStateBeanArrayList);
                //取各项数据的数值
                allSleepTime = value[0];
                deepSleepTime = value[1];
                middleSleepTime = value[2];
                lightSleepTime = value[3];
                awakeSleepTime = value[4];

                //设置4个图像X轴刻度
                mLineChar.setXvelue(5,value[5],value[6]);
                mLineCharforHeart.setXvelue(5,value[5],value[6]);
                mLineCharforBreath.setXvelue(5,value[5],value[6]);
                mLineCharforThird.setXvelue(5,value[5],value[6]);;

                //设置进度条的max值
                deepSleepTimePb.setMax(allSleepTime);
                midSleepTimePb.setMax(allSleepTime);
                lightSleepTimePb.setMax(allSleepTime);
                dreamTimePb.setMax(allSleepTime);

                ArrayList<SleepStateBean> sleepDataForDraw = MathUitl.makeDrawDataWithSleep(sleepStateBeanArrayList);
                for (int i = 0;i<sleepDataForDraw.size();i++)
                    lines1.add(new Jchart((float) (sleepDataForDraw.get(i).getState()),"",MathUitl.getRadioWithSleep(sleepDataForDraw,
                            i,allSleepTime)));
            }else {
                //取各项数据的数值
                allSleepTime = 0;
                deepSleepTime = 0;
                middleSleepTime = 0;
                lightSleepTime = 0;
                awakeSleepTime = 0;

                //设置4个图像X轴刻度
                mLineChar.setXvelue(5,0,1440);
                mLineCharforHeart.setXvelue(5,0,1440);
                mLineCharforBreath.setXvelue(5,0,1440);

                //设置进度条的max值
                deepSleepTimePb.setMax(0);
                midSleepTimePb.setMax(0);
                lightSleepTimePb.setMax(0);
                dreamTimePb.setMax(0);
                lines1.add(new Jchart((float) 0,"",(float) 0));
            }
        }else {
            //取各项数据的数值
            allSleepTime = 0;
            deepSleepTime = 0;
            middleSleepTime = 0;
            lightSleepTime = 0;
            awakeSleepTime = 0;

            //设置4个图像X轴刻度
            mLineChar.setXvelue(5,0,1440);
            mLineCharforHeart.setXvelue(5,0,1440);
            mLineCharforBreath.setXvelue(5,0,1440);

            //设置进度条的max值
            deepSleepTimePb.setMax(0);
            midSleepTimePb.setMax(0);
            lightSleepTimePb.setMax(0);
            dreamTimePb.setMax(0);
            lines1.add(new Jchart((float) 0,"",(float) 0));
        }

        /**
         * 拿心率/呼吸率数据
         * */
        if (heartData!=null&&breathData!=null){
            outputarray = heartData.getDataForHeart();
            ArrayList<HealthDataBean> heartDatas = gson.fromJson(outputarray, type1);
            outputarray = breathData.getDataForBreath();
            ArrayList<HealthDataBean> breathDatas = gson.fromJson(outputarray, type1);

            if (heartDatas.size()!=0 && breathDatas.size()!=0){
                for (int i = 0;i<heartDatas.size();i++){
                    lines2.add(new Jchart(heartDatas.get(i).getValue()&0xff, "",1));
                    lines3.add(new Jchart(breathDatas.get(i).getValue(), "", 1));
                }
                averageHeartData = MathUitl.getAverageDataOfList(heartDatas,false);
                averageBreathData =  MathUitl.getAverageDataOfList(breathDatas,false);
            }else {
                averageHeartData = 0;
                averageBreathData =  0;
                lines2.add(new Jchart(0, "",1));
                lines3.add(new Jchart(0, "",1));
            }
        }else {
            averageHeartData = 0;
            averageBreathData =  0;
            lines2.add(new Jchart(0, "",1));
            lines3.add(new Jchart(0, "",1));
        }

        /**
         * 拿起床次数数据
         * */
        if (turnOverData!=null){
            outputarray = turnOverData.getDataForturnOver();
            ArrayList<HealthDataBean> turnOverDatas = gson.fromJson(outputarray, type1);
            if (turnOverDatas.size()!=0){
                for (int i = 0;i<turnOverDatas.size();i++){
                    lines4.add(new Jchart(turnOverDatas.get(i).getValue()&0x0f, "",1));
                }
                averageTurnOver = MathUitl.getAverageDataOfList(turnOverDatas,true);
            }else {
                averageTurnOver = 0;
                lines4.add(new Jchart(0, "",1));
            }
        }else {
            averageTurnOver = 0;
            lines4.add(new Jchart(0, "",1));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMsgFromDatabase(UpdataReportBean connectBean){
        upDataView();
    }


}
