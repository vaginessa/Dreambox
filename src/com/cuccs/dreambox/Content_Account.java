package com.cuccs.dreambox;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.cuccs.dreambox.objects.CustomTextDialog;
import com.cuccs.dreambox.utils.AppAccountInfo;
import com.cuccs.dreambox.utils.ClearCache;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Content_Account extends Activity{
	Context mContext;
	Button Btn_Back, Btn_ExitAccount;
	TextView mAvatarName;
	TextView mLastSDcard, mLastCloud;
	TextView mLastSDcard_day, mLastCloud_day, mCloudSpace;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_account);
		mContext = this;
		
		mAvatarName = (TextView) findViewById(R.id.content_account_avatar_name); 
		mLastSDcard = (TextView) findViewById(R.id.content_account_lastSDcard_Text); 
		mLastCloud = (TextView) findViewById(R.id.content_account_lastCloud_Text); 
		mLastSDcard_day = (TextView) findViewById(R.id.content_account_lastSDcard_whatday); 
		mLastCloud_day = (TextView) findViewById(R.id.content_account_lastCloud_whatday); 
		mCloudSpace = (TextView) findViewById(R.id.content_account_CloudSpace_Size); 
		SharedPreferences mSpreferences = this.getSharedPreferences("Backup_info",0);
		long timeMillis1 = mSpreferences.getLong("lastSDCard_Backup", 0);
		long timeMillis2 = mSpreferences.getLong("lastCloud_Backup", 0);
		String spacesize = mSpreferences.getString("CloudSpace_Size", "0KB");
		SimpleDateFormat sformat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");  
		SimpleDateFormat dformat = new SimpleDateFormat("E");  
		Date d1=new Date(timeMillis1);  
		Date d2=new Date(timeMillis2);  
		
		if(timeMillis1 == 0){
			mLastSDcard.setText("尚未进行过本地备份");
			mLastSDcard_day.setText("");
		}else{
			mLastSDcard.setText(sformat.format(d1));
			mLastSDcard_day.setText(dformat.format(d1));
		}
		if(timeMillis2 == 0){
			mLastCloud.setText("尚未进行过云端备份");
			mLastCloud_day.setText("");
		}else{
			mLastCloud.setText(sformat.format(d2));
			mLastCloud_day.setText(dformat.format(d2));
		}
		mCloudSpace.setText(spacesize);
		mAvatarName.setText(AppAccountInfo.getUsername(mContext));	//名字
		
		Btn_Back = (Button) findViewById(R.id.account_icon_back);  
        Btn_Back.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.holding_anima, R.anim.translate_right_exit);
            }  
        });
        Btn_ExitAccount = (Button) findViewById(R.id.content_account_Exit);  
        Btn_ExitAccount.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Dialog mDialog;
            	CustomTextDialog.Builder customBuilder = new
						CustomTextDialog.Builder(Content_Account.this);
		            customBuilder.setTitle("清理缓存")
		            	.setIcon(R.drawable.confirm_dialog_warning)
		                .setMessage("本地缓存将被清空！")
		                .setNegativeButton("取 消", 
		                        new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog01, int which) {
		                    	dialog01.dismiss(); 
		                    }
		                })
		                .setPositiveButton("清 除", 
		                        new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog02, int which) {
		                    	/*AppAccountInfo.setisLoginSuccess(mContext, false);	//还原帐号信息
		                    	AppAccountInfo.setUsername(mContext, null);
		                    	AppAccountInfo.setUserId(mContext, null);
		                    	AppAccountInfo.setlastloginTime(mContext, 0);
		                    	AppAccountInfo.setSessionID(mContext, null);
		                    	AppAccountInfo.setlastSDCard_Backup(mContext, 0);
		                    	AppAccountInfo.setlastCloud_Backup(mContext, 0);*/
		                    	
		                    	/*Intent intent = new Intent(Content_Account.this, Homepage.class);
		                    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //注意本行的FLAG设置
		                    	startActivity(intent);
		                    	overridePendingTransition(R.anim.holding_anima, R.anim.contend_minify_lu);
		                    	finish();
		                    	Toast.makeText(Content_Account.this, "Will be done...", Toast.LENGTH_SHORT).show();*/
		                    	//Looper.prepare();
								new ClearCache(mContext).clear();
								Toast.makeText(mContext, "缓存清理完毕!", Toast.LENGTH_SHORT).show();
								//Looper.loop();
		                        dialog02.dismiss();
		                    }
		                });
		            mDialog = customBuilder.create();
		            mDialog.show();
            }  
        });
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {     //返回键退出
	    if(keyCode == KeyEvent.KEYCODE_BACK 
	            && event.getAction() == KeyEvent.ACTION_DOWN){   
	    	finish();
            overridePendingTransition(R.anim.holding_anima, R.anim.contend_minify_lu);
	        return true;   
	    }
	    return super.onKeyDown(keyCode, event);
	}
}
