package com.zhuoting.health.bean;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Administrator on 2018/12/28.
 */

public class DataWithSleepMBean {
    private int dataTime;
    private ArrayList<Data> list;

    public DataWithSleepMBean(int dataTime, ArrayList<Data> list) {
        this.dataTime = dataTime;
        this.list = list;
    }

    public int getDataTime() {
        return dataTime;
    }

    public byte[] getData(){
        byte myData[] = null;
        if (list!=null){
            Collections.sort(list);
            for (Data data:list){
                byte sumData[];
                if (myData==null){
                    sumData = new byte[data.getDataList().length-2];
                    System.arraycopy(data.getDataList(),1,sumData,0,data.getDataList().length-2);
                }else {
                    sumData = new byte[data.getDataList().length-2+myData.length];
                    System.arraycopy(myData,0,sumData,0,myData.length);
                    System.arraycopy(data.getDataList(),1,sumData,myData.length,data.getDataList().length-2);
                }
                myData = new byte[sumData.length];
                myData = sumData;
            }
            return myData;
        }else
            return null;

    }

    public byte[][] getHealthData(){
        byte [][]healthData;
        byte myData[] = null;

        if (list!=null){
            Collections.sort(list);
            for (Data data:list){
                byte sumData[];
                if (myData==null){
                    sumData = new byte[data.getDataList().length-2];
                    System.arraycopy(data.getDataList(),1,sumData,0,data.getDataList().length-2);
                }else {
                    sumData = new byte[data.getDataList().length-2+myData.length];
                    System.arraycopy(myData,0,sumData,0,myData.length);
                    System.arraycopy(data.getDataList(),1,sumData,myData.length,data.getDataList().length-2);
                }
                myData = new byte[sumData.length];
                myData = sumData;
            }

            healthData = new byte[2][myData.length/2];
            for (int i = 0,pos = 0;i<myData.length;i+=2,pos++){
                healthData[0][pos] = myData[i];
                healthData[1][pos] = myData[i+1];
            }
            return healthData;
        }else
            return null;

    }


    public static class Data implements Comparable<Data>{

        private byte dataList[];

        public Data(byte[] data){
            this.dataList = data;
        }

        public byte[] getDataList() {
            return dataList;
        }

        public void setDataList(byte[] overTime) {
            this.dataList = overTime;
        }

        @Override
        public int compareTo(@NonNull Data another) {
            return this.dataList[0]-another.dataList[0];
        }
    }
}
