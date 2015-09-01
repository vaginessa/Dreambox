package com.cuccs.dreambox.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Handler;

public class DownloadData {
	public static final int MSG_DOWNLOAD_SUCCESS = 999;
	public static final int MSG_DOWNLOAD_FAILED = -1;
	Handler mHandler;
	int msgCode;
	String sourceURL = "";
	File destFile;
	String mDirPath = AppFolderPaths.getRootDir(null);
	
	
	public DownloadData(Handler mHandler, int msgCode, String sourceURL, File destFile){
		this.mHandler = mHandler;
		this.msgCode = msgCode;
		this.sourceURL = sourceURL;
		this.destFile = destFile;
	} 
    
	public void startdown(){
	new Thread(){  
        @Override  
        public void run() {  
        	try { 
        		  URL url = new URL(sourceURL);
                  HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                  urlCon.setConnectTimeout(7000);
                  InputStream is = urlCon.getInputStream();
                  if(is!=null){
                	  String expandName = sourceURL.substring(sourceURL.lastIndexOf(".")+1,
                			  sourceURL.length()).toLowerCase();		//获取文件扩展名
                	  String fileName = sourceURL.substring(sourceURL.lastIndexOf("/")+1,
                			  sourceURL.lastIndexOf("."));			//获取文件名     
                	  if(destFile == null){
                		  destFile = new File(mDirPath+ "/" +fileName+ "." +expandName);
                	  }
                	  if(destFile.exists()){
                		  destFile.delete();
                	  }
                	  FileOutputStream fos = new FileOutputStream(destFile);
                	  byte buf[] = new byte[128];
                	  while(true){
                		  int numread = is.read(buf);
                		  if(numread<=0){
                			  break;
                		  }else{
                			  fos.write(buf, 0, numread);
                		  }
                	  }
                	  is.close();
                	  urlCon.disconnect();
                  }
            } catch (Exception e) {  
            	mHandler.sendEmptyMessage(MSG_DOWNLOAD_FAILED);  
                e.printStackTrace();  
                return;
            }
        	if(msgCode == 0){
        		mHandler.sendEmptyMessage(MSG_DOWNLOAD_SUCCESS);  
        	}else{
        		mHandler.sendEmptyMessage(msgCode);  
        	}
        	
        }  
    	}.start();
	}	
	
}
