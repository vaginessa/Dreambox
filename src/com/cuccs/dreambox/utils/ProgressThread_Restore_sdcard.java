package com.cuccs.dreambox.utils;

import java.util.HashMap;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class ProgressThread_Restore_sdcard extends Thread {
	public static final int MSG_SUCCESS_WHAT = 999;
	public HashMap<Integer, Boolean> ItemisSelected;
	public String parentDir;
	public Context mContext;
	public Handler mHandler;
	
	public ProgressThread_Restore_sdcard(Handler h, Context mContext, String parentDir, HashMap<Integer, Boolean> ItemisSelected) {  
        mHandler = h;
        this.mContext = mContext;
        this.parentDir = parentDir;
        this.ItemisSelected = ItemisSelected;
    }  
	
	public void run() {
		Looper.prepare();
		for(int i=0;i<6;i++){
			if(ItemisSelected.get(i) == false){
				continue;
			}
			switch (i) {
			case 0:
				new B_R_AddressBook(mContext, parentDir, mHandler).restoreFromDB();
				break;
			case 1:
				new B_R_SMS(mContext, parentDir, mHandler).restoreFromXml();
				break;
			case 2:
				new B_R_PhoneCalls(mContext, parentDir, mHandler).restoreFromDB();
				break;
			default:
				break;
			}
		}
		Message msg = mHandler.obtainMessage();
		msg.what = MSG_SUCCESS_WHAT;
		mHandler.sendMessage(msg);
		
		Looper.loop();
	}
} 