package com.zhuoting.health.bean;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TurnOverListBean {

    public boolean flag6 = false;
    public boolean flag5 = false;
    public boolean flag4 = false;
    public boolean flag3 = false;
    public boolean flag2 = false;
    public boolean flag1 = false;
    public boolean flag0 = false;

    private ArrayList<Data> list6;
    private ArrayList<Data> list5;
    private ArrayList<Data> list4;
    private ArrayList<Data> list3;
    private ArrayList<Data> list2;
    private ArrayList<Data> list1;
    private ArrayList<Data> list0;

    public ArrayList<Data> getList6() {
        return list6;
    }

    public void setList6(ArrayList<Data> list6) {
        this.list6 = list6;
    }

    public ArrayList<Data> getList5() {
        return list5;
    }

    public void setList5(ArrayList<Data> list5) {
        this.list5 = list5;
    }

    public ArrayList<Data> getList4() {
        return list4;
    }

    public void setList4(ArrayList<Data> list4) {
        this.list4 = list4;
    }

    public ArrayList<Data> getList3() {
        return list3;
    }

    public void setList3(ArrayList<Data> list3) {
        this.list3 = list3;
    }

    public ArrayList<Data> getList2() {
        return list2;
    }

    public void setList2(ArrayList<Data> list2) {
        this.list2 = list2;
    }

    public ArrayList<Data> getList1() {
        return list1;
    }

    public void setList1(ArrayList<Data> list1) {
        this.list1 = list1;
    }

    public ArrayList<Data> getList0() {
        return list0;
    }

    public void setList0(ArrayList<Data> list0) {
        this.list0 = list0;
    }

    public boolean getFlag(int day){
        if (day == 0){
            return flag0;
        }else if (day == 1){
            return flag1;
        }else if (day == 2){
            return flag2;
        }else if (day == 3){
            return flag3;
        }else if (day == 4){
            return flag4;
        }else if (day == 5){
            return flag5;
        }else if (day == 6){
            return flag6;
        }else
            return false;
    }

    public byte[] getData(int day){
        byte myData[] = null;
        ArrayList<Data> list;
        if (day == 0){
            list = this.list0;
        }else if (day == 1){
            list = this.list1;
        }else if (day == 2){
            list = this.list2;
        }else if (day == 3){
            list = this.list3;
        }else if (day == 4){
            list = this.list4;
        }else if (day == 5){
            list = this.list5;
        }else if (day == 6){
            list = this.list6;
        }else
            list = null;


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

    public byte[][] getHealthData(int day){
        byte [][]healthData;
        byte myData[] = null;
        ArrayList<Data> list;
        if (day == 0){
            list = this.list0;
        }else if (day == 1){
            list = this.list1;
        }else if (day == 2){
            list = this.list2;
        }else if (day == 3){
            list = this.list3;
        }else if (day == 4){
            list = this.list4;
        }else if (day == 5){
            list = this.list5;
        }else if (day == 6){
            list = this.list6;
        }else
            list = null;


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
