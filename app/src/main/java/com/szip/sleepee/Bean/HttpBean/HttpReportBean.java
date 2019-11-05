package com.szip.sleepee.Bean.HttpBean;

import com.szip.sleepee.Bean.HealthDataBean;
import com.szip.sleepee.Bean.SleepStateBean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2019/7/22.
 */

public class HttpReportBean {
    private long time;
    private ArrayList<SleepStateBean> dataForSleep;
    private ArrayList<HealthDataBean> dataForBreath,dataForHeart,dataForTurnOver;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public ArrayList<SleepStateBean> getDataForSleep() {
        return dataForSleep;
    }

    public void setDataForSleep(ArrayList<SleepStateBean> dataForSleep) {
        this.dataForSleep = dataForSleep;
    }

    public ArrayList<HealthDataBean> getDataForBreath() {
        return dataForBreath;
    }

    public void setDataForBreath(ArrayList<HealthDataBean> dataForBreath) {
        this.dataForBreath = dataForBreath;
    }

    public ArrayList<HealthDataBean> getDataForHeart() {
        return dataForHeart;
    }

    public void setDataForHeart(ArrayList<HealthDataBean> dataForHeart) {
        this.dataForHeart = dataForHeart;
    }

    public ArrayList<HealthDataBean> getDataForTurnOver() {
        return dataForTurnOver;
    }

    public void setDataForTurnOver(ArrayList<HealthDataBean> dataForTurnOver) {
        this.dataForTurnOver = dataForTurnOver;
    }
}
