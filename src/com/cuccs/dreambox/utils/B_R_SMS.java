package com.cuccs.dreambox.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Xml;
import android.widget.Toast;

/**
 * @author TimeTraveler
 * 备份短信，将手机短信导出成为xml格式的文件
 *From   http://www.open-open.com/lib/view/open1337693444949.html
 */

public class B_R_SMS {
	public static final int MSG_SMS_WHAT = 101;
	public static final String ADDRESS = "address";
	public static final String PERSON = "person";
	public static final String DATE = "date";
	public static final String PROTOCOL = "protocol";
	public static final String READ = "read";
	public static final String STATUS = "status";
	public static final String TYPE = "type";
	public static final String REPLY_PATH_PRESENT = "reply_path_present";
	public static final String BODY = "body";
	public static final String LOCKED = "locked";
	public static final String ERROR_CODE = "error_code";
	public static final String SEEN = "seen";
	public static String[] projection = new String[] { ADDRESS, PERSON, DATE, PROTOCOL, 
			READ, STATUS,TYPE, REPLY_PATH_PRESENT,
			BODY,LOCKED,ERROR_CODE, SEEN }; // type=1是收件箱，==2是发件箱;read=0表示未读，read=1表示读过，seen=0表示未读，seen=1表示读过
	
	public static final String SMS_URI_ALL = "content://sms/";
	private FileOutputStream outStream = null;
	private XmlSerializer serializer;
	public Handler mHandler;  
	private Context context;
	private String smsPath;
	private String dirname;
	
	public B_R_SMS(Context context, String dirname) {
		this.context = context;
		this.dirname = dirname;
		this.smsPath = AppFolderPaths.getBackupFilesDir(context) + "/" +dirname;
	}
	public B_R_SMS(Context context, String dirname, Handler mHandler) {
		this.context = context;
		this.mHandler = mHandler;
		this.dirname = dirname;
		this.smsPath = AppFolderPaths.getBackupFilesDir(context) + "/" +dirname;
	}

	public void xmlStart() {

		File file = new File(smsPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		File file2 = new File(smsPath, "message.xml");
		try {
			outStream = new FileOutputStream(file2);
			serializer = Xml.newSerializer();
			serializer.setOutput(outStream, "UTF-8");
			serializer.startDocument("UTF-8", true);
			serializer.startTag(null, "sms");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**创建XML文件，然后写入SMS信息
	 * */
	public boolean backupToXml() throws Exception {

		this.xmlStart();
		Cursor cursor = null;
		try {
			int counter = 0;
			Message msg = mHandler.obtainMessage();  
    		Bundle b = new Bundle();  
    		
			ContentResolver conResolver = context.getContentResolver();
			Uri uri = Uri.parse(SMS_URI_ALL);
			cursor = conResolver.query(uri, projection, null, null, "_id asc");
			if (cursor.moveToFirst()) {
				// 查看数据库sms表得知 subject和service_center始终是null所以这里就不获取它们的数据了。
				String address;
				String person;
				String date;
				String protocol;
				String read;
				String status;
				String type;
				String reply_path_present;
				String body;
				String locked;
				String error_code;
				String seen;
				do {
					// 如果address == null，xml文件中是不会生成该属性的,为了保证解析时，属性能够根据索引一一对应，必须要保证所有的item标记的属性数量和顺序是一致的
					address = cursor.getString(cursor.getColumnIndex(ADDRESS));
					if (address == null) {
						address = "";
					}
					person = cursor.getString(cursor.getColumnIndex(PERSON));
					if (person == null) {
						person = "";
					}
					date = cursor.getString(cursor.getColumnIndex(DATE));
					if (date == null) {
						date = "";
					}
					protocol = cursor.getString(cursor.getColumnIndex(PROTOCOL));
					if (protocol == null) {// 为了便于xml解析
						protocol = "";
					}
					read = cursor.getString(cursor.getColumnIndex(READ));
					if (read == null) {
						read = "";
					}
					status = cursor.getString(cursor.getColumnIndex(STATUS));
					if (status == null) {
						status = "";
					}
					type = cursor.getString(cursor.getColumnIndex(TYPE));
					if (type == null) {
						type = "";
					}
					reply_path_present = cursor.getString(cursor.getColumnIndex(REPLY_PATH_PRESENT));
					if (reply_path_present == null) {// 为了便于XML解析
						reply_path_present = "";
					}
					body = cursor.getString(cursor.getColumnIndex(BODY));
					if (body == null) {
						body = "";
					}
					locked = cursor.getString(cursor.getColumnIndex(LOCKED));
					if (locked == null) {
						locked = "";
					}
					error_code = cursor.getString(cursor.getColumnIndex(ERROR_CODE));
					if (error_code == null) {
						error_code = "";
					}
					seen = cursor.getString(cursor.getColumnIndex(SEEN));
					if (seen == null) {
						seen = "";
					}
					// 生成xml子标记
					// 开始标记
					serializer.startTag(null, "item");
					// 加入属性
					serializer.attribute(null, ADDRESS, address);
					serializer.attribute(null, PERSON, person);
					serializer.attribute(null, DATE, date);
					serializer.attribute(null, PROTOCOL, protocol);
					serializer.attribute(null, READ, read);
					serializer.attribute(null, STATUS, status);
					serializer.attribute(null, TYPE, type);
					serializer.attribute(null, REPLY_PATH_PRESENT, reply_path_present);
					serializer.attribute(null, BODY, body);
					serializer.attribute(null, LOCKED, locked);
					serializer.attribute(null, ERROR_CODE, error_code);
					serializer.attribute(null, SEEN, seen);
					// 结束标记
					serializer.endTag(null, "item");
					
					msg = mHandler.obtainMessage();
	        		b.putInt("total", ++counter); 
	        		msg.what = MSG_SMS_WHAT;
	        		msg.setData(b);  
	        		mHandler.sendMessage(msg);
	        		
				} while (cursor.moveToNext());
			} else {
				return false;
			}

		} catch (SQLiteException ex) {
			ex.printStackTrace();
		}
		if(cursor != null) {
			cursor.close();//手动关闭cursor，及时回收
		}
		serializer.endTag(null, "sms");
		serializer.endDocument();
		outStream.flush();
		outStream.close();
		return true;
	}
	
	//从XML文件恢复短息
	public void restoreFromXml(){
		
		XmlPullParser parser = Xml.newPullParser();
		String Xmlfilepath = smsPath + "/message.xml";
		File file = new File(Xmlfilepath);
		if (!file.exists()) {
			Toast.makeText(context, "短信备份文件不在sd卡中", Toast.LENGTH_LONG).show();
			new LogRecorder_Backup(context).updateRecord_SMS(dirname, 0);	//更新备份日志信息，将短信置空
			return;
		}
		try {
			int counter = 0;
			Message msg = mHandler.obtainMessage();  
			Bundle b = new Bundle(); 
			
			FileInputStream fis = new FileInputStream(file);
			parser.setInput(fis, "UTF-8");
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {
				switch (event) {
				case XmlPullParser.START_DOCUMENT:
					break;

				case XmlPullParser.START_TAG: // 如果遇到开始标记，如<smsItems>,<smsItem>等
					if ("item".equals(parser.getName())) {

						// 判断短信数据库中是否已包含该条短信，如果有，则不需要恢复
						ContentResolver resolver = context.getContentResolver();
						Cursor cursor = resolver.query(Uri.parse("content://sms"), new String[] { DATE }, DATE + "=?",
								new String[] { parser.getAttributeValue(2) }, null);
						
						if (!cursor.moveToFirst()) {// 没有该条短信
							
							ContentValues values = new ContentValues();
							values.put(ADDRESS, parser.getAttributeValue(0));
							// 如果是空字符串说明原来的值是null，所以这里还原为null存入数据库
							values.put(PERSON, parser.getAttributeValue(1).equals("") ? null : parser.getAttributeValue(1));
							values.put(DATE, parser.getAttributeValue(2));
							values.put(PROTOCOL, parser.getAttributeValue(3).equals("") ? null : parser.getAttributeValue(3));
							values.put(READ, parser.getAttributeValue(4));
							values.put(STATUS, parser.getAttributeValue(5));
							values.put(TYPE, parser.getAttributeValue(6));
							values.put(REPLY_PATH_PRESENT, parser.getAttributeValue(7).equals("") ? null : parser.getAttributeValue(7));
							values.put(BODY, parser.getAttributeValue(8));
							values.put(LOCKED, parser.getAttributeValue(9));
							values.put(ERROR_CODE, parser.getAttributeValue(10));
							values.put(SEEN, parser.getAttributeValue(11));
							resolver.insert(Uri.parse("content://sms"), values);
						}
						cursor.close();
//						smsItem.setAddress(parser.getAttributeValue(0));
//						smsItem.setPerson(parser.getAttributeValue(1));
//						smsItem.setDate(parser.getAttributeValue(2));
//						smsItem.setProtocol(parser.getAttributeValue(3));
//						smsItem.setRead(parser.getAttributeValue(4));
//						smsItem.setStatus(parser.getAttributeValue(5));
//						smsItem.setType(parser.getAttributeValue(6));
//						smsItem.setReply_path_present(parser.getAttributeValue(7));
//						smsItem.setBody(parser.getAttributeValue(8));
//						smsItem.setLocked(parser.getAttributeValue(9));
//						smsItem.setError_code(parser.getAttributeValue(10));
//						smsItem.setSeen(parser.getAttributeValue(11));
						
						msg = mHandler.obtainMessage();
		        		b.putInt("total", ++counter); 
		        		msg.what = MSG_SMS_WHAT;
		        		msg.setData(b);  
		        		mHandler.sendMessage(msg);
					}
					break;
				case XmlPullParser.END_TAG:// 结束标记,如</smsItems>,</smsItem>等
					break;
				}
				event = parser.next();
			}
		} catch (Exception e) {
			Toast.makeText(context, "短信恢复出错", 1).show();
			e.printStackTrace();
		} 
	}
	
	/**获得短信的条数*/
	public static int getItemQuantity(Context context){
		int quantity = 0;
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse(SMS_URI_ALL);
		Cursor smsCursor = resolver.query(uri, projection, null, null, "_id asc");
		if (smsCursor != null) {
			while (smsCursor.moveToNext()){
				quantity++;
			}
		}
		smsCursor.close();
        return quantity;
	}
}