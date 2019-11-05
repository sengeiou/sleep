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
public class BreathInDayData extends BaseModel implements Comparable<BreathInDayData>{
    @PrimaryKey(autoincrement = true)
    public long id;

    @Column
    public int time;

    @Column
    public short breathInDay;

    public BreathInDayData(int time, short dataForBreath) {
        this.time = time;
        this.breathInDay = dataForBreath;
    }

    public BreathInDayData() {}

    @Override
    public int compareTo(@NonNull BreathInDayData o) {
        return this.time-o.time;
    }
}
