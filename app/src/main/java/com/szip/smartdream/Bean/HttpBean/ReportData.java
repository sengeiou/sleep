package com.szip.smartdream.Bean.HttpBean;

import java.util.ArrayList;

public class ReportData {
    private String dates;
    private ArrayList<Data> list;

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    public ArrayList<Data> getList() {
        return list;
    }

    public void setList(ArrayList<Data> list) {
        this.list = list;
    }

    public static class Data{
        int type;
        ArrayList<String> dataContent;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public ArrayList<String> getDataContent() {
            return dataContent;
        }

        public void setDataContent(ArrayList<String> dataContent) {
            this.dataContent = dataContent;
        }

        public Data(int type,ArrayList<String> dataContent){
            this.type = type;
            this.dataContent = dataContent;
        }
    }
}
