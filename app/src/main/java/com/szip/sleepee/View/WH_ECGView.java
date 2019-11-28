package com.szip.sleepee.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.szip.sleepee.R;


/**
 * Created by wenh on 16/10/2511:09.
 */

public class WH_ECGView extends View {


    private int allData[];
    private float gap_grid;//网格间距
    private int width,height;//本页面宽，高
    private int grid_hori,grid_ver;//横、纵线条数
    private float gap_x;//两点间横坐标间距
    private float dataNum_per_grid = 10;//每小格内的数据个数
    private float y_center;//中心y值

    private int data_num;//总的数据个数

    private boolean flag = false;

    private Paint paint = new Paint();

    private Path path = new Path();
    private int pos = 0;
    private Canvas canvas;
    private int i;
    private int changeX;

    private boolean isHeart = false;


    public WH_ECGView(Context context, AttributeSet attrs){
        super(context,attrs);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getResources().getColor(R.color.MainColor));
        paint.setStrokeWidth(6.0f);

    }

    public WH_ECGView(Context context){
        super(context);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getResources().getColor(R.color.MainColor));
        paint.setStrokeWidth(6.0f);


    }

    public void setHeart(boolean heart) {
        isHeart = heart;
    }

    public void setColor(int color){
        paint.setColor(getResources().getColor(color));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed){
            gap_grid = 30.0f;
            width = getWidth();
            height = getHeight();
            grid_hori = height/(int)gap_grid;
            grid_ver = width/(int)gap_grid;
            y_center = (height)/2;
            gap_x = gap_grid/dataNum_per_grid;
            if (allData !=null)
                path.moveTo(0, getY_coordinate(allData[0]));
            Log.e("json","本页面宽： " + width +"  高:" + height);
        }

        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
        DrawECGWave(canvas);
//        invalidateMyView();
    }

    private void invalidateMyView() {
        if (flag){
            scrollBy(2,0);
            if (getScrollX()>=allData.length*gap_x){
                flag = false;
            }

        }

    }

//    /**
//     * 画心电图
//     */
//    public void DrawECGWave(Canvas canvas){
//        Paint paint = new Paint();
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setColor(getResources().getColor(R.color.red));
//        paint.setStrokeWidth(2.0f);
//        Path path = new Path();
//
//        path.moveTo(width, getY_coordinate(data_source.get(0)));
//        for (int i = 1; i < this.data_source.size(); i ++){
//                path.lineTo( gap_x * i+width, getY_coordinate(data_source.get(i)));
//        }
//        canvas.drawPath(path,paint);
//    }


    /**
     * 画心电图
     */
    public void DrawECGWave(Canvas canvas){

        for (i = pos; i < allData.length; i ++,pos = i){
            path.lineTo( gap_x * (i%(width/gap_x)), getY_coordinate(allData[i]));
            Log.d("View******","reset"+((width/gap_x)-(i%(width/gap_x))));
            if ((width/gap_x)-(i%(width/gap_x))<1||(width/gap_x)-(i%(width/gap_x))==1){
                path.reset();
                path.moveTo(0, getY_coordinate(allData[0]));
            }

        }
        canvas.drawPath(path,paint);

    }

    /**
     * 将数值转换为y坐标，中间大的显示心电图的区域
     */
    private float getY_coordinate(int data){
        data = (data - 1024) *(-1);
        float y_coor;
        if (isHeart){
            y_coor = (data*2+y_center);
            if (y_coor>480)
                y_coor = 480;
            else if (y_coor<120)
                y_coor = 120;
        }else {
            y_coor = (float) (data*0.35+y_center);
            if (y_coor>480)
                y_coor = 480;
            else if (y_coor<120)
                y_coor = 120;
        }
        return y_coor;
    }

    public void addData(int data[]){
        int sumData[];
        if (allData==null){
            sumData = new int[data.length];
            System.arraycopy(data,0,sumData,0,data.length);
        }else {
            sumData = new int[data.length+allData.length];
            System.arraycopy(allData,0,sumData,0,allData.length);
            System.arraycopy(data,0,sumData,allData.length,data.length);
        }
        allData = new int[sumData.length];
        allData = sumData;
        if (!flag){
            flag = true;
            postInvalidate();
        }
        postInvalidate();

    }

    public void setFlag(boolean flag){
        this.flag = flag;
    }

}
