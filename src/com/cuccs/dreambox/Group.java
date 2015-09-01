package com.cuccs.dreambox;

import java.util.ArrayList;

import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class Group extends ActivityGroup implements OnGestureListener{
	private View oneView;
	private View twoView;
	private ArrayList<View> views;
	private ViewPager mViewPager;	//多页面滑动切换效果
	GestureDetector mGestureDetector;      //处理手势

	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.groups);
		mGestureDetector = new GestureDetector((OnGestureListener) this); 
		
		
		//Animation a = AnimationUtils.loadAnimation(this, R.anim.translate_left_enter);
		//Animation b = AnimationUtils.loadAnimation(this, R.anim.translate_right_exit);
		views = new ArrayList<View>();
		mViewPager = (ViewPager)findViewById(R.id.loginviewpager);        
	    mViewPager.setOnPageChangeListener(null);
	    oneView=getViews(LoginActivity.class,"one");
		twoView=getViews(RegisterActivity.class,"two");
		//oneView.startAnimation(a);
		//twoView.startAnimation(b);
		
		views.add(oneView);
		views.add(twoView);
		

		PagerAdapter mPagerAdapter = new PagerAdapter() {
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}
			@Override
			public int getCount() {
				return views.size();
			}
			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager)container).removeView(views.get(position));
			}
			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager)container).addView(views.get(position));
				return views.get(position);
			}
		};
		mViewPager.setAdapter(mPagerAdapter);
		
	}
	
	
	public View getViews(Class<?> cls,String pageid){
		return getLocalActivityManager().startActivity(pageid, new Intent(Group.this,cls).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)).getDecorView();
	}


	
	
	@Override  /*当在activity中添加ScrollView实现滚动activity的效果后
	或外部容器为RelativeLayout时，activity的滑动效果却无法生效了，
	原因是因为activity没有处理滑动效果，解决方法如下：
	实现dispatchTouchEvent函数，
	在其实现中调用mGestureDetector.onTouchEvent(ev)，类似OnTouch。
	*******************************************************/
	public boolean dispatchTouchEvent(MotionEvent ev) {    
       mGestureDetector.onTouchEvent(ev);  
       return super.dispatchTouchEvent(ev);  
	}  
	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		int Windowwidth = wm.getDefaultDisplay().getWidth(); //屏幕宽度
		int Windowheight = wm.getDefaultDisplay().getHeight(); //屏幕高度
		Rect rect= new Rect();
		this.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);  
		int statusBarHeight = rect.top; //状态栏高度
		
		if (e2.getY() - e1.getY() > Windowheight/3 && Math.abs(velocityY) > 30) {      //判断为向下滑动
	    	// 切换到Homepage
	    	Intent intent = new Intent(Group.this, Homepage.class);  
	        startActivity(intent);
	        overridePendingTransition(R.anim.translate_up_enter, R.anim.holding_anima);
	        finish();
	    }  
		return false;
	}
	@Override
	public void onLongPress(MotionEvent e) {
	}
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}
	@Override
	public void onShowPress(MotionEvent e) {
		
	}
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
