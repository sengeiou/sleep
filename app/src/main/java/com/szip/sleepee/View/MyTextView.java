package com.szip.sleepee.View;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Administrator on 2018/12/26.
 */


public class MyTextView extends TextView {

    private int mRadius;

    private float mTextSize;

    /**
     * 控件圆心
     * */
    private float cancleX,cancleY;

    private float mDegrees;

    public void setCycleCancle(float cancleX,float cancleY){
        this.cancleX = cancleX;
        this.cancleY = cancleY;
    }

    public void setCycleCancle(int mRadius){
        this.mRadius = mRadius;
    }

    public MyTextView(Context context) {
        super(context);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTextSize = getTextSize();
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mDegrees = getAngle((left+((right-left)/2)),(top+((bottom-top)/2)));
        if (mDegrees>=-65&&mDegrees<=-25){
            setTextSize(mTextSize*(2f-(0.05f*Math.abs(mDegrees+45f))));
        }else {
            setTextSize(mTextSize);
        }
        invalidate();


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getCompoundPaddingLeft(), getExtendedPaddingTop());
        canvas.rotate(mDegrees, this.getWidth() / 2f, this.getHeight() / 2f);
        super.onDraw(canvas);
        canvas.restore();
    }

    /**
     * 根据触摸的位置，计算角度
     *
     * @param xTouch
     * @param yTouch
     * @return
     */
    private float getAngle(float xTouch, float yTouch)
    {
        double x = xTouch - (mRadius);
        double y = yTouch - (mRadius);
        if (x<0)
            return (float) (Math.asin(y / Math.hypot(x, y)) * -180 / Math.PI)-90;
        else
            return (float) (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI)+90;
    }

    public void setmTextSize(float radio){
        mTextSize = radio*mTextSize;
        setTextSize(mTextSize);
        invalidate();
    }
}
