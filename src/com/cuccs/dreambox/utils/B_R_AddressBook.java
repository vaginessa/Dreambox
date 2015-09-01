package com.cuccs.dreambox.utils;

import java.io.File;
import java.io.IOException;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts.Data;
import android.provider.ContactsContract.RawContacts;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class B_R_AddressBook {
	public static final int MSG_ADDRESSBOOK_WHAT = 100;
	public Handler mHandler;  
	public String dirname;
	public static Context mContext;
	private static SQLiteDatabase mDB;
	private static final String[] PHONES_PROJECTION = new String[] {
        Phone.DISPLAY_NAME, Phone.NUMBER, Phone.CONTACT_ID };
	private static final int PHONES_DISPLAY_NAME_INDEX = 0;		/*联系人显示名称*/    
    private static final int PHONES_NUMBER_INDEX = 1;		/*电话号码*/     
    private static final int PHONES_CONTACT_ID_INDEX = 2;		/*联系人的ID*/
    String DBPath;
    
	public B_R_AddressBook(Context context, String dirname) {	//构造函数,获取当前context
			B_R_AddressBook.mContext = context;
			this.dirname = dirname;
			this.DBPath = AppFolderPaths.getBackupFilesDir(context) + "/" +dirname;
	}
	public B_R_AddressBook(Context context, String dirname, Handler mHandler) {
		B_R_AddressBook.mContext = context;
		this.mHandler = mHandler;
		this.dirname = dirname;
		this.DBPath = AppFolderPaths.getBackupFilesDir(context) + "/" +dirname;
}
	
	public boolean creatDBfile(){
		 File dbp=new File(DBPath);
		 File dbf=new File(DBPath+"/"+"contacts.db");
	        
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
        	 Log.e("B_R_AddressBook", "数据库文件创建失败！！");
        	 return false;
         }
         
         if(isFileCreateSuccess){
        	//如果想把数据库文件默认放在系统中,那么创建数据库mysql如下操作：
        	//SQLiteDatabase mysql = myOpenHelper.getWritableDatabase(); // 实例数据库
        	//如果你使用的是将数据库的文件创建在SD卡中，那么创建数据库mysql如下操作：
        	mDB = SQLiteDatabase.openOrCreateDatabase(dbf, null);
        	
        	String CREATE_TABLE="CREATE TABLE addressbook(cid varchar, cname varchar, cnumber varchar)";		//创建表
        	mDB.execSQL(CREATE_TABLE);
        }
         return true;
   }
	
	
	public void backupToDB(){
		if(creatDBfile()==true){	//先创建数据库文件
			int counter = 0;
			Message msg = mHandler.obtainMessage();  
    		Bundle b = new Bundle();  
    		
			ContentResolver resolver = mContext.getContentResolver();
	        // 获取手机联系人
	        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, 
	        		android.provider.ContactsContract.Contacts._ID + " ASC"); 
	        Log.e("phoneCursor.getCount()", phoneCursor.getCount()+"");
	        if (phoneCursor != null) {
	            while (phoneCursor.moveToNext()) {

	                //得到手机号码
	                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
	                //当手机号码为空的或者为空字段 跳过当前循环
	                if (TextUtils.isEmpty(phoneNumber))
	                    continue;
	                
	                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);	//得到联系人名称
	                String contactid = phoneCursor.getString(PHONES_CONTACT_ID_INDEX);	//得到联系人ID
	                Log.i("addressbook", contactid+" "+contactName+" "+phoneNumber);
	                
	                String INSERT_DATA = "INSERT INTO addressbook(cid, cname, cnumber) " +
	                		"VALUES("+contactid+",\" "+contactName+"\", '"+phoneNumber+"')";	//注意此处用  \" 来添加特殊的双引号
	                mDB.execSQL(INSERT_DATA);
	                
	                msg = mHandler.obtainMessage();
	        		b.putInt("total", ++counter); 
	        		msg.what = MSG_ADDRESSBOOK_WHAT;
	        		msg.setData(b);  
	        		mHandler.sendMessage(msg);
	            }
	        }
	        phoneCursor.close();
	        mDB.close();
		};
	}
	
	/**
	 *从DataBase文件恢复联系人到手机
	 */
	public void restoreFromDB(){
		String DBfilepath = DBPath+"/"+"contacts.db";
		Log.v("DBfilepath: ", DBfilepath);
		int counter = 0;
		Message msg = mHandler.obtainMessage();  
		Bundle b = new Bundle(); 
		
		//打开或创建test.db数据库  
		File dbFile=new File(DBfilepath);
		if(!dbFile.exists()){
			Toast.makeText(mContext, "联系人备份文件未找到", Toast.LENGTH_LONG).show();
			new LogRecorder_Backup(mContext).updateRecord_Contacts(dirname, 0);	//更新备份日志信息，将联系人置空
			return;
		}
		SQLiteDatabase restore_db = mContext.openOrCreateDatabase(DBfilepath, Context.MODE_PRIVATE, null);
		Cursor cursor_from = restore_db.rawQuery("SELECT * FROM addressbook", null);  
		Log.e("getCount", cursor_from.getCount()+"");
		
		while (cursor_from.moveToNext()) {
			String mName = cursor_from.getString(cursor_from.getColumnIndex("cname"));
			String mNumber = cursor_from.getString(cursor_from.getColumnIndex("cnumber"));
			
			ContentResolver resolver = mContext.getContentResolver();
			// 获取手机联系人
            Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null,
            		android.provider.ContactsContract.Contacts.DISPLAY_NAME + " ASC");
            Log.e("getCount", phoneCursor.getCount()+"");
            if (phoneCursor != null) {
            	boolean hasExist = false;
                while (phoneCursor.moveToNext()) {
                	//得到手机号码
                    String phoneNumber = phoneCursor.getString(1);
                    if(phoneNumber.equals(mNumber)){
                    	hasExist = true;
                    	break;
                    }
                }
                if(hasExist == false){
                	this.insertContacts(mName, mNumber, null);
                	Log.i("B_R_AddressBook_lines141", mName+" "+mNumber);
                }
            }
            phoneCursor.close();
            
            msg = mHandler.obtainMessage();
    		b.putInt("total", ++counter); 
    		msg.what = MSG_ADDRESSBOOK_WHAT;
    		msg.setData(b);  
    		mHandler.sendMessage(msg);
		}
		cursor_from.close();
		restore_db.close();	//关闭数据库
	}
	
	private void insertContacts(String name, String phoneNo, String email) {
		
		/** From:   http://blog.csdn.net/fly413413/article/details/7763436  */
	    //创建一个空的ContentValues
		ContentValues  values=new ContentValues();
		//向rawcontent。content——uri执行一个空值插入	
		//目的是获取系统返回的raw_contact_id		
		Uri  rawcontacturi = mContext.getContentResolver().insert(RawContacts.CONTENT_URI, values);
		long  rawcontactid = ContentUris.parseId(rawcontacturi);	
		values.clear();		
		values.put(Data.RAW_CONTACT_ID, rawcontactid);
		//设置内容类型		
		values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);	  // mimitype_id 字段，用于描述此数据的类型，电话号码？Email？....	
		//设置联系人姓名
		values.put(StructuredName.GIVEN_NAME, name);
		//向联系人URI添加联系人姓名
		mContext.getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
		
		values.clear();
		values.put(Data.RAW_CONTACT_ID, rawcontactid);
		values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
		//设置联系人电话号码
		values.put(Phone.NUMBER, phoneNo);
		//设置电话类型		
		values.put(Phone.TYPE, Phone.TYPE_MOBILE);		
		//向联系人电话号码URI添加电话号码		
		mContext.getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);		
		
		values.clear();
		values.put(Data.RAW_CONTACT_ID, rawcontactid);
		values.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
		//设置联系人email的地址
		values.put(Email.DATA, email);		
		//设置改电子邮件类型
		values.put(Email.TYPE, Email.TYPE_WORK);		
		//向联系人email  URI添加 email数据
		mContext.getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);		
	}
	
	/**获得联系人的个数*/
	public static int getItemQuantity(Context context){
		int quantity = 0;
		ContentResolver resolver = context.getContentResolver();
        // 获取手机联系人
        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, 
        		android.provider.ContactsContract.Contacts._ID + " ASC"); 
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
            	quantity++;
            }
        }
        phoneCursor.close();
        return quantity;
	}
}
