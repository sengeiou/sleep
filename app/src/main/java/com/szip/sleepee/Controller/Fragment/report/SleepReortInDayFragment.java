package com.szip.sleepee.Controller.Fragment.report;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jonas.jgraph.graph.JcoolGraph;
import com.jonas.jgraph.models.Jchart;
import com.szip.sleepee.Bean.HealthDataBean;
import com.szip.sleepee.Bean.SleepStateBean;
import com.szip.sleepee.Controller.Fragment.BaseFragment;
import com.szip.sleepee.Controller.MainActivity;
import com.szip.sleepee.Controller.SleepReportInDayActivity;
import com.szip.sleepee.DB.DBModel.BreathData;
import com.szip.sleepee.DB.DBModel.HeartData;
import com.szip.sleepee.DB.DBModel.SleepData;
import com.szip.sleepee.DB.DBModel.TurnOverData;
import com.szip.sleepee.DB.LoadDataUtil;
import com.szip.sleepee.Interface.MyTouchListener;
import com.szip.sleepee.MyApplication;
import com.szip.sleepee.R;
import com.szip.sleepee.Util.DateUtil;
import com.szip.sleepee.Util.MathUitl;
import com.szip.sleepee.View.MyScrollView;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.jonas.jgraph.graph.JcoolGraph.LINE_DASH_0;
import static com.jonas.jgraph.graph.JcoolGraph.LINE_EVERYPOINT;

/**
 * Created by Administrator on 2019/3/11.
 */

public class SleepReortInDayFragment extends BaseFragment {
    private int [] value;

    private MyScrollView myScrollView;
    private GestureDetector gestureDetector;
    private boolean scrollAble = false;

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

    private int reportTime;
    private SleepData sleepData;
    private HeartData heartData;
    private BreathData breathData;
    private TurnOverData turnOverData;

    private SleepReportInDayActivity sleepReportInDayActivity;

    /**
     * 返回一个fragment实例，Activity中调用
     * */
    public static SleepReortInDayFragment newInstance(int date, String sleep, String heart, String breath, String turnOver){
        Bundle bundle = new Bundle();
        bundle.putInt("date",date);
        bundle.putString("sleep",sleep);
        bundle.putString("heart",heart);
        bundle.putString("breath",breath);
        bundle.putString("turnOver",turnOver);
        SleepReortInDayFragment fragment = new SleepReortInDayFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setSleepReportInDayActivity(SleepReportInDayActivity sleepReportInDayActivity) {
        this.sleepReportInDayActivity = sleepReportInDayActivity;
        if (sleepReportInDayActivity!=null)//图标在滑动的时候禁止滑动换页
            sleepReportInDayActivity.setViewPagerScroll(true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_report_data;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {
        gestureDetector = new GestureDetector(getActivity(),gestureListener);
        reportTime = getArguments().getInt("date");
        initData(getArguments().getString("sleep"),getArguments().getString("heart"),
                getArguments().getString("breath"),getArguments().getString("turnOver"));
        getDrawData();
        initView();
        updataDate();
        updataView();
    }



    /**
     * 初始化数据
     * */
    private void initData(String sleep,String heart,String breath,String turnOver) {
        Gson gson = new Gson();
        Type type = new TypeToken<SleepData>(){}.getType();
        Type type1 = new TypeToken<HeartData>(){}.getType();
        Type type2 = new TypeToken<BreathData>(){}.getType();
        Type type3 = new TypeToken<TurnOverData>(){}.getType();

        sleepData = gson.fromJson(sleep, type);
        heartData = gson.fromJson(heart, type1);
        breathData = gson.fromJson(breath, type2);
        turnOverData = gson.fromJson(turnOver,type3);
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

        String outputarray;
        /**
         * 拿睡眠数据
         * */
        if(sleepData!=null){
            outputarray = sleepData.getDataForSleep();
            ArrayList<SleepStateBean> sleepStateBeanArrayList = gson.fromJson(outputarray, type);
            if (sleepStateBeanArrayList.size()!=0){
                value = MathUitl.getSleepStateValue(sleepData.getTime(),sleepStateBeanArrayList);
                //取各项数据的数值
                allSleepTime = value[0];
                deepSleepTime = value[1];
                middleSleepTime = value[2];
                lightSleepTime = value[3];
                awakeSleepTime = value[4];

                //优化曲线
                ArrayList<SleepStateBean> sleepDataForDraw = MathUitl.makeDrawDataWithSleep(sleepStateBeanArrayList);
                for (int i = 0;i<sleepDataForDraw.size();i++)
                    lines1.add(new Jchart((float) (sleepDataForDraw.get(i).getState()),"",MathUitl.getRadioWithSleep(sleepDataForDraw,
                            i,allSleepTime)));
            }
        }else {
            lines1.add(new Jchart((float) 0,"",(float) 0));
        }


        /**
         * 拿心率/呼吸率数据
         * */
        if (heartData!=null && breathData!=null){
            outputarray = heartData.getDataForHeart();
            ArrayList<HealthDataBean> heartDatas = gson.fromJson(outputarray, type1);
            outputarray = breathData.getDataForBreath();
            ArrayList<HealthDataBean> breathDatas = gson.fromJson(outputarray, type1);
            if (heartDatas.size()!=0&&breathDatas.size()!=0){
                for (int i = 0;i<heartDatas.size();i++){
                    lines2.add(new Jchart(heartDatas.get(i).getValue()&0xff, "",1));
                    lines3.add(new Jchart(breathDatas.get(i).getValue(), "", 1));
                }
                averageHeartData = MathUitl.getAverageDataOfList(heartDatas,false);
                averageBreathData =  MathUitl.getAverageDataOfList(breathDatas,false);
            }else {
                lines2.add(new Jchart(0, "",1));
                lines3.add(new Jchart(0, "",1));
            }
        }else {
            lines2.add(new Jchart(0, "",1));
            lines3.add(new Jchart(0, "",1));
        }


        /**
         * 拿起床次数数据
         * */
        if (turnOverData!=null){
            outputarray = turnOverData.getDataForturnOver();
            ArrayList<HealthDataBean> turnOverData = gson.fromJson(outputarray, type1);
            if (turnOverData.size()!=0){
                for (int i = 0;i<turnOverData.size();i++){
                    lines4.add(new Jchart(turnOverData.get(i).getValue()&0x0f, "",1));
                }
                averageTurnOver = MathUitl.getAverageDataOfList(turnOverData,true);
            }else
                lines4.add(new Jchart(0, "",1));
        }else
            lines4.add(new Jchart(0, "",1));
    }

    /**
     * 有数据后，更新视图
     * */
    private void updataView() {

        if(value!=null){
            //设置4个图像X轴刻度
            mLineChar.setXvelue(5,value[5],value[6]);
            mLineCharforHeart.setXvelue(5,value[5],value[6]);
            mLineCharforBreath.setXvelue(5,value[5],value[6]);
            mLineCharforThird.setXvelue(5,value[5],value[6]);

            //设置进度条的max值
            deepSleepTimePb.setMax(allSleepTime);
            midSleepTimePb.setMax(allSleepTime);
            lightSleepTimePb.setMax(allSleepTime);
            dreamTimePb.setMax(allSleepTime);
            averageHeartPb.setMax(180);
            averageBreathPb.setMax(40);

            averageRealSleepTimeTv.setText(DateUtil.initText(String.format("%02dh%02dmin",allSleepTime/60,allSleepTime%60), true));
            averageDeepSleepTimeTv.setText(DateUtil.initText(String.format("%02dh%02dmin",deepSleepTime/60,deepSleepTime%60), true));
            averageMidSleepTimeTv.setText(DateUtil.initText(String.format("%02dh%02dmin",middleSleepTime/60,middleSleepTime%60), true));
            averageLightSleepTimeTv.setText(DateUtil.initText(String.format("%02dh%02dmin",lightSleepTime/60,lightSleepTime%60), true));
            averageDreamTimeTv.setText(DateUtil.initText(String.format("%02dh%02dmin",awakeSleepTime/60,awakeSleepTime%60), true));
            averageHeartTv.setText(DateUtil.initText(String.format("%02d", averageHeartData)+getString(R.string.heartUnit), false));
            averageBreathTv.setText(DateUtil.initText(String.format("%02d",averageBreathData) +getString(R.string.heartUnit), false));
            heartForTableTv.setText(String.format("%02d", averageHeartData)+getString(R.string.heartUnit));
            breathForTableTv.setText(String.format("%02d",averageBreathData) +getString(R.string.heartUnit));
            averageHeartPb.setProgress(averageHeartData);
            averageBreathPb.setProgress(averageBreathData);
            deepSleepTimePb.setProgress(deepSleepTime);
            midSleepTimePb.setProgress(middleSleepTime);
            lightSleepTimePb.setProgress(lightSleepTime);
            dreamTimePb.setProgress(awakeSleepTime);
            awakaTimesForTableTv.setText(String.format("%02d",averageTurnOver) +getString(R.string.turnOver));
        }
    }

    /**
     * 初始化视图
     */
    @SuppressLint("WrongConstant")
    private void initView() {

        myScrollView = getView().findViewById(R.id.myScollView);

        myScrollView.setMyScrollIsEnd(myScrollIsEnd);
        reportTimeTv = getView().findViewById(R.id.reportTimeTv);

        menuOneTv = getView().findViewById(R.id.menuOneTv);
        menuOneTv.setText(getString(R.string.averageDeep));
        menuTwoTv = getView().findViewById(R.id.menuTwoTv);
        menuTwoTv.setText(getString(R.string.averageMid));
        menuThreeTv = getView().findViewById(R.id.menuThreeTv);
        menuThreeTv.setText(getString(R.string.averageLight));
        menuFourTv = getView().findViewById(R.id.menuFourTv);
        menuFourTv.setText(getString(R.string.averageAwake));

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
        mLineChar.setOnTouchListener(onTouchListener);
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
        mLineCharforHeart.setOnTouchListener(onTouchListener);
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
        mLineCharforBreath.setOnTouchListener(onTouchListener);
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
        mLineCharforThird.setOnTouchListener(onTouchListener);
        if (!mLineCharforThird.isDetachFlag())
            mLineCharforThird.feedData(lines4);
    }

    /**
     * 更新时间
     * */
    private void updataDate(){
        reportTimeTv.setText(DateUtil.getDateToString(reportTime));
    }

    private GestureDetector.OnGestureListener gestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            if(scrollAble){
                mLineCharforBreath.onMyDown(e);
                mLineCharforHeart.onMyDown(e);
                mLineCharforThird.onMyDown(e);
                return mLineChar.onMyDown(e);
            }else
                return false;

        }

        @Override
        public void onShowPress(MotionEvent e) {
            if (scrollAble){
                mLineCharforBreath.onMyShowPress(e);
                mLineCharforHeart.onMyShowPress(e);
                mLineCharforThird.onMyShowPress(e);
                mLineChar.onMyShowPress(e);
            }
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {

            if (scrollAble){
                mLineCharforBreath.onMySingleTapUp(e);
                mLineCharforHeart.onMySingleTapUp(e);
                mLineCharforThird.onMySingleTapUp(e);
                return mLineChar.onMySingleTapUp(e);
            }else
                return false;

        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            myScrollView.setScroll(false);
            if (sleepReportInDayActivity!=null)//图标在滑动的时候禁止滑动换页
                sleepReportInDayActivity.setViewPagerScroll(false);
            if (scrollAble){
                mLineCharforBreath.onMyScroll(e1,e2,distanceX,distanceY);
                mLineCharforHeart.onMyScroll(e1,e2,distanceX,distanceY);
                mLineCharforThird.onMyScroll(e1,e2,distanceX,distanceY);
                return mLineChar.onMyScroll(e1,e2,distanceX,distanceY);
            }else
                return false;

        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (scrollAble){
                mLineCharforBreath.onMyLongPress(e);
                mLineCharforHeart.onMyLongPress(e);
                mLineCharforThird.onMyLongPress(e);
                mLineChar.onMyLongPress(e);
            }
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (sleepReportInDayActivity!=null)//没在滑动的时候可以滑动换页
                sleepReportInDayActivity.setViewPagerScroll(true);
            if (scrollAble){
                mLineCharforBreath.onMyFling(e1,e2,velocityX,velocityY);
                mLineCharforHeart.onMyFling(e1,e2,velocityX,velocityY);
                mLineCharforThird.onMyFling(e1,e2,velocityX,velocityY);
                return mLineChar.onMyFling(e1,e2,velocityX,velocityY);
            }else
                return false;
        }

    };


    boolean state;
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        float baseValue;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                baseValue = 0;
                if (sleepReportInDayActivity!=null)//缩放的时候禁止滑动换页
                    sleepReportInDayActivity.setViewPagerScroll(false);
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (event.getPointerCount() == 2) {
                    scrollAble = false;//缩放的时候图表禁止滑动
                    if (sleepReportInDayActivity!=null)//缩放的时候禁止滑动换页
                        sleepReportInDayActivity.setViewPagerScroll(false);
                    myScrollView.setScroll(false);
                    float x = event.getX(0) - event.getX(1);
                    float y = event.getY(0) - event.getY(1);
                    float value = (float) Math.sqrt(x * x + y * y);// 计算两点的距离
                    if (baseValue == 0) {
                        baseValue = value;
                    } else {
                        if (value - baseValue >= 10 || value - baseValue <= -10) {
                            state = true;
                            float scale = value / baseValue;// 当前两点间的距离除以手指落下时两点间的距离就是需要缩放的比例。
                            mLineChar.addStretchValue(scale);
                            mLineCharforBreath.addStretchValue(scale);
                            mLineCharforHeart.addStretchValue(scale);
                            mLineCharforThird.addStretchValue(scale);
                        }
                    }
                }
            }else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (sleepReportInDayActivity!=null)//没在滑动的时候可以滑动换页
                    sleepReportInDayActivity.setViewPagerScroll(true);
                myScrollView.setScroll(true);
                if (state){
                    scrollAble = true;
                    state = false;
                    mLineChar.setStretchTimes(true);
                    mLineCharforBreath.setStretchTimes(true);
                    mLineCharforHeart.setStretchTimes(true);
                    mLineCharforThird.setStretchTimes(true);
                }
            }
            return gestureDetector.onTouchEvent(event);
        }


    };

    private MyScrollIsEnd myScrollIsEnd = new MyScrollIsEnd() {
        @Override
        public void onEnd() {
            if (sleepReportInDayActivity!=null)//上下滑动结束，使能左右滑动
                sleepReportInDayActivity.setViewPagerScroll(true);
        }
    };

    /**
     * 监听上下滑动结束的接口
     * */
    public interface MyScrollIsEnd{
        void onEnd();
    }

}
