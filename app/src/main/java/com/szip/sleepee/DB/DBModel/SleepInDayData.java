package com.szip.sleepee.DB.DBModel;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.szip.sleepee.DB.AppDatabase;

/**
 * Created by Administrator on 2019/3/5.
 */
@Table(database = AppDatabase.class)
public class SleepInDayData extends BaseModel implements Comparable<SleepInDayData>{
    @PrimaryKey(autoincrement = true)
    public long id;

    @Column
    public int time;

    @Column
    public short deepSleepInDay;
    @Column
    public short middleSleepInDay;
    @Column
    public short lightSleepInDay;
    @Column
    public short wakeSleepInDay;

    public short getAllTime(){
        return (short) (deepSleepInDay+middleSleepInDay+lightSleepInDay+wakeSleepInDay);
    }

    public SleepInDayData(int time, short deepSleepInDay, short middleSleepInDay, short lightSleepInDay, short wakeSleepInDay) {
        this.time = time;
        this.deepSleepInDay = deepSleepInDay;
        this.middleSleepInDay = middleSleepInDay;
        this.lightSleepInDay = lightSleepInDay;
        this.wakeSleepInDay = wakeSleepInDay;
    }

    public SleepInDayData() {}

    @Override
    public int compareTo(@NonNull SleepInDayData o) {
        return this.time-o.time;
    }
}
