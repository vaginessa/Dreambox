package com.cuccs.dreambox.utils;

import java.io.File;
import java.io.IOException;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.widget.Toast;


public class B_R_PhoneCalls {
	/**获取库PhoneCalls表字段**/
    private static final String[] CALLS_PROJECTION = new String[] {
    			Calls.DURATION, Calls.TYPE, Calls.DATE, Calls.NUMBER,Calls._ID};
    public static final int MSG_PHONECALLS_WHAT = 102;
    public Handler mHandler; 
	public Context context;
	private SQLiteDatabase mDB;
	private String DBPath;
	private String dirname;
    
	public B_R_PhoneCalls(Context context, String dirname) {	//构造函数,获取当前context
			this.context = context;
			this.dirname = dirname;
			this.DBPath = AppFolderPaths.getBackupFilesDir(context) + "/" +dirname;
	}
	public B_R_PhoneCalls(Context context, String dirname, Handler mHandler) {
		this.context = context;
		this.mHandler = mHandler;
		this.dirname = dirname;
		this.DBPath = AppFolderPaths.getBackupFilesDir(context) + "/" +dirname;
}
	
	public boolean creatDBfile(){
		 File dbp=new File(DBPath);
		 File dbf=new File(DBPath+"/"+"phonecalls.db");
		 
		 if(!dbp.exists()){
			 dbp.mkdir();
		 }
		 //数据库文件是否创建成功
		 boolean isFileCreateSuccess=false; 
		 if(dbf.exists()){
			 dbf.delete();
		 }
		 try{                 
			 isFileCreateSuccess = dbf.createNewFile();
		 }catch(IOException ioex){
			 Log.e("B_R_PhoneCalls", "数据库文件创建失败！！");
			 return false;
		 }
        
        if(isFileCreateSuccess){
       	//如果想把数据库文件默认放在系统中,那么创建数据库mysql如下操作：
       	//SQLiteDatabase mysql = myOpenHelper.getWritableDatabase(); // 实例数据库
       	//如果你使用的是将数据库的文件创建在SD卡中，那么创建数据库mysql如下操作：
       	mDB = SQLiteDatabase.openOrCreateDatabase(dbf, null);
       	
       	String CREATE_TABLE="CREATE TABLE phonecalls(cid varchar, cname varchar, cnumber varchar, calltype Integer, date BIGINT, duration BIGINT)"; //创建表
       	mDB.execSQL(CREATE_TABLE);
       }
        return true;
  }
	
	public void backupToDB(){
		if(creatDBfile()==false){	//先创建数据库文件
			return;
		}
		int counter = 0;
		Message msg = mHandler.obtainMessage();  
		Bundle b = new Bundle();  
		ContentResolver resolver = context.getContentResolver();
        Cursor callsCursor = resolver.query(Calls.CONTENT_URI,CALLS_PROJECTION, 
        		null, null, Calls.DEFAULT_SORT_ORDER);
        if (callsCursor != null) {
        	 while (callsCursor.moveToNext()) {
        		String name;
        		int type = callsCursor.getInt(callsCursor.getColumnIndex(Calls.TYPE));  
 			    long duration = callsCursor.getLong(callsCursor.getColumnIndex(Calls.DURATION));  
 			    String phonenumber = callsCursor.getString(callsCursor.getColumnIndex(Calls.NUMBER));
 			    long _id = callsCursor.getLong(callsCursor.getColumnIndex(Calls._ID)); 
 			    long _date = callsCursor.getLong(callsCursor.getColumnIndex(Calls.DATE));
 			    /*获取通话联系人 姓名*/
  				Uri personUri = Uri.withAppendedPath(  
  			            ContactsContract.PhoneLookup.CONTENT_FILTER_URI, phonenumber);  
  			    Cursor cur = context.getContentResolver().query(personUri,  
  			            new String[] { PhoneLookup.DISPLAY_NAME }, null, null, null );  
  			    if( cur.moveToFirst() ) {  
  			        int nameIdx = cur.getColumnIndex(PhoneLookup.DISPLAY_NAME);  
  			        name = cur.getString(nameIdx);
  			    }else{
  			    	name = phonenumber; 
  			    }
  			    cur.close();  
  			    
  			    String INSERT_DATA = "INSERT INTO phonecalls(cid, cname, cnumber, calltype, date, duration) " +
              		"VALUES("+_id+", \""+name+"\", '"+phonenumber+"', "+type+", "+_date+", "+duration+")";	//注意此处用  \" 来添加特殊的双引号
  			    mDB.execSQL(INSERT_DATA);
  			    
  			    msg = mHandler.obtainMessage();
  			    b.putInt("total", ++counter); 
  			    msg.what = MSG_PHONECALLS_WHAT;
  			    msg.setData(b);  
  			    mHandler.sendMessage(msg);
        	 }
        }
        callsCursor.close();
        mDB.close();
	}
	
	/**从DataBase文件恢复通话记录到手机*/
	public void restoreFromDB(){
		String DBfilepath = DBPath+"/"+"phonecalls.db";
		int counter = 0;
		Message msg = mHandler.obtainMessage();  
		Bundle b = new Bundle();  
		
		//打开或创建test.db数据库  
		File dbFile=new File(DBfilepath);
		if(!dbFile.exists()){
			Toast.makeText(context, "通话记录备份文件未找到", Toast.LENGTH_LONG).show();
			new LogRecorder_Backup(context).updateRecord_Calls(dirname, 0);	//更新备份日志信息，将短信置空
			return;
		}
		SQLiteDatabase restore_db = context.openOrCreateDatabase(DBfilepath, Context.MODE_PRIVATE, null);
		Cursor DBcursor = restore_db.rawQuery("SELECT * FROM phonecalls", null);  
		Log.e("getCount", DBcursor.getCount()+"");
		
		while (DBcursor.moveToNext()) {
			String mName = DBcursor.getString(DBcursor.getColumnIndex("cname"));
			String mNumber = DBcursor.getString(DBcursor.getColumnIndex("cnumber"));
			String mType =  DBcursor.getString(DBcursor.getColumnIndex("calltype"));
			String mDate = DBcursor.getString(DBcursor.getColumnIndex("date"));
			String mDuration = DBcursor.getString(DBcursor.getColumnIndex("duration"));
			
			ContentResolver resolver = context.getContentResolver();
			Cursor callsCursor = resolver.query(Calls.CONTENT_URI,CALLS_PROJECTION, 
	        		null, null, Calls.DEFAULT_SORT_ORDER);
			Log.e("getCount", callsCursor.getCount()+"");
			
			if (callsCursor != null) {
            	boolean hasExist = false;
                while (callsCursor.moveToNext()) {
                	//得到手机号码
        			String date = callsCursor.getString(2);
                    if(date.equals(mDate)){
                    	hasExist = true;
                    	break;
                    }
                }
                if(hasExist == false){
                	this.insertCallLog(mNumber, mType, mDate, mDuration, "1"); //isNew:0已看1未看  
                	Log.i("B_R_PhoneCalls135", mName+" "+mNumber+" "+mDate+" "+mDuration);
                }
            }
			msg = mHandler.obtainMessage();
			b.putInt("total", ++counter); 
			msg.what = MSG_PHONECALLS_WHAT;
			msg.setData(b);  
			mHandler.sendMessage(msg);
			
			callsCursor.close();
		}
		DBcursor.close();
		restore_db.close();	//关闭数据库
	}
	
	private void insertCallLog(String number, String type, String date, String duration, String isNew){ 
		ContentValues values = new ContentValues();   
		values.put(CallLog.Calls.NUMBER, number);  
		values.put(CallLog.Calls.DATE, date);  
		values.put(CallLog.Calls.DURATION, duration);  
		values.put(CallLog.Calls.TYPE, type);//未接  
		values.put(CallLog.Calls.NEW, isNew);//0已看1未看  
     
		context.getContentResolver().insert(CallLog.Calls.CONTENT_URI, values);  
    }
	
	/**获得通话记录的个数*/
	public static int getItemQuantity(Context context){
		int quantity = 0;
		ContentResolver resolver = context.getContentResolver();
		Cursor callsCursor = resolver.query(Calls.CONTENT_URI,CALLS_PROJECTION, 
        		null, null, Calls.DEFAULT_SORT_ORDER);
        if (callsCursor != null) {
            while (callsCursor.moveToNext()) {
            	quantity++;
            }
        }
        callsCursor.close();
        return quantity;
	}
}
