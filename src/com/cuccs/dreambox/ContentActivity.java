package com.cuccs.dreambox;


import com.cuccs.dreambox.layouts.AboutLayout;
import com.cuccs.dreambox.layouts.CloudSpaceLayout;
import com.cuccs.dreambox.layouts.ContentLayout;
import com.cuccs.dreambox.layouts.OperatingRecordLayout;
import com.cuccs.dreambox.layouts.PersonalAccountLayout;
import com.cuccs.dreambox.layouts.SetupLayout;
import com.cuccs.dreambox.layouts.ContentLayout.OnContentSeletedListener;
import com.cuccs.dreambox.layouts.LeftMenuPanelLayout;
import com.cuccs.dreambox.layouts.LeftMenuPanelLayout.OnMenuSeletedListener;
import com.cuccs.dreambox.layouts.SharewithFriendsLayout;
import com.cuccs.dreambox.layouts.SlidingMenuContainer;
import com.cuccs.dreambox.layouts.SlidingMenuContainer.OnSlideListener;
import com.cuccs.dreambox.layouts.ThirdpartyAccountLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.ViewGroup.LayoutParams;
import android.view.View;

public class ContentActivity extends Activity implements OnSlideListener, OnMenuSeletedListener, OnContentSeletedListener {
	public static ContentActivity mContentActivity = null;
	public static Context mContext;
	//使用SharedPreferences来记录程序的设置参数
    SharedPreferences mSpreferences;
	public SlidingMenuContainer mSlideContainer;
	private ContentLayout mContentLayout;
	private LeftMenuPanelLayout mLeftPanelLayout;
	
	private PersonalAccountLayout mPersonalAccountLayout;
	private ThirdpartyAccountLayout mThirdpartyAccountLayout;
	private CloudSpaceLayout mCloudSpaceLayout;
	private OperatingRecordLayout mOperatingRecordLayout;
	private SharewithFriendsLayout mSharewithFriendsLayout;
	private SetupLayout mSetupLayout;
	private AboutLayout mAboutLayout;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContentActivity = this;
		mContext = getApplicationContext();

		mSlideContainer = new SlidingMenuContainer(mContext); 
		mLeftPanelLayout = new LeftMenuPanelLayout(mContext);
		mLeftPanelLayout.setOnMenuSeletedListener(this);
		
		mContentLayout = new ContentLayout(mContext);
		mContentLayout.setOnSlideListener(this);
		mContentLayout.setOnContentSeletedListener(this);	
		
		//**********************************************************************//
		new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				mPersonalAccountLayout = new PersonalAccountLayout(mContext);
				mPersonalAccountLayout.setOnSlideListener(mContentActivity);
				mThirdpartyAccountLayout = new ThirdpartyAccountLayout(mContext);
				mThirdpartyAccountLayout.setOnSlideListener(mContentActivity);
				mCloudSpaceLayout = new CloudSpaceLayout(mContext);
				mCloudSpaceLayout.setOnSlideListener(mContentActivity);
				
				mOperatingRecordLayout = new OperatingRecordLayout(mContentActivity);
				mOperatingRecordLayout.setOnSlideListener(mContentActivity);
				mSharewithFriendsLayout = new SharewithFriendsLayout(mContentActivity);
				mSharewithFriendsLayout.setOnSlideListener(mContentActivity);
				mSetupLayout = new SetupLayout(mContentActivity);
				mSetupLayout.setOnSlideListener(mContentActivity);
				mAboutLayout = new AboutLayout(mContentActivity);
				mAboutLayout.setOnSlideListener(mContentActivity);
				Looper.loop();
			}
		}).start();

        LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);  
        mSlideContainer.addView(mLeftPanelLayout, 0, layoutParams);  
        mSlideContainer.addView(mContentLayout, 1, layoutParams);

        setContentView(mSlideContainer);
	}
	

	public boolean onKeyDown(int keyCode, KeyEvent event) {  
	    if(keyCode == KeyEvent.KEYCODE_BACK 
	            && event.getAction() == KeyEvent.ACTION_DOWN){  
	    	if(mSlideContainer.mPanelVisible == false){
	    		this.toRight();
	    	}else{
	    		Intent intent = new Intent(ContentActivity.this, Homepage.class);  
		        startActivity(intent);
		        overridePendingTransition(R.anim.translate_up_enter, R.anim.holding_anima);   
		        this.finish();
	    	}
	        return true;
	    }
	    if(keyCode == KeyEvent.KEYCODE_MENU 
	            && event.getAction() == KeyEvent.ACTION_DOWN){
	    	if(mSlideContainer.mPanelVisible == false){
	    		this.toRight();
	    	}else{
	    		this.toShow(null);
	    		mLeftPanelLayout.ClearSelectedView();	//清除选中项目
	    		mLeftPanelLayout.RLayout_1_1.setBackgroundResource(R.drawable.left_menuitem_selected);
	    	}
	    	 return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}


	@Override
	public void toLeft() {}
	@Override
	public void toRight() {
		mSlideContainer.slideToRight();  
	}
	@Override
	public void toShow(View view) {
		mSlideContainer.show(mContentLayout);
	}


	@Override
	public void seletedMenuChild(int groupPosition, int childPosition) {
		 switch (groupPosition) {  
	        case 0: 
	  
	            switch (childPosition) {  
	            case 0:  
	                mSlideContainer.show(mContentLayout);  
	                break;  
	            case 1:  
	            	mSlideContainer.show(mPersonalAccountLayout);	
	                break;  
	            case 2:  
	            	mSlideContainer.show(mThirdpartyAccountLayout);	
	                break;  
	            case 3:  
	            	mSlideContainer.show(mCloudSpaceLayout);	
	                break;
	            }  
	            break;
	            
	        case 1:
	            switch (childPosition) {  
	            case 0:  
	            	/*Intent intent = new Intent();
                    intent.setClass(getApplicationContext(),Whatsnew.class);
                    intent.putExtra("whereFrom", "ContentActivity");
                    startActivity(intent);
                    this.finish();*/
	            	mOperatingRecordLayout.setupViews();	//更新listview
	            	mSlideContainer.show(mOperatingRecordLayout);
	                break;   
	            case 1:  
	            	mSlideContainer.show(mSharewithFriendsLayout);
	                break;  
	            case 2:  
	            	mSlideContainer.show(mSetupLayout);
	                break;
	            case 3:  
	            	mSlideContainer.show(mAboutLayout); 
	                break;
	            }  
	            break;
	        
	      }
	}

	@Override
	public void seletedContentChild(int viewId) {
		switch(viewId){
		case R.id.MyAccount:
			Intent intent0 = new Intent(ContentActivity.this, Content_Account.class);  
	        startActivity(intent0);
	        overridePendingTransition(R.anim.contend_enlarge_lu,R.anim.holding_anima);
			break;
		case R.id.MyRecover:
			Intent intent1 = new Intent(ContentActivity.this, Content_Restore.class);  
	        startActivity(intent1);
	        overridePendingTransition(R.anim.contend_enlarge_ru,R.anim.holding_anima);
			break;
		case R.id.MyBackup:
			Intent intent2 = new Intent(ContentActivity.this, Content_BackUp.class);  
	        startActivity(intent2);
	        overridePendingTransition(R.anim.contend_enlarge_ld,R.anim.holding_anima);   
			break;
		case R.id.MyLookOver:
			Intent intent3 = new Intent(ContentActivity.this, Content_LookOver.class);  
	        startActivity(intent3);
	        overridePendingTransition(R.anim.contend_enlarge_rd,R.anim.holding_anima); 
			break;
		}
	}
	
	public static void startNewActivity(Class<?> cls){
		Intent mIntent = new Intent(mContext, cls);  
		mContentActivity.startActivity(mIntent);
		mContentActivity.overridePendingTransition(R.anim.translate_right_enter,R.anim.translate_left_exit);
	}
}
