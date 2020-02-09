package com.szip.smartdream.Bean.HttpBean;


import java.util.ArrayList;

public class ReportDataBean extends BaseApi {
    Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data{
        ArrayList<HttpReportBean> list;

        public ArrayList<HttpReportBean> getList() {
            return list;
        }

        public void setList(ArrayList<HttpReportBean> list) {
            this.list = list;
        }
    }
}
