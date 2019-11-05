package com.zhuoting.health.bean;

import android.database.Cursor;

//import com.zhuoting.health.tools.DBHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by cowork16 on 2017/8/8.
 */

public class SportInfo {
    public long begindate;                   //日期
    public long enddate;

    public int step;//步数
    public int des;//距离
    public int cakl;//卡路里

    public int hour;//小时//
    public String timeFormet;

    public String beginhhmm;
    public String endhhmm;


    public SportInfo(){

    }

    public void fameDate(){
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        timeFormet = format.format(new Date(begindate));

        format=new SimpleDateFormat("HH:mm");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        beginhhmm = format.format(new Date(begindate));
        endhhmm = format.format(new Date(enddate));

        format=new SimpleDateFormat("HH");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        hour = Integer.parseInt(format.format(new Date(begindate)));
    }

    public void initWithData(byte[] data){


        byte[] btime = {data[3], data[2], data[1], data[0]};
        begindate = 946656000 + TransUtils.Bytes2Dec(btime);
        begindate = begindate * 1000;

        byte[] etime = { data[7], data[6], data[5], data[4]};
        enddate = 946656000 + TransUtils.Bytes2Dec(etime);
        enddate = enddate * 1000;

        fameDate();

        byte[] steps = {0x00,0x00,data[9], data[8]};
        step = TransUtils.Bytes2Dec(steps);

        byte[] kms = {0x00,0x00,data[11], data[10]};
        des = TransUtils.Bytes2Dec(kms);

        byte[] kcals_total = { 0x00,0x00,data[13], data[12] };
        cakl = TransUtils.Bytes2Dec(kcals_total);

        fameDate();
    }

    @Override
    public String toString() {
        return "SportInfo{" +
                "begindate=" + begindate +
                ", enddate=" + enddate +
                ", step=" + step +
                ", des=" + des +
                ", cakl=" + cakl +
                ", hour=" + hour +
                ", timeFormet='" + timeFormet + '\'' +
                ", beginhhmm='" + beginhhmm + '\'' +
                ", endhhmm='" + endhhmm + '\'' +
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
        String sql = "INSERT INTO sport (timeFormet,begindate,step,des,cakl,enddate) VALUES (?,?,?,?,?,?)";
//        DBHelper.getInstance(null).execSQL(sql,new Object[]{timeFormet,begindate,step,des,cakl,enddate});
    }

    public void setCursor(Cursor cursor){

        timeFormet = cursor.getString(cursor.getColumnIndex("timeFormet"));
        begindate = cursor.getLong(cursor.getColumnIndex("begindate"));
        step = cursor.getInt(cursor.getColumnIndex("step"));
        des = cursor.getInt(cursor.getColumnIndex("des"));
        cakl = cursor.getInt(cursor.getColumnIndex("cakl"));
        enddate = cursor.getLong(cursor.getColumnIndex("enddate"));

        fameDate();
    }
}
