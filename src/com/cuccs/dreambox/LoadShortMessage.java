package com.cuccs.dreambox;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * @author TimeTraveler
 * 读取手机短信内容并显示到listview
 * 需要权限 <uses-permission android:name="android.permission.READ_SMS" />
 * From:  
 */

public class LoadShortMessage extends Activity{
	String[] SMS_PROJECTION = new String[] { 
		     "_id",  
		     "thread_id",
		     "address",  
		     "person", 
		     "date",
		     "body",
		     "type" 
		    };  
	private ArrayList<String> mPersonName = new ArrayList<String>();		/*联系人名称**/        
    private ArrayList<String> mAddress = new ArrayList<String>();		/*联系人号码**/ 
    private ArrayList<String> mDate = new ArrayList<String>(); 
    private ArrayList<String> mBody = new ArrayList<String>();    
       
    
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loadshortmessage);
		
		/*得到手机所有短信*/
		getAllShortMessages();
		ListView listView = (ListView)findViewById(R.id.loadshortmessage_listview);
		List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();		//创建list
		
		for(int i=0;i<mAddress.size();i++){
			Map<String,Object> maps = new HashMap<String, Object>();			//实例化map对象
			if((mPersonName.get(i)).equals(mAddress.get(i))){
				maps.put("nums", "");
			}else{
				maps.put("nums", mAddress.get(i));
			}
			maps.put("title", mPersonName.get(i));
			maps.put("date", mDate.get(i));
			maps.put("body", mBody.get(i));
			listItems.add(maps);
		}
		SimpleAdapter adapter = new SimpleAdapter(this,listItems,
				R.layout.loadshortmessage_items,new String[]{"title","body","date"},
				new int[]{R.id.loadshortmessage_titles,R.id.loadshortmessage_bodys,R.id.loadshortmessage_date});
		listView.setAdapter(adapter);
	}
	
	
	
	/**得到手机所有短信信息**/
    private void getAllShortMessages() {
    	final String SMS_URI_ALL = "content://sms/";	/*所有的短信 */
    	final String SMS_URI_INBOX = "content://sms/inbox";		/* 收件箱短信*/
    	final String SMS_URI_SEND = "content://sms/sent";
    	final String SMS_URI_DRAFT = "content://sms/draft";		/*草稿箱短信*/
    	
    	ContentResolver conResolver = this.getContentResolver();
    	Uri uri = Uri.parse(SMS_URI_ALL);
    	Cursor cusor = this.managedQuery(uri, SMS_PROJECTION, null, null,
    			"date desc");

    	int threadIdColumn = cusor.getColumnIndex("thread_id");
    	int phoneNumberColumn = cusor.getColumnIndex("address");
    	int dateColumn = cusor.getColumnIndex("date");
    	int smsbodyColumn = cusor.getColumnIndex("body");
    	int typeColumn = cusor.getColumnIndex("type");

    	if (cusor != null) {
			while (cusor.moveToNext()) {
				/*获取短信发信人姓名
				 * From   http://kevinlynx.iteye.com/blog/845920 */
				Uri personUri = Uri.withAppendedPath(  
			            ContactsContract.PhoneLookup.CONTENT_FILTER_URI, cusor.getString(phoneNumberColumn));  
			    Cursor cur = this.getContentResolver().query(personUri,  
			            new String[] { PhoneLookup.DISPLAY_NAME },  
			            null, null, null ); 
			    int nameIdx = cur.getColumnIndex(PhoneLookup.DISPLAY_NAME);
			    if( cur.moveToFirst() ) {  
			        mPersonName.add(cur.getString(nameIdx)); 
			    }else{
			    	mPersonName.add(cusor.getString(phoneNumberColumn)); 
			    }
			    cur.close(); 
				//mPersonName.add(cusor.getString(nameColumn));
			    mAddress.add(cusor.getString(phoneNumberColumn));
				mDate.add(strToDateLong(cusor.getLong(dateColumn)));
				mBody.add(cusor.getString(smsbodyColumn));
				
			}
			cusor.close();
		}
    }
    /**
    * 将时间格式由long类型转换成Date类型
    * @param strDate
    */
    public static String strToDateLong(Long strDate) {
    	SimpleDateFormat sdf= new SimpleDateFormat("MM月dd日");
    	java.util.Date dt = new Date(strDate);  
    	String sDateTime = sdf.format(dt);  //得到精确到秒的表示：08/31/2006 21:08:00
		return sDateTime;
    }
}
