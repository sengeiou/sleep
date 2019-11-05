package com.szip.sleepee.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.szip.sleepee.Controller.MainActivity;
import com.szip.sleepee.R;

import java.util.ArrayList;

public class MyPagerAdapter extends FragmentPagerAdapter {


    private ArrayList<Fragment> fragmentArrayList;
    private Context context;


    public void setFragmentArrayList(ArrayList<Fragment> list){
        this.fragmentArrayList = list;
    }

    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }

}
