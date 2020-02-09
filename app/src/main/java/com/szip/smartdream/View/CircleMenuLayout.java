package com.szip.smartdream.View;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.szip.smartdream.R;


/**
 * <pre>
 * @author zhy 
 * http://blog.csdn.net/lmj623565791/article/details/43131133
 * </pre>
 */
public class CircleMenuLayout extends ViewGroup
{

	private boolean isSub = false;
	private boolean isNeedTab = false;
	/**
	 * 选项类型，0 = 年，1 = 月，2 = 日，3 = 时，4 = 分
	 * */
	private int STYLE_TAG = 0;

	private int mRadius;
	/**
	 * 该容器内child item的默认尺寸
	 */
	private static final float RADIO_DEFAULT_CHILD_DIMENSION = 1 / 4f;
	/**
	 * 菜单的中心child的默认尺寸
	 */
	private float RADIO_DEFAULT_CENTERITEM_DIMENSION = 1 / 3f;
	/**
	 * 该容器的内边距,无视padding属性，如需边距请用该变量
	 */
	private static final float RADIO_PADDING_LAYOUT = 1 / 12f;

	/**
	 * 当每秒移动角度达到该值时，认为是快速移动
	 */
	private static final int FLINGABLE_VALUE = 300;

	/**
	 * 如果移动角度达到该值，则屏蔽点击
	 */
	private static final int NOCLICK_VALUE = 3;

	/**
	 * 当每秒移动角度达到该值时，认为是快速移动
	 */
	private int mFlingableValue = FLINGABLE_VALUE;
	/**
	 * 该容器的内边距,无视padding属性，如需边距请用该变量
	 */
	private float mPadding;
	/**
	 * 布局时的开始角度
	 */
	private double mStartAngle = 225;
	private double oldFirstAngle=225;
	private double firstAngle=225;
	private double allAngle = 0;
	/**
	 * 菜单项的文本
	 */
	private String[] mItemTexts;

	/**
	 * 菜单的个数
	 */
	private int mMenuItemCount;

	/**
	 * 检测按下到抬起时旋转的角度
	 */
	private float mTmpAngle;
	/**
	 * 检测按下到抬起时使用的时间
	 */
	private long mDownTime;

	/**
	 * 判断是否正在自动滚动
	 */
	private boolean isFling;

	private float cancleX,cancleY;

	private int mMenuItemLayoutId = R.layout.circle_menu_item;

	public CircleMenuLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// 无视padding
		setPadding(0, 0, 0, 0);
	}

	/**
	 * 设置布局的宽高，并策略menu item宽高
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int resWidth = 0;
		int resHeight = 0;

		/**
		 * 根据传入的参数，分别获取测量模式和测量值
		 */
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);

		int height = MeasureSpec.getSize(heightMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);

		/**
		 * 如果宽或者高的测量模式非精确值
		 */
		if (widthMode != MeasureSpec.EXACTLY
				|| heightMode != MeasureSpec.EXACTLY)
		{
			// 主要设置为背景图的高度
			resWidth = getSuggestedMinimumWidth();
			// 如果未设置背景图片，则设置为屏幕宽高的默认值
			resWidth = resWidth == 0 ? getDefaultWidth() : resWidth;

			resHeight = getSuggestedMinimumHeight();
			// 如果未设置背景图片，则设置为屏幕宽高的默认值
			resHeight = resHeight == 0 ? getDefaultWidth() : resHeight;
		} else
		{
			// 如果都设置为精确值，则直接取小值；
			resWidth = resHeight = Math.min(width, height);
		}

		setMeasuredDimension(resWidth, resHeight);

		// 获得半径
		mRadius = Math.max(getMeasuredWidth(), getMeasuredHeight());

		// menu item数量
		final int count = getChildCount();
		// menu item尺寸
		int childSize = (int) (mRadius * RADIO_DEFAULT_CHILD_DIMENSION);
		// menu item测量模式
		int childMode = MeasureSpec.EXACTLY;

		// 迭代测量
		for (int i = 0; i < count; i++)
		{
			final View child = getChildAt(i);

			if (child.getVisibility() == GONE)
			{
				continue;
			}

			// 计算menu item的尺寸；以及和设置好的模式，去对item进行测量
			int makeMeasureSpec = -1;

			if (child.getId() == R.id.id_circle_menu_item_center)
			{
				makeMeasureSpec = MeasureSpec.makeMeasureSpec(
						(int) (mRadius * RADIO_DEFAULT_CENTERITEM_DIMENSION),
						childMode);
			} else
			{
				makeMeasureSpec = MeasureSpec.makeMeasureSpec(childSize,
						childMode);
			}
			child.measure(makeMeasureSpec, makeMeasureSpec);
		}

		cancleX = getLeft()+getWidth();
		cancleY = getTop()+getHeight();
		mPadding = RADIO_PADDING_LAYOUT * mRadius;

	}

	/**
	 * MenuItem的点击事件接口
	 * 
	 * @author zhy
	 * 
	 */
	public interface OnMenuItemClickListener
	{
		void itemClick(View view, int pos);

		void itemCenterClick(View view);
	}

	/**
	 * MenuItem的点击事件接口
	 */
	private OnMenuItemClickListener mOnMenuItemClickListener;

	/**
	 * 设置MenuItem的点击事件接口
	 * 
	 * @param mOnMenuItemClickListener
	 */
	public void setOnMenuItemClickListener(
			OnMenuItemClickListener mOnMenuItemClickListener)
	{
		this.mOnMenuItemClickListener = mOnMenuItemClickListener;
	}

	/**
	 * 设置menu item的位置
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{

		int layoutRadius = mRadius;

		// Laying out the child views
		final int childCount = getChildCount();

		int left, top;
		// menu item 的尺寸
		int cWidth = (int) (layoutRadius * RADIO_DEFAULT_CHILD_DIMENSION);

		// 根据menu item的个数，计算角度
		float angleDelay;
		if (STYLE_TAG==0||STYLE_TAG==2||STYLE_TAG==3||STYLE_TAG==4){
			angleDelay = 360f / 24f;
			allAngle = (getChildCount()-1)*15;
//			if (Math.abs(firstAngle)>allAngle)
				firstAngle %=allAngle;

		}
		else
			angleDelay = 270f / (float)(getChildCount() - 1);

			if ((childCount>25&&allAngle!=0&&((int)firstAngle/15-(int)oldFirstAngle/15)!=0)||firstAngle*oldFirstAngle<0){
				getVisiableValue();//计算显示的列表
			}


		// 遍历去设置menuitem的位置
		int startPos = getStartPoint();

		int index = 1;
		boolean first = true;
		while (index<childCount){
			final View child = getChildAt(startPos);
			if (child.getVisibility() == GONE)
			{
				startPos++;
				if (startPos>childCount-1)
					startPos = 1;
				index++;
				continue;
			}

			float tmp = 0;
			if (STYLE_TAG==0||STYLE_TAG==2||STYLE_TAG==3||STYLE_TAG==4) {
				mStartAngle %= 360;
				if (STYLE_TAG==0)
					tmp = layoutRadius  - cWidth / 2 - mPadding;
				else if (STYLE_TAG==2)
					tmp = (layoutRadius  - cWidth / 2 - mPadding)*0.5f;
				else if (STYLE_TAG==3)
					tmp = (layoutRadius  - cWidth / 2 - mPadding)*0.75f;
				else if (STYLE_TAG==4){
					tmp = (layoutRadius  - cWidth / 2 - mPadding)*0.5f;
				}
			} else{
				mStartAngle %= 270;
				// 计算，中心点到menu item中心的距离
				tmp = (layoutRadius  - cWidth - mPadding)*0.75f;
			}



			// tmp cosa 即menu item中心点的横坐标
			left = layoutRadius
					+ (int) Math.round(tmp
					* Math.cos(Math.toRadians(mStartAngle)) - 1 / 2f
					* cWidth);
			// tmp sina 即menu item的纵坐标
			top = layoutRadius
					+ (int) Math.round(tmp
					* Math.sin(Math.toRadians(mStartAngle)) - 1 / 2f
					* cWidth);
			((MyTextView)child).setCycleCancle(mRadius);
			if (mStartAngle>=205&&mStartAngle<=245){
				int offset = 120-(int)(120*(0.05*(Math.abs(mStartAngle-225d))));
				child.layout(left-offset, top-offset, left-offset + cWidth, top-offset + cWidth);
			}else {
				child.layout(left, top, left + cWidth, top + cWidth);
			}
			mStartAngle += angleDelay;
			startPos++;
			if (startPos>60)
				startPos = 1;
			index++;

			if (first){
				((MyTextView) child).setTextColor(Color.parseColor("#335846"));
				first = false;
			}
		}

	}


	/**
	 * 如果选项大于24，则根据旋转的角度部分隐藏部分显示
	 * */
	private void getVisiableValue(){
		int a = 1,b = 1;
		int startGonePoint;//开始隐藏View的起点
		int startVisiablePoint;//开始显示View的起点
		startGonePoint = 25-((int)firstAngle/15);
		if (startGonePoint<=0)
			startGonePoint = startGonePoint-1+getChildCount();
		else if (startGonePoint>=61)
			startGonePoint %= 60;

		startVisiablePoint = startGonePoint-24;
		if (startVisiablePoint<=0)
			startVisiablePoint = startVisiablePoint-1+getChildCount();
		else if (startVisiablePoint>=61)
			startVisiablePoint %= 60;

		while (a<=(getChildCount()-25)){//开始隐藏
			getChildAt(startGonePoint).setVisibility(GONE);
			startGonePoint++;
			if (startGonePoint>60)
				startGonePoint = 1;
			a++;
		}
		while (b<=24){//开始显示
			getChildAt(startVisiablePoint).setVisibility(VISIBLE);
			startVisiablePoint++;
			if (startVisiablePoint>60)
				startVisiablePoint = 1;
			b++;
		}

//		isNeedTab = needToTab();
//		Log.d("Angle******",isNeedTab?"缩进":"免缩进");
	}

	/**
	 * 计算画圆的起始点
	 * */
	private int getStartPoint(){
		int start = 1;
		while (true){
			if (start>getChildCount())
				start = 1;
			View view = getChildAt(start);
			if (view.getVisibility() == VISIBLE)
				break;
			start++;
		}
		int tab = start-1;//需要缩进的个数
		if (tab == 0)
			return 1;
		else {
			start = getChildCount()-1;
			while (true){
				View view = getChildAt(start);
				if (view.getVisibility()==VISIBLE){
					tab--;
					if (tab == 0)
						break;
				}
				start--;
				if (start<=1)
					start = start-2+getChildCount();
			}
			return start;
		}
	}

	/**
	 * 判断是否需要缩进一个排序
	 * */
	private boolean needToTab(){
		if((int)firstAngle/15==0){
			return false;
		}else{
			if (isSub){
				isSub = subAll();
				if (isSub)
					return true;
				else
					return false;
			}else{
				isSub = subAll();
				return false;
			}
		}
	}

	/**
	 * 判断是否刚好相差23
	 * */
	private boolean subAll(){
		boolean first = true;
		int start=0,end=0;
		for (int i=1;i<getChildCount();i++){
			if (getChildAt(i).getVisibility()==GONE)
				continue;
			if (first){
				start = i;
				first = false;
			}
			end = i;
		}
		Log.d("Angle******","start="+start+";end="+end);
		if ((end-start)==23)
			return true;
		else
			return false;

	}

	/**
	 * 记录上一次的x，y坐标
	 */
	private float mLastX;
	private float mLastY;

	/**
	 * 自动滚动的Runnable
	 */
	private AutoFlingRunnable mFlingRunnable;

	@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
		float x = event.getX();
		float y = event.getY();

		// Log.e("TAG", "x = " + x + " , y = " + y);

		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:

			mLastX = x;
			mLastY = y;
			mDownTime = System.currentTimeMillis();
			mTmpAngle = 0;

			// 如果当前已经在快速滚动
			if (isFling)
			{
				// 移除快速滚动的回调
				removeCallbacks(mFlingRunnable);
				isFling = false;
				return true;
			}

			break;
		case MotionEvent.ACTION_MOVE:

			/**
			 * 获得开始的角度
			 */
			float start = getAngle(mLastX, mLastY);
			/**
			 * 获得当前的角度
			 */
			float end = getAngle(x, y);

			// Log.e("TAG", "start = " + start + " , end =" + end);
			// 如果是一、四象限，则直接end-start，角度值都是正值
//			if (getQuadrant(x, y) == 1 || getQuadrant(x, y) == 3)
//			{
				{
					mStartAngle += start - end;
					oldFirstAngle = firstAngle;
					firstAngle += start -end;
//					Log.d("Angle******","oldAngle = "+oldFirstAngle);
//					Log.d("Angle******","angle = "+firstAngle);
					mTmpAngle += start - end;
				}
//			} else
			// 二、三象限，色角度值是付值
//			{
//				mStartAngle += end - start;
//				mTmpAngle +=  end - start;
//			}
			// 重新布局
			requestLayout();

			mLastX = x;
			mLastY = y;

			break;
		case MotionEvent.ACTION_UP:

			// 计算，每秒移动的角度
			float anglePerSecond = mTmpAngle * 1000
					/ (System.currentTimeMillis() - mDownTime);

			// Log.e("TAG", anglePrMillionSecond + " , mTmpAngel = " +
			// mTmpAngle);

			// 如果达到该值认为是快速移动
			if (Math.abs(anglePerSecond) > mFlingableValue && !isFling)
			{
				// post一个任务，去自动滚动
				post(mFlingRunnable = new AutoFlingRunnable(anglePerSecond));

				return true;
			}

			// 如果当前旋转角度超过NOCLICK_VALUE屏蔽点击
			if (Math.abs(mTmpAngle) > NOCLICK_VALUE)
			{
				return true;
			}

			break;
		}
		return super.dispatchTouchEvent(event);
	}

	/**
	 * 主要为了action_down时，返回true
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		return true;
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
		return (float) (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
	}

//	/**
//	 * 根据当前位置计算象限
//	 *
//	 * @param x
//	 * @param y
//	 * @return
//	 */
//	private int getQuadrant(float x, float y)
//	{
//		int tmpX = (int) (x - mRadius / 2);
//		int tmpY = (int) (y - mRadius / 2);
//		if (tmpX >= 0)
//		{
//			return tmpY >= 0 ? 4 : 1;
//		} else
//		{
//			return tmpY >= 0 ? 3 : 2;
//		}
//
//	}

	/**
	 * 设置菜单条目的图标和文本
	 */
	public void setMenuItemIconsAndTexts(String[] texts,int tag)
	{
		this.STYLE_TAG = tag;

		mItemTexts = texts;

		// 初始化mMenuCount
		mMenuItemCount = texts.length ;

		if ( texts != null)
		{
			mMenuItemCount =  texts.length;
		}

		addMenuItems();

	}

	/**
	 * 设置MenuItem的布局文件，必须在setMenuItemIconsAndTexts之前调用
	 * 
	 * @param mMenuItemLayoutId
	 */
	public void setMenuItemLayoutId(int mMenuItemLayoutId)
	{
		this.mMenuItemLayoutId = mMenuItemLayoutId;
	}

	/**
	 * 添加菜单项
	 */
	private void addMenuItems()
	{
		LayoutInflater mInflater = LayoutInflater.from(getContext());

		/**
		 * 根据用户设置的参数，初始化view
		 */
		for (int i = 0; i < mMenuItemCount; i++)
		{
			final int j = i;
			View view = mInflater.inflate(mMenuItemLayoutId, this, false);
			((MyTextView)view).setText(mItemTexts[i]);
			if (STYLE_TAG==2||STYLE_TAG==4)
				((MyTextView)view).setmTextSize(0.5f);
			else if(STYLE_TAG==1||STYLE_TAG==3)
				((MyTextView)view).setmTextSize(0.75f);
			// 添加view到容器中
			addView(view);
		}
		if (getChildCount()>25){
			getVisiableValue();//计算显示的列表
		}
	}

	/**
	 * 如果每秒旋转角度到达该值，则认为是自动滚动
	 * 
	 * @param mFlingableValue
	 */
	public void setFlingableValue(int mFlingableValue)
	{
		this.mFlingableValue = mFlingableValue;
	}

	/**
	 * 设置内边距的比例
	 * 
	 * @param mPadding
	 */
	public void setPadding(float mPadding)
	{
		this.mPadding = mPadding;
	}

	/**
	 * 获得默认该layout的尺寸
	 * 
	 * @return
	 */
	private int getDefaultWidth()
	{
		WindowManager wm = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return Math.min(outMetrics.widthPixels, outMetrics.heightPixels);
	}

	/**
	 * 自动滚动的任务
	 * 
	 * @author zhy
	 * 
	 */
	private class AutoFlingRunnable implements Runnable
	{

		private float angelPerSecond;

		public AutoFlingRunnable(float velocity)
		{
			this.angelPerSecond = velocity;
		}

		public void run()
		{
			// 如果小于20,则停止
			if ((int) Math.abs(angelPerSecond) < 20)
			{
				isFling = false;
				return;
			}
			isFling = true;
			// 不断改变mStartAngle，让其滚动，/30为了避免滚动太快
			mStartAngle += (angelPerSecond / 30);
			// 逐渐减小这个值
			angelPerSecond /= 1.0666F;
			postDelayed(this, 30);
			// 重新布局
			requestLayout();
		}
	}

}
