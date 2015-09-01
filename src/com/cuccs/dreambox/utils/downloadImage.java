package com.cuccs.dreambox.utils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Handler;
import android.os.Message;

public class downloadImage {
	String sourceURL = "";
	File file = new File(new AppFolderPaths().getRootDir(null) +"/UserHeadshow/000000.jpg");
	
	public downloadImage(String sourceURL){
		this.sourceURL = sourceURL;
	}
	
	private Handler handler = new Handler() {  
		  
        public void handleMessage(Message msg) {  
            switch (msg.what) {  
                case 0:  
                    break;  
            }  
        };  
    }; 
    
	public void startdown(){
	new Thread(){  
        @Override  
        public void run() {  
        	try {  
        		  URL url = new URL(sourceURL);
                  HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                  InputStream is = urlCon.getInputStream();
                  if(is!=null){
                	  /*String expandName = sourceURL.substring(sourceURL.lastIndexOf(".")+1,
                			  sourceURL.length()).toLowerCase();		//获取文件扩展名
                	  String fileName = sourceURL.substring(sourceURL.lastIndexOf("/")+1,
                			  sourceURL.lastIndexOf("."));			//获取文件名*/                	  
                	  FileOutputStream fos = new FileOutputStream(file);
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
                e.printStackTrace();  
            }  
        	
            handler.sendEmptyMessage(0);  
        	}  
    	}.start();
	}	
	
}
