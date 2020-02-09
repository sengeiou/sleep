package com.szip.smartdream.DB.DBModel;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.szip.smartdream.DB.AppDatabase;

/**
 * Created by Administrator on 2019/3/5.
 */
@Table(database = AppDatabase.class)
public class TurnOverInDayData extends BaseModel implements Comparable<TurnOverInDayData>{
    @PrimaryKey(autoincrement = true)
    public long id;

    @Column
    public int time;

    @Column
    public short turnOverInDay;

    public TurnOverInDayData(int time, short turnOverInDay) {
        this.time = time;
        this.turnOverInDay = turnOverInDay;
    }

    public TurnOverInDayData() {}

    @Override
    public int compareTo(@NonNull TurnOverInDayData o) {
        return this.time-o.time;
    }
}
