package com.szip.sleepee.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.szip.sleepee.R;

import java.util.ArrayList;

public class MyMenuAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<MenuData> list1;
    private ArrayList<MenuData> list2;
    private ArrayList<MenuData> list;
    private int flag;

    public MyMenuAdapter(Context context){
        this.mContext = context;
        initList(context);
        list = list1;
    }

    public void setFlag(int flag){
        this.flag = flag;
        if (flag == 0){
            list = list1;
        }else if (flag == 1){
            list = list2;
        }
    }

    private void initList(Context context) {
        list1 = new ArrayList<>();
        list1.add(new MenuData(context.getResources().getString(R.string.sleep),R.mipmap.menu_icon_sleep));
        list1.add(new MenuData(context.getResources().getString(R.string.report),R.mipmap.menu_icon_report));
        list1.add(new MenuData(context.getResources().getString(R.string.alarm),R.mipmap.menu_icon_alarm));
        list1.add(new MenuData(context.getResources().getString(R.string.me),R.mipmap.menu_icon_me));


    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        HolderView viewHolder = null;
        if (null == convertView)
        {
            viewHolder = new HolderView();
            LayoutInflater mInflater = LayoutInflater.from(mContext);
            convertView = mInflater.inflate(R.layout.menu_adapter, null);

            viewHolder.text =  convertView.findViewById(R.id.text);
            viewHolder.image =  convertView.findViewById(R.id.image);


            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (HolderView) convertView.getTag();
        }

            viewHolder.text.setText(list.get(position).getMenuStr());
            viewHolder.image.setImageResource(list.get(position).getImageRource());

        return convertView;

    }

    class HolderView{
        ImageView image;
        TextView text;
    }

    private class MenuData{
        private int imageRource;
        private String menuStr;

        public MenuData(String string, int menu_icon_run) {
            this.imageRource = menu_icon_run;
            this.menuStr = string;
        }

        public int getImageRource() {
            return imageRource;
        }

        public String getMenuStr() {
            return menuStr;
        }
    }
}
