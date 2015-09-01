package com.cuccs.dreambox;

import com.cuccs.dreambox.layouts.SlideSwitch;
import com.cuccs.dreambox.layouts.SlideSwitch.OnCheckedChangeListener;
import com.cuccs.dreambox.objects.ShakeListener;
import com.cuccs.dreambox.objects.ShakeListener.OnShakeListener;
import com.cuccs.dreambox.utils.AppAccountInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class Homepage extends Activity implements OnGestureListener{
	Context mContext;
	ShakeListener mShakeListener = null;	//摇一摇进入应用
	ImageView BigImgView;
	GestureDetector mGestureDetector;      //处理手势
	MediaPlayer mMediaPlayer;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homepage);
		mContext = this.getApplicationContext();
		mGestureDetector = new GestureDetector((OnGestureListener) this); 
		mMediaPlayer = MediaPlayer.create(this, R.raw.done);
		
		final SlideSwitch vSlideSwitch = (SlideSwitch)findViewById(R.id.Homepage_SlideSwitch);
		BigImgView = (ImageView)findViewById(R.id.BigImg);
		Button Btn_justEnter = (Button)findViewById(R.id.login_JsLook);
		Button Btn_login = (Button)findViewById(R.id.login_Gen);
		
		String imgpath1 = "/mnt/sdcard/Dreambox_Homepage/01.jpg";
		String imgpath2 = "/mnt/sdcard/Dreambox_Homepage/02.jpg";
		String imgpath3 = "/mnt/sdcard/Dreambox_Homepage/03.jpg";
		String imgpath4 = "/mnt/sdcard/Dreambox_Homepage/04.jpg";
		String imgpath5= "/mnt/sdcard/Dreambox_Homepage/05.jpg";
		Bitmap bm;
		int random = (int)(Math.random()*10);
//		if(random>=0&&random<=1){
//			bm = BitmapFactory.decodeFile(imgpath1);
//		}else if(random>=2&&random<=3){
//			bm = BitmapFactory.decodeFile(imgpath2);
//		}
//		else if(random>=4&&random<=5){
//			bm = BitmapFactory.decodeFile(imgpath3);
//		}
//		else if(random>=6&&random<=7){
//			bm = BitmapFactory.decodeFile(imgpath4);
//		}
//		else if(random>=8&&random<=9){
//			bm = BitmapFactory.decodeFile(imgpath5);
//		}
//		else{
//			bm = BitmapFactory.decodeFile(imgpath3);
//		}
		if(random>=0&&random<=1){
			BigImgView.setImageResource(R.drawable.a_02);
		}else if(random>=2&&random<=3){
			BigImgView.setImageResource(R.drawable.a_03);
		}
		else if(random>=4&&random<=5){
			BigImgView.setImageResource(R.drawable.a_04);
		}
		else if(random>=6&&random<=7){
			BigImgView.setImageResource(R.drawable.a_05);
		}
		else if(random>=8&&random<=9){
			BigImgView.setImageResource(R.drawable.a_06);
		}
		else{
			BigImgView.setImageResource(R.drawable.a_06);
		}
		//BigImgView.setImageBitmap(bm);
		//BigImgView.setImageResource(R.drawable.a_03);
		
		if(AppAccountInfo.getisLoginSuccess(mContext) == true){
			vSlideSwitch.setVisibility(View.VISIBLE);
			Btn_justEnter.setVisibility(View.GONE);
			Btn_login.setVisibility(View.GONE);
		}
		
		Btn_justEnter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//mMediaPlayer.start();
				Intent intent = new Intent(Homepage.this, ContentActivity.class);  
		        startActivity(intent);
		        overridePendingTransition(R.anim.holding_anima, R.anim.translate_up_exit);    //设定新的Activity进入和当前Activity退出时的动画。
		        finish();
			}
		});
		
		Btn_login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Homepage.this, LoginActivity.class);  
		        startActivity(intent);
		        overridePendingTransition(R.anim.holding_anima, R.anim.translate_left_exit);       //设定新的Activity进入和当前Activity退出时的动画。
		        finish();
			}
		});
		
		
		//获得动画，并开始转动
		final ImageView imgView_getup_arrow = (ImageView)findViewById(R.id.getup_arrow);
		AnimationDrawable animArrowDrawable = (AnimationDrawable) imgView_getup_arrow.getBackground() ;
		animArrowDrawable.start();
		
		vSlideSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(SlideSwitch view, boolean bechecked) {
				if( bechecked == true) {
					imgView_getup_arrow.setVisibility(View.GONE);
					Vibrator mVibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
					mVibrator.vibrate( new long[]{100,50,0,0}, -1);	 //开始 震动
					Intent intent = new Intent(Homepage.this, ContentActivity.class);  
			        startActivity(intent);
			        overridePendingTransition(R.anim.holding_anima, R.anim.translate_up_exit);    //设定新的Activity进入和当前Activity退出时的动画。
			        finish();
				} 
			}
        });
		vSlideSwitch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				imgView_getup_arrow.setVisibility(View.GONE);
				vSlideSwitch.playAnimation();
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(350);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Vibrator mVibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
						mVibrator.vibrate( new long[]{0,50,0,0}, -1);	 //开始 震动
						Intent intent = new Intent(Homepage.this, ContentActivity.class);  
				        startActivity(intent);
				        overridePendingTransition(R.anim.holding_anima, R.anim.translate_up_exit);    //设定新的Activity进入和当前Activity退出时的动画。
				        finish();
					}
				}).start();
			}
		});
		
		mShakeListener = new ShakeListener(this);
		mShakeListener.setOnShakeListener(new OnShakeListener(){
			public void onShake() {
				mShakeListener.stop();
				Vibrator mVibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
				mVibrator.vibrate( new long[]{0,50,0,0}, -1);	 //开始 震动
				imgView_getup_arrow.setVisibility(View.GONE);
				vSlideSwitch.playAnimation();
				new Handler().post(new Runnable(){
					@Override
					public void run(){
						//mShakeListener.start();
						try {
							Thread.sleep(350);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Intent intent = new Intent(Homepage.this, ContentActivity.class);  
				        startActivity(intent);
				        overridePendingTransition(R.anim.holding_anima, R.anim.translate_up_exit);    //设定新的Activity进入和当前Activity退出时的动画。
				        finish();
					}
				});
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mShakeListener != null) {
			mShakeListener.stop();
		}
	}
	
	
	@Override  /*当在activity中添加ScrollView实现滚动activity的效果后
	或外部容器为RelativeLayout时，activity的滑动效果却无法生效了，
	原因是因为activity没有处理滑动效果，解决方法如下：
	实现dispatchTouchEvent函数，
	在其实现中调用mGestureDetector.onTouchEvent(ev)，类似OnTouch。
	*******************************************************/
	public boolean dispatchTouchEvent(MotionEvent ev) {    
       mGestureDetector.onTouchEvent(ev);  
       // scroll.onTouchEvent(ev);  
       return super.dispatchTouchEvent(ev);  
	}  
	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (e1.getX() - e2.getX() > 80 && Math.abs(velocityX) > 40) {       //判断为向左滑动
			
			// 切换到LoginActivity
	    	Intent intent = new Intent(Homepage.this, LoginActivity.class);  
	        startActivity(intent);
	        overridePendingTransition(0, R.anim.translate_left_exit);   
	        finish();			
	    } else if (e1.getY() - e2.getY() > 30 && Math.abs(velocityY) > 10) {      //判断为向上滑动
	    	
	    	// 切换到Maincontent  
	        Intent intent = new Intent(Homepage.this, ContentActivity.class);  
		        startActivity(intent);
		        overridePendingTransition(0, R.anim.translate_up_exit);    //设定新的Activity进入和当前Activity退出时的动画。
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
		return false;
	}
	
	
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	private long exitTime = 0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {     //双击返回键退出程序
	    if(keyCode == KeyEvent.KEYCODE_BACK 
	            && event.getAction() == KeyEvent.ACTION_DOWN){   
	        if((System.currentTimeMillis()-exitTime) > 2000){  
	            Toast.makeText(getApplicationContext(), "再按一次返回键退出", Toast.LENGTH_SHORT).show();                                
	            exitTime = System.currentTimeMillis();   
	        } else {
	            finish();
	            System.exit(0);
	        }
	        return true;   
	    }
	    return super.onKeyDown(keyCode, event);
	}
}
