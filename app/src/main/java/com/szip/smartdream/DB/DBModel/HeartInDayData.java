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
public class HeartInDayData extends BaseModel implements Comparable<HeartInDayData>{
    @PrimaryKey(autoincrement = true)
    public long id;

    @Column
    public int time;

    @Column
    public short heartInDay;

    public HeartInDayData(int time, short dataForHeart) {
        this.time = time;
        this.heartInDay = dataForHeart;
    }

    public HeartInDayData() {}

    @Override
    public int compareTo(@NonNull HeartInDayData o) {
        return this.time-o.time;
    }
}
