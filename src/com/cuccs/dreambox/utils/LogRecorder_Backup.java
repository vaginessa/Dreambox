package com.cuccs.dreambox.utils;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LogRecorder_Backup {
	public SQLiteDatabase mLogDB;
	public Context mContext;

	public LogRecorder_Backup(Context mContext) {
		this.mContext = mContext;
	}

	public boolean dbExist() {
		// 打开或创建backuplog.db数据库
		File dbFile = new File("/data/data/com.cuccs.dreambox/databases/"
				+ AppAccountInfo.getUsername(mContext) + "_backuplog.db");
		if (dbFile.exists() == false) {
			return false;
		}
		return true;
	}

	public void openOrCreateLogDB() {

		// 如果想把数据库文件默认放在系统中,那么创建数据库如下操作：
		mLogDB = mContext.openOrCreateDatabase(
				AppAccountInfo.getUsername(mContext) + "_backuplog.db",
				Context.MODE_PRIVATE, null);
		mLogDB.execSQL("CREATE TABLE IF NOT EXISTS backuplog("
				+ "_date VARCHAR, _type VARCHAR, dirname VARCHAR"
				+ ", contactsnum INT, smsnum INT, callsnum INT, photosnum INT, documentsnum INT, musicsnum INT)"); // 创建表
	}

	public void addRecord(long date, boolean type_isCloud, String dirname, int[] itemInfo) {
		if(dbExist() == false){
			return;
		}
		openOrCreateLogDB();
		String INSERT_DATA = "INSERT INTO backuplog(_date, _type, dirname, contactsnum, smsnum, callsnum, photosnum, documentsnum, musicsnum) "
				+ "VALUES('"
				+ date
				+ "','"
				+ type_isCloud
				+ "', '"
				+ dirname
				+ "'"
				+ ","
				+ itemInfo[0]
				+ ","
				+ itemInfo[1]
				+ ","
				+ itemInfo[2]
				+ ","
				+ itemInfo[3]
				+ ","
				+ itemInfo[4]
				+ ","
				+ itemInfo[5] + ")";

		Log.v("LogRecorder_Backup----line29", "'" + date + "','" + type_isCloud
				+ "', '" + dirname + "'" + "," + itemInfo[0] + ","
				+ itemInfo[1] + "," + itemInfo[2] + "," + itemInfo[3] + ","
				+ itemInfo[4] + "," + itemInfo[5]);

		mLogDB.execSQL(INSERT_DATA);
		mLogDB.close();
	}

	public void deleteRecord(String date) {
		openOrCreateLogDB();
		String DELETE_DATA = "DELETE FROM backuplog where _date='" + date + "'";
		mLogDB.execSQL(DELETE_DATA);
		mLogDB.close();
	}

	public void updateRecord_Contacts(String dirname, int contactsnum) {
		openOrCreateLogDB();
		String UPDATE_DATA = "UPDATE backuplog SET contactsnum=" + contactsnum
				+ " where dirname='" + dirname + "'";
		mLogDB.execSQL(UPDATE_DATA);
		mLogDB.close();
	}

	public void updateRecord_SMS(String dirname, int smsnum) {
		openOrCreateLogDB();
		String UPDATE_DATA = "UPDATE backuplog SET smsnum=" + smsnum
				+ " where dirname='" + dirname + "'";
		mLogDB.execSQL(UPDATE_DATA);
		mLogDB.close();
	}

	public void updateRecord_Calls(String dirname, int callsnum) {
		openOrCreateLogDB();
		String UPDATE_DATA = "UPDATE backuplog SET callsnum=" + callsnum
				+ " where dirname='" + dirname + "'";
		mLogDB.execSQL(UPDATE_DATA);
		mLogDB.close();
	}
}
