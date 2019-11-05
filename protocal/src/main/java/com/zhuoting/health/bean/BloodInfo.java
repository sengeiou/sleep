package com.zhuoting.health.bean;

import android.database.Cursor;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by cowork16 on 2017/8/8.
 */

public class BloodInfo {
    public int SBP;
    public int DBP;

    public long rtime;
    public String rtimeFormat;
    public String timeStr;

    public BloodInfo(){

    }

    public void fameDate(){
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        rtimeFormat = format.format(new Date(rtime));

        format=new SimpleDateFormat("HH:mm");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        timeStr = format.format(new Date(rtime));
    }

    public void initWithData(byte[] data){



        byte[] btime = {data[3], data[2], data[1], data[0]};
        rtime = 946656000 + TransUtils.Bytes2Dec(btime);
        rtime = rtime * 1000;


        byte[] SBPs = {0x00,0x00,0x00, data[6]};
        SBP = TransUtils.Bytes2Dec(SBPs);

        byte[] DBPs = {0x00,0x00,0x00, data[5]};
        DBP = TransUtils.Bytes2Dec(DBPs);

        fameDate();
    }

    @Override
    public String toString() {
        return "BloodInfo{" +
                "SBP=" + SBP +
                ", DBP=" + DBP +
                ", rtime=" + rtime +
                ", rtimeFormat='" + rtimeFormat + '\'' +
                ", timeStr='" + timeStr + '\'' +
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

        String sql = "INSERT INTO blood (rtime,rtimeFormat,SBP,DBP) VALUES (?,?,?,?)";
//        DBHelper.getInstance(null).execSQL(sql,new Object[]{rtime,rtimeFormat,SBP,DBP});
    }

    public void setCursor(Cursor cursor){

        rtime = cursor.getLong(cursor.getColumnIndex("rtime"));
        rtimeFormat = cursor.getString(cursor.getColumnIndex("rtimeFormat"));
        SBP = cursor.getInt(cursor.getColumnIndex("SBP"));
        DBP = cursor.getInt(cursor.getColumnIndex("DBP"));

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
