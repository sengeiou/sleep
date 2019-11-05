package com.zhuoting.health.bean;

import android.database.Cursor;


import com.zhuoting.health.util.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by cowork16 on 2017/8/8.
 */

public class SleepInfo {
    public long beginTime;
    public long endTime;
    public int dsCount;//深睡次数
    public int qsCount;//浅睡次数

    public int dsTimes;//深睡总时间
    public int qsTimes;//浅睡总时间

    public List<SleepMegInfo> mlist;

    public String timeFormet;

    public String beginFormet;
    public String endFormet;

    public SleepInfo(){
        mlist = new ArrayList<>();
    }

    public void fameDate(){
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        timeFormet = format.format(new Date(endTime));

        format=new SimpleDateFormat("HH:mm");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        beginFormet = format.format(new Date(beginTime));
        endFormet = format.format(new Date(endTime));

    }

    public void initWithData(byte[] data){

        if (data == null){
            return;
        }

        int jg = 0;

        byte[] btime = {data[3+jg], data[2+jg], data[1+jg], data[0+jg]};
        beginTime = 946656000 + TransUtils.Bytes2Dec(btime);
        beginTime = beginTime * 1000;

        byte[] etime = { data[7+jg], data[6+jg], data[5+jg], data[4+jg]};
        endTime = 946656000 + TransUtils.Bytes2Dec(etime);
        endTime = endTime * 1000;



        fameDate();

        byte[] dsCounts = {0x00,0x00,data[9+jg], data[8+jg]};
        dsCount = TransUtils.Bytes2Dec(dsCounts);

        byte[] qsCounts = {0x00,0x00,data[11+jg], data[10+jg]};
        qsCount = TransUtils.Bytes2Dec(qsCounts);

        byte[] dsTimess = { 0x00,0x00,data[13+jg], data[12+jg] };
        dsTimes = TransUtils.bytes2short(dsTimess);

        byte[] qsTimess = { 0x00,0x00,data[15+jg], data[14+jg] };
        qsTimes = TransUtils.bytes2short(qsTimess);

        dsTimes = dsTimes * 60;
        qsTimes = qsTimes *60;



        if (data.length > 19){

            int lenght = data.length - 20;
            byte[] msg = new byte[lenght];
            System.arraycopy(data, 20, msg, 0, lenght);




            List<byte[]> blist = Tools.makeSendMsg(msg,8);
            for (byte[] obj : blist ){

//                System.out.println(Tools.logbyte(obj));

                SleepMegInfo sleepMegInfo = new SleepMegInfo();
                sleepMegInfo.initWithData(obj);
                mlist.add(sleepMegInfo);
            }
        }

    }

    @Override
    public String toString() {
        return "SleepInfo{" +
                "beginTime=" + beginTime +
                ", endTime=" + endTime +
                ", dsCount=" + dsCount +
                ", qsCount=" + qsCount +
                ", dsTimes=" + dsTimes +
                ", qsTimes=" + qsTimes +
                ", mlist=" + mlist +
                ", timeFormet='" + timeFormet + '\'' +
                ", beginFormet='" + beginFormet + '\'' +
                ", endFormet='" + endFormet + '\'' +
                '}';
    }

    //    public void sqlupdate(){
//        String sql = "UPDATE SportInfo SET date='"+date.getTime()+"'" +
//                ", stepCount='"+stepCount+"'" +
//                ", distance='"+distance+"'" +
//                ",totalCalories='"+totalCalories+"'" +
//                ",sportCalories='"+sportCalories+"'" +
//                " WHERE userName = ?" +
//                " and dev_id = '"+Tools.dev_id+"'" +
//                " and dateformat = '"+dateformat+"'";
//        Tools.db.execSQL(sql,new String[]{Tools.username});
//    }
//    public void sqldelete(){
//        String sql = "DELETE FROM SportInfo WHERE userName = ?" +
//                " and dev_id = '"+Tools.dev_id+"' " +
//                "and dateformat = '"+dateformat+"'";
//        Tools.db.execSQL(sql,new String[]{Tools.username});
//
//    }
//
    public void sqlinster(){

        String str = "";
        JSONArray alist = new JSONArray();
        for (SleepMegInfo sminfo : mlist){
            alist.put(sminfo.objectToDictionary());
        }

        str = alist.toString();

        String sql = "INSERT INTO sleep (beginTime,endTime,dsCount,qsCount,dsTimes,qsTimes,mlist,timeFormet) VALUES (?,?,?,?,?,?,?,?)";
//        DBHelper.getInstance(null).execSQL(sql,new Object[]{beginTime,endTime,dsCount,qsCount,dsTimes,qsTimes,str,timeFormet});
    }

    public void setCursor(Cursor cursor){



        beginTime = cursor.getLong(cursor.getColumnIndex("beginTime"));
        endTime = cursor.getLong(cursor.getColumnIndex("endTime"));
        dsCount = cursor.getInt(cursor.getColumnIndex("dsCount"));
        qsCount = cursor.getInt(cursor.getColumnIndex("qsCount"));
        dsTimes = cursor.getInt(cursor.getColumnIndex("dsTimes"));
        qsTimes = cursor.getInt(cursor.getColumnIndex("qsTimes"));
        String str = cursor.getString(cursor.getColumnIndex("mlist"));
        timeFormet = cursor.getString(cursor.getColumnIndex("timeFormet"));
        try {
            JSONArray alist = new JSONArray(str);
            for (int i=0;i<alist.length();i++){
                JSONObject obj = alist.getJSONObject(i);
                SleepMegInfo smInfo = new SleepMegInfo();
                smInfo.setValue(obj);
                mlist.add(smInfo);
            }
        }catch (JSONException e){

        }

        fameDate();
    }
//
//    public Map<String, String> objectToDictionary(){
//
//        Map<String, String> dlist = new HashMap<String, String>();
//        dlist.put("date", (date.getTime()/1000)+"");
//        dlist.put("stepCount", stepCount+"");
//        dlist.put("distance", distance+"");
//        dlist.put("totalCalories", totalCalories+"");
//        dlist.put("sportCalories", sportCalories+"");
//        dlist.put("dateformat", dateformat);
//        return dlist;
//    }
//
//    public void setValue(JSONObject dlist){
//
//        try {
//
//            dateformat = dlist.getString("dateformat");
//            date = new Date(Long.parseLong(dlist.getString("date"))*1000);
//            stepCount = Integer.parseInt(dlist.getString("stepCount"));
//            distance = Integer.parseInt(dlist.getString("distance"));
//            totalCalories = Integer.parseInt(dlist.getString("totalCalories"));
//            sportCalories = Integer.parseInt(dlist.getString("sportCalories"));
//
//        } catch (JSONException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//    }
}
