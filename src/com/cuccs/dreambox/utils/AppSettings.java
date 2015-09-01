package com.cuccs.dreambox.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppSettings {
	
	public static boolean OnlyWiFi;
	public static boolean SoundOn;
	
	
	public static void readSettings(Context mContext){	//读取设置
		SharedPreferences mSpreferences = mContext.getSharedPreferences("App_settings",0);
		OnlyWiFi = mSpreferences.getBoolean("onlywifi", false);
		SoundOn = mSpreferences.getBoolean("soundon", false);
	}
	
	public static void writeDefaultSetup(Context mContext){	//应用第一次安装时写入 默认设置
		SharedPreferences mSpreferences = mContext.getSharedPreferences("App_settings",0);
		Editor editor = mSpreferences.edit();        
        editor.putBoolean("onlywifi", true);	//存入数据
        editor.putBoolean("soundon", true);
        editor.commit();	//提交修改
        
        OnlyWiFi = mSpreferences.getBoolean("onlywifi", true);
		SoundOn = mSpreferences.getBoolean("soundon", true);
	}
	
	public static void setOnlyWifi(Context mContext, boolean mOnlywifi){
		SharedPreferences mSpreferences = mContext.getSharedPreferences("App_settings",0);
		Editor editor = mSpreferences.edit();        
        editor.putBoolean("onlywifi", mOnlywifi);	//存入数据
        editor.commit();	//提交修改
        
        OnlyWiFi = mOnlywifi;
	}
	public static void setSoundOn(Context mContext, boolean mSoundOn){
		SharedPreferences mSpreferences = mContext.getSharedPreferences("App_settings",0);
		Editor editor = mSpreferences.edit();        
        editor.putBoolean("soundon", mSoundOn);	//存入数据
        editor.commit();	//提交修改
        
        SoundOn = mSoundOn;
	}
	
}
