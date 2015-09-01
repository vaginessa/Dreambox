package com.cuccs.dreambox.utils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import com.baidu.inf.iis.bcs.BaiduBCS;
import com.baidu.inf.iis.bcs.auth.BCSCredentials;
import com.baidu.inf.iis.bcs.model.BCSClientException;
import com.baidu.inf.iis.bcs.model.BCSServiceException;
import com.baidu.inf.iis.bcs.request.GetObjectRequest;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

public class ProgressThread_Restore_cloud extends Thread {
	public static final int MSG_DOWNLOAD_SUCCESS = 665;
	public static final int MSG_DOWNLOAD_WHAT = 666;
	public static final int MSG_SUCCESS_WHAT = 999;
	public static final int MSG_FAILED_WHAT = 1000;
	public HashMap<Integer, Boolean> ItemisSelected;
	public String parentDir;
	public Context mContext;
	public Handler mHandler;
	
	public ProgressThread_Restore_cloud(Handler h, Context mContext, String parentDir, HashMap<Integer, Boolean> ItemisSelected) {  
        mHandler = h;
        this.mContext = mContext;
        this.parentDir = parentDir;
        this.ItemisSelected = ItemisSelected;
    }  
	
	public void run() {
		Message msg;
		Looper.prepare();
		
		msg = mHandler.obtainMessage();
		msg.what = MSG_DOWNLOAD_WHAT;
		mHandler.sendMessage(msg);
		BCSCredentials credentials = new BCSCredentials(AppAutoConstants.Baidu_BCS.accessKey,
				AppAutoConstants.Baidu_BCS.secretKey);
		BaiduBCS baiduBCS = new BaiduBCS(credentials, AppAutoConstants.Baidu_BCS.host);
		baiduBCS.setDefaultEncoding("UTF-8");
		for(int i=0;i<6;i++){	//云端下载备份文件
			if(ItemisSelected.get(i) == false){
				continue;
			}
			String path,filename = null;
			String dirpath = AppFolderPaths.getBackupFilesDir(mContext)+"/"+parentDir;
			File destfile, dirfile = new File(dirpath);
			try {
				dirfile.mkdirs();	//如果目录不存在，创建目录
				dirfile.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			switch (i) {
			case 0:
				filename = "contacts.db";
				break;
			case 1:
				filename = "message.xml";
				break;
			case 2:
				filename = "phonecalls.db";
				break;
			default:
				break;
			}
			if(filename.equals(null) == false && filename.equals("") == false){
				path = dirpath+"/"+filename;
				destfile = new File(path);
				try {
					if(destfile.exists()){	//如果文件已经存在，先删除
						destfile.delete();
					}
					destfile.createNewFile();
					getObjectWithDestFile(baiduBCS, "/" +
							AppAccountInfo.getUsername(mContext)+"/"+parentDir+"/"+filename, destfile);
				}catch (IOException e) {
					e.printStackTrace();
				}catch (BCSServiceException e) {
					AppAutoConstants.Baidu_BCS.log.warn("Bcs return:" + e.getBcsErrorCode() + ", " + e.getBcsErrorMessage() + ", RequestId=" + e.getRequestId());
					if(e.getRequestId() == null){
						msg = mHandler.obtainMessage();
						msg.what = MSG_FAILED_WHAT;
						mHandler.sendMessage(msg);
						return;
					}
				} catch (BCSClientException e) {
					e.printStackTrace();
					msg = mHandler.obtainMessage();
					msg.what = MSG_FAILED_WHAT;
					mHandler.sendMessage(msg);
					return;
				}
				
			}
		}
		
		msg = mHandler.obtainMessage();
		msg.what = MSG_DOWNLOAD_SUCCESS;
		mHandler.sendMessage(msg);
		for(int i=0;i<6;i++){
			if(ItemisSelected.get(i) == false){
				continue;
			}
			switch (i) {
			case 0:
				new B_R_AddressBook(mContext, parentDir, mHandler).restoreFromDB();
				break;
			case 1:
				new B_R_SMS(mContext, parentDir, mHandler).restoreFromXml();
				break;
			case 2:
				new B_R_PhoneCalls(mContext, parentDir, mHandler).restoreFromDB();
				break;
			default:
				break;
			}
		}
		msg = mHandler.obtainMessage();
		msg.what = MSG_SUCCESS_WHAT;
		mHandler.sendMessage(msg);
		
		Looper.loop();
	}
	
	private static void getObjectWithDestFile(BaiduBCS baiduBCS, String object, File destFile) {
		GetObjectRequest getObjectRequest = new GetObjectRequest(AppAutoConstants.Baidu_BCS.bucket, object);
		baiduBCS.getObject(getObjectRequest, destFile);
	}

} 