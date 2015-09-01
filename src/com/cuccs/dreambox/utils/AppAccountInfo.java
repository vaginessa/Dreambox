package com.cuccs.dreambox.utils;

/**
 * 此版本为用户免登陆版，账号直接使用用户的手机SIM卡IMSI号作为云端备份帐号
 * 全局都隐去登录界面和注册界面
 * 改动主要发生在AppAccountInfo、Content_Account中
 * 
 * AppAccountInfo 
 * 			-- getisLoginSuccess()总是返回true;
 * 			-- getUsername()直接返回IMSI码;
 */

import com.cuccs.dreambox.LoginActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class AppAccountInfo {
	
	public static boolean isLoginSuccess;	//是否登陆成功
	public static long lastloginTime;	//最后登录时间
	
	public	static String username;
	public	static String userId;
	public	static String sessionID;
	
	public static boolean getisLoginSuccess(Context mContext){
		SharedPreferences mSpreferences = mContext.getSharedPreferences("Account_Info",0);
        //return isLoginSuccess = mSpreferences.getBoolean("isLoginSuccess", false);
        return isLoginSuccess = mSpreferences.getBoolean("isLoginSuccess", true);
	}
	public static long getlastloginTime(Context mContext){
		SharedPreferences mSpreferences = mContext.getSharedPreferences("Account_Info",0);
        return lastloginTime = mSpreferences.getLong("isLoginSuccess", 0);
	}
	public static String getUsername(Context mContext){
		SharedPreferences mSpreferences = mContext.getSharedPreferences("Account_Info",0);
        username = mSpreferences.getString("username", null);
		/*if(username == null){
			Toast.makeText(mContext, "您还未登录应用", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(mContext, LoginActivity.class);  
			if(!mContext.getClass().getName().equals("LoginActivity")){
				mContext.startActivity(intent);
				((Activity)mContext).finish();
			}
		}
		return username; */
		return getPhoneIMSI(mContext);
	}
	public static String getUserId(Context mContext){
		SharedPreferences mSpreferences = mContext.getSharedPreferences("Account_Info",0);
        return userId = mSpreferences.getString("userId", null);
	}
	public static String getSessionID(Context mContext){
		SharedPreferences mSpreferences = mContext.getSharedPreferences("Account_Info",0);
        return sessionID = mSpreferences.getString("sessionID", null);
	}
	
	public static void setisLoginSuccess(Context mContext, boolean IsLoginSuccess){
		SharedPreferences mSpreferences = mContext.getSharedPreferences("Account_Info",0);
		Editor editor = mSpreferences.edit();        
        editor.putBoolean("isLoginSuccess", IsLoginSuccess);	//存入数据
        editor.commit();	//提交修改
        
        isLoginSuccess = IsLoginSuccess;
	}
	
	public static void setlastloginTime(Context mContext, long LastloginTime){
		SharedPreferences mSpreferences = mContext.getSharedPreferences("Account_Info",0);
		Editor editor = mSpreferences.edit();        
        editor.putLong("lastloginTime", LastloginTime);	//存入数据
        editor.commit();	//提交修改
        
        lastloginTime = LastloginTime;
	}
	
	public static void setUsername(Context mContext, String userName){
		SharedPreferences mSpreferences = mContext.getSharedPreferences("Account_Info",0);
		Editor editor = mSpreferences.edit();        
        editor.putString("username", userName);	//存入数据
        editor.commit();	//提交修改
        
        username = userName;
	}
	
	public static void setUserId(Context mContext, String userid){
		SharedPreferences mSpreferences = mContext.getSharedPreferences("Account_Info",0);
		Editor editor = mSpreferences.edit();        
        editor.putString("userId", userid);	//存入数据
        editor.commit();	//提交修改
        
        userId = userid;
	}
	
	public static void setSessionID(Context mContext, String sessionid){
		SharedPreferences mSpreferences = mContext.getSharedPreferences("Account_Info",0);
		Editor editor = mSpreferences.edit();        
        editor.putString("sessionID", sessionid);	//存入数据
        editor.commit();	//提交修改
        
        sessionID = sessionid;
	}
	
	//记录上次备份时间
	public static void setlastSDCard_Backup(Context mContext, long time){
		SharedPreferences mSpreferences = mContext.getSharedPreferences("Backup_info",0);
		Editor editor = mSpreferences.edit();        
        editor.putLong("lastSDCard_Backup", time);	//存入数据
        editor.commit();	//提交修改
	}
	public static void setlastCloud_Backup(Context mContext, long time){
		SharedPreferences mSpreferences = mContext.getSharedPreferences("Backup_info",0);
		Editor editor = mSpreferences.edit();        
        editor.putLong("lastCloud_Backup", time);	//存入数据
        editor.commit();	//提交修改
	}
	
	
	
	public static String getPhoneIMSI(Context mContext){
		TelephonyManager telephonyManager = (TelephonyManager)mContext  
	                .getSystemService(Context.TELEPHONY_SERVICE); 
		//获取手机唯一的标识
		String IMEI =((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		String mProvidersName = null;  
		// 返回唯一的用户ID;就是这张卡的编号神马的  
        String IMSI = telephonyManager.getSubscriberId();
        String number = telephonyManager.getLine1Number();
        // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。  
        if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {  
            mProvidersName = "中国移动";  
        } else if (IMSI.startsWith("46001")) {  
            mProvidersName = "中国联通";  
        } else if (IMSI.startsWith("46003")) {  
            mProvidersName = "中国电信";  
        } 
        //Log.v("=-=-============", IMSI+" ,"+mProvidersName+"--->"+number+" ,imei:"+IMEI);
		return IMSI;  
	}
}
