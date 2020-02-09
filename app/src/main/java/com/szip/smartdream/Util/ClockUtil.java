package com.szip.smartdream.Util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.szip.smartdream.Bean.HttpBean.ClockData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2019/2/28.
 */

public class ClockUtil implements Runnable{

    private static ClockUtil mInstance;
    /**
     * 闹钟倒计时时间，为0时闹钟响起
     * */
    private int delayTime = 0;
    /**
     * 闹钟列表
     * */
    private ArrayList<ClockData> clockDataList;
    /**
     * 计时线程
     * */
    private Thread thread;
    /**
     * 判断是否列表中有使能的闹钟，如果无（闹钟都在睡眠带或者都已经关闭）则不需要往下递归
     * */
    private boolean enableClock = false;
    /**
     * 闹钟日期增加的天数
     * */
    private int day = 0;

    /**
     * 闹钟编号
     * */
    private int pos = 0;

    private Context context;


    public static ClockUtil getInstance(Context context)
    {
        if (mInstance == null)
        {
            synchronized (ClockUtil.class)
            {
                if (mInstance == null)
                {
                    mInstance = new ClockUtil(context);
                }
            }
        }
        return mInstance;
    }

    private ClockUtil(Context context){
        this.context = context;
        clockDataList = new ArrayList<>();
    }

    public void setClockDataList(ArrayList<ClockData> list){
        day = 0;
        enableClock = false;
        delayTime = 0;
        clockDataList = list;
        initDelayTime();
    }

    /**
     * 获取倒计时时间
     * */
    private void initDelayTime(){


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        int nowTime = (int)(calendar.getTimeInMillis()/1000);
        calendar.add(Calendar.DAY_OF_YEAR,day);

        ClockData clockData = null;
        for (int i = 0;i<clockDataList.size();i++){//遍历闹钟列表，获取倒计时时间
            clockData = clockDataList.get(i);
            calendar.set(Calendar.HOUR_OF_DAY,clockData.getHour());
            calendar.set(Calendar.MINUTE,clockData.getMinute());
            int endTime = (int)(calendar.getTimeInMillis()/1000);
            if (clockData.getIsOn()==1&&clockData.getIsPhone()==1){
                enableClock = true;
                if (endTime>nowTime){
                    pos = i;
                    delayTime = endTime-nowTime;
                    Log.d("CLOCK******","next clock is ="+delayTime);
                    break;
                }
            }
        }
        if (delayTime!=0){//如果闹钟的时间减去当前的时间>0证明该闹钟无执行过，开始倒计时线程
            if (thread==null){
                thread = new Thread(this);
                thread.start();
            }
        }else {//如果遍历过闹钟列表后发现闹钟都执行过，则执行第二天的闹钟递归回去继续拿dalayTime
            if (enableClock){//如果列表中没有使能的闹钟，则不需要递归
                day++;
                initDelayTime();
            }
        }
    }

    /**
     * 开始计时线程
     * */
    @Override
    public void run() {
        Log.d("CLOCK******","开始倒计时"+delayTime);
        while (delayTime!=0){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            delayTime--;
        }
        Intent intent = new Intent("clockIsComing");
        intent.putExtra("pos",pos);
        context.sendBroadcast(intent);
        Log.d("CLOCK******","闹钟到点,下一个");
        thread = null;
        initDelayTime();//计时结束，开始计算下一次闹钟的时间

    }
}
