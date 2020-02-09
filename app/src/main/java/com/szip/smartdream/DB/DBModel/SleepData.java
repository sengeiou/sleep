package com.szip.smartdream.DB.DBModel;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.szip.smartdream.DB.AppDatabase;

/**
 * Created by Administrator on 2019/3/1.
 */

@Table(database = AppDatabase.class)
public class SleepData extends BaseModel implements Comparable<SleepData>{
    @PrimaryKey(autoincrement = true)
    public long id;

    @Column
    public long time;

    @Column
    public String dataForSleep;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getDataForSleep() {
        return dataForSleep;
    }

    public void setDataForSleep(String dataForSleep) {
        this.dataForSleep = dataForSleep;
    }

    public SleepData(long time, String dataForSleep) {
        this.time = time;
        this.dataForSleep = dataForSleep;
    }

    public SleepData() {}

    @Override
    public int compareTo(@NonNull SleepData o) {
        return (int)(this.time-o.time);
    }






}
