package com.szip.sleepee.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.inuker.bluetooth.library.search.SearchResult;
import com.szip.sleepee.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DeviceListAdapter extends BaseAdapter implements Comparator<SearchResult> {

    private Context mContext;

    private List<SearchResult> mDataList;

    public DeviceListAdapter(Context context) {
        mContext = context;
        mDataList = new ArrayList<SearchResult>();
    }

    public void setDataList(List<SearchResult> datas) {
        mDataList.clear();
        mDataList.addAll(datas);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int compare(SearchResult lhs, SearchResult rhs) {
        return rhs.rssi - lhs.rssi;
    }

    private static class ViewHolder {
        TextView name;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.adapter_device_list, null, false);
            holder = new ViewHolder();
            holder.name = convertView.findViewById(R.id.nameTv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final SearchResult result = (SearchResult) getItem(position);

        holder.name.setText(result.getName());

        return convertView;
    }
}
