package com.szip.sleepee.Bean.HttpBean;

public class AddClockBean extends BaseApi{
    Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }


    public class Data {
        ClockData alarmClock;

        public ClockData getAlarmClock() {
            return alarmClock;
        }

        public void setAlarmClock(ClockData alarmClock) {
            this.alarmClock = alarmClock;
        }
    }
}
