package com.szip.smartdream.Bean.HttpBean;

import java.util.ArrayList;

public class ClockDataBean extends BaseApi{

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data{

        private ArrayList<ClockData> list;

        public ArrayList<ClockData> getArray() {
            return list;
        }

        public void setArray(ArrayList<ClockData> array) {
            this.list = array;
        }
    }

}
