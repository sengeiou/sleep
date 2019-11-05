package com.szip.sleepee.View;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.szip.sleepee.R;
import com.szip.sleepee.View.wheelview.adapters.AbstractWheelTextAdapter;
import com.szip.sleepee.View.wheelview.views.OnWheelChangedListener;
import com.szip.sleepee.View.wheelview.views.OnWheelScrollListener;
import com.szip.sleepee.View.wheelview.views.WheelView;

import java.util.ArrayList;



/**
 * Created by Administrator on 2019/3/26.
 */

public class SelectTimeView extends LinearLayout {
    private Context context;
    private WheelView wv_hour;
    private WheelView wv_min;
    private ArrayList<String> arrHour = new ArrayList<String>();
    private ArrayList<String> arrMin = new ArrayList<String>();
    private AddressTextAdapter hourAdapter, minAdapter;
    private String strHour = "00", strMin = "00";

    private int maxsize = 24;
    private int minsize = 14;



    public SelectTimeView(Context context) {
        super(context);
        this.context = context;
//        initView();
    }

    public SelectTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
//        initView();

    }

    public SelectTimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
//        initView();
    }

    private void initView() {
        LayoutInflater.from(context).inflate(R.layout.select_time_layout,this);
        initTime();
        wv_hour =  findViewById(R.id.wv_hour);
        wv_min =  findViewById(R.id.wv_min);
        hourAdapter = new AddressTextAdapter(context, arrHour, getHourItem(strHour), maxsize, minsize);
        wv_hour.setVisibleItems(5);
        wv_hour.setViewAdapter(hourAdapter);
        wv_hour.setCurrentItem(getHourItem(strHour));


        wv_hour.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // TODO Auto-generated method stub
                String currentText = (String) hourAdapter.getItemText(wheel.getCurrentItem());
                strHour = currentText;
                setTextviewSize(currentText, hourAdapter);
            }
        });

        wv_hour.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                // TODO Auto-generated method stub
                String currentText = (String) hourAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, hourAdapter);
            }
        });

        minAdapter = new AddressTextAdapter(context, arrMin, getMinItem(strMin), maxsize, minsize);
        wv_min.setVisibleItems(5);
        wv_min.setViewAdapter(minAdapter);
        wv_min.setCurrentItem(getMinItem(strMin));


        wv_min.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // TODO Auto-generated method stub
                String currentText = (String) minAdapter.getItemText(wheel.getCurrentItem());
                strMin = currentText;
                setTextviewSize(currentText, minAdapter);
            }
        });

        wv_min.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                // TODO Auto-generated method stub
                String currentText = (String) minAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, minAdapter);
            }
        });
    }

    private void initTime() {

        for (int i = 0; i < 24; i++) {
            arrHour.add(String.format("%02d",i));
        }

        for (int i = 0; i < 60; i++) {
            arrMin.add(String.format("%02d",i));
        }
    }

    private class AddressTextAdapter extends AbstractWheelTextAdapter {
        ArrayList<String> list;

        protected AddressTextAdapter(Context context, ArrayList<String> list, int currentItem, int maxsize, int minsize) {
            super(context, R.layout.dialog_wheelview_item, NO_RESOURCE, currentItem, maxsize, minsize);
            this.list = list;
            setItemTextResource(R.id.tempValue);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return list.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return list.get(index) + "";
        }
    }

    /**
     * 设置字体大小
     *
     * @param curriteItemText
     * @param adapter
     */
    public void setTextviewSize(String curriteItemText, AddressTextAdapter adapter) {
        ArrayList<View> arrayList = adapter.getTestViews();
        int size = arrayList.size();
        String currentText;
        for (int i = 0; i < size; i++) {
            TextView textvew = (TextView) arrayList.get(i);
            currentText = textvew.getText().toString();
            if (curriteItemText.equals(currentText)) {
                textvew.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
            } else {
                textvew.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            }
        }
    }

    /**
     * 返回分钟索引，没有就返回默认“00”
     *
     * @return
     */
    public int getMinItem(String min) {
        int size = arrMin.size();
        int provinceIndex = 0;
        boolean noprovince = true;
        for (int i = 0; i < size; i++) {
            if (min.equals(arrMin.get(i))) {
                noprovince = false;
                return provinceIndex;
            } else {
                provinceIndex++;
            }
        }
        if (noprovince) {
            strMin = "00";
            return 1;
        }
        return provinceIndex;
    }
    /**
     * 返回小时索引，没有就返回默认“00”
     *
     * @return
     */
    public int getHourItem(String hour) {
        int size = arrHour.size();
        int provinceIndex = 0;
        boolean noprovince = true;
        for (int i = 0; i < size; i++) {
            if (hour.equals(arrHour.get(i))) {
                noprovince = false;
                return provinceIndex;
            } else {
                provinceIndex++;
            }
        }
        if (noprovince) {
            strHour = "00";
            return 1;
        }
        return provinceIndex;
    }

    public void setTime(String strHour) {
        int i = strHour.indexOf(":");
        if (i>=0){
            this.strHour = strHour.substring(0,i);
            this.strMin = strHour.substring(i+1,strHour.length());
        }
        initView();
    }

    public String getTime(){
        return this.strHour+":"+strMin;
    }
}
