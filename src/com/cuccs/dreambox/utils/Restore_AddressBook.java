package com.cuccs.dreambox.utils;

import java.io.File;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.RawContacts;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * @author TimeTraveler
 * 其中判断联系人是否存在函数  From:    http://bbs.csdn.net/topics/340231857
 */

public class Restore_AddressBook {
	Context context;
	SQLiteDatabase mDB;
	private static final String[] PHONES_PROJECTION = new String[] {
        Phone.DISPLAY_NAME, Phone.NUMBER, Phone.CONTACT_ID };
	String DBPath;
	String databaseFilename;
	
	public Restore_AddressBook(Context context) {	//构造函数,获取当前context
		this.context = context;
		
		File dir = new File(new AppFolderPaths().getRootDir(context));
		String dirname = null;
		for(File f : dir.listFiles()){
			if(f.isDirectory()){
				dirname = f.getName();
			}
		}
		if(!dirname.equals("")){
			DBPath = new AppFolderPaths().getRootDir(context) + "/" + dirname;
		}
	}
	
	// 初始化从SD卡读取数据库文件
    private boolean openDatabase() {
        try {
        	databaseFilename = DBPath+"/"+"contact.db";
            File dir = new File(DBPath);
            if (!dir.exists()){
                dir.mkdir();}
            if (!(new File(databaseFilename)).exists()) {
                Toast.makeText(context, "Oops！没找到备份的通讯录", Toast.LENGTH_SHORT).show();
            }
            mDB = SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public void getContactsFromDB(){
    	if(openDatabase()==true){
    		Cursor DBcursor = mDB.rawQuery("select * from addressbook", null);
    		
    		if (DBcursor != null) {
                while (DBcursor.moveToNext()) {
                	// 得到手机号码
                    String phoneNumber = DBcursor.getString(2);
                    // 当手机号码为空的或者为空字段 跳过当前循环
                    if (TextUtils.isEmpty(phoneNumber))
                        continue;
                    // 得到联系人名称
                    String contactName = DBcursor.getString(1);
                    //得到联系人ID
                    Long contactid = DBcursor.getLong(0);
                    
                    if(!this.hasExist(contactName, phoneNumber)){
                    	Log.v("addressbook_Restore_Copyed", contactid+" "+contactName+" "+phoneNumber);
                    	ContentValues values = new ContentValues();
                    	Uri rawContactUri = context.getContentResolver().insert(RawContacts.CONTENT_URI, values);
                    	long rawContactId = ContentUris.parseId(rawContactUri);

                    	values.clear();
                    	values.put(Data.RAW_CONTACT_ID, rawContactId);
                    	values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
                    	values.put(StructuredName.GIVEN_NAME, contactName);
                    	//values.put(StructuredName.FAMILY_NAME, "Mike");
                    	

                    	context.getContentResolver().insert(Data.CONTENT_URI, values);

                    	values.clear();
                    	values.put(Data.RAW_CONTACT_ID, rawContactId);
                    	values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
                    	values.put(Phone.NUMBER, phoneNumber);
                    	context.getContentResolver().insert(Data.CONTENT_URI, values);
                    }
                }
            }
    		DBcursor.close();
    	}
    }
    
    public boolean hasExist(String contactName, String PhoneNumber){
    	ContentResolver resolver = context.getContentResolver();
    	Cursor phonesCursor = resolver.query(Phone.CONTENT_URI,
    			PHONES_PROJECTION, ContactsContract.CommonDataKinds.Phone.NUMBER + " = \'" + PhoneNumber + "\'", null, null);//查询所有包含content的名字
        if (phonesCursor != null && phonesCursor.moveToFirst()) {
        	while(phonesCursor.moveToFirst()){
        		Log.e("Restore_AddressBook_hasExist()", phonesCursor.getString(0)+" "+phonesCursor.getString(1));
        		String stringNumber = phonesCursor.getString(1);
        		if(stringNumber.equals(PhoneNumber)){
        			phonesCursor.close();
        			return true;
        		}
        	}
        } 
        phonesCursor.close();
		return false;
    }
}
