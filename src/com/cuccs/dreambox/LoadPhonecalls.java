package com.cuccs.dreambox;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract.PhoneLookup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class LoadPhonecalls extends Activity{
	/**获取库PhoneCalls表字段**/
    private static final String[] CALLS_PROJECTION = new String[] {
    			Calls.DURATION, Calls.TYPE, Calls.DATE, Calls.NUMBER,Calls._ID};
    
    private ArrayList<Integer> mCallsType = new ArrayList<Integer>();		/*通话类型：来电:1,拨出:2,未接:3**/  
    private ArrayList<String> mCallsName = new ArrayList<String>();		/*联系人姓名*/
    private ArrayList<String> mCallsNumber = new ArrayList<String>();		/*联系人号码**/ 
    private ArrayList<String> mCallsDate = new ArrayList<String>();		/*通话日期**/  
    private ArrayList<Long> mCallsDuration = new ArrayList<Long>();			/*通话时长**/  
    private ArrayList<Long> mCallsId = new ArrayList<Long>();			/*联系人ID*/
    
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loadphonecalls);
		
		/*得到手机通话记录**/
        getPhoneCalls();
        ListView mCallsList = (ListView)findViewById(R.id.loadphonecalls_listview);
		List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();		//创建list
		
		for(int i=0;i<mCallsNumber.size();i++){
			Map<String,Object> maps = new HashMap<String, Object>();			//实例化map对象
			if(mCallsType.get(i) == Calls.INCOMING_TYPE){
				maps.put("type", R.drawable.icon_phonecall_incoming);
			}else if(mCallsType.get(i) == Calls.OUTGOING_TYPE){
				maps.put("type", R.drawable.icon_phonecall_outcoming);
			}else{
				maps.put("type", R.drawable.icon_phonecall_missed);
			}
			maps.put("name", mCallsName.get(i).toString());
			maps.put("duration", "通话时长: "+mCallsDuration.get(i).toString()+"秒");
			maps.put("date", mCallsDate.get(i).toString());
			listItems.add(maps);
		}
		//此处String[]的顺序应该和maps中的添加顺序一致，否则listview匹配显示会出错
		SimpleAdapter adapter = new SimpleAdapter(this,listItems,
				R.layout.loadphonecalls_items,new String[]{"type","name","duration","date"},
				new int[]{R.id.loadphonecalls_type,R.id.loadphonecalls_name,R.id.loadphonecalls_duration,R.id.loadphonecalls_date});
		mCallsList.setAdapter(adapter);
	}
	
	/**得到手机通话记录信息**/
    private void getPhoneCalls() {
    	ContentResolver resolver = this.getContentResolver();
        Cursor callsCursor = resolver.query(Calls.CONTENT_URI,CALLS_PROJECTION, 
        		null, null, Calls.DEFAULT_SORT_ORDER);
        if (callsCursor != null) {
        	 while (callsCursor.moveToNext()) {
        		int type = callsCursor.getInt(callsCursor.getColumnIndex(Calls.TYPE));  
 			    long duration = callsCursor.getLong(callsCursor.getColumnIndex(Calls.DURATION));  
 			    String phonenumber = callsCursor.getString(callsCursor.getColumnIndex(Calls.NUMBER));
 			    long _id = callsCursor.getLong(callsCursor.getColumnIndex(Calls._ID)); 
 			    long _date = callsCursor.getLong(callsCursor.getColumnIndex(Calls.DATE));
 			    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
 			    String date = dateFormat.format(_date);
 			    /*获取通话联系人 姓名*/
  				Uri personUri = Uri.withAppendedPath(  
  			            ContactsContract.PhoneLookup.CONTENT_FILTER_URI, phonenumber);  
  			    Cursor cur = this.getContentResolver().query(personUri,  
  			            new String[] { PhoneLookup.DISPLAY_NAME }, null, null, null );  
  			    if( cur.moveToFirst() ) {  
  			        int nameIdx = cur.getColumnIndex(PhoneLookup.DISPLAY_NAME);  
  			        mCallsName.add(cur.getString(nameIdx));
  			    } 
  			    else{
  			    	mCallsName.add(phonenumber); 
  			    }
  			    cur.close();  
  			    
 			     mCallsType.add(type);
 			     mCallsNumber.add(phonenumber);
 			     mCallsDate.add(date);
 			     mCallsDuration.add(duration);
 			     mCallsId.add(_id);
 			    //Log.v("LoadPhonecalls_lines53", _id+" "+type+" "+phonenumber+" "+date+" "+duration+" ");
        	 }
        }
    }
}
