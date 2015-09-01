package com.cuccs.dreambox.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class CheckNetwork {
	private Context context;

	public CheckNetwork(Context context){
	        this.context = context;
	}

	
	 public boolean isConnectingToInternet(){
		 ConnectivityManager connectivity = (ConnectivityManager)
				 context.getSystemService(Context.CONNECTIVITY_SERVICE);
		 NetworkInfo[] info = connectivity.getAllNetworkInfo();
		 if (info != null){
			 for (int i = 0; i < info.length; i++){
				 Log.v("info."+i, info[i].getTypeName());
				 if (info[i].getState() == NetworkInfo.State.CONNECTED)
				 {
					 return true;
				 }
			 }
		 }
		 Toast.makeText(context, "哎呀！网络尚没有连接", Toast.LENGTH_SHORT).show();
	 return false;
	 }
	 
	 public boolean isWiFiConnected(){		//判断当前活动的连接是否是  WiFi连接
		 ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		 boolean wifiOpen = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
		 if(wifiOpen == false){
			 Toast.makeText(context, "您限制了仅 WiFi下获取数据.\n如果需要, 您可以在设置中更改", Toast.LENGTH_SHORT).show();
			 return false;
		 }
		 String state = connectivity.getActiveNetworkInfo().getTypeName();
		 if(!state.equals("")&&state.equals("WIFI")){
			 return true;
		 }else{
			 Toast.makeText(context, "您正在使用的连接不是 WiFi", Toast.LENGTH_SHORT).show();
			 return false;
		 }
	 }
}
