package com.szip.sleepee.DB;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
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
import com.szip.sleepee.MyApplication;
import com.szip.sleepee.Util.DateUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/3/1.
 */

public class SaveDataUtil {
    private static SaveDataUtil saveDataUtil;
    private Context mContext;
    private SaveDataUtil(Context context){
        mContext = context;
    }

    public static SaveDataUtil newInstance(Context context){                     // 单例模式，双重锁
        if( saveDataUtil == null ){
            synchronized (SaveDataUtil.class){
                if( saveDataUtil == null ){
                    saveDataUtil = new SaveDataUtil(context);
                }
            }
        }
        return saveDataUtil ;
    }

    /**
     * 批量保存睡眠数据
     * */
    public void saveSleepDataListData(List<SleepData> sleepDataList){
        FlowManager.getDatabase(AppDatabase.class)
                .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                        new ProcessModelTransaction.ProcessModel<SleepData>() {
                            @Override
                            public void processModel(SleepData sleepData, DatabaseWrapper wrapper) {
                                if (sleepData.time< DateUtil.getStringToDateMillis()){
                                    SQLite.delete()
                                            .from(SleepData.class)
                                            .where(SleepData_Table.time.is(sleepData.time))
                                            .execute();

                                    sleepData.save();
                                }
                            }
                        }).addAll(sleepDataList).build())  // add elements (can also handle multiple)
                .error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {

                    }
                })
                .success(new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {
                        //Log.d("SZIP******","睡眠数据保存成功");
                        ((MyApplication)mContext.getApplicationContext()).setDataUpdateState(0);
                    }
                }).build().execute();
    }

    /**
     * 批量保存日均睡眠数据
     * */
    public void saveSleepInDayDataListData(List<SleepData> sleepDataList){
        List<SleepInDayData> sleepInDayDataList = new ArrayList<>();
        List<SleepData> dataForDay = new ArrayList<>();
        long time[] = new long[2];
        //遍历睡眠质量，统计每日的日均睡眠数据
        for (int i = 0;i<sleepDataList.size();i++){
            SleepData sleepData = sleepDataList.get(i);
            if (dataForDay.size() == 0){//如果统计链表的个数为0，则获取数据中的一组新的时间戳范围
                time = DateUtil.getTimeScopeForDay(sleepData.getTime());//获取当前数据时间戳所在当天的时间戳范围
            }
            if (sleepData.getTime()>=time[0]&&sleepData.getTime()<=time[1]){//如果遍历的数据在同一天，则加入统计链表
                dataForDay.add(sleepData);
            }else {//如果遍历到的数据不在同一天，则开始统计统计链表中的数据，并清空统计链表重新统计
                sleepInDayDataList.add(getSleepInDayData(dataForDay));
                dataForDay.clear();//统计完了清空链表，为下一次统计做准备
                time = DateUtil.getTimeScopeForDay(sleepData.getTime());//重新获取新一天的时间戳范围
                dataForDay.add(sleepData);
            }

            if(i == sleepDataList.size()-1){//如果是最后一个数据了，直接统计
                sleepInDayDataList.add(getSleepInDayData(dataForDay));
            }
        }


        FlowManager.getDatabase(AppDatabase.class)
                .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                        new ProcessModelTransaction.ProcessModel<SleepInDayData>() {
                            @Override
                            public void processModel(SleepInDayData sleepInDayData, DatabaseWrapper wrapper) {
                                SleepInDayData sleepInDayData1;

                                sleepInDayData1 = SQLite.select()
                                        .from(SleepInDayData.class)
                                        .where(SleepInDayData_Table.time.is(sleepInDayData.time))
                                        .querySingle();
                                if (sleepInDayData1==null)//统计过的数据不存
                                    sleepInDayData.save();
                                else if (DateUtil.isToday(sleepInDayData.time)){//当天的数据覆盖
                                    SQLite.delete()
                                            .from(SleepInDayData.class)
                                            .where(SleepInDayData_Table.time.is(sleepInDayData.time))
                                            .execute();
                                    sleepInDayData.save();
                                }
                            }
                        }).addAll(sleepInDayDataList).build())  // add elements (can also handle multiple)
                .error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {

                    }
                })
                .success(new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {
                        //Log.d("SZIP******","统计睡眠数据保存成功");
                        ((MyApplication)mContext.getApplicationContext()).setDataUpdateState(1);
                    }
                }).build().execute();
    }


    /**
     * 统计日均睡眠数据
     * */
    private SleepInDayData getSleepInDayData(List<SleepData> dataForDay){

        ArrayList<SleepStateBean> sleepStateBeans = new ArrayList<>();//一天所有的睡眠质量数据列表
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<SleepStateBean>>(){}.getType();
        //整合一天所有的睡眠记录
        for (int a = 0;a<dataForDay.size();a++){
            String outputarray = dataForDay.get(a).getDataForSleep();
            ArrayList<SleepStateBean> data = gson.fromJson(outputarray, type);
            if (data!=null)
                sleepStateBeans.addAll(data);
        }
        int allDeep = 0,allMid = 0,allLight = 0,allWake = 0;//深睡、中睡、浅睡、清醒状态的时间
        for (SleepStateBean sleepStateBean:sleepStateBeans){//统计一天所有睡眠的平均值
            if (sleepStateBean.getState()<=90){//深睡
                allDeep+=sleepStateBean.getStateTime();
            }else if (sleepStateBean.getState()<=150){//中睡
                allMid+=sleepStateBean.getStateTime();
            }else if (sleepStateBean.getState()<=234){//浅睡
                allLight+=sleepStateBean.getStateTime();
            }else if (sleepStateBean.getState()<=255){//清醒
                allWake+=sleepStateBean.getStateTime();
            }
        }
        return new SleepInDayData(DateUtil.getDateLongToInt(dataForDay.get(0).getTime()),(short) allDeep
                ,(short)allMid,(short)allLight,(short)allWake);
    }

    /**
     * 批量保存心率
     * */
    public void saveHeartDataListData(List<HeartData> heartDataList){
        //Log.d("SZIP******","heartDataList.size = "+heartDataList.size());
        FlowManager.getDatabase(AppDatabase.class)
                .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                        new ProcessModelTransaction.ProcessModel<HeartData>() {
                            @Override
                            public void processModel(HeartData heartData, DatabaseWrapper wrapper) {
                                if (heartData.time< DateUtil.getStringToDateMillis()){
                                    SQLite.delete()
                                            .from(HeartData.class)
                                            .where(HeartData_Table.time.is(heartData.time))
                                            .execute();

                                    heartData.save();
                                }

                            }
                        }).addAll(heartDataList).build())  // add elements (can also handle multiple)
                .error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {

                    }
                })
                .success(new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {
                        //Log.d("SZIP******","心率数据保存成功");
                        ((MyApplication)mContext.getApplicationContext()).setDataUpdateState(2);
                    }
                }).build().execute();
    }

    /**
     * 批量保存日均心率数据
     * */
    public void saveHeartInDayDataListData(List<HeartData> heartDataList){
        List<HeartInDayData> heartInDayDataList = new ArrayList<>();
        List<HeartData> dataForDay = new ArrayList<>();
        long time[] = new long[2];
        //遍历睡眠质量，统计每日的日均睡眠数据
        for (int i = 0;i<heartDataList.size();i++){
            HeartData heartData = heartDataList.get(i);
            if (dataForDay.size() == 0){//如果统计链表的个数为0，则获取数据中的一组新的时间戳范围
                time = DateUtil.getTimeScopeForDay(heartData.getTime());//获取当前数据时间戳所在当天的时间戳范围
            }
            if (heartData.getTime()>=time[0]&&heartData.getTime()<=time[1]){//如果遍历的数据在同一天，则加入统计链表
                dataForDay.add(heartData);
            }else {//如果遍历到的数据不在同一天，则开始统计统计链表中的数据，并清空统计链表重新统计
                heartInDayDataList.add(getHeartInDayData(dataForDay));
                dataForDay.clear();//统计完了清空链表，为下一次统计做准备
                time = DateUtil.getTimeScopeForDay(heartData.getTime());//重新获取新一天的时间戳范围
                dataForDay.add(heartData);
            }

            if(i == heartDataList.size()-1){//如果是最后一个数据了，直接统计
                heartInDayDataList.add(getHeartInDayData(dataForDay));
            }
        }


        FlowManager.getDatabase(AppDatabase.class)
                .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                        new ProcessModelTransaction.ProcessModel<HeartInDayData>() {
                            @Override
                            public void processModel(HeartInDayData heartInDayData, DatabaseWrapper wrapper) {
                                HeartInDayData heartInDayData1;

                                heartInDayData1 = SQLite.select()
                                        .from(HeartInDayData.class)
                                        .where(HeartInDayData_Table.time.is(heartInDayData.time))
                                        .querySingle();
                                if (heartInDayData1 == null)//统计过的不存
                                    heartInDayData.save();
                                else if (DateUtil.isToday(heartInDayData.time)){//当天数据覆盖
                                    SQLite.delete()
                                            .from(HeartInDayData.class)
                                            .where(HeartInDayData_Table.time.is(heartInDayData.time))
                                            .execute();
                                    heartInDayData.save();
                                }
                            }
                        }).addAll(heartInDayDataList).build())  // add elements (can also handle multiple)
                .error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {

                    }
                })
                .success(new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {
                        //Log.d("SZIP******","心率统计数据保存成功");
                        ((MyApplication)mContext.getApplicationContext()).setDataUpdateState(3);
                    }
                }).build().execute();
    }


    /**
     * 统计日均心率
     * */
    private HeartInDayData getHeartInDayData(List<HeartData> dataForDay){

        ArrayList<HealthDataBean> heartDates = new ArrayList<>();//一天所有的心率数据列表
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<HealthDataBean>>(){}.getType();
        //整合一天所有的心率记录
        for (int a = 0;a<dataForDay.size();a++){
            String outputarray = dataForDay.get(a).getDataForHeart();
            ArrayList<HealthDataBean> data = gson.fromJson(outputarray, type);
            if (data!=null)
                heartDates.addAll(data);
        }
        short averageHeart = 0;//平均心率
        int sum = 0;//心率的个数
        int allHeart = 0;//总心率数据之和
        for (HealthDataBean heartDataBean:heartDates){//统计一天所有心率的平均值
            if(heartDataBean.getValue()!=0){
                sum++;
                allHeart+=heartDataBean.getValue();
            }
        }
        if (sum!=0)
            averageHeart = (short) (allHeart/sum);
        return new HeartInDayData(DateUtil.getDateLongToInt(dataForDay.get(0).getTime()),averageHeart);

    }

    /**
     * 批量保存呼吸率数据
     * */
    public void saveBreathDataListData(List<BreathData> breathDataList){
        FlowManager.getDatabase(AppDatabase.class)
                .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                        new ProcessModelTransaction.ProcessModel<BreathData>() {
                            @Override
                            public void processModel(BreathData breathData, DatabaseWrapper wrapper) {
                                if (breathData.time< DateUtil.getStringToDateMillis()){
                                    SQLite.delete()
                                            .from(BreathData.class)
                                            .where(BreathData_Table.time.is(breathData.time))
                                            .execute();

                                    breathData.save();
                                }

                            }
                        }).addAll(breathDataList).build())  // add elements (can also handle multiple)
                .error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {

                    }
                })
                .success(new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {
                        //Log.d("SZIP******","呼吸率数据保存成功");
                        ((MyApplication)mContext.getApplicationContext()).setDataUpdateState(4);
                    }
                }).build().execute();
    }



    /**
     * 批量保存日均呼吸率数据
     * */
    public void saveBreathInDayDataListData(List<BreathData> breathDataList){
        List<BreathInDayData> breathInDayDataList = new ArrayList<>();
        List<BreathData> dataForDay = new ArrayList<>();
        long time[] = new long[2];
        //遍历呼吸率列表，统计每日的日均呼吸率数据
        for (int i = 0;i<breathDataList.size();i++){
            BreathData breathData = breathDataList.get(i);
            if (dataForDay.size() == 0){//如果统计链表的个数为0，则获取数据中的一组新的时间戳范围
                time = DateUtil.getTimeScopeForDay(breathData.getTime());//获取当前数据时间戳所在当天的时间戳范围
            }
            if (breathData.getTime()>=time[0]&&breathData.getTime()<=time[1]){//如果遍历的数据在同一天，则加入统计链表
                dataForDay.add(breathData);
            }else {//如果遍历到的数据不在同一天，则开始统计统计链表中的数据，并清空统计链表重新统计
                breathInDayDataList.add(getBreathInDayData(dataForDay));
                dataForDay.clear();//统计完了清空链表，为下一次统计做准备
                time = DateUtil.getTimeScopeForDay(breathData.getTime());//重新获取新一天的时间戳范围
                dataForDay.add(breathData);
            }

            if(i == breathDataList.size()-1){//如果是最后一个数据了，直接统计
                breathInDayDataList.add(getBreathInDayData(dataForDay));
            }
        }



        FlowManager.getDatabase(AppDatabase.class)
                .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                        new ProcessModelTransaction.ProcessModel<BreathInDayData>() {
                            @Override
                            public void processModel(BreathInDayData breathInDayData, DatabaseWrapper wrapper) {
                                BreathInDayData breathInDayData1;

                                breathInDayData1 = SQLite.select()
                                        .from(BreathInDayData.class)
                                        .where(BreathInDayData_Table.time.is(breathInDayData.time))
                                        .querySingle();
                                if (breathInDayData1 == null )//统计过的数据不存
                                    breathInDayData.save();
                                else if (DateUtil.isToday(breathInDayData.time)){//当天数据覆盖
                                    SQLite.delete()
                                            .from(BreathInDayData.class)
                                            .where(BreathInDayData_Table.time.is(breathInDayData.time))
                                            .execute();
                                    breathInDayData.save();
                                }
                            }
                        }).addAll(breathInDayDataList).build())  // add elements (can also handle multiple)
                .error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {

                    }
                })
                .success(new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {
                        //Log.d("SZIP******","呼吸率统计数据保存成功");
                        ((MyApplication)mContext.getApplicationContext()).setDataUpdateState(5);
                    }
                }).build().execute();
    }



    /**
     * 统计日均呼吸率
     * */
    private BreathInDayData getBreathInDayData(List<BreathData> dataForDay){

        ArrayList<HealthDataBean> breathDates = new ArrayList<>();//一天所有的呼吸率数据列表
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<HealthDataBean>>(){}.getType();
        //整合一天所有的呼吸率记录
        for (int a = 0;a<dataForDay.size();a++){
            String outputarray = dataForDay.get(a).getDataForBreath();
            ArrayList<HealthDataBean> data = gson.fromJson(outputarray, type);
            if (data!=null)
                breathDates.addAll(data);
        }
        short averageBreath = 0;//平均呼吸率
        int sum = 0;//呼吸率的个数
        int allBreath = 0;//总呼吸率数据之和
        for (HealthDataBean heartDataBean:breathDates){//统计一天所有呼吸率的平均值
            if(heartDataBean.getValue()!=0){
                sum++;
                allBreath+=heartDataBean.getValue();
            }
        }
        if (sum!=0)
            averageBreath = (short) (allBreath/sum);
        return new BreathInDayData(DateUtil.getDateLongToInt(dataForDay.get(0).getTime()),averageBreath);

    }

    /**
     * 批量保存翻身数据
     * */
    public void saveTurnOverDataListData(List<TurnOverData> turnOverDataList){
        //Log.d("SZIP******","turnOverDataList.size = "+turnOverDataList.size());
        FlowManager.getDatabase(AppDatabase.class)
                .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                        new ProcessModelTransaction.ProcessModel<TurnOverData>() {
                            @Override
                            public void processModel(TurnOverData turnOverData, DatabaseWrapper wrapper) {
                                if (turnOverData.time< DateUtil.getStringToDateMillis()){
                                SQLite.delete()
                                        .from(TurnOverData.class)
                                        .where(TurnOverData_Table.time.is(turnOverData.time))
                                        .execute();

                                    turnOverData.save();
                                }

                            }
                        }).addAll(turnOverDataList).build())  // add elements (can also handle multiple)
                .error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {

                    }
                })
                .success(new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {
                        //Log.d("SZIP******","翻身数据保存成功");
                        ((MyApplication)mContext.getApplicationContext()).setDataUpdateState(6);
                    }
                }).build().execute();
    }


    /**
     * 批量保存日均翻身数据
     * */
    public void saveTurnOverInDayDataListData(List<TurnOverData> turnOverDataList){
        List<TurnOverInDayData> turnOverInDayDataList = new ArrayList<>();
        List<TurnOverData> dataForDay = new ArrayList<>();
        long time[] = new long[2];
        //遍历翻身数据，统计每日的日均翻身数据
        for (int i = 0;i<turnOverDataList.size();i++){
            TurnOverData turnOverData = turnOverDataList.get(i);
            if (dataForDay.size() == 0){//如果统计链表的个数为0，则获取数据中的一组新的时间戳范围
                time = DateUtil.getTimeScopeForDay(turnOverData.getTime());//获取当前数据时间戳所在当天的时间戳范围
            }
            if (turnOverData.getTime()>=time[0]&&turnOverData.getTime()<=time[1]){//如果遍历的数据在同一天，则加入统计链表
                dataForDay.add(turnOverData);
            }else {//如果遍历到的数据不在同一天，则开始统计统计链表中的数据，并清空统计链表重新统计
                turnOverInDayDataList.add(getTurnOverInDayData(dataForDay));
                dataForDay.clear();//统计完了清空链表，为下一次统计做准备
                time = DateUtil.getTimeScopeForDay(turnOverData.getTime());//重新获取新一天的时间戳范围
                dataForDay.add(turnOverData);
            }

            if(i == turnOverDataList.size()-1){//如果是最后一个数据了，直接统计
                turnOverInDayDataList.add(getTurnOverInDayData(dataForDay));
            }
        }



        FlowManager.getDatabase(AppDatabase.class)
                .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                        new ProcessModelTransaction.ProcessModel<TurnOverInDayData>() {
                            @Override
                            public void processModel(TurnOverInDayData turnOverInDayData, DatabaseWrapper wrapper) {
                                TurnOverInDayData turnOverInDayData1;

                                turnOverInDayData1 = SQLite.select()
                                        .from(TurnOverInDayData.class)
                                        .where(TurnOverInDayData_Table.time.is(turnOverInDayData.time))
                                        .querySingle();
                                if (turnOverInDayData1 == null )//统计过的数据不存
                                    turnOverInDayData.save();
                                else if (DateUtil.isToday(turnOverInDayData.time)){//当天数据覆盖
                                    SQLite.delete()
                                            .from(TurnOverInDayData.class)
                                            .where(TurnOverInDayData_Table.time.is(turnOverInDayData.time))
                                            .execute();
                                    turnOverInDayData.save();
                                }
                            }
                        }).addAll(turnOverInDayDataList).build())  // add elements (can also handle multiple)
                .error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {

                    }
                })
                .success(new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {
                        //Log.d("SZIP******","翻身次数统计数据保存成功");
                        ((MyApplication)mContext.getApplicationContext()).setDataUpdateState(7);
                    }
                }).build().execute();
    }


    /**
     * 统计日均翻身数据
     * */
    private TurnOverInDayData getTurnOverInDayData(List<TurnOverData> dataForDay){

        ArrayList<HealthDataBean> turnOverDatas = new ArrayList<>();//一天所有的翻身数据列表
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<HealthDataBean>>(){}.getType();
        //整合一天所有的翻身数据记录
        for (int a = 0;a<dataForDay.size();a++){
            String outputarray = dataForDay.get(a).getDataForturnOver();
            ArrayList<HealthDataBean> data = gson.fromJson(outputarray, type);
            if (data!=null)
                turnOverDatas.addAll(data);
        }
        short averageTurnOver = 0;//平均翻身数据
        int sum = 0;//翻身数据的个数
        int allTurnOver = 0;//总翻身数据之和
        for (HealthDataBean turnOverDataBean:turnOverDatas){//统计一天所有翻身次数的平均值
            sum++;
            allTurnOver+=turnOverDataBean.getValue()&0x0f;
        }
        if (sum!=0)
            averageTurnOver = (short) ((float)allTurnOver/(float) sum*20);
        return new TurnOverInDayData(DateUtil.getDateLongToInt(dataForDay.get(0).getTime()),averageTurnOver);

    }


    /**
     * 清楚所有数据
     * */
    public void clearDB(){
        SQLite.delete(BreathData.class).execute();
        SQLite.delete(BreathInDayData.class).execute();
        SQLite.delete(HeartData.class).execute();
        SQLite.delete(HeartInDayData.class).execute();
        SQLite.delete(SleepData.class).execute();
        SQLite.delete(SleepInDayData.class).execute();
        SQLite.delete(TurnOverData.class).execute();
        SQLite.delete(TurnOverInDayData.class).execute();
    }
}
