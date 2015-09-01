package com.cuccs.dreambox.objects;

import java.io.File;
import java.text.DecimalFormat;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.baidu.inf.iis.bcs.BaiduBCS;
import com.baidu.inf.iis.bcs.model.ObjectListing;
import com.baidu.inf.iis.bcs.model.ObjectMetadata;
import com.baidu.inf.iis.bcs.model.ObjectSummary;
import com.baidu.inf.iis.bcs.request.GetObjectRequest;
import com.baidu.inf.iis.bcs.request.ListObjectRequest;
import com.baidu.inf.iis.bcs.request.PutObjectRequest;
import com.baidu.inf.iis.bcs.response.BaiduBCSResponse;
import com.cuccs.dreambox.utils.AppAccountInfo;
import com.cuccs.dreambox.utils.AppAutoConstants;

public class BaiduCloud_BCS {
	Context mContext;
	
	public BaiduCloud_BCS(Context mContext){
		this.mContext = mContext;
	}
	
	public void putObjectByFile( BaiduBCS baiduBCS, String dirname, File f) {		
		PutObjectRequest request = new PutObjectRequest(AppAutoConstants.Baidu_BCS.bucket, "/"+
						AppAccountInfo.getUsername(mContext)+"/" + dirname
						+ "/" + f.getName(), f);
		ObjectMetadata metadata = new ObjectMetadata();
		// metadata.setContentType("text/html");
		request.setMetadata(metadata);
		BaiduBCSResponse<ObjectMetadata> response = baiduBCS.putObject(request);
		ObjectMetadata objectMetadata = response.getResult();
		AppAutoConstants.Baidu_BCS.log.info("x-bs-request-id: " + response.getRequestId());
		AppAutoConstants.Baidu_BCS.log.info(objectMetadata);
	}
	
	public void getObjectWithDestFile(BaiduBCS baiduBCS, String object, File destFile) {
		GetObjectRequest getObjectRequest = new GetObjectRequest(AppAutoConstants.Baidu_BCS.bucket, object);
		baiduBCS.getObject(getObjectRequest, destFile);
	}
	
	public void listObject_getSpacesize(BaiduBCS baiduBCS) {
		ListObjectRequest listObjectRequest = new ListObjectRequest(AppAutoConstants.Baidu_BCS.bucket);
		listObjectRequest.setStart(0);
		// listObjectRequest.setLimit(20);
		// ------------------by dir
		{
			// prefix must start with '/' and end with '/'
			listObjectRequest.setPrefix("/" +AppAccountInfo.getUsername(mContext)+ "/");
			// listObjectRequest.setListModel(2);
		}
		// ------------------only object
		{
			// prefix must start with '/'
			// listObjectRequest.setPrefix("/2013-11-11_192736/");
		}
		BaiduBCSResponse<ObjectListing> response = baiduBCS
				.listObject(listObjectRequest);
		AppAutoConstants.Baidu_BCS.log.info("we get [" + response.getResult().getObjectSummaries().size()
				+ "] object record.");
		
		long cloudsize = 0;	//记录云端已用空间大小
		for (ObjectSummary os : response.getResult().getObjectSummaries()) {
			AppAutoConstants.Baidu_BCS.log.info(os.toString());
			cloudsize = cloudsize+os.getSize();
		}
		DecimalFormat df = new DecimalFormat("#0.00");		//保留两位小数
		String sizeinfo = "";
		if(cloudsize < 1024*1024){	//1MB
			sizeinfo = df.format((double)cloudsize/1024) +"KB";
		}else{
			sizeinfo = df.format((double)cloudsize/(1024*1024)) +"MB";
		}
		//记录已用云端空间
		SharedPreferences mSpreferences = mContext.getSharedPreferences("Backup_info",0);
		Editor editor = mSpreferences.edit();        
		editor.putString("CloudSpace_Size", sizeinfo );	//存入数据
		editor.commit();	//提交修改
	}
}
