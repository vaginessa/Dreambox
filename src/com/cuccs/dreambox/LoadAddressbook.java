package com.cuccs.dreambox;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts.Photo;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author TimeTraveler
 * 读取手机或SIM卡联系人，实现拨打电话，发送短信功能
 * 需要权限   <uses-permission android:name="android.permission.READ_CONTACTS"/>
 * From:  www.apkbus.com/android-13445-1-1.html
 */

public class LoadAddressbook extends Activity{
	/**获取库Phone表字段**/
    private static final String[] PHONES_PROJECTION = new String[] {
            Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID };
    
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;		/*联系人显示名称*/    
    private static final int PHONES_NUMBER_INDEX = 1;		/*电话号码*/    
    private static final int PHONES_PHOTO_ID_INDEX = 2;		/*头像ID*/     
    private static final int PHONES_CONTACT_ID_INDEX = 3;		/*联系人的ID*/
    
    private ArrayList<String> mContactsName = new ArrayList<String>();		/*联系人名称**/        
    private ArrayList<String> mContactsNumber = new ArrayList<String>();		/*联系人号码**/    
    private ArrayList<Bitmap> mContactsPhoto = new ArrayList<Bitmap>();		/*联系人头像**/  
    private ArrayList<Long> mContactsId = new ArrayList<Long>();			/*联系人ID*/

    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loadaddressbook);
		
		/*得到手机通讯录联系人信息*/
        getPhoneContacts();
		//getSIMContacts(); 
        ListView listView = (ListView)findViewById(R.id.loadaddressbook_listview);
		List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();		//创建list
		
		Map<String,Object> map = new HashMap<String, Object>();			//添加第一个map对象，显示“共有xx个联系人”
		map.put("image", null);
		map.put("title", "========"+mContactsNumber.size()+"位联系人=======");
		map.put("nums", null);
		listItems.add(map);
		for(int i=0;i<mContactsNumber.size();i++){
			Map<String,Object> maps = new HashMap<String, Object>();			//实例化map对象
			maps.put("image", R.drawable.user);			/**SIM卡联系人没有头像,小心出现RuntimeException异常**/
			maps.put("title", mContactsName.get(i).toString());
			maps.put("nums", mContactsNumber.get(i).toString());
			listItems.add(maps);
		}
		
		
		SimpleAdapter adapter = new SimpleAdapter(this,listItems,
				R.layout.loadaddressbook_items,new String[]{"image","title","nums"},new int[]{R.id.loadaddressbook_icons,R.id.loadshortmessage_titles,R.id.loadaddressbook_nums});
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                    int position, long id) {
            	if(position==0){
            		return;}
            	AlertDialog alert = new AlertDialog.Builder(LoadAddressbook.this,AlertDialog.THEME_HOLO_LIGHT).create();
            	alert.setIcon(R.drawable.user);
            	alert.setTitle(mContactsName.get(position-1));
            	alert.setMessage("电话：  "+mContactsNumber.get(position-1));
            	final int posi = position-1;
            	alert.setButton(DialogInterface.BUTTON_NEUTRAL, "拨打", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//调用系统方法拨打电话
		                Intent dialIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + mContactsNumber.get(posi)));
		                startActivity(dialIntent);
		                //直接拨打电话
		                //startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:"+"15010824958")));
					}
				});
            	alert.setButton(DialogInterface.BUTTON_POSITIVE, "发短信", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//发送短信
						Uri uri = Uri.parse("smsto:"+ mContactsNumber.get(posi)); 	
		            	Intent it = new Intent(Intent.ACTION_SENDTO, uri); 
		            	it.putExtra("sms_body", "From Dreambox: "); 
		            	startActivity(it);
					}
				});
            	alert.show();
            	
                
            	//查看某个联系人，当然这里是ACTION_VIEW，如果为选择并返回action改为ACTION_PICK，当然处理intent时返回需要用到 startActivityforResult 
            	/*Log.i("list", id+"");
            	Log.i("lister", mContactsId.get(position)+"");
            	Uri personUri = ContentUris.withAppendedId(People.CONTENT_URI, 10);//最后的ID参数为联系人Provider中的数据库BaseID，即哪一行 
            	Intent dialIntent = new Intent(); 
            	dialIntent.setAction(Intent.ACTION_VIEW); 
            	dialIntent.setData(personUri);
            	startActivity(dialIntent);*/
            	                                
            }

        });
	}
	
	
	/**得到手机通讯录联系人信息**/
    private void getPhoneContacts() {
        ContentResolver resolver = this.getContentResolver();

        // 获取手机联系人
        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null,
        		android.provider.ContactsContract.Contacts.DISPLAY_NAME + " ASC");
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {

                //得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                //当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                
                //得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
                
                //得到联系人ID
                long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);

                //得到联系人头像ID
                long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
                
                //得到联系人头像Bitamp
                Bitmap contactPhoto = null;

                //photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
                if(photoid > 0 ) {
                    Uri uri =ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,contactid);
                    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
                    contactPhoto = BitmapFactory.decodeStream(input);
                }else {
                    contactPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.user);
                }
                
                mContactsName.add(contactName);
                mContactsNumber.add(phoneNumber);
                mContactsPhoto.add(contactPhoto);
                mContactsId.add(contactid);
            }

            phoneCursor.close();
        }
    }
    
    /**得到手机SIM卡联系人人信息**/
    private void getSIMContacts() {
        ContentResolver resolver = this.getContentResolver();
        // 获取Sims卡联系人
        Uri uri = Uri.parse("content://icc/adn");
        Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,
                null);

        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {

                // 得到手机号码
                String phoneNumber = phoneCursor.getString(2);
                // 当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                // 得到联系人名称
                String contactName = phoneCursor.getString(1);
                //得到联系人ID
                Long contactid = phoneCursor.getLong(0);
                
                //Sim卡中没有联系人头像
                
                mContactsName.add(contactName);
                mContactsNumber.add(phoneNumber);
                mContactsId.add(contactid);
                Log.v("LoadAddressbook_name_lines204", contactName);
                Log.v("LoadAddressbook_number_lines205", phoneNumber);
            }
            phoneCursor.close();
        }
    }
}
