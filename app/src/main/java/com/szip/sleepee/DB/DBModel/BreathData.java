package com.szip.sleepee.DB.DBModel;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.szip.sleepee.DB.AppDatabase;

/**
 * Created by Administrator on 2019/3/1.
 */
@Table(database = AppDatabase.class)
public class BreathData extends BaseModel implements Comparable<BreathData>{

    @PrimaryKey(autoincrement = true)
    public long id;

    @Column
    public long time;

    @Column
    public String dataForBreath;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getDataForBreath() {
        return dataForBreath;
    }

    public void setDataForBreath(String dataForBreath) {
        this.dataForBreath = dataForBreath;
    }

    public BreathData(long time, String dataForBreath) {
        this.time = time;
        this.dataForBreath = dataForBreath;
    }

    public BreathData() {}

    @Override
    public int compareTo(@NonNull BreathData o) {
        return (int)(this.time-o.time);
    }
}
