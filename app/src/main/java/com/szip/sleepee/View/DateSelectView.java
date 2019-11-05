package com.szip.sleepee.View;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.szip.sleepee.R;

import java.util.Timer;

/**
 * Created by Administrator on 2019/4/15.
 */

public class DateSelectView extends RelativeLayout {
    private Context context;
    private ImageView circle1;
    private ImageView circle2;
    private ImageView circle3;
    private ImageView circle4;
    private DateSelectListener selectListener;

    private RotateAnimation rotateLeft  = new RotateAnimation(-90f, 0f, Animation.RELATIVE_TO_SELF,
            1f, Animation.RELATIVE_TO_SELF, 0f);

    private RotateAnimation rotateLeft1  = new RotateAnimation(-90f, 0f, Animation.RELATIVE_TO_SELF,
            1f, Animation.RELATIVE_TO_SELF, 0f);

    private RotateAnimation rotateRight  = new RotateAnimation(90f, 0f, Animation.RELATIVE_TO_SELF,
            1f, Animation.RELATIVE_TO_SELF, 0f);

    private RotateAnimation rotateRight1  = new RotateAnimation(90f, 0f, Animation.RELATIVE_TO_SELF,
            1f, Animation.RELATIVE_TO_SELF, 0f);

    private RotateAnimation rotateLeftGone  = new RotateAnimation(0f, -90f, Animation.RELATIVE_TO_SELF,
            1f, Animation.RELATIVE_TO_SELF, 0f);

    private RotateAnimation rotateLeftGone1  = new RotateAnimation(0f, -90f, Animation.RELATIVE_TO_SELF,
            1f, Animation.RELATIVE_TO_SELF, 0f);

    private RotateAnimation rotateRightGone  = new RotateAnimation(0f, 90f, Animation.RELATIVE_TO_SELF,
            1f, Animation.RELATIVE_TO_SELF, 0f);

    private RotateAnimation rotateRightGone1  = new RotateAnimation(0f, 90f, Animation.RELATIVE_TO_SELF,
            1f, Animation.RELATIVE_TO_SELF, 0f);

    private CircleMenuLayout circleMenuLayoutYear;

    private String[] mItemTexts1 = new String[] {"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23" };;

    public DateSelectView(Context context) {
        super(context);
        this.context = context;
        initView();
        intiAnimation();
    }

    public DateSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
        intiAnimation();
    }

    public DateSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
        intiAnimation();
    }

    private void intiAnimation() {
        rotateLeft.setDuration(1000);//设置动画持续时间
        rotateLeft.setRepeatCount(0);//设置重复次数

        rotateLeft1.setDuration(1000);//设置动画持续时间
        rotateLeft1.setRepeatCount(0);//设置重复次数

        rotateRight.setDuration(1000);//设置动画持续时间
        rotateRight.setRepeatCount(0);//设置重复次数

        rotateRight1.setDuration(1000);//设置动画持续时间
        rotateRight1.setRepeatCount(0);//设置重复次数


        rotateLeftGone.setDuration(1000);//设置动画持续时间
        rotateLeftGone.setRepeatCount(0);//设置重复次数

        rotateLeftGone1.setDuration(1000);//设置动画持续时间
        rotateLeftGone1.setRepeatCount(0);//设置重复次数

        rotateRightGone.setDuration(1000);//设置动画持续时间
        rotateRightGone.setRepeatCount(0);//设置重复次数

        rotateRightGone1.setDuration(1000);//设置动画持续时间
        rotateRightGone1.setRepeatCount(0);//设置重复次数

    }

    private void initView() {
        LayoutInflater.from(context).inflate(R.layout.date_select_view,this);
        circle1 = findViewById(R.id.image1);
        circle2 = findViewById(R.id.image2);
        circle3 = findViewById(R.id.image3);
        circle4 = findViewById(R.id.image4);

        circle4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectListener!=null){
                    selectListener.onTouchOk();
                }
            }
        });

        circleMenuLayoutYear = findViewById(R.id.id_menulayout1);
        circleMenuLayoutYear.setMenuItemIconsAndTexts(mItemTexts1,0);
    }

    public void startAnimotion(boolean visible){
        if (visible){
            circle1.startAnimation(rotateLeft);
            circle2.startAnimation(rotateRight);
            circle3.startAnimation(rotateLeft1);
            circle4.startAnimation(rotateRight1);
            circle1.setVisibility(VISIBLE);
            circle2.setVisibility(VISIBLE);
            circle3.setVisibility(VISIBLE);
            circle4.setVisibility(VISIBLE);
            circleMenuLayoutYear.setVisibility(VISIBLE);
        }else {
            circle1.startAnimation(rotateLeftGone);
            circle2.startAnimation(rotateRightGone);
            circle3.startAnimation(rotateLeftGone1);
            circle4.startAnimation(rotateRightGone1);
            circle1.setVisibility(GONE);
            circle2.setVisibility(GONE);
            circle3.setVisibility(GONE);
            circle4.setVisibility(GONE);
            circleMenuLayoutYear.setVisibility(GONE);
        }
    }

    public void setSelectListener(DateSelectListener selectListener){
        this.selectListener = selectListener;
    }

    public interface DateSelectListener{
        void onTouchOk();
    }


}
