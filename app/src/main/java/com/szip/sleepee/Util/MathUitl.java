package com.szip.sleepee.Util;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jonas.jgraph.utils.MathHelper;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.szip.sleepee.Bean.HealthDataBean;
import com.szip.sleepee.Bean.HttpBean.ClockData;
import com.szip.sleepee.Bean.HttpBean.HttpReportBean;
import com.szip.sleepee.Bean.SleepStateBean;
import com.szip.sleepee.DB.DBModel.BreathData;
import com.szip.sleepee.DB.DBModel.BreathData_Table;
import com.szip.sleepee.DB.DBModel.HeartData;
import com.szip.sleepee.DB.DBModel.HeartData_Table;
import com.szip.sleepee.DB.DBModel.SleepData;
import com.szip.sleepee.DB.DBModel.SleepData_Table;
import com.szip.sleepee.DB.DBModel.TurnOverData;
import com.szip.sleepee.DB.DBModel.TurnOverData_Table;
import com.szip.sleepee.DB.SaveDataUtil;
import com.zhuoting.health.bean.TurnOverListBean;
import com.zhuoting.health.util.DataUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/1/28.
 */

public class MathUitl {
    /**
     * 设置下划线宽度
     * */
    static public void reflex(final TabLayout tabLayout){
        //了解源码得知 线的宽度是根据 tabView的宽度来设置的
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    //拿到tabLayout的mTabStrip属性
                    LinearLayout mTabStrip = (LinearLayout) tabLayout.getChildAt(0);
                    int dp10 = MathHelper.dip2px(tabLayout.getContext(), 16);
                    for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                        View tabView = mTabStrip.getChildAt(i);
                        //拿到tabView的mTextView属性  tab的字数不固定一定用反射取mTextView
                        Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                        mTextViewField.setAccessible(true);

                        TextView mTextView = (TextView) mTextViewField.get(tabView);
                        tabView.setPadding(0, 0, 0, 0);

                        //因为我想要的效果是   字多宽线就多宽，所以测量mTextView的宽度
                        int width = 0;
                        width = mTextView.getWidth();
                        if (width == 0) {
                            mTextView.measure(0, 0);
                            width = mTextView.getMeasuredWidth();
                        }

                        //设置tab左右间距为10dp  注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                        params.width = width ;
                        params.leftMargin = dp10;
                        params.rightMargin = dp10;
                        tabView.setLayoutParams(params);
                        tabView.invalidate();
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Array转换成Stirng
     * */
    static public String ArrayToString(ArrayList<String> repeatList){
        StringBuilder repeatString = new StringBuilder();
        if (repeatList.contains("1")){
            repeatString.append("1,");
        }
        if (repeatList.contains("2")){
            repeatString.append("2,");
        }
        if (repeatList.contains("3")){
            repeatString.append("3,");
        }
        if (repeatList.contains("4")){
            repeatString.append("4,");
        }
        if (repeatList.contains("5")){
            repeatString.append("5,");
        }
        if (repeatList.contains("6")){
            repeatString.append("6,");
        }
        if (repeatList.contains("7")){
            repeatString.append("7,");
        }
        if (repeatString.length()>0)
            return repeatString.substring(0,repeatString.length()-1);
        else
            return "";
    }

    private static ArrayList<Long> longs = new ArrayList<>();//异常数据所在的时间戳列表

    /**
     * 格式化睡眠数据(byte数组的数据，格式化成时间戳+数据组的格式)
     * */
    static public void bytesToArray(byte[] datas,int flag,Context context){
        if (flag == 0){//格式化睡眠数据
            ArrayList<SleepData> sleepDataArrayList = new ArrayList<>();
            int pos = 0;
            Log.d("SZIP******","datas = "+ DataUtil.byteToHexString(datas));
            while (pos != datas.length){
                int stateNum = (datas[pos+5]&0xff)<<8|(datas[pos+4]&0xff)&0x0ffff;//状态个数
                int sleepLenght = stateNum*3+6;//一段睡眠状态的总字节数
                long time  = ((((datas[pos+3]&0xffl)<<24|(datas[pos+2]&0xffl)<<16|(datas[pos+1]&0xffl)<<8|(datas[pos]&0xffl)))&0xffffffffl);//时间戳
                ArrayList<SleepStateBean> stateBeans = new ArrayList<>();
                for (int i = 0,a = pos+6;i<stateNum;i++,a+=3){//初始化睡眠状态链表（byte数组->list）
                    stateBeans.add(new SleepStateBean(((datas[a+1]&0xff)<<8|(datas[a]&0xff)),datas[a+2]&0xff));
                }
                if (getAllSleepTime(stateBeans)>=30){//过滤掉睡眠总时长小于30分钟的异常数据
                    Log.d("SZIP******","data time = "+DateUtil.getDateToString(time)+"; data state = "+new Gson().toJson(stateBeans));
                    sleepDataArrayList.add(new SleepData(time,new Gson().toJson(stateBeans)));
                }else {
                    longs.add(time);//如果出现异常数据，把异常数据所在的时间戳加入列表里面
                }
                pos += sleepLenght;
            }
            SaveDataUtil.newInstance(context).saveSleepDataListData(sleepDataArrayList);
            SaveDataUtil.newInstance(context).saveSleepInDayDataListData(sleepDataArrayList);
        }else if (flag == 1){//格式化心率呼吸率数据
            ArrayList<BreathData> breathDataArrayList = new ArrayList<>();
            ArrayList<HeartData> heartDataArrayList = new ArrayList<>();
            int pos = 0;
            Log.d("SZIP******","datas = "+ DataUtil.byteToHexString(datas));
            while (pos != datas.length){
                int stateNum = (datas[pos+5]&0xff)<<8|(datas[pos+4]&0xff)&0x0ffff;//数据个数
                int dataLengt = stateNum*2+6;//一段睡眠的心率呼吸总字节数
                long time  = ((((datas[pos+3]&0xffl)<<24|(datas[pos+2]&0xffl)<<16|(datas[pos+1]&0xffl)<<8|(datas[pos]&0xffl)))&0xffffffffl);//时间戳
                ArrayList<HealthDataBean> heartList = new ArrayList<>();
                ArrayList<HealthDataBean> breathList = new ArrayList<>();
                for (int i = 0,a = pos+6;i<stateNum;i++,a+=2){//初始化睡眠状态链表（byte数组->list）
                    heartList.add(new HealthDataBean(datas[a]&0xff));
                    breathList.add(new HealthDataBean(datas[a+1]&0xff));
                }
                if (!longs.contains(time)){//过滤掉睡眠总时长小于30分钟的异常数据
                    Log.d("SZIP******","data time = "+DateUtil.getDateToString(time)+"; heart datas = "+new Gson().toJson(heartList));
                    Log.d("SZIP******","data time = "+DateUtil.getDateToString(time)+"; breath datas = "+new Gson().toJson(breathList));
                    breathDataArrayList.add(new BreathData(time,new Gson().toJson(breathList)));
                    heartDataArrayList.add(new HeartData(time,new Gson().toJson(heartList)));
                }
                pos += dataLengt;
            }
            SaveDataUtil.newInstance(context).saveBreathDataListData(breathDataArrayList);
            SaveDataUtil.newInstance(context).saveBreathInDayDataListData(breathDataArrayList);
            SaveDataUtil.newInstance(context).saveHeartDataListData(heartDataArrayList);
            SaveDataUtil.newInstance(context).saveHeartInDayDataListData(heartDataArrayList);
        }else if (flag == 2){//格式化翻身数据
            ArrayList<TurnOverData> turnOverDataArrayList = new ArrayList<>();
            int pos = 0;
            Log.d("SZIP******","datas = "+ DataUtil.byteToHexString(datas));
            while (pos != datas.length){
                int stateNum = (datas[pos+5]&0xff)<<8|(datas[pos+4]&0xff)&0x0ffff;//状态个数
                int dataLengt = stateNum+6;//一段翻身次数的总字节数
                long time  = ((((datas[pos+3]&0xffl)<<24|(datas[pos+2]&0xffl)<<16|(datas[pos+1]&0xffl)<<8|(datas[pos]&0xffl)))&0xffffffffl);//时间戳
                ArrayList<HealthDataBean> turnOverList = new ArrayList<>();
                for (int i = 0,a = pos+6;i<stateNum;i++,a+=1){//初始化翻身次数链表（byte数组->list）
                    turnOverList.add(new HealthDataBean(datas[a]&0xff));
                }
                if (!longs.contains(time)){//过滤掉睡眠总时长小于30分钟的异常数据
                    Log.d("SZIP******","data time = "+DateUtil.getDateToString(time)+"; data state = "+new Gson().toJson(turnOverList));
                    turnOverDataArrayList.add(new TurnOverData(time,new Gson().toJson(turnOverList)));
                }
                pos += dataLengt;
            }
            longs.clear();//同步完了，清空之前的异常数据所在的时间戳列表
            SaveDataUtil.newInstance(context).saveTurnOverDataListData(turnOverDataArrayList);
            SaveDataUtil.newInstance(context).saveTurnOverInDayDataListData(turnOverDataArrayList);
        }
    }

    private static int getAllSleepTime(ArrayList<SleepStateBean> stateBeans){
        int sum = 0;
        for (int i = 0;i<stateBeans.size();i++){
            sum+=stateBeans.get(i).getStateTime();
        }
        return sum;
    }

    /**
     * 把云端下载的数据存到本地
     * */
    static public void saveDataFromHttp(ArrayList<HttpReportBean> reportBeans,Context context){
        ArrayList<SleepData> sleepDataArrayList = new ArrayList<>();
        ArrayList<BreathData> breathDataArrayList = new ArrayList<>();
        ArrayList<HeartData> heartDataArrayList = new ArrayList<>();
        ArrayList<TurnOverData> turnOverDataArrayList = new ArrayList<>();

        for (int i = 0;i<reportBeans.size();i++){
            sleepDataArrayList.add(new SleepData(reportBeans.get(i).getTime(),new Gson().toJson(reportBeans.get(i).getDataForSleep())));
            breathDataArrayList.add(new BreathData(reportBeans.get(i).getTime(),new Gson().toJson(reportBeans.get(i).getDataForBreath())));
            heartDataArrayList.add(new HeartData(reportBeans.get(i).getTime(),new Gson().toJson(reportBeans.get(i).getDataForHeart())));
            turnOverDataArrayList.add(new TurnOverData(reportBeans.get(i).getTime(),new Gson().toJson(reportBeans.get(i).getDataForTurnOver())));
        }
        SaveDataUtil.newInstance(context).saveSleepDataListData(sleepDataArrayList);
        SaveDataUtil.newInstance(context).saveSleepInDayDataListData(sleepDataArrayList);
        SaveDataUtil.newInstance(context).saveBreathDataListData(breathDataArrayList);
        SaveDataUtil.newInstance(context).saveBreathInDayDataListData(breathDataArrayList);
        SaveDataUtil.newInstance(context).saveHeartDataListData(heartDataArrayList);
        SaveDataUtil.newInstance(context).saveHeartInDayDataListData(heartDataArrayList);
        SaveDataUtil.newInstance(context).saveTurnOverDataListData(turnOverDataArrayList);
        SaveDataUtil.newInstance(context).saveTurnOverInDayDataListData(turnOverDataArrayList);
    }

    /**
     * 获取列表平均值
     * */
    public static int getAverageDataOfList( ArrayList<HealthDataBean> healthDataBeans,boolean isTurnOver){
        int sum = 0;
        int size = 0;
        if(isTurnOver){
            for (HealthDataBean data:healthDataBeans){
                sum+=(data.getValue()&0x0f);
                size++;
            }
            return size == 0?0:(int)((float)sum/(float) size*20);
        }else {
            for (HealthDataBean data:healthDataBeans){
                if (data.getValue()!=(byte) 0x00 && data.getValue()!=(byte) 0xff && data.getValue()!=(byte) 0xf0){
                    sum+=(data.getValue()&0xff);
                    size++;
                }
            }
            return size == 0?0:sum/size;
        }
    }

    /**
     * 睡眠列表总睡眠时长，深睡、中睡、浅睡、清醒总时长，以及睡眠起始时间跟结束时间
     * */
    public static int[] getSleepStateValue(long time,ArrayList<SleepStateBean> sleepStateBeanArrayList){
        int [] values = {0,0,0,0,0,0,1560};
        if (sleepStateBeanArrayList!=null){
            int allSleep = 0,deepSleep = 0,middleSleep = 0,lightSleep = 0,wake = 0;
            for (SleepStateBean sleepStateBean:sleepStateBeanArrayList){
                allSleep+=sleepStateBean.getStateTime();
                if (sleepStateBean.getState()<=90)
                    deepSleep+=sleepStateBean.getStateTime();
                else if (sleepStateBean.getState()<=150)
                    middleSleep+=sleepStateBean.getStateTime();
                else if (sleepStateBean.getState()<=234)
                    lightSleep+=sleepStateBean.getStateTime();
                else if (sleepStateBean.getState()<=255)
                    wake+=sleepStateBean.getStateTime();
            }
            values[0] = allSleep;
            values[1] = deepSleep;
            values[2] = middleSleep;
            values[3] = lightSleep;
            values[4] = wake;
            values[5] = DateUtil.getMinueOfDay(time);
            values[6] = values[5]+allSleep;
            return values;
        }else
            return values;
    }
    /**
     * 获取睡眠数据每个点的比值
     * */
    public static float getRadioWithSleep(ArrayList<SleepStateBean> sleepStateBeanArrayList,int pos,int allSleep){
        float radio;
        int sumTime = 0;
        for (int i = 0;i<sleepStateBeanArrayList.size();i++){
            sumTime+=sleepStateBeanArrayList.get(i).getStateTime();
            if (i == pos)
                break;
        }
        radio = (float)sumTime/(float)allSleep;
        return radio;
    }

    /**
     * 把睡眠带的数据换成json格式字符串用于上传到服务器
     * */
    public static String getStringWithJson(long time){
        List<SleepData> sleepDataList = SQLite.select()
                .from(SleepData.class)
                .where(SleepData_Table.time.greaterThan(time))
                .queryList();

        List<HeartData> heartDataList = SQLite.select()
                .from(HeartData.class)
                .where(HeartData_Table.time.greaterThan(time))
                .queryList();

        List<BreathData> breathDataList = SQLite.select()
                .from(BreathData.class)
                .where(BreathData_Table.time.greaterThan(time))
                .queryList();

        List<TurnOverData> turnOverDataList = SQLite.select()
                .from(TurnOverData.class)
                .where(TurnOverData_Table.time.greaterThan(time))
                .queryList();

        JSONArray array = new JSONArray();
        JSONObject data = new JSONObject();

        /**
         * 遍历数据库里面的数据
         * */
        try {
            for (int i = 0;i<sleepDataList.size();i++){
                JSONObject object = new JSONObject();
                object.put("time",sleepDataList.get(i).getTime());
                object.put("dataForSleep",new JSONArray(sleepDataList.get(i).getDataForSleep()));
                object.put("dataForBreath",new JSONArray(breathDataList.get(i).getDataForBreath()));
                object.put("dataForHeart",new JSONArray(heartDataList.get(i).getDataForHeart()));
                object.put("dataForTurnOver",new JSONArray(turnOverDataList.get(i).getDataForturnOver()));
                array.put(object);
                Log.d("SZIP******","PUT STRING = "+array.toString());
            }
            data.put("data",array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data.toString();
    }

    /**
     * 获取最新数据的时间戳
     * */
    public static long getLastTimeFromData(){
        SleepData sleepData;
        sleepData = SQLite.select()
                .from(SleepData.class)
                .orderBy(OrderBy.fromString(SleepData_Table.time+OrderBy.DESCENDING))
                .limit(0)
                .querySingle();
            return sleepData.getTime();
    }

    /**
     * 优化睡眠曲线
     * */
    public static ArrayList<SleepStateBean> makeDrawDataWithSleep(ArrayList<SleepStateBean> sleepStateBeanArrayList){
        ArrayList<SleepStateBean> newList = new ArrayList<>();

        if (sleepStateBeanArrayList.size()>5){
            int delTime = 0;//需要优化的睡眠点的时间总和
            int direction = sleepStateBeanArrayList.get(1).getState() > 0? -1:1;
            newList.add(sleepStateBeanArrayList.get(0));
            for (int i = 1;i<sleepStateBeanArrayList.size()-1;i++){
                SleepStateBean sleepStateBean1 = sleepStateBeanArrayList.get(i);
                SleepStateBean sleepStateBean2 = sleepStateBeanArrayList.get(i+1);

                if (sleepStateBean1.getStateTime()<12){//低于15分钟的睡眠状态全部把时间归于超过15分钟的时间点或者峰值点
                    delTime += sleepStateBeanArrayList.get(i).getStateTime();
                    if((sleepStateBean2.getState()-sleepStateBean1.getState())*direction>0){//峰值点
                        direction*=-1;
                        newList.add(new SleepStateBean(delTime,
                                sleepStateBeanArrayList.get(i).getState()));
                        delTime = 0;
                    }
                } else {
                    newList.add(new SleepStateBean(sleepStateBeanArrayList.get(i).getStateTime()+delTime,
                            sleepStateBeanArrayList.get(i).getState()));
                    delTime = 0;
                }
            }
            newList.add(new SleepStateBean(sleepStateBeanArrayList.get(sleepStateBeanArrayList.size()-1).getStateTime()+delTime,
                    sleepStateBeanArrayList.get(sleepStateBeanArrayList.size()-1).getState()));
        }else {//睡眠状态少于5个的不需要优化
            newList.addAll(sleepStateBeanArrayList);
        }

        return newList;
    }

    /**
     * 判断邮箱是否合法
     * */
    public static boolean isEmail(String strEmail) {
        String strPattern = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        if (TextUtils.isEmpty(strPattern)) {
            return false;
        } else {
            return strEmail.matches(strPattern);
        }
    }

    public static void showToast(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    /**
     * 获取离当前最近的一次闹钟
     * */
    public static String getNearClock(ArrayList<ClockData> clockDataArrayList){
        String clock = "";
        int hour = -1,min = -1;
        Calendar calendar = Calendar.getInstance();
        String day = calendar.get(Calendar.DAY_OF_WEEK)-1 == 0?"7":(( calendar.get(Calendar.DAY_OF_WEEK)-1)+"");
        int hourNow = calendar.get(Calendar.HOUR_OF_DAY);
        int minNow = calendar.get(Calendar.MINUTE);
        for (int i = 0;i<clockDataArrayList.size();i++){
            ClockData clockData = clockDataArrayList.get(i);
            if (clockData.getRepeat().contains(day)&&(clockData.getHour()*60+clockData.getMinute()-hourNow*60-minNow>0)){
                if (hour == -1) {
                    hour = clockData.getHour();
                    min = clockData.getMinute();
                }else if (clockData.getHour()*60+clockData.getMinute()-hour*60-min<0){
                    hour = clockData.getHour();
                    min = clockData.getMinute();
                }
            }
        }
        if (hour==-1){
            for (int i = 0;i<clockDataArrayList.size();i++){
                ClockData clockData = clockDataArrayList.get(i);
                if (hour == -1) {
                    hour = clockData.getHour();
                    min = clockData.getMinute();
                }else if (clockData.getHour()*60+clockData.getMinute()-hourNow*60-minNow<0){
                    hour = clockData.getHour();
                    min = clockData.getMinute();
                }
            }
        }
        if (hour!=-1)
            clock = String.format("%02d:%02d",hour,min);

        Log.d("clock******","clock = "+clock);
        return clock;
    }
}
