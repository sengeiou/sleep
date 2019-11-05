package com.szip.sleepee.Bean.HttpBean;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;

public class ClockData extends BaseApi implements Comparable<ClockData>{

    private int id;
    private int hour;
    private int minute;
    private int type;//1 看护 2 起床 3 早睡
    private int index;

    private int isDevice;
    private int isPhone;
    private int isOn;

    private String repeat;
    private byte repeatState;

    private String music;
    private int isIntelligentWake;
    private String remark;

    public String makeCycle(){
        StringBuffer stringBuffer = new StringBuffer();
        if (repeat==null){
            this.repeat = byteToRepeat();
        }

        if (this.repeat.length()==13){
            stringBuffer.append("Everyday ");
        }else if (this.repeat.length()==9&&this.repeat.indexOf("6")<0&&this.repeat.indexOf("7")<0){
            stringBuffer.append("Workday ");
        }else if (this.repeat.length()==3&&this.repeat.indexOf("6")>=0&&this.repeat.indexOf("7")>=0){
            stringBuffer.append("Weekend ");
        }else if (this.repeat.length()>0){
            if (this.repeat.indexOf("7")>=0){
                stringBuffer.append("Sun. ");
            }
            if (this.repeat.indexOf("1")>=0){
                stringBuffer.append("Mon. ");
            }
            if (this.repeat.indexOf("2")>=0){
                stringBuffer.append("Tue. ");
            }
            if (this.repeat.indexOf("3")>=0){
                stringBuffer.append("Wed. ");
            }
            if (this.repeat.indexOf("4")>=0){
                stringBuffer.append("Thu. ");
            }
            if (this.repeat.indexOf("5")>=0){
                stringBuffer.append("Fri. ");
            }
            if (this.repeat.indexOf("6")>=0){
                stringBuffer.append("Sat. ");
            }
        }else {
            stringBuffer.append("Only once ");
        }
        String string =  stringBuffer.substring(0,stringBuffer.length()-1);
        return string;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIsPhone() {
        return isPhone;
    }

    public void setIsPhone(int isPhone) {
        this.isPhone = isPhone;
    }

    public int getIsOn() {
        return isOn;
    }

    public void setIsOn(int isOn) {
        this.isOn = isOn;
    }

    public ArrayList<String> getRepeat() {
        if (repeat!=null)
            return StirngToArray(repeat);
        else
            return StirngToArray(byteToRepeat());
    }

    private String byteToRepeat() {
        StringBuilder stringBuilder = new StringBuilder();
        if (((repeatState>>0)&0x01) == 0x01){
            stringBuilder.append("7,");
        }
        if (((repeatState>>1)&0x01) == 0x01) {
            stringBuilder.append("1,");
        }
        if (((repeatState>>2)&0x01) == 0x01){
            stringBuilder.append("2,");
        }
        if (((repeatState>>3)&0x01) == 0x01){
            stringBuilder.append("3,");
        }
        if (((repeatState>>4)&0x01) == 0x01){
            stringBuilder.append("4,");
        }
        if (((repeatState>>5)&0x01) == 0x01){
            stringBuilder.append("5,");
        }
        if (((repeatState>>6)&0x01) == 0x01){
            stringBuilder.append("6,");
        }
        if (stringBuilder.length()>0)
            return stringBuilder.substring(0,stringBuilder.length()-1);
        else
            return "";
    }


    private ArrayList<String> StirngToArray(String repeatString){
        ArrayList<String> repeatList = new ArrayList<>();
        if (repeatString.indexOf("7")>=0){
            repeatList.add("7");
        }
        if (repeatString.indexOf("1")>=0){
            repeatList.add("1");
        }
        if (repeatString.indexOf("2")>=0){
            repeatList.add("2");
        }
        if (repeatString.indexOf("3")>=0){
            repeatList.add("3");
        }
        if (repeatString.indexOf("4")>=0){
            repeatList.add("4");
        }
        if (repeatString.indexOf("5")>=0){
            repeatList.add("5");
        }
        if (repeatString.indexOf("6")>=0){
            repeatList.add("6");
        }
        return repeatList;
    }

    public void setRepeatState(byte repeatState) {
        this.repeatState = repeatState;
    }

    public String getMusic() {
        return music;
    }

    public void setMusic(String music) {
        this.music = music;
    }

    public int getIsIntelligentWake() {
        return isIntelligentWake;
    }

    public void setIsIntelligentWake(int isIntelligentWake) {
        this.isIntelligentWake = isIntelligentWake;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTime() {
        return String.format("%02d:%02d",this.hour,this.minute);
    }


    @Override
    public int compareTo(@NonNull ClockData o) {
        int result = this.hour-o.hour;
        if (result == 0){
            return this.minute-o.minute;
        }else {
            return result;
        }

    }
}
