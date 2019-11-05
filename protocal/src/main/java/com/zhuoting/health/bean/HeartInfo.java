package com.zhuoting.health.bean;

import android.database.Cursor;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by cowork16 on 2017/8/8.
 */

public class HeartInfo {
    public long rtime;
    public int heartTimes;
    public int hour;
    public String rtimeFormat;
    public String timeStr;

    public HeartInfo(){

    }

    public void fameDate(){
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        rtimeFormat = format.format(new Date(rtime));

        format=new SimpleDateFormat("HH:mm");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        timeStr = format.format(new Date(rtime));

        System.out.print(timeStr);

        format=new SimpleDateFormat("HH");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        hour = Integer.parseInt(format.format(new Date(rtime)));
    }

    public void initWithData(byte[] data){



        byte[] btime = {data[3], data[2], data[1], data[0]};
        rtime = 946656000 + TransUtils.Bytes2Dec(btime);
        rtime = rtime * 1000;


        byte[] heartTimess = {0x00,0x00,0x00, data[5]};
        heartTimes = TransUtils.Bytes2Dec(heartTimess);


        fameDate();
    }

    @Override
    public String toString() {
        return "HeartInfo{" +
                "rtime=" + rtime +
                ", heartTimes=" + heartTimes +
                ", hour=" + hour +
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
        String sql = "INSERT INTO heart (rtime,rtimeFormat,heartTimes) VALUES (?,?,?)";
//        DBHelper.getInstance(null).execSQL(sql,new Object[]{rtime,rtimeFormat,heartTimes});
    }

    public void setCursor(Cursor cursor){

        rtime = cursor.getLong(cursor.getColumnIndex("rtime"));
        rtimeFormat = cursor.getString(cursor.getColumnIndex("rtimeFormat"));
        heartTimes = cursor.getInt(cursor.getColumnIndex("heartTimes"));

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
