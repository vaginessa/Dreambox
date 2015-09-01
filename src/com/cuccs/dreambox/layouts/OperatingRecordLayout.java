package com.cuccs.dreambox.layouts;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.cuccs.dreambox.R;
import com.cuccs.dreambox.layouts.SlidingMenuContainer.OnSlideListener;
import com.cuccs.dreambox.objects.CardData_restore;
import com.cuccs.dreambox.objects.CustomTextDialog;
import com.cuccs.dreambox.utils.AppAccountInfo;
import com.cuccs.dreambox.utils.LogRecorder_Operating;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class OperatingRecordLayout extends FrameLayout{
	private Dialog mDialog;
	private Context mContext;
	private Button Btn_Back, Btn_Clear_record;
	private ListView mListView;
	public static SimpleAdapter adapter;
	private boolean hasFinished = false;
	private OnSlideListener mOnSlideListener;
	ArrayList<CardData_restore> mCardsData = new ArrayList<CardData_restore>();
	List<Map<String,Object>> listItemsData = new ArrayList<Map<String,Object>>();		//创建list数据
	
	public OperatingRecordLayout(Context context) {  
        super(context);  
        this.mContext = context;
        setupViews();  
    }  
  
    public OperatingRecordLayout(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        this.mContext = context;
        setupViews();  
    }

    public void setOnSlideListener(OnSlideListener onSlideListener) {  
        mOnSlideListener = onSlideListener;  
    }
    
    public void setupViews() {  
        final LayoutInflater mLayoutInflater = LayoutInflater.from(getContext());  
        LinearLayout mOperating = (LinearLayout) mLayoutInflater.inflate(R.layout.menu_operatingrecord, null);  
        addView(mOperating);
        
        mCardsData.clear();
        listItemsData.clear();
        int itemNum = getCardsData();		//获取卡片信息
        mListView = (ListView) mOperating.findViewById(R.id.menu_operating_listview); 
        for(int i=0;i<mCardsData.size();i++){
			Map<String,Object> map = new HashMap<String, Object>();			//实例化map对象
			map.put("operatingType", mCardsData.get(i).mType);
			map.put("date", mCardsData.get(i).mTitle);
			map.put("content", mCardsData.get(i).mContent);
			listItemsData.add(map);
		}
		adapter = new SimpleAdapter(mContext,listItemsData,
				R.layout.menu_operatingrecord_items,new String[]{"operatingType","date","content"},
				new int[]{R.id.menu_operatingrecord_items_title,R.id.menu_operatingrecord_items_date,
				R.id.menu_operatingrecord_items_content});
		adapter.notifyDataSetChanged();	//更新mListView
		mListView.setAdapter(adapter);
		
		Btn_Clear_record = (Button) mOperating.findViewById(R.id.menu_operating_button);  
		Btn_Clear_record.getBackground().setAlpha(200);
		Btn_Clear_record.setOnClickListener(new OnClickListener() {  
            public void onClick(View v) {
            	CustomTextDialog.Builder customBuilder = new
						CustomTextDialog.Builder(mContext);
		            customBuilder.setTitle("警告!")
		            	.setIcon(R.drawable.confirm_dialog_warning)
		                .setMessage("您确定清空所有操作记录吗?\n清空后无法找回 ")
		                .setNegativeButton("取 消", 
		                        new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog01, int which) {
		                    	dialog01.dismiss(); 
		                    }
		                })
		                .setPositiveButton("清 空", 
		                        new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog02, int which) {
		                    	new LogRecorder_Operating(mContext).deleteAllRecord();
		                    	Toast.makeText(mContext, "操作记录已清空!", Toast.LENGTH_SHORT).show();
		                        dialog02.dismiss();
		                    }
		                });
		            mDialog = customBuilder.create();
		            mDialog.show();
            }
        });
        Btn_Back = (Button) mOperating.findViewById(R.id.operating_icon_back);  
        Btn_Back.setOnClickListener(new OnClickListener() {  
            public void onClick(View v) {
                if (mOnSlideListener != null) { 
                    mOnSlideListener.toRight();  
                }
            }
        });
        if(itemNum == 0){
			Btn_Clear_record.setText("还没有进行过操作");
			Btn_Clear_record.setEnabled(false);
		}
    }
    
    /**
	 * 获取卡片数据
	 */
	public int getCardsData(){
		int nums = 0;
		if(hasFinished == true){
			return nums;
		}
		//打开或创建backuplog.db数据库  
		File dbFile=new File("/data/data/com.cuccs.dreambox/databases/" +
				AppAccountInfo.getUsername(mContext) + "_operatinglog.db");
		if(!dbFile.exists()){
			return nums;
		}
		System.out.println();
		SQLiteDatabase mRecorderDB = mContext.openOrCreateDatabase(
				AppAccountInfo.getUsername(mContext) + "_operatinglog.db", Context.MODE_PRIVATE, null);
		System.out.println(mRecorderDB.getPath());	//========================
		mRecorderDB.execSQL("CREATE TABLE IF NOT EXISTS operatinglog("
				+ "_date VARCHAR, _type VARCHAR, dirname VARCHAR"
				+ ", contactsnum INT, smsnum INT, callsnum INT, photosnum INT, documentsnum INT, musicsnum INT)"); //创建表
		Cursor mcursor = mRecorderDB.rawQuery("SELECT * FROM operatinglog ORDER By _date DESC", 
					null);
		/*ListView 分页加载,主要是要搞明白数据库的查询操作 */
		Log.e("Content_Restore_getCount_line104==>", mcursor.getCount()+"");
		
		while (mcursor.moveToNext()) {
			CardData_restore metadata = new CardData_restore();
			SimpleDateFormat minFormat = new SimpleDateFormat("HH:mm:ss");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");  //注意此处小时不能用小写h，是12小时制
			long date = mcursor.getLong(mcursor.getColumnIndex("_date"));
			
			long curdate =  System.currentTimeMillis();    //获取当前时间,用来判断是否是今天、昨天、或者前天      
			Calendar mCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));  //获取东八区时间
			int hour = mCalendar.getTime().getHours();
			int minute = mCalendar.getTime().getMinutes();
			int second = mCalendar.getTime().getSeconds();
			long reduce = (hour*60*60 + minute*60 + second)*1000;
			long sign_yesterday = curdate - reduce;
			long sign_before_yesterday = sign_yesterday - (24*60*60*1000);
			
			if(date >= sign_yesterday && date <= curdate){
				metadata.mTitle = "今天  "+minFormat.format(date);
			}else if(date >= sign_before_yesterday && date < sign_yesterday){
				metadata.mTitle = "昨天  "+minFormat.format(date);
			}else{
				metadata.mTitle = dateFormat.format(date);
			}
			String[] itemname = new String[]{"联系人","短信","通话记录","照片相册","文档","音乐"};
			metadata.mContentInfo[0] = mcursor.getInt(mcursor.getColumnIndex("contactsnum"));
			metadata.mContentInfo[1] = mcursor.getInt(mcursor.getColumnIndex("smsnum"));
			metadata.mContentInfo[2] = mcursor.getInt(mcursor.getColumnIndex("callsnum"));
			metadata.mContentInfo[3] = mcursor.getInt(mcursor.getColumnIndex("photosnum"));
			metadata.mContentInfo[4] = mcursor.getInt(mcursor.getColumnIndex("documentsnum"));
			metadata.mContentInfo[5] = mcursor.getInt(mcursor.getColumnIndex("musicsnum"));
			int m = 0;
			boolean isEmpty = true;
			for(int i=0;i<6;i++){
				if(metadata.mContentInfo[i] != 0){
					isEmpty = false;
					m++;
					if(m == 4){
						metadata.mContent = metadata.mContent +"\n";
					}
					metadata.mContent = metadata.mContent+"  "+itemname[i]+" "+metadata.mContentInfo[i];
				}
			}
			metadata.mDate = mcursor.getString(mcursor.getColumnIndex("_date"));
			metadata.mType = mcursor.getString(mcursor.getColumnIndex("_type"));
			metadata.mParentDir = mcursor.getString(mcursor.getColumnIndex("dirname"));
			if(isEmpty == false){		//如果恢复项目不为空，添加到卡片中
				mCardsData.add(metadata);
			}else{						//如果恢复项目为空，删除掉备份日志记录信息
				new LogRecorder_Operating(mContext).deleteRecord(metadata.mDate);
			}
		}
		nums = mcursor.getCount();
		mcursor.close(); 
		mRecorderDB.close();
		return nums;
	}
}
