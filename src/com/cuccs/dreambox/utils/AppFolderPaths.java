package com.cuccs.dreambox.utils;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

public class AppFolderPaths {
	
	/**获取根目录路径*/
	public static String getRootDir(Context context){
		String RootDirectory;		//本应用根目录路径
		final String SDcardPath;
		boolean sdCardExist = Environment.getExternalStorageState()
				.equals(android.os.Environment.MEDIA_MOUNTED);		//判断SD卡是否存在 
		if(sdCardExist){ 
			//mShowToast(context, "亲,您的SD卡一切正常");
			SDcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
			RootDirectory = SDcardPath+"/DreamBox";
			File dir= new File(RootDirectory);  //创建根目录
			if (!dir.exists()) {
				dir.mkdirs();
			}
	    }else{
	    	mShowToast(context, "亲,爱机为何还没有SD卡呢");
	    	SDcardPath = Environment.getDataDirectory().getAbsolutePath();
	    	RootDirectory = SDcardPath+"/DreamBox";
			File dir= new File(RootDirectory);  //创建内部存储的根目录
			if (!dir.exists()) {
				dir.mkdirs();
			}
	    }
		return RootDirectory;
	}
	
	/**获取备份文件夹路径*/
	public static String getBackupFilesDir(Context context){
		String BackupFilesDir = getRootDir(context) + "/BackupFiles";
		return BackupFilesDir;
	}
	
	public static void mShowToast(Context context, String msg){
		if(context != null){
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		}
	}
	
}
