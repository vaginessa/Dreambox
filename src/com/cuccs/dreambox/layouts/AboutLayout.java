package com.cuccs.dreambox.layouts;

import com.cuccs.dreambox.ContactUs;
import com.cuccs.dreambox.ContentActivity;
import com.cuccs.dreambox.FunctionIntroduction;
import com.cuccs.dreambox.R;
import com.cuccs.dreambox.Website_Dreamanster;
import com.cuccs.dreambox.layouts.SlidingMenuContainer.OnSlideListener;
import com.cuccs.dreambox.utils.AppSettings;
import com.cuccs.dreambox.utils.CheckNetwork;
import com.cuccs.dreambox.utils.Thread_CheckNewVersion;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class AboutLayout extends FrameLayout{
	Context mContext;
	Button Btn_Back;
	ImageView ic_launcher;
	RelativeLayout RLayout_01;
	RelativeLayout RLayout_02;
	RelativeLayout RLayout_03;
	private OnSlideListener mOnSlideListener;
	
	public AboutLayout(Context mContext) {  
        super(mContext);  
        this.mContext = mContext;
        setupViews();  
    }  
  
    public AboutLayout(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        setupViews();  
    }

    public void setOnSlideListener(OnSlideListener onSlideListener) {  
        mOnSlideListener = onSlideListener;  
    }
    
    private void setupViews() {  
        final LayoutInflater mLayoutInflater = LayoutInflater.from(getContext());  
        LinearLayout mAbout = (LinearLayout) mLayoutInflater.inflate(R.layout.menu_about, null);  
        addView(mAbout);
        
        ic_launcher = (ImageView)mAbout.findViewById(R.id.menu_about_ic_launcher);
        ic_launcher.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ContentActivity.startNewActivity(Website_Dreamanster.class);	//开启Activity
			}
		});
        
        RLayout_01 = (RelativeLayout) mAbout.findViewById(R.id.menu_about_relativelayout01);
        RLayout_01.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CheckNetwork checknet = new CheckNetwork(mContext);		//检查网络连接是否正常
				if(checknet.isConnectingToInternet() == false){
					return;
				}
				AppSettings.readSettings(mContext);		//读取用户设置
		        if(AppSettings.OnlyWiFi == true && checknet.isWiFiConnected() == true 
		        		|| AppSettings.OnlyWiFi == false){
		        	Thread_CheckNewVersion mThread = new Thread_CheckNewVersion(mContext);	//检测新版本
		        	mThread.start();
		        }
			}
		});
        RLayout_02 = (RelativeLayout) mAbout.findViewById(R.id.menu_about_relativelayout02);
        RLayout_02.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ContentActivity.startNewActivity(FunctionIntroduction.class);	//开启Activity
			}
		});
        RLayout_03 = (RelativeLayout) mAbout.findViewById(R.id.menu_about_relativelayout03);
        RLayout_03.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ContentActivity.startNewActivity(ContactUs.class);	//开启Activity
			}
		});
        Btn_Back = (Button) mAbout.findViewById(R.id.about_icon_back);  
        Btn_Back.setOnClickListener(new OnClickListener() {  
            public void onClick(View v) {
                if (mOnSlideListener != null) { 
                    mOnSlideListener.toRight();  
                }
            }  
        });
    }
}
