package com.szip.sleepee.Bean;

/**
 * Created by Administrator on 2019/3/1.
 */

public class SleepStateBean {
    private int stateTime;
    private int state;

    public int getStateTime() {
        return stateTime;
    }

    public int getState() {
        return state;
    }

    public void setStateTime(int stateTime) {
        this.stateTime = stateTime;
    }

    public void setState(int state) {
        this.state = state;
    }

    public SleepStateBean(int stateTime, int state) {
        this.stateTime = stateTime;
        this.state = state;
    }
}
