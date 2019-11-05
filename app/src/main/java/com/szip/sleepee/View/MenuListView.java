package com.szip.sleepee.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.szip.sleepee.R;

/**
 * Created by Administrator on 2019/1/23.
 */

public class MenuListView extends ViewGroup {
    private Context context;
    int width,height;
    private Bitmap sectorBit;
    private ListView listView;
    private View view1;
    private View view2;
    private View view3;
    private View view4;
    private View view5;



    public MenuListView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public MenuListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public MenuListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    /**
     *
     * 初始化控件布局
     * */
    private void initView() {
        sectorBit = BitmapFactory.decodeResource(context.getResources(), R.mipmap.menu_bg);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        listView.layout(dip2px(context,40),(height-dip2px(context,200))/2,
                width-dip2px(context,40),(height-dip2px(context,200))/2+dip2px(context,200));
        view1.layout(dip2px(context,40),(height-dip2px(context,200))/2-dip2px(context,0.5f),
                width/2,(height-dip2px(context,200))/2);
        view2.layout(dip2px(context,40),(height-dip2px(context,200))/2+dip2px(context,50f),
                width-dip2px(context,90),(height-dip2px(context,200))/2+dip2px(context,50.5f));
        view3.layout(dip2px(context,40),(height-dip2px(context,200))/2+dip2px(context,100f),
                width-dip2px(context,35),(height-dip2px(context,200))/2+dip2px(context,100.5f));
        view4.layout(dip2px(context,40),(height-dip2px(context,200))/2+dip2px(context,150f),
                width-dip2px(context,70),(height-dip2px(context,200))/2+dip2px(context,150.5f));
        view5.layout(dip2px(context,40),(height-dip2px(context,200))/2+dip2px(context,200f),
                width-dip2px(context,90),(height-dip2px(context,200))/2+dip2px(context,200.5f));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec,MeasureSpec.makeMeasureSpec(sectorBit.getHeight(),MeasureSpec.EXACTLY));
        width = sectorBit.getWidth();
        height = sectorBit.getHeight();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        listView = (ListView) getChildAt(0);
        view1 = getChildAt(1);
        view2 = getChildAt(2);
        view3 = getChildAt(3);
        view4 = getChildAt(4);
        view5 = getChildAt(5);
    }

    public static int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }
}
