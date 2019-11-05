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
public class TurnOverData extends BaseModel implements Comparable<TurnOverData>{
    @PrimaryKey(autoincrement = true)
    public long id;

    @Column
    public long time;

    @Column
    public String dataForTurnOver;



    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getDataForturnOver() {
        return dataForTurnOver;
    }

    public void setDataForturnOver(String dataForTurnOver) {
        this.dataForTurnOver = dataForTurnOver;
    }

    public TurnOverData(long time, String dataForTurnOver) {
        this.time = time;
        this.dataForTurnOver = dataForTurnOver;
    }

    public TurnOverData() {}




    @Override
    public int compareTo(@NonNull TurnOverData o) {
        return (int)(this.time-o.time);
    }
}
