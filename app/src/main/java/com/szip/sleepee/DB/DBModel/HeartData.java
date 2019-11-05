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
public class HeartData extends BaseModel implements Comparable<HeartData>{
    @PrimaryKey(autoincrement = true)
    public long id;

    @Column
    public long time;

    @Column
    public String dataForHeart;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getDataForHeart() {
        return dataForHeart;
    }

    public void setDataForHeart(String dataForHeart) {
        this.dataForHeart = dataForHeart;
    }

    public HeartData(long time, String dataForHeart) {
        this.time = time;
        this.dataForHeart = dataForHeart;
    }

    public HeartData() {}


    @Override
    public int compareTo(@NonNull HeartData o) {
        return (int)(this.time-o.time);
    }
}
