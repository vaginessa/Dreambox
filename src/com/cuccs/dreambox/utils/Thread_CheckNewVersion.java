package com.cuccs.dreambox.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import com.cuccs.dreambox.R;
import com.cuccs.dreambox.objects.CustomTextDialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Xml;
import android.widget.Toast;

public class Thread_CheckNewVersion extends Thread {
	public static final int MSG_DOWNLOAD_FAILED = -1;
	public static final int MSG_DOWNLOAD_SUCCESS = 999;
	public static final int MSG_Ver_DOWNLOAD_SUCCESS = 998;
	public static final int MSG_apk_DOWNLOAD_SUCCESS = 997;
	public Context mContext;
	public Dialog mDialog;
	public static String mNewversion;
	public static String mDescription;
	public static String mBCS_object = "/App_Info/NewVersion.xml";
	public  File versionFile;
	public  File apkFile;
	
	public Handler mHandler = new Handler() { 
		public void handleMessage(Message msg) {
			mDialog.dismiss();
			switch (msg.what) {
				case MSG_DOWNLOAD_FAILED:
					Toast.makeText(mContext, "下载失败,可能网络异常!", Toast.LENGTH_SHORT).show();
					break;
					
				case MSG_DOWNLOAD_SUCCESS:
					Toast.makeText(mContext, "下载完成!", Toast.LENGTH_SHORT).show();
					break;
					
				case MSG_Ver_DOWNLOAD_SUCCESS:
					try {
						mNewversion = getUpdataInfo(new FileInputStream(versionFile));
					}catch (Exception e) {
						Toast.makeText(mContext, "检测失败", Toast.LENGTH_SHORT).show();
						e.printStackTrace();
						return;
					}
					if(mNewversion.equals(getVersionName()) == false){
						CustomTextDialog.Builder customBuilder = new
								CustomTextDialog.Builder(mContext);
				            customBuilder.setTitle("发现新版本")
				            	.setIcon(R.drawable.confirm_dialog_warning)
				                .setMessage("现在更新吗?\n ")
				                .setNegativeButton("取 消", 
				                        new DialogInterface.OnClickListener() {
				                    public void onClick(DialogInterface dialog01, int which) {
				                    	dialog01.dismiss(); 
				                    }
				                })
				                .setPositiveButton("确 定", 
				                        new DialogInterface.OnClickListener() {
				                    public void onClick(DialogInterface dialog02, int which) {
				                    	Toast.makeText(mContext, "下载ing...", Toast.LENGTH_SHORT).show();
				                    	apkFile = new File(AppFolderPaths.getRootDir(mContext) + "/DreamboxNew.apk");
										new DownloadData(mHandler,MSG_apk_DOWNLOAD_SUCCESS,
												"http://bcs.duapp.com/dreambox/App_Info/DreamBox.apk", apkFile)
												.startdown();
										Toast.makeText(mContext, "下载Update:"+mDescription, Toast.LENGTH_SHORT).show();
				                        dialog02.dismiss();
				                    }
				                });
				            mDialog = customBuilder.create();
				            mDialog.show();
					}else{
						Toast.makeText(mContext, "已经是最新版本", Toast.LENGTH_SHORT).show();
					}
					break;
					
				case MSG_apk_DOWNLOAD_SUCCESS:
					versionFile.delete();	//删除版本信息数据
					installApk(apkFile);	//安装新版本
					break;
			}
			super.handleMessage(msg);
		}
	};
	
	
	public Thread_CheckNewVersion(Context mContext) {  
        this.mContext = mContext;
        mDialog = new Dialog(mContext, R.style.Theme_ShareDialog);
        mDialog.setContentView(R.layout.loading);
        mDialog.setCancelable(false);
        mDialog.show();
    }  
	
	public void run() {
		Looper.prepare();
		
		versionFile = new File(AppFolderPaths.getRootDir(mContext) + "/NewVersion.xml");
		new DownloadData(mHandler,MSG_Ver_DOWNLOAD_SUCCESS,
				"http://bcs.duapp.com/dreambox/App_Info/NewVersion.xml", versionFile)
				.startdown();
		
		Looper.loop();
	}
	
	
	/** 安装apk */     
	protected void installApk(File file) {  
	    Intent intent = new Intent();  
	    intent.setAction(Intent.ACTION_VIEW);  //执行动作  
	    //执行的数据类型  
	    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");  
	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    mContext.startActivity(intent);  
	}
	
	/** 获取当前程序的版本号  */  
	private String getVersionName(){  
	    //获取packagemanager的实例 
	    PackageManager packageManager = mContext.getPackageManager();  
	    //getPackageName()是你当前类的包名，0代表是获取版本信息  
	    PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}  
	    return packInfo.versionName;   
	}  
	
	/*** 用pull解析器解析服务器返回的xml文件 (xml封装了版本号) */  
	public String getUpdataInfo(InputStream is) throws Exception{
		String versionName = null;
	    XmlPullParser  parser = Xml.newPullParser();    
	    parser.setInput(is, "utf-8");	//设置解析的数据源   
	    int type = parser.getEventType();  
	    while(type != XmlPullParser.END_DOCUMENT ){  
	        switch (type) {  
	        case XmlPullParser.START_TAG:  
	            if("version".equals(parser.getName())){  
	            	versionName = parser.nextText(); //获取版本号  
	            }else if ("url".equals(parser.getName())){  
	                //info.setUrl(parser.nextText()); //获取要升级的APK文件  
	            }else if ("description".equals(parser.getName())){ 
	            	mDescription = parser.nextText(); //获取该文件的信息  
	            }  
	            break;  
	        }  
	        type = parser.next();  
	    }
		return versionName;  
	}  
} 