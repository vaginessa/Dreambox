package com.cuccs.dreambox.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class ProgressThread_Backup_sdcard extends Thread {
	public static final int MSG_SUCCESS_WHAT = 999;
	public HashMap<Integer, Boolean> ItemisSelected;
	public Context mContext;
	public Handler mHandler;  
	private int[] iteminfo = new int[6];
	
	public ProgressThread_Backup_sdcard(Handler h, Context mContext, int[] Iteminfo, HashMap<Integer, Boolean> ItemisSelected) {  
        mHandler = h;  
        this.mContext = mContext;
        this.ItemisSelected = ItemisSelected;
        this.iteminfo = Iteminfo;
    }  
	
	public void run() {
		Looper.prepare();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HHmmss");     
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间     
		final String time_str = formatter.format(curDate); 
		for(int i=0;i<6;i++){
			if(ItemisSelected.get(i) == false){
				continue;
			}
			switch (i) {
			case 0:
				new B_R_AddressBook(mContext, time_str, mHandler).backupToDB();
				break;
			case 1:
				try {
					new B_R_SMS(mContext, time_str, mHandler).backupToXml();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 2:
				new B_R_PhoneCalls(mContext, time_str, mHandler).backupToDB();
				break;
			default:
				break;
			}
		}
		new LogRecorder_Backup(mContext).addRecord(curDate.getTime(), false, time_str, iteminfo);	//写入日志
		new LogRecorder_Operating(mContext).addRecord(curDate.getTime(), "本地备份", time_str, iteminfo);	//写入日志
		//记录备份时间
		AppAccountInfo.setlastSDCard_Backup(mContext, curDate.getTime());
        
		Message msg = mHandler.obtainMessage();
		msg.what = MSG_SUCCESS_WHAT;
		mHandler.sendMessage(msg);
		
		Looper.loop();
	}
} 