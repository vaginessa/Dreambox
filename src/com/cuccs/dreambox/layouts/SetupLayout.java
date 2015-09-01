package com.cuccs.dreambox.layouts;

import com.cuccs.dreambox.ContentActivity;
import com.cuccs.dreambox.R;
import com.cuccs.dreambox.TermsofService;
import com.cuccs.dreambox.layouts.SlidingMenuContainer.OnSlideListener;
import com.cuccs.dreambox.objects.CustomTextDialog;
import com.cuccs.dreambox.utils.AppSettings;
import com.cuccs.dreambox.utils.ClearCache;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SetupLayout extends FrameLayout{
	Context mContext;
	Button Btn_Back;
	RelativeLayout RLayout_01, RLayout_02, RLayout_03, RLayout_04, RLayout_05;
	RelativeLayout RLayout_11, RLayout_12, RLayout_13, RLayout_14;
	ImageView mSwitch01;
	ImageView mSwitch02;
	Dialog loadingDialog;
	public static boolean OnlyWiFi;
	public static boolean SoundOn;
	private OnSlideListener mOnSlideListener;
	
	public SetupLayout(Context context) {
        super(context);  
        this.mContext = context;
        setupViews();  
    }
  
    public SetupLayout(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        setupViews();  
    }

    public void setOnSlideListener(OnSlideListener onSlideListener) {  
        mOnSlideListener = onSlideListener;  
    }
    
    private void setupViews() {  
        final LayoutInflater mLayoutInflater = LayoutInflater.from(getContext());  
        LinearLayout mSetup = (LinearLayout) mLayoutInflater.inflate(R.layout.menu_setting, null);  
        addView(mSetup);
       
        AppSettings.readSettings(mContext);
        OnlyWiFi = AppSettings.OnlyWiFi;
        SoundOn = AppSettings.SoundOn;
        
        mSwitch01 = (ImageView) mSetup.findViewById(R.id.menu_setting_relativelayout01_switch);
        mSwitch02 = (ImageView) mSetup.findViewById(R.id.menu_setting_relativelayout02_switch);
        if(OnlyWiFi == true){
			mSwitch01.setImageResource(R.drawable.settingv3_btn_on);
		}else{
			mSwitch01.setImageResource(R.drawable.settingv3_btn_off);
		}
        if(SoundOn == true){
        	mSwitch02.setImageResource(R.drawable.settingv3_btn_on);
		}else{
			mSwitch02.setImageResource(R.drawable.settingv3_btn_off);
		}
        
        RLayout_01 = (RelativeLayout) mSetup.findViewById(R.id.menu_setting_relativelayout01);
        RLayout_01.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(OnlyWiFi == true){
					mSwitch01.setImageResource(R.drawable.settingv3_btn_off);
				}else{
					mSwitch01.setImageResource(R.drawable.settingv3_btn_on);
				}
				OnlyWiFi = !OnlyWiFi;
				AppSettings.setOnlyWifi(mContext, OnlyWiFi);
			}
		});
        RLayout_02 = (RelativeLayout) mSetup.findViewById(R.id.menu_setting_relativelayout02);
        RLayout_02.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(SoundOn == true){
					mSwitch02.setImageResource(R.drawable.settingv3_btn_off);
				}else{
					mSwitch02.setImageResource(R.drawable.settingv3_btn_on);
				}
				SoundOn = !SoundOn;
				AppSettings.setSoundOn(mContext, SoundOn);
			}
		});
        RLayout_03 = (RelativeLayout) mSetup.findViewById(R.id.menu_setting_relativelayout03);
        RLayout_03.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CustomTextDialog.Builder customBuilder = new
						CustomTextDialog.Builder(mContext);
		            customBuilder.setTitle("警告!")
		            	.setIcon(R.drawable.confirm_dialog_warning)
		                .setMessage("您确定清空手机通讯录吗?\n清空后可能无法找回 ")
		                .setNegativeButton("取 消", 
		                        new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog01, int which) {
		                    	dialog01.dismiss(); 
		                    }
		                })
		                .setPositiveButton("清 空", 
		                        new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog02, int which) {
		                    	Toast.makeText(mContext, "危险动作, 被暂时保留!", Toast.LENGTH_SHORT).show();
		                        dialog02.dismiss();
		                    }
		                });
		            loadingDialog = customBuilder.create();
		            loadingDialog.show();
			}
		});
        
        RLayout_04 = (RelativeLayout) mSetup.findViewById(R.id.menu_setting_relativelayout04);
        RLayout_04.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				loadingDialog = new Dialog(mContext, R.style.Theme_ShareDialog);
		        loadingDialog.setContentView(R.layout.loading);
				loadingDialog.setCancelable(false);
				loadingDialog.show();
				new Thread(new Runnable(){
					@Override
					public void run() {
						Looper.prepare();
						new ClearCache(mContext).clear();
						loadingDialog.dismiss();
						Toast.makeText(mContext, "缓存清理完毕!", Toast.LENGTH_SHORT).show();
						Looper.loop();
					}
				}).start();
			}
		});
        RLayout_05 = (RelativeLayout) mSetup.findViewById(R.id.menu_setting_relativelayout05);
        RLayout_05.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CustomTextDialog.Builder customBuilder = new
						CustomTextDialog.Builder(mContext);
		            customBuilder.setTitle("提示")
		                .setMessage("默认下载目录为: \n SD卡\\DreamBox\\cache")
		                .setPositiveButton("OK", 
		                        new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int which) {
		                    	dialog.dismiss();
		                    }
		                });
		            loadingDialog = customBuilder.create();
		            loadingDialog.show();
			}
		});
        RLayout_13 = (RelativeLayout) mSetup.findViewById(R.id.menu_setting_relativelayout13);
        RLayout_13.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ContentActivity.startNewActivity(TermsofService.class);	//开启Activity
			}
		});
        Btn_Back = (Button) mSetup.findViewById(R.id.setup_icon_back);  
        Btn_Back.setOnClickListener(new OnClickListener() {  

            public void onClick(View v) {
                if (mOnSlideListener != null) { 
                    mOnSlideListener.toRight();  
                }
            }
        });
    }
}
