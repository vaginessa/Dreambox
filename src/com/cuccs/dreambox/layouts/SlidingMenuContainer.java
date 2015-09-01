package com.cuccs.dreambox.layouts;

/** @author TimeTraveler
 * 功能描述：为侧滑菜单的实现准备，将所有的布局页面统一放在父容器中
 * 手指在屏幕上左右滑动时，该类的实例负责让其子View根据用户的手势左右偏移（滑动） 
 * 父容器采用ViewGroup 
 * From：	http://blog.csdn.net/android_ls/article/details/8761410 
 */

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;


public class SlidingMenuContainer extends ViewGroup {

    private static final String TAG = "ScrollerContainer";
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    /**
     * 手柄（手把）的宽度
     */
    private int mHandlebarWidth;

    /**
     * 在偏移过程中，动画持续的时间
     */
    private static final int ANIMATION_DURATION_TIME = 300;
    
    /**
     * 记录当前的滑动结束后的状态，左侧面板是否可见
     * true  向右滑动（左侧面板处于可见）
     * false 向左滑动（左侧面板处于不可见）
     */
    public boolean mPanelVisible;
    
    /**
     * 是否已滑动结束
     */
    private boolean mFinished;
    
    /**
     * 是否允许滚动
     * 满足的条件：
     *     左侧面板可见，当前手指按下的坐标x值 ，是在手柄宽度范围内；
     *     左侧面板不可见，当前手指按下的坐标x值 < 手柄宽度
     */
    private boolean mAllowScroll;
    
    /**
     * 是否满足响应单击事件的条件
     * 满足的条件：左侧面板可见，当前手指按下的坐标x值 ，是在手柄宽度范围内
     */
    private boolean isClick;
    
    public SlidingMenuContainer(Context context) {
        super(context);
        // this.setBackgroundResource(R.drawable.v5_3_0_guide_pic1);
        
        mScroller = new Scroller(context);
        mHandlebarWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 51, getResources().getDisplayMetrics());
    }
    
    // 测量自己及其子View的实际宽度和高度
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
    }
    
    /*
     * 为所有子View分配显示空间的大小 ，可能会与子View定义时设置的大小不一样。
     * 比如：父容器是LinearLayout，里面的子View排列方向是竖直方式，我向父容器中添加第一个子View（A），
     * 设置宽度为fill_parent，高度为50dip；我再向父容器中添加第二个子View（B），设置宽度为fill_parent，高度也为fill_parent。
     * 在这种情况下，子View（B）的高度为多少？ 是fill_parent吗？肯定不是啦，是父容器的高度减去子View（A）的高度。
     * （可能会与子View定义时设置的大小不一样）
     * @see android.view.ViewGroup#onLayout(boolean, int, int, int, int)
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
        }
    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.e(TAG, "dispatchTouchEvent()");
        
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            Log.i(TAG, "dispatchTouchEvent():  ACTION_DOWN");
            
            mFinished = mScroller.isFinished();
            if(mFinished){
                int x = (int) ev.getX();
                int width = getWidth();
                
                if(mPanelVisible)// 左侧面板可见
                {
                    if(x > (width - mHandlebarWidth)){ // 当前手指按下的坐标x值 ，是在手柄宽度范围内
                        isClick = true;
                        mAllowScroll = true;
                        return true;
                    } else {
                        isClick = false;
                        mAllowScroll = false;
                    }
                } else { // 左侧面板不可见
                    if(x < mHandlebarWidth ){ // 当前手指按下的坐标x值 < 手柄宽度 （也就是说在手柄宽度范围内，是可以相应用户的向右滑动手势）
                        mAllowScroll = true;
                    }else{
                        mAllowScroll = false;
                    }
                }
                
            } else {
                // 当前正在滚动子View，其它的事不响应
                return false;
            }
            
            break;

        case MotionEvent.ACTION_MOVE:
            Log.i(TAG, "dispatchTouchEvent():  ACTION_MOVE");
            int margin = getWidth() - (int) ev.getX();
            if (margin < mHandlebarWidth && mAllowScroll) {
            	
                Log.e(TAG, "dispatchTouchEvent ACTION_MOVE margin = " + margin + "\t mHandlebarWidth = " + mHandlebarWidth);
                return true;
            }
            
            break;
        case MotionEvent.ACTION_UP:
            Log.i(TAG, "dispatchTouchEvent():  ACTION_UP");
            
            if (isClick && mPanelVisible && mAllowScroll) {
                isClick = false;
                mPanelVisible = false;
                
                int scrollX = getChildAt(1).getScrollX();
                mScroller.startScroll(scrollX, 0, -scrollX, 0, ANIMATION_DURATION_TIME);
                invalidate();
                
                return true;
            }
            
            break;
        case MotionEvent.ACTION_CANCEL:
            Log.i(TAG, "dispatchTouchEvent():  ACTION_CANCEL");
            break;
        default:
            break;
        }
        
        return super.dispatchTouchEvent(ev);
    }
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.e(TAG, "onInterceptTouchEvent()");
        
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            Log.i(TAG, "onInterceptTouchEvent():  ACTION_DOWN");
            mFinished = mScroller.isFinished();
            if(!mFinished){
                return false;
            }
            
            break;
        case MotionEvent.ACTION_MOVE:
            Log.i(TAG, "onInterceptTouchEvent():  ACTION_MOVE");
            
            mVelocityTracker = VelocityTracker.obtain();
            mVelocityTracker.addMovement(ev);
            
            // 一秒时间内移动了多少个像素
            mVelocityTracker.computeCurrentVelocity(1000, ViewConfiguration.getMaximumFlingVelocity());
            float velocityValue = Math.abs(mVelocityTracker.getXVelocity()) ;
            Log.d(TAG, "onInterceptTouchEvent():  mVelocityValue = " + velocityValue);
            
            if (velocityValue > 300 && mAllowScroll) {
                return true;
            }
            
            break;
        case MotionEvent.ACTION_UP:
            Log.i(TAG, "onInterceptTouchEvent():  ACTION_UP");
            
            if (mVelocityTracker != null) {
                mVelocityTracker.recycle();
                mVelocityTracker = null;
            }
            
            break;
        case MotionEvent.ACTION_CANCEL:
            Log.i(TAG, "onInterceptTouchEvent():  ACTION_CANCEL");
            break;
        default:
            break;
        }
        
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e(TAG, "onTouchEvent()");

        float x = event.getX();
        
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            Log.i(TAG, "onTouchEvent():  ACTION_DOWN");
            mFinished = mScroller.isFinished();
            if(!mFinished){
                return false;
            }
            break;

        case MotionEvent.ACTION_MOVE:
            Log.i(TAG, "onTouchEvent():  ACTION_MOVE");
            getChildAt(1).scrollTo(-(int)x, 0);
            break;

        case MotionEvent.ACTION_UP:
            Log.i(TAG, "onTouchEvent():  ACTION_UP");
            
            if(!mAllowScroll){
                break;
            }
            
           float width = getWidth();
           // 响应滚动子View的临界值，若觉得响应过于灵敏，可以将只改大些。
           // 比如：criticalWidth = width / 3或criticalWidth = width / 2，看情况而定，呵呵。
           float criticalWidth = width / 5;
           
           Log.i(TAG, "onTouchEvent():  ACTION_UP x = " + x + "\t criticalWidth = " + criticalWidth);
           
           int scrollX = getChildAt(1).getScrollX();
           
           if ( x < criticalWidth) {
               Log.i(TAG, "onTouchEvent():  ACTION_UP 向左滑动");
               
                mPanelVisible = false;
               
                mScroller.startScroll(scrollX, 0, -scrollX, 0, ANIMATION_DURATION_TIME);
                invalidate();
            } else if ( x > criticalWidth){
                Log.i(TAG, "onTouchEvent():  ACTION_UP 向右滑动");
              
                mPanelVisible = true;
                
                int toX = (int)(width - Math.abs(scrollX) - mHandlebarWidth);
                mScroller.startScroll(scrollX, 0, -toX, 0, ANIMATION_DURATION_TIME);
                invalidate();
            }
            
            break;
        case MotionEvent.ACTION_CANCEL:
            Log.i(TAG, "onTouchEvent():  ACTION_CANCEL");
            break;
        default:
            break;
        }
        
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        // super.computeScroll();
        
        if(mScroller.computeScrollOffset()){
            this.getChildAt(1).scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            this.postInvalidate();
        }
    }

    /**
     * 向右滑动View，让左侧操作面饭可见
     */
    public void slideToRight() {
        mFinished = mScroller.isFinished();
        if(mFinished && !mPanelVisible){
            mPanelVisible = true;
            
            float width = getWidth();
            int scrollX = getChildAt(1).getScrollX();
            int toX = (int)(width - Math.abs(scrollX) - mHandlebarWidth);
            
            mScroller.startScroll(scrollX, 0, -toX, 0, ANIMATION_DURATION_TIME);
            invalidate();
        }
    }
    

    //View滑动事件监听器
    public interface OnSlideListener {
        /**
         * 向左滑动子View
         */
        public abstract void toLeft();
        
        /**
         * 向右滑动子View
         */
        public abstract void toRight();
        /** 
         * 快速切换View
         */
        public abstract void toShow(View view);
    }
    
    /** 
     * 切换视图 
     * @param view 
     */  
    public void show(View view) {  
        mPanelVisible = false;  
          
        int scrollX = getChildAt(1).getScrollX();  
        mScroller.startScroll(scrollX, 0, -scrollX, 0, ANIMATION_DURATION_TIME);  
        invalidate();  
        
        removeViewAt(1);  
        addView(view, 1, getLayoutParams());  
    }
}
