package com.szip.sleepee.DB;

import android.util.Log;

import com.google.gson.Gson;
import java.lang.reflect.Type;

import com.google.gson.reflect.TypeToken;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.szip.sleepee.Bean.HealthDataBean;
import com.szip.sleepee.Bean.SleepStateBean;
import com.szip.sleepee.DB.DBModel.BreathData;
import com.szip.sleepee.DB.DBModel.BreathData_Table;
import com.szip.sleepee.DB.DBModel.BreathInDayData;
import com.szip.sleepee.DB.DBModel.BreathInDayData_Table;
import com.szip.sleepee.DB.DBModel.HeartData;
import com.szip.sleepee.DB.DBModel.HeartData_Table;
import com.szip.sleepee.DB.DBModel.HeartInDayData;
import com.szip.sleepee.DB.DBModel.HeartInDayData_Table;
import com.szip.sleepee.DB.DBModel.SleepData;
import com.szip.sleepee.DB.DBModel.SleepData_Table;
import com.szip.sleepee.DB.DBModel.SleepInDayData;
import com.szip.sleepee.DB.DBModel.SleepInDayData_Table;
import com.szip.sleepee.DB.DBModel.TurnOverData;
import com.szip.sleepee.DB.DBModel.TurnOverData_Table;
import com.szip.sleepee.DB.DBModel.TurnOverInDayData;
import com.szip.sleepee.DB.DBModel.TurnOverInDayData_Table;
import com.szip.sleepee.Util.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;

/**
 * Created by Administrator on 2019/3/1.
 */

public class LoadDataUtil {
    private static LoadDataUtil loadDataUtil;

    private LoadDataUtil(){
    }

    public static LoadDataUtil newInstance(){                     // 单例模式，双重锁
        if( loadDataUtil == null ){
            synchronized (SaveDataUtil.class){
                if( loadDataUtil == null ){
                    loadDataUtil = new LoadDataUtil();
                }
            }
        }
        return loadDataUtil ;
    }

    /**
     * 取一天的睡眠数据
     * */
    public List<SleepData> loadSleepStateListInDay(int day){
        List<SleepData> list;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String dateStr = DateUtil.getDateToString(day);
        long startTime,endTime;
        try {
            date = dateFormat.parse(dateStr+" 05:00:00");
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        startTime = date.getTime()/1000;
        endTime = startTime+24*60*60-1;

        list = SQLite.select()
                .from(SleepData.class)
                .where(SleepData_Table.time.lessThanOrEq(endTime),SleepData_Table.time.greaterThanOrEq(startTime))
                .queryList();
        Collections.sort(list);
        return list;
    }

    /**
     * 取一天的最后一次睡眠数据
     * */
    public SleepData loadSleepStateListInDayLast(int day){
        SleepData sleepData;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String dateStr = DateUtil.getDateToString(day);
        String dateStr1 = DateUtil.getDateToString(day+1);
        long startTime,endTime;
        try {
            date = dateFormat.parse(dateStr+" 05:00:00");
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        startTime = date.getTime()/1000;
        endTime = startTime+24*60*60-1;

        sleepData = SQLite.select()
                .from(SleepData.class)
                .where(SleepData_Table.time.lessThanOrEq(endTime),SleepData_Table.time.greaterThanOrEq(startTime))
                .orderBy(OrderBy.fromString(SleepData_Table.time+OrderBy.DESCENDING))
                .limit(0)
                .querySingle();

        /**
         * 拿测试数据
         * */
//        sleepData = SQLite.select()
//                .from(SleepData.class)
//                .orderBy(OrderBy.fromString(SleepData_Table.time+OrderBy.DESCENDING))
//                .limit(0)
//                .querySingle();

        return sleepData;
    }

    /**
     * 取一周的日均睡眠数据
     * */
    public List<SleepInDayData> loadSleepStateListInWeek(int monday){
        long time[];
        //在数据库拿一周的睡眠质量数据
        List<SleepInDayData> sleepInDayDataArrayList = SQLite.select()
                .from(SleepInDayData.class)
                .where(SleepInDayData_Table.time.lessThanOrEq(monday+6),SleepInDayData_Table.time.greaterThanOrEq(monday))
                .queryList();
        if (sleepInDayDataArrayList.size() == 7)//如果拿到七天数据，说明已经有缓存，直接返回数据用于绘图
        {
            Log.d("SZIP******","get 7 days");
            return sleepInDayDataArrayList;
        }else {
            for (int i = monday;i<=monday+6;i++){
                int a;
                boolean flag = false;
                for (a = 0;a<sleepInDayDataArrayList.size();a++){
                    if(i == sleepInDayDataArrayList.get(a).time){
                        flag = true;
                        break;
                    }
                }
                if (!flag)
                    sleepInDayDataArrayList.add(new SleepInDayData(i,(short)0,(short)0,(short)0,(short) 0));

            }
            Collections.sort(sleepInDayDataArrayList);
            return sleepInDayDataArrayList;
        }
    }

    /**
     * 取一个月的日均睡眠数据
     * */
    public List<SleepInDayData> loadSleepStateListInMonth(int dataForFirst,int size){
        long time[];
        //在数据库拿一个月的睡眠质量数据
        List<SleepInDayData> sleepInDayDataArrayList = SQLite.select()
                .from(SleepInDayData.class)
                .where(SleepInDayData_Table.time.lessThan(dataForFirst+size),SleepInDayData_Table.time.greaterThanOrEq(dataForFirst))
                .queryList();
        if (sleepInDayDataArrayList.size() == size)//如果拿到一整个月的数据，说明已经有缓存，直接返回数据用于绘图
            return sleepInDayDataArrayList;
        else {
            for (int i = dataForFirst;i<dataForFirst+size;i++){
                int a;
                boolean flag = false;
                for (a = 0;a<sleepInDayDataArrayList.size();a++){
                    if(i == sleepInDayDataArrayList.get(a).time){
                        flag = true;
                        break;
                    }
                }
                if (!flag){
                    sleepInDayDataArrayList.add(new SleepInDayData(i,(short)0,(short)0,(short)0,(short) 0));
                }
            }
            Collections.sort(sleepInDayDataArrayList);
            return sleepInDayDataArrayList;
        }
    }


    /**
     * 取一天的心率数据
     * */
    public List<HeartData> loadHeartDataListInDay(int day){
        List<HeartData> list;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String dateStr = DateUtil.getDateToString(day);
        String dateStr1 = DateUtil.getDateToString(day+1);
        long startTime,endTime;
        try {
            date = dateFormat.parse(dateStr+" 05:00:00");
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        startTime = date.getTime()/1000;
        endTime = startTime+24*60*60-1;

        list = SQLite.select()
                .from(HeartData.class)
                .where(HeartData_Table.time.lessThanOrEq(endTime),HeartData_Table.time.greaterThanOrEq(startTime))
                .queryList();
        Collections.sort(list);
        return list;
    }

    /**
     * 取一天的最后一次心率数据
     * */
    public HeartData loadHeartDataListInDayLast(int day){
        HeartData heartData;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String dateStr = DateUtil.getDateToString(day);
        long startTime,endTime;
        try {
            date = dateFormat.parse(dateStr+" 05:00:00");
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        startTime = date.getTime()/1000;
        endTime = startTime+24*60*60-1;

        heartData = SQLite.select()
                .from(HeartData.class)
                .where(HeartData_Table.time.lessThanOrEq(endTime),HeartData_Table.time.greaterThanOrEq(startTime))
                .orderBy(OrderBy.fromString(SleepData_Table.time+OrderBy.DESCENDING))
                .limit(0)
                .querySingle();

        return heartData;
    }

    /**
     * 取一周的日均心率数据
     * */
    public List<HeartInDayData> loadHeartListInWeek(int monday){
        long time[];
        //在数据库拿一周的心率数据
        List<HeartInDayData> heartInDayDataList = SQLite.select()
                .from(HeartInDayData.class)
                .where(HeartInDayData_Table.time.lessThanOrEq(monday+6), HeartInDayData_Table.time.greaterThanOrEq(monday))
                .queryList();
        if (heartInDayDataList.size() == 7)//如果拿到七天数据，说明已经有缓存，直接返回数据用于绘图
            return heartInDayDataList;
        else{
            for (int i = monday;i<=monday+6;i++){
                int a;
                boolean flag = false;
                for (a = 0;a<heartInDayDataList.size();a++){
                    if(i == heartInDayDataList.get(a).time){
                        flag = true;
                        break;
                    }
                }
                if (!flag){
                    heartInDayDataList.add(new HeartInDayData(i,(short)0));
                }
            }
            Collections.sort(heartInDayDataList);
            return heartInDayDataList;
        }
    }

    /**
     * 取一个月的日均心率数据
     * */
    public List<HeartInDayData> loadHeartListInMonth(int dataForFirst,int size){
        long time[];
        //在数据库拿一个月的心率数据
        List<HeartInDayData> heartInDayDataList = SQLite.select()
                .from(HeartInDayData.class)
                .where(HeartInDayData_Table.time.lessThan(dataForFirst+size), HeartInDayData_Table.time.greaterThanOrEq(dataForFirst))
                .queryList();
        if (heartInDayDataList.size() == size)//如果拿到一整个月的数据，说明已经有缓存，直接返回数据用于绘图
            return heartInDayDataList;
        else {
            for (int i = dataForFirst;i<dataForFirst+size;i++){
                int a;
                boolean flag = false;
                for (a = 0;a<heartInDayDataList.size();a++){
                    if(i == heartInDayDataList.get(a).time){
                        flag = true;
                        break;
                    }
                }
                if (!flag){
                    heartInDayDataList.add(new HeartInDayData(i,(short)0));
                }
            }
            Collections.sort(heartInDayDataList);
            return heartInDayDataList;
        }
    }


    /**
     * 取一天的呼吸率数据
     * */
    public List<BreathData> loadBreathDataListInDay(int day){
        List<BreathData> list;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String dateStr = DateUtil.getDateToString(day);
        long startTime,endTime;
        try {
            date = dateFormat.parse(dateStr+" 05:00:00");
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        startTime = date.getTime()/1000;
        endTime = startTime+24*60*60-1;

        list = SQLite.select()
                .from(BreathData.class)
                .where(BreathData_Table.time.lessThanOrEq(endTime),BreathData_Table.time.greaterThanOrEq(startTime))
                .queryList();
        Collections.sort(list);
        return list;
    }

    /**
     * 取一天的最后一次呼吸率数据
     * */
    public BreathData loadBreathDataListInDayLast(int day){
        BreathData breathData;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String dateStr = DateUtil.getDateToString(day);
        long startTime,endTime;
        try {
            date = dateFormat.parse(dateStr+" 05:00:00");
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        startTime = date.getTime()/1000;
        endTime = startTime+24*60*60-1;

        breathData = SQLite.select()
                .from(BreathData.class)
                .where(BreathData_Table.time.lessThanOrEq(endTime),BreathData_Table.time.greaterThanOrEq(startTime))
                .orderBy(OrderBy.fromString(SleepData_Table.time+OrderBy.DESCENDING))
                .limit(0)
                .querySingle();

        return breathData;
    }

    /**
     * 取一周的日均呼吸率数据
     * */
    public List<BreathInDayData> loadBreathListInWeek(int monday){
        long time[];
        //在数据库拿一周的呼吸率数据
        List<BreathInDayData> breathInDayDataList = SQLite.select()
                .from(BreathInDayData.class)
                .where(BreathInDayData_Table.time.lessThanOrEq(monday+6), BreathInDayData_Table.time.greaterThanOrEq(monday))
                .queryList();
        if (breathInDayDataList.size() == 7)//如果拿到七天数据，说明已经有缓存，直接返回数据用于绘图
            return breathInDayDataList;
        else {
            for (int i = monday;i<=monday+6;i++){
                int a;
                boolean flag = false;
                for (a = 0;a<breathInDayDataList.size();a++){
                    if(i == breathInDayDataList.get(a).time){
                        flag = true;
                        break;
                    }
                }
                if (!flag){
                    breathInDayDataList.add(new BreathInDayData(i,(short)0));
                }
            }
            Collections.sort(breathInDayDataList);
            return breathInDayDataList;
        }
    }

    /**
     * 取一个月的日均呼吸率数据
     * */
    public List<BreathInDayData> loadBreathListInMonth(int dataForFirst,int size){
        long time[];
        //在数据库拿一个月的呼吸率数据
        List<BreathInDayData> breathInDayDataList = SQLite.select()
                .from(BreathInDayData.class)
                .where(BreathInDayData_Table.time.lessThan(dataForFirst+size), BreathInDayData_Table.time.greaterThanOrEq(dataForFirst))
                .queryList();
        if (breathInDayDataList.size() == size)//如果拿到一个月的数据，说明已经有缓存，直接返回数据用于绘图
            return breathInDayDataList;
        else{
            for (int i = dataForFirst;i<dataForFirst+size;i++){
                int a;
                boolean flag = false;
                for (a = 0;a<breathInDayDataList.size();a++){
                    if(i == breathInDayDataList.get(a).time){
                        flag = true;
                        break;
                    }
                }
                if (!flag){
                    breathInDayDataList.add(new BreathInDayData(i,(short)0));
                }
            }
            Collections.sort(breathInDayDataList);
            return breathInDayDataList;
        }
    }

    /**
     * 取一天的翻身数据
     * */
    public List<TurnOverData> loadTurnOverDataListInDay(int day){
        List<TurnOverData> list;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String dateStr = DateUtil.getDateToString(day);
        long startTime,endTime;
        try {
            date = dateFormat.parse(dateStr+" 05:00:00");
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        startTime = date.getTime()/1000;
        endTime = startTime+24*60*60-1;

        list = SQLite.select()
                .from(TurnOverData.class)
                .where(TurnOverData_Table.time.lessThanOrEq(endTime),TurnOverData_Table.time.greaterThanOrEq(startTime))
                .queryList();
        Collections.sort(list);
        return list;
    }

    /**
     * 取一天的最后一次翻身数据
     * */
    public TurnOverData loadTurnOverDataListInDayLast(int day){
        TurnOverData turnOverData;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String dateStr = DateUtil.getDateToString(day);
        long startTime,endTime;
        try {
            date = dateFormat.parse(dateStr+" 05:00:00");
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        startTime = date.getTime()/1000;
        endTime = startTime+24*60*60-1;

        turnOverData = SQLite.select()
                .from(TurnOverData.class)
                .where(TurnOverData_Table.time.lessThanOrEq(endTime),TurnOverData_Table.time.greaterThanOrEq(startTime))
                .orderBy(OrderBy.fromString(TurnOverData_Table.time+OrderBy.DESCENDING))
                .limit(0)
                .querySingle();

        return turnOverData;
    }

    /**
     * 取一周的日均翻身数据
     * */
    public List<TurnOverInDayData> loadTurnOverListInWeek(int monday){
        long time[];
        //在数据库拿一周的呼吸率数据
        List<TurnOverInDayData> turnOverInDayDataList = SQLite.select()
                .from(TurnOverInDayData.class)
                .where(TurnOverInDayData_Table.time.lessThanOrEq(monday+6), TurnOverInDayData_Table.time.greaterThanOrEq(monday))
                .queryList();
        if (turnOverInDayDataList.size() == 7)//如果拿到七天数据，说明已经有缓存，直接返回数据用于绘图
            return turnOverInDayDataList;
        else {
            for (int i = monday;i<=monday+6;i++){
                int a;
                boolean flag = false;
                for (a = 0;a<turnOverInDayDataList.size();a++){
                    if(i == turnOverInDayDataList.get(a).time){
                        flag = true;
                        break;
                    }
                }
                if (!flag){
                    turnOverInDayDataList.add(new TurnOverInDayData(i,(short)0));
                }
            }
            Collections.sort(turnOverInDayDataList);
            return turnOverInDayDataList;
        }
    }

    /**
     * 取一个月的日均翻身数据
     * */
    public List<TurnOverInDayData> loadTurnOverListInMonth(int dataForFirst,int size){
        long time[];
        //在数据库拿一个月的呼吸率数据
        List<TurnOverInDayData> turnOverInDayDataList = SQLite.select()
                .from(TurnOverInDayData.class)
                .where(TurnOverInDayData_Table.time.lessThan(dataForFirst+size), TurnOverInDayData_Table.time.greaterThanOrEq(dataForFirst))
                .queryList();
        if (turnOverInDayDataList.size() == size)//如果拿到一个月的数据，说明已经有缓存，直接返回数据用于绘图
            return turnOverInDayDataList;
        else{
            for (int i = dataForFirst;i<dataForFirst+size;i++){
                int a;
                boolean flag = false;
                for (a = 0;a<turnOverInDayDataList.size();a++){
                    if(i == turnOverInDayDataList.get(a).time){
                        flag = true;
                        break;
                    }
                }
                if (!flag){
                    turnOverInDayDataList.add(new TurnOverInDayData(i,(short)0));
                }
            }
            Collections.sort(turnOverInDayDataList);
            return turnOverInDayDataList;
        }
    }

    /**
     * 判断当前日期是否有报告
     * */
    public boolean dataCanGet(String dateStr){
        List<SleepData> list;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        long startTime,endTime;
        try {
            date = dateFormat.parse(dateStr+" 05:00:00");
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        startTime = date.getTime()/1000;
        endTime = startTime+24*60*60-1;

        list = SQLite.select()
                .from(SleepData.class)
                .where(SleepData_Table.time.lessThanOrEq(endTime),SleepData_Table.time.greaterThanOrEq(startTime))
                .queryList();
        return list.size()!=0?true:false;
    }
}
