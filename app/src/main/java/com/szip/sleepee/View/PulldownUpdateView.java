package com.szip.sleepee.View;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by PettySion on 2018/9/3.
 */

public class PulldownUpdateView extends RelativeLayout {
    private View mAutoComeBackView;
    private View view1;
    private View view2;
    private Point mAutoBackOriginPos = new Point();
    private ViewDragHelper mDragHelper;
    private PulldownListener listener;

    public PulldownUpdateView(Context context) {
        super(context);
    }

    public PulldownUpdateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return child == mAutoComeBackView;
            }



            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                    final int leftBound = child.getLeft();
                    final int rightBound = getWidth() - mAutoComeBackView.getWidth() - getPaddingLeft();
                    final int newleft = Math.min(Math.max(left,leftBound),rightBound);
                    return leftBound;

            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                final int bottom = child.getBottom();
                final int newTop = Math.min(top, bottom);
                return newTop;
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);
                if (releasedChild == mAutoComeBackView){
                    mDragHelper.settleCapturedViewAt(mAutoBackOriginPos.x,mAutoBackOriginPos.y);
                    invalidate();
                    Log.d("SZIP*******","滑动到这里取消"+mAutoComeBackView.getTop());
                    if (mAutoComeBackView.getTop()<340)
                        if (listener!=null){
                            listener.updateNow();
                        }
                }
            }
        });
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragHelper.continueSettling(true)){
            invalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mAutoBackOriginPos.x = mAutoComeBackView.getLeft();
        mAutoBackOriginPos.y = mAutoComeBackView.getTop();
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }

    public void setListener(PulldownListener listener){
        this.listener = listener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mAutoComeBackView = getChildAt(0);
        view1 = getChildAt(1);
        view2 = getChildAt(2);
    }

    public interface PulldownListener{
        void updateNow();
    }
}
