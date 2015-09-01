package com.cuccs.dreambox.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cuccs.dreambox.R;
import com.cuccs.dreambox.utils.AppSettings;
import com.cuccs.dreambox.utils.B_R_AddressBook;
import com.cuccs.dreambox.utils.B_R_PhoneCalls;
import com.cuccs.dreambox.utils.B_R_SMS;
import com.cuccs.dreambox.utils.CheckNetwork;
import com.cuccs.dreambox.utils.LogRecorder_Operating;
import com.cuccs.dreambox.utils.ProgressThread_Backup_cloud;
import com.cuccs.dreambox.utils.ProgressThread_Backup_sdcard;
import com.cuccs.dreambox.utils.ProgressThread_Restore_cloud;
import com.cuccs.dreambox.utils.ProgressThread_Restore_sdcard;

import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class CustomListDialog extends Dialog{
	public static final int MSG_ADDRESSBOOK_WHAT = 100;
	public static final int MSG_SMS_WHAT = 101;
	public static final int MSG_PHONECALLS_WHAT = 102;
	public static final int MSG_UPLOAD_WHAT = 777;
	public static final int MSG_DOWNLOAD_WHAT = 666;
	public static final int MSG_DOWNLOAD_SUCCESS = 665;
	public static final int MSG_CLEARCACHE_WHAT = 888;
	public static final int MSG_SUCCESS_WHAT = 999;
	public static final int MSG_FAILED_WHAT = 1000;
	
	private PowerManager powerManager = null;
	private WakeLock wakeLock = null;
	public HashMap<Integer, Boolean> ItemisSelected;
	private boolean isBackupNotRestore = false;	//指明执行备份操作,还是恢复
	private boolean isCloudUsed = false;
	private String mDirname_restore = null;
	private Context mContext;
	private TextView mTitle;
	private ImageView mIcon;
	private LinearLayout mLinearLayout;
	private Button mBtn_Goon, mBtn_Cancle, mBtn_OK;
	private MediaPlayer mMediaPlayer;
	private ListView mListview;
	private SimpleAdapter adapter;
	private List<Map<String,Object>> listItems;
	final String[] titles = new String[]{"联系人","短信","通话记录","照片相册","文档","音乐"};
	private int[] itemposition = new int[6];
	private int[] iteminfo = new int[6];
	
	final Handler mhandler = new Handler() {  
        public void handleMessage(Message msg) {
        	Map<String,Object> maps = new HashMap<String, Object>();			//实例化map对象
        	int total = 0;
        	switch (msg.what) {
        		case MSG_ADDRESSBOOK_WHAT:
        			total = msg.getData().getInt("total"); 
        			Log.e("total-----> ", total+" "+itemposition[0]);
        			listItems.get(itemposition[0]).clear();
        			maps.clear();
        			maps.put("title", titles[0]);
        			maps.put("info", total+"/"+iteminfo[0]);
        			listItems.set(0, maps);
        			adapter.notifyDataSetChanged();
        			break;
        			
        		case MSG_SMS_WHAT:
        			total = msg.getData().getInt("total"); 
        			Log.e("total-----> ", total+" "+itemposition[1]);
        			listItems.get(itemposition[1]).clear();
        			maps.clear();
        			maps.put("title", titles[1]);
        			maps.put("info", total+"/"+iteminfo[1]);
        			listItems.set(itemposition[1], maps);
        			adapter.notifyDataSetChanged();
        			break;
        			
        		case MSG_PHONECALLS_WHAT:
        			total = msg.getData().getInt("total"); 
        			Log.e("total-----> ", total+" "+itemposition[2]);
        			listItems.get(itemposition[2]).clear();
        			maps.clear();
        			maps.put("title", titles[2]);
        			maps.put("info", total+"/"+iteminfo[2]);
        			listItems.set(itemposition[2], maps);
        			adapter.notifyDataSetChanged();
        			break;
        			
        		case MSG_UPLOAD_WHAT:
        			mTitle.setText("正在上传到云端...");
        			mIcon.setImageResource(R.drawable.cloud_upload);
        			break;
        			
        		case MSG_DOWNLOAD_WHAT:
        			mTitle.setText("正在从云端下载...");
        			mIcon.setImageResource(R.drawable.cloud_download);
        			break;
        			
        		case MSG_DOWNLOAD_SUCCESS:
        			mTitle.setText("下载完毕,恢复开始...");
        			mIcon.setImageResource(R.drawable.finished_mark_pressed);
        			break;
        			
        		case MSG_CLEARCACHE_WHAT:
        			mTitle.setText("正在清理本地缓存...");
        			mIcon.setImageResource(R.drawable.clear_cache_normal);
        			break;
        			
        		case MSG_FAILED_WHAT:
        			Vibrator mVibrator01 = (Vibrator)mContext.getSystemService(Context.VIBRATOR_SERVICE);
    				mVibrator01.vibrate( new long[]{0,200,0,0}, -1);	 //开始 震动
        			mTitle.setText("任务失败!");
        			mBtn_Cancle.setClickable(true);		//取消键解封
        			mIcon.setImageResource(R.drawable.confirm_dialog_warning);
        			mLinearLayout.setVisibility(View.INVISIBLE);	//两个按键变为一个确定键
        			mBtn_OK.setVisibility(View.VISIBLE);
        			mBtn_OK.setClickable(true);
        			break;
        			
        		case MSG_SUCCESS_WHAT:
        			Vibrator mVibrator = (Vibrator)mContext.getSystemService(Context.VIBRATOR_SERVICE);
    				mVibrator.vibrate( new long[]{0,100,300,100}, -1);	 //开始 震动
        			AppSettings.readSettings(mContext);
        			if(AppSettings.SoundOn == true){	//音效设置开启，则播放声音
        				mMediaPlayer.start();
        			}
        			if(isBackupNotRestore == true){
        				mTitle.setText("OK,备份完毕!");		//备份的操作记录日志 已经在备份过程中完成
        			}else{
        				mTitle.setText("OK,恢复完成!");
        				Date curDate = new Date(System.currentTimeMillis());//获取当前时间     
        				if(isCloudUsed == false){
        					new LogRecorder_Operating(mContext).addRecord(curDate.getTime(), "SD卡恢复", "time_str", iteminfo);	//写入日志
        				}else{
        					new LogRecorder_Operating(mContext).addRecord(curDate.getTime(), "云端恢复", "time_str", iteminfo);	//写入日志
        				}
        				
        			}
        			mBtn_Cancle.setClickable(true);		//取消键解封
        			mIcon.setImageResource(R.drawable.btn_check_on_normal);
        			mLinearLayout.setVisibility(View.INVISIBLE);	//两个按键变为一个确定键
        			mBtn_OK.setVisibility(View.VISIBLE);
        			mBtn_OK.setClickable(true);
        			break;
        	}
        	super.handleMessage(msg);
        }  
    };   
	
    
	public CustomListDialog(Context context, int theme, HashMap<Integer,Boolean> ItemisSelected, boolean isCloudBackup){
        super(context, theme);
        this.mContext = context;
        this.ItemisSelected = ItemisSelected;
        this.isCloudUsed = isCloudBackup;
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_backup_cloud);
		powerManager = (PowerManager) mContext.getSystemService(Service.POWER_SERVICE);		//开启屏幕常亮
		wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Lock");
		wakeLock.setReferenceCounted(false);  //是否需计算锁的数量
		wakeLock.acquire();
		
		mMediaPlayer = MediaPlayer.create(mContext, R.raw.done);
		mLinearLayout = (LinearLayout)findViewById(R.id.dialog_backup_cloud_layout_bottom);
		mIcon = (ImageView)findViewById(R.id.dialog_backup_cloud_icon);
		mTitle = (TextView)findViewById(R.id.dialog_backup_cloud_title);
		mTitle.setText("AlertDialog");
		mBtn_Cancle = (Button)findViewById(R.id.dialog_backup_cloud_btn_cancle);
		mBtn_Cancle.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CustomListDialog.this.dismiss();
			}
		});
		mBtn_Goon = (Button)findViewById(R.id.dialog_backup_cloud_btn_goon);
		if(isBackupNotRestore == false){
			mBtn_Goon.setText("恢 复");
			mTitle.setText("恢复到\n"+mDirname_restore);
		}
		mBtn_Goon.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(isBackupNotRestore == true && isCloudUsed == true){	//云端备份
					ProgressThread_Backup_cloud mBcloudthread = new ProgressThread_Backup_cloud(mhandler, mContext, iteminfo, ItemisSelected);
					mBcloudthread.start();
					mTitle.setText("正在打包数据...");
					mBtn_Goon.setClickable(false);
					mBtn_Cancle.setClickable(false);
					return;
				}
				else if(isBackupNotRestore == false && isCloudUsed == true){	//云端恢复
					CheckNetwork checknet = new CheckNetwork(mContext);		//检查网络连接是否正常
					if(checknet.isConnectingToInternet() == false){
						return;
					}
					AppSettings.readSettings(mContext);		//读取用户设置
			        if(AppSettings.OnlyWiFi == true && checknet.isWiFiConnected() == true 
			        		|| AppSettings.OnlyWiFi == false){
			        	ProgressThread_Restore_cloud mRthread = new ProgressThread_Restore_cloud(
								mhandler, mContext, mDirname_restore, ItemisSelected); 
						mRthread.start();
						mTitle.setText("正在从云端恢复...");
						mBtn_Goon.setClickable(false);
						mBtn_Cancle.setClickable(false);
			        }
					return;
				}
				else if(isBackupNotRestore == true && isCloudUsed == false){	//本地SD卡备份
					ProgressThread_Backup_sdcard mBthread = new ProgressThread_Backup_sdcard(
							mhandler, mContext, iteminfo, ItemisSelected);
					mBthread.start();
					mTitle.setText("正在备份,请稍后...");
					mBtn_Goon.setClickable(false);
					mBtn_Cancle.setClickable(false);
					return;
				}
				else if(isBackupNotRestore == false && isCloudUsed == false){	//本地SD卡恢复
					ProgressThread_Restore_sdcard mRthread = new ProgressThread_Restore_sdcard(
							mhandler, mContext, mDirname_restore, ItemisSelected); 
					mRthread.start();
					mTitle.setText("正在从SD卡恢复...");
					mBtn_Goon.setClickable(false);
					mBtn_Cancle.setClickable(false);
					return;
				}
			}
		});
		mBtn_OK = (Button)findViewById(R.id.dialog_backup_cloud_btn_ok);
		mBtn_OK.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CustomListDialog.this.dismiss();
			}
		});
		
		mListview = (ListView)findViewById(R.id.dialog_backup_cloud_listview);
		listItems = new ArrayList<Map<String,Object>>();	//创建list
		if(isBackupNotRestore == true && ItemisSelected.size() != 0){
			for(int i=0;i<6;i++){
				if(ItemisSelected.get(i) == true){
					Map<String,Object> maps = new HashMap<String, Object>();			//实例化map对象
					maps.put("title", titles[i]);
					int quantuty = 0;
					switch(i){
						case 0:	//联系人
							quantuty = new B_R_AddressBook(mContext, null).getItemQuantity(mContext);
							break;
						case 1:	//短信
							quantuty = new B_R_SMS(mContext, null).getItemQuantity(mContext);
							break;
						case 2:	//通话记录
							quantuty = new B_R_PhoneCalls(mContext, null).getItemQuantity(mContext);
							break;
						case 3:	//照片相册
							break;
						case 4:	//电子书文档
							break;
						case 5:	//音乐
							break;
					}
					iteminfo[i] = quantuty;
					itemposition[i] = listItems.size();
					maps.put("info", "0/"+quantuty);
					listItems.add(maps);
				}
			}
		}
		if(isBackupNotRestore == false && ItemisSelected.size() != 0){
			for(int i=0;i<6;i++){
				if(ItemisSelected.get(i) == true){
					itemposition[i] = listItems.size();
					Map<String,Object> maps = new HashMap<String, Object>();			//实例化map对象
					maps.put("title", titles[i]);
					maps.put("info", "0/"+iteminfo[i]);
					listItems.add(maps);
				}
			}
		}
		if(listItems.size() == 0){		//提示尚未选择
			mLinearLayout.setVisibility(View.INVISIBLE);
			mBtn_OK.setVisibility(View.VISIBLE);
			mBtn_OK.setClickable(true);
			
			Map<String,Object> maps0 = new HashMap<String, Object>();			//实例化map对象
			maps0.put("title", "   ");
			maps0.put("info", "");
			listItems.add(maps0);
			Map<String,Object> maps = new HashMap<String, Object>();
			maps.put("title", "   抱歉, 还没有选择的项目");
			maps.put("info", "");
			listItems.add(maps);
			Map<String,Object> maps2 = new HashMap<String, Object>();
			maps2.put("title", "   ");
			maps2.put("info", "");
			listItems.add(maps2);
		}
		adapter = new SimpleAdapter(mContext,listItems,
				R.layout.dialog_backup_cloud_listview_items,new String[]{"title","info"},
				new int[]{R.id.dialog_backup_cloud_listview_title,R.id.dialog_backup_cloud_listview_info});
		mListview.setAdapter(adapter);
		
	}
	
	public void show(){
		super.show();
		//请求常亮
		if (wakeLock != null) {
			wakeLock.acquire();
		}
	}
	public void dismiss() {
		super.dismiss();
		//取消屏幕常亮
		if (wakeLock != null) {
			wakeLock.release();
		}
	}
	
	public void setTitle(String title){
		mTitle.setText(title);
	}
	public void setIcon(int resId){
		mIcon.setImageResource(resId);
	}
	public void setUseType(boolean isBackupOrNot){
		this.isBackupNotRestore = isBackupOrNot;
	}
	public void setParentDir(String parentDir){
		this.mDirname_restore = parentDir;
	}
	public void deliverItemInfo(int[] iteminfo){	/**预置信息，供恢复线程用*/
		this.iteminfo = iteminfo;
	}
}
