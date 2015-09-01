package com.cuccs.dreambox;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cuccs.dreambox.objects.CustomListDialog;
import com.cuccs.dreambox.objects.myBackupListviewAdapter;
import com.cuccs.dreambox.utils.AppAccountInfo;
import com.cuccs.dreambox.utils.B_R_AddressBook;
import com.cuccs.dreambox.utils.B_R_PhoneCalls;
import com.cuccs.dreambox.utils.B_R_SMS;
import com.cuccs.dreambox.utils.CheckNetwork;
import com.cuccs.dreambox.utils.ProgressThread_Backup_cloud;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class Content_BackUp extends Activity{
	private Button Btn_Back,Btn_SelectAll;
	private Button Btn_SDcardBackup,Btn_CloudBackup;
	private boolean isSelectedAll = false;
	private Context mContext;
	
	ListView listView;
	List<Map<String,Object>> listItemsData = new ArrayList<Map<String,Object>>();		//创建list数据
	private myBackupListviewAdapter mListviewAdapter;
	static final int PROGRESS_DIALOG = 0;  
	Thread  mThread_getCounter;
    ProgressThread_Backup_cloud progressThread;  
    ProgressDialog progressDialog; 
    Dialog mDialog;
	
	// Define the Handler that receives messages from the thread and update the progress  
    final Handler mhandler = new Handler() {  
        public void handleMessage(Message msg) {
        	switch (msg.what) {
        		case 111:
        			int total = msg.getData().getInt("total");  
        			String str = msg.getData().getString("ps");
        			progressDialog.setProgress(total);
        			progressDialog.setMessage(str);
        			Log.e("total-----> ", total+"");
        			if (total >= 100){  
        				dismissDialog(PROGRESS_DIALOG);  
        				progressThread.setState(ProgressThread_Backup_cloud.STATE_DONE);  
        			} 
        			break;
        		case 100:
        			mListviewAdapter.notifyDataSetChanged();
        			listView.setAdapter(mListviewAdapter);
					break;
        	}
        	super.handleMessage(msg);
        }  
    };   
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this.getApplicationContext();
		setContentView(R.layout.content_backup);
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 	//开启屏幕常亮
		
		final int[] imageId = new int[]{R.drawable.icon_addressbook,R.drawable.icon_bubbles,R.drawable.icon_phone,R.drawable.icon_images,R.drawable.icon_book,R.drawable.icon_music};
		final String[] title = new String[]{"联系人","短信","通话记录","照片相册","文档","音乐"};
		String[] counter = new String[]{"正在读取..","正在读取..","正在读取..","正在读取..","正在读取..","正在读取.."};
		listView = (ListView)findViewById(R.id.backup_listview);
		
		for(int i=0;i<imageId.length;i++){
			Map<String,Object> map = new HashMap<String, Object>();			//实例化map对象
			map.put("image", imageId[i]);
			map.put("title", title[i]);
			map.put("counter", counter[i]);
			listItemsData.add(map);
		}
		mListviewAdapter = new myBackupListviewAdapter(this);
		mListviewAdapter.setData(listItemsData);
		listView.setAdapter(mListviewAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
                    int position, long id) {
				mDialog = builderDialog3();
				mDialog.show();
				Thread mDismissThread = new Thread(){
					public void run() {
						mDialog.dismiss();
					}
		        };
				switch(position){
				case 0:
					Intent Addressintent = new Intent(Content_BackUp.this, LoadAddressbook.class);  
			        startActivity(Addressintent);
					break;
				case 1:
					Intent Messageintent = new Intent(Content_BackUp.this, LoadShortMessage.class);  
			        startActivity(Messageintent);
					break;
				case 2:
					Intent PhoneCallsintent = new Intent(Content_BackUp.this, LoadPhonecalls.class);  
			        startActivity(PhoneCallsintent);
					break;
				case 3:
					Intent Photointent = new Intent(Content_BackUp.this, LoadPhotoAlbum.class);  
			        startActivity(Photointent);
					break;
				case 4:
					break;
				}
				mDismissThread.start();
			}});
		
		mThread_getCounter = new Thread(new Runnable() {    //读取各项记录条数线程
			public void run(){
				List<Map<String,Object>> mlistItemsData = new ArrayList<Map<String,Object>>();		//创建list数据
				String mcounter[] = new String[]{"正在读取..","正在读取..","正在读取..","正在读取..","正在读取..","正在读取.."};
				
				getItemCounter(mcounter);
				mlistItemsData.clear();
				for(int i=0;i<6;i++){
					Map<String,Object> map = new HashMap<String, Object>();			//实例化map对象
					map.put("image", imageId[i]);
					map.put("title", title[i]);
					map.put("counter", mcounter[i]);
					mlistItemsData.add(map);
				}
				mListviewAdapter.setData(mlistItemsData);
				Message message = new Message(); 
                message.what = 100 ;			//myHandler.GUIUPDATEIDENTIFIER;   
                mhandler.sendMessage(message);              //给主线程发送Message处理UI界面 
			}
		});
		mThread_getCounter.start();
		
		  Btn_Back = (Button) findViewById(R.id.backup_icon_back);  
		  Btn_Back.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	                finish();
	                overridePendingTransition(R.anim.holding_anima, R.anim.translate_right_exit);
	            }  
	        });
	      Btn_SelectAll = (Button) findViewById(R.id.backup_icon_selectall); 
	      Btn_SelectAll.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	            	if(isSelectedAll == false){	//全选
	            		isSelectedAll = true;
	            		Btn_SelectAll.setBackgroundResource(R.drawable.btn_check_on_normal);
		            	for (int i = 0; i < mListviewAdapter.getCount(); i++) {
		            		mListviewAdapter.getIsSelected().put(i, true);
		    			}
		            	mListviewAdapter.notifyDataSetChanged();
	            	}else{		//取消全选
	            		isSelectedAll = false;
	            		Btn_SelectAll.setBackgroundResource(R.drawable.btn_check_off_normal);
		            	for (int i = 0; i < mListviewAdapter.getCount(); i++) {
		            		mListviewAdapter.getIsSelected().put(i, false);
		    			}
		            	mListviewAdapter.notifyDataSetChanged();
	            	}
	            	
	            }  
	        });
	      Btn_SDcardBackup = (Button) findViewById(R.id.backup_Btn_SDcard);
	      Btn_SDcardBackup.setOnClickListener(new OnClickListener() {  
	            public void onClick(View v) {
	                //开始本地备份所选项目,*****照片相册不进行备份*******
	            	/* 
	                 * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到 
	                 * */
	            	try {
	            		CustomListDialog mDialog = new CustomListDialog(Content_BackUp.this, 
	    						R.style.Theme_ShareDialog, mListviewAdapter.getIsSelected(), false);
	            		mDialog.setUseType(true);	//指明执行备份操作,而不是恢复
	            		mDialog.show();
	            		mDialog.setCancelable(false);  //点击对话框外侧不关闭写法
	            		mDialog.setIcon(R.drawable.icon_mobile);
	            		mDialog.setTitle("备份到SD卡");
	            		
					} catch (Exception e) {
						e.printStackTrace();
					}
	            }  
	        });
	      Btn_CloudBackup = (Button) findViewById(R.id.backup_Btn_Cloud);
	      Btn_CloudBackup.setOnClickListener(new OnClickListener() {  
	            public void onClick(View v) {
	                //开始进行云端备份
	            	CheckNetwork checknet = new CheckNetwork(mContext);		//检查网络连接是否正常
	    			if(checknet.isConnectingToInternet() == false){
	    				return;
	    			}
	    			if(AppAccountInfo.getUsername(mContext) == null){
	    				Intent intent = new Intent(Content_BackUp.this, LoginActivity.class);  
				        startActivity(intent);
	    			}
	    			//showDialog(PROGRESS_DIALOG);
	    			CustomListDialog mDialog = new CustomListDialog(Content_BackUp.this, 
	    						R.style.Theme_ShareDialog, mListviewAdapter.getIsSelected(), true);
	    			mDialog.setUseType(true);	//指明执行备份操作,而不是恢复
	    			mDialog.show();
	    			mDialog.setCancelable(false);  //点击对话框外侧不关闭写法
	    			mDialog.setTitle("备份到云端");
	            }  
	        });
	}
	
	public Dialog builderDialog3(){  
		ProgressDialog pro_dialog = new ProgressDialog(this); 
		 pro_dialog.setMessage("正在读取,请稍候...");  
		return pro_dialog; 
	}  
	
	protected Dialog onCreateDialog(int id) {  
        switch(id) {  
        case PROGRESS_DIALOG:  
            progressDialog = new ProgressDialog(Content_BackUp.this,ProgressDialog.THEME_HOLO_LIGHT);  
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);  
            progressDialog.setMessage("Loading...");  
            progressDialog.setCancelable(false);	//防止点击对话框外取消dialog
            // 不关闭Dialog的写法
   		 	try
   		 	{
   		 		Field field = progressDialog.getClass().getSuperclass().getDeclaredField("mShowing");
   		 		field.setAccessible(true);
   		 		//设置mShowing值，欺骗android系统
   		 		field.set(progressDialog, false);
   		 	}catch(Exception e) {
   		 		e.printStackTrace();
   		 	}
            //progressThread = new ProgressThread_Backup_cloud(mhandler,mContext);  
            //progressThread.start();  
            return progressDialog;  
        default:  
            return null;  
        }  
    }   
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {     //返回键退出
	    if(keyCode == KeyEvent.KEYCODE_BACK 
	            && event.getAction() == KeyEvent.ACTION_DOWN){   
	    	finish();
            overridePendingTransition(R.anim.holding_anima, R.anim.contend_minify_ld);
	        return true;   
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	public void getItemCounter(String[] counter){
		counter[0] = "记录" + B_R_AddressBook.getItemQuantity(mContext) + "条";
		counter[1] = "记录" + B_R_SMS.getItemQuantity(mContext) + "条";
		counter[2] = "记录" + B_R_PhoneCalls.getItemQuantity(mContext) + "条";
		counter[3] = "记录0条";
		counter[4] = "记录0条";
		counter[5] = "记录0条";
	}
}
