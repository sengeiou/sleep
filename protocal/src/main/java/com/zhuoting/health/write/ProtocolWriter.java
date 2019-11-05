package com.zhuoting.health.write;

import android.provider.ContactsContract;
import android.util.Log;

import com.zhuoting.health.util.DataUtil;
import com.zhuoting.health.util.Tools;

import java.util.Calendar;

/**
 * Created by Hqs on 2018/1/8
 * 写指令，包含各种功能
 */
public class ProtocolWriter {

    /**
     * 同步版本
     * */
    public static byte[] writeForGetVersion(){
        byte[] getPower = new byte[]{(byte) 0xab,0x00};
        return Tools.makeSend(getPower);
    }

    /**
     * 同步电量
     * */
    public static byte[] writeForGetPower(){
        byte[] getPower = new byte[]{(byte) 0xab,0x02};
        return Tools.makeSend(getPower);
    }

    /**
     * 时间设置/读取
     * */
    public static byte[] writeForReadTime(){
        byte[] readTime = new byte[]{(byte) 0xab,0x03,0x00};
        return Tools.makeSend(readTime);
    }

    public static byte[] writeForWriteTime(byte year,byte month,byte day,byte hours,byte min,byte second){
        byte[] writeTime = new byte[]{(byte) 0xab,0x03,0x01,year,month,day,hours,min,second};
        return Tools.makeSend(writeTime);
    }

    /**
     *设备重置
     * */
    public static byte[] writeForResetDevice(){
        byte[] resetDevice = new byte[]{(byte) 0xab,0x04};
        return Tools.makeSend(resetDevice);
    }


    /**
     *读取闹钟
     * */
    public static byte[] writeForReadClock(){
        byte[] readClock = new byte[]{(byte) 0xab,0x06,0x04,(byte) 0xff};
        return Tools.makeSend(readClock);
    }

    /**
     *删除闹钟
     * @param pos       删除的序号
     * */
    public static byte[] writeForDeleteClock(byte pos){
        byte[] readClock = new byte[]{(byte) 0xab,0x06,0x02,(byte) pos};
        return Tools.makeSend(readClock);
    }

    /**
     *添加闹钟
     * @param alarmDev          提醒设备
     * @param onOff             开关
     * @param reCallNum         重复次数
     * @param reCallTime        重复间隔
     * @param cycle             重复周
     * @param hours             小时
     * @param min               分钟
     * @param smart             智能闹钟
     * @param remark            事件类型
     * */
    public static byte[] writeForAddClock(byte alarmDev,byte onOff,byte reCallNum,byte reCallTime,byte cycle,byte hours,byte min,byte smart,byte remark){
        byte[] writeClock = new byte[]{(byte) 0xab,0x06,0x01,(byte) 0xff,alarmDev,0x01,reCallNum,reCallTime,cycle,hours,min,smart,remark};
        return Tools.makeSend(writeClock);
    }

    /**
     *修改闹钟
     * @param alarmDev          提醒设备
     * @param onOff             开关
     * @param reCallNum         重复次数
     * @param reCallTime        重复间隔
     * @param cycle             重复周
     * @param hours             小时
     * @param min               分钟
     * @param smart             智能闹钟
     * @param remark            事件类型
     * */
    public static byte[] writeForChangeClock(byte pos,byte alarmDev,byte onOff,byte reCallNum,byte reCallTime,byte cycle,byte hours,byte min,byte smart,byte remark){
        byte[] writeClock = new byte[]{(byte) 0xab,0x06,0x03,pos,alarmDev,onOff,reCallNum,reCallTime,cycle,hours,min,smart,remark};
        return Tools.makeSend(writeClock);
    }

    /**
     *设置/读取用户参数
     * */
    public static byte[] writeForReadUser(){
        byte[] readUser = new byte[]{(byte) 0xab,0x08,0x00};
        return Tools.makeSend(readUser);
    }

    public static byte[] writeForWriteUser(byte gender,byte age,byte stature,byte weight){
        byte[] writeUser = new byte[]{(byte) 0xab,0x08,0x01,gender,age,stature,weight};
        return Tools.makeSend(writeUser);
    }


//    /**
//     *设置/读取目标参数
//     * */
//    public static byte[] writeForReadTarget(){
//        byte[] writeTime = new byte[]{(byte) 0xab,0x09,0x00};
//        return Tools.makeSend(writeTime);
//    }
//
//    public static byte[] writeForWriteTarget(){
//        byte[] writeTime = new byte[]{(byte) 0xab,0x09,0x01};
//        return Tools.makeSend(writeTime);
//    }

    /**
     *推送实时心率/呼吸率开关
     * */
    public static byte[] writeForReadHealth(byte onOff){
        byte[] readHealth = new byte[]{(byte) 0xab,0x50,onOff};
        return Tools.makeSend(readHealth);
    }


    /**
     *读取历史心率/呼吸率
     * */
    public static byte[] writeForReadHistoryHealth(){
        byte[] readHistoryHealth = new byte[]{(byte) 0xab,0x53,0x01};
        return Tools.makeSend(readHistoryHealth);
    }


    /**
     *心率/呼吸率原始数据
     * */
    public static byte[] writeForReadAdcData(byte onOff){
        byte[] readAdc = new byte[]{(byte) 0xab,0x51,onOff};
        return Tools.makeSend(readAdc);
    }


    /**
     *读取历史睡眠数据
     * */
    public static byte[] writeForReadSleepState(){
        byte[] readHistoryTurnNum = new byte[]{(byte) 0xab,0x52,0x01};
        return Tools.makeSend(readHistoryTurnNum);
    }

    /**
     *读取历史翻身数据
     * */
    public static byte[] writeForReadHistoryTurnNum(){
        byte[] readHistoryTurnNum = new byte[]{(byte) 0xab,0x54,0x01};
        return Tools.makeSend(readHistoryTurnNum);
    }

    /**
     *删除底层数据
     * */
    public static byte[] writeForDeleteData(){
        byte[] deleteTurnNum = new byte[]{(byte) 0xab,0x55};
        return Tools.makeSend(deleteTurnNum);
    }

    /**
     *电气特性测试模式
     * */
    public static byte[] writeForTestElectricalSpecification(byte flag1,byte flag2){
        byte[] electrical = new byte[]{(byte) 0xab, (byte) 0x80,flag1,flag2};
        return Tools.makeSend(electrical);
    }

}
