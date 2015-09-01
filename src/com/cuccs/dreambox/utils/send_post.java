package com.cuccs.dreambox.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

public class send_post {
	public static HttpClient mHttpClient = new DefaultHttpClient();
	public static String sendpost(String str_url,String str_send){
		String result = "";
		try{
			
			URL urlconn = new URL(str_url);
			HttpURLConnection hconn = (HttpURLConnection)urlconn.openConnection();
			hconn.setRequestMethod("POST");
			hconn.setDoInput(true);
			hconn.setDoOutput(true);
			hconn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.172 Safari/537.22");
			hconn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
			
			//hconn.connect();
			
			String content = "name=" + URLEncoder.encode("li") ;
			
			DataOutputStream out = new DataOutputStream(hconn.getOutputStream());
			out.writeBytes(content);
			out.flush();
			out.close();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(hconn.getInputStream()));
			
			String line;
			while((line = in.readLine())!= null)
			{
				result += "\n" + line;
			}
			
			
		}catch (IOException e){
			e.printStackTrace();
		}
		return result;
		
	}
	public static String phclient_login(Context mContext, String str_url,String str_uname,String str_passwd){
		
		String  msg_return = null;
		HttpEntity mHttpEntity = null;
		HttpPost http_post = new HttpPost(str_url); 
		mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		mHttpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
        
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", str_uname));
		params.add(new BasicNameValuePair("password", str_passwd));
		
		try{
			mHttpEntity = new UrlEncodedFormEntity(params,HTTP.UTF_8); 
			CookieStore mCookieStore = ((AbstractHttpClient) mHttpClient).getCookieStore();
			List<Cookie> cookies = mCookieStore.getCookies();
			Log.e("=====cookies.size()", cookies.size()+"");
			for (int i = 0; i < cookies.size(); i++) {
                if ("PHPSESSID".equals(cookies.get(i).getName()) && !cookies.get(i).getValue().equals("")) {
                	String mPHP_sessionId = cookies.get(i).getValue();
                	Log.e("========mPHP_sessionId", mPHP_sessionId+","+i);
                    //http_post.setHeader("Cookie", "PHPSESSID=" + mPHP_sessionId);
                    break;
                }
            }
			http_post.setHeader("Cookie", "PHPSESSID=" + AppAccountInfo.getSessionID(mContext));
			
//			CookieStore localCookies = new BasicCookieStore();
//			DefaultHttpClient httpClient = new DefaultHttpClient();
//			 
//			HttpContext localContext = new BasicHttpContext();
//			localContext.setAttribute(ClientContext.COOKIE_STORE, localCookies);
//			HttpResponse response = httpClient.execute(request, localContext);
//			 
//			List<Cookie> respCookieList = localCookies.getCookies();
//			for(Cookie ck : respCookieList){
//			    System.out.println(ck);
//			}
			
			
			http_post.setEntity(mHttpEntity);
			HttpResponse response = mHttpClient.execute(http_post);
			
			if(response.getStatusLine().getStatusCode() == 200){
				msg_return = EntityUtils.toString(response.getEntity());
                List<Cookie> mCookies = mCookieStore.getCookies();
                for (int i = 0; i < mCookies.size(); i++) {
                    if ("PHPSESSID".equals(mCookies.get(i).getName())) {
                    	String sessionid = mCookies.get(i).getValue();
                    	Log.e("+++++++PHPSESSID+", sessionid+","+i);
                    	AppAccountInfo.setSessionID(mContext, sessionid);	//保存sessionID
                        break;
                    }
                }
                for (int i = 0; i < mCookies.size(); i++) {
                    if ("uid".equals(mCookies.get(i).getName())) {
                    	String uid = mCookies.get(i).getValue();
                    	Log.e("-=-=-uid.i=-=-=-=-=-=--", uid+","+i);
                    	AppAccountInfo.setUserId(mContext, uid);	//保存用户id
                        break;
                    }
                }
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return msg_return;
	}
public static String phclient_reg(String str_url, String str_reg_email, String str_reg_pswd){
		
		
		String  msg_return = null;
		HttpEntity hentity = null;
		HttpClient client = new DefaultHttpClient();
		HttpPost http_post = new  HttpPost(str_url); 
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 8000);
        
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("email",str_reg_email));
		params.add(new BasicNameValuePair("password", str_reg_pswd));
		
		try{
			hentity = new UrlEncodedFormEntity(params,HTTP.UTF_8);
			http_post.setEntity(hentity);
			HttpResponse response = client.execute(http_post);
			
			if(response.getStatusLine().getStatusCode() == 200){
				msg_return = EntityUtils.toString(response.getEntity());
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return msg_return;
	}
	public boolean isconnect(String url,String strEncoding){
		int intTimeout = 5;   
	    try   
	    { 
	      URL urlconn = new URL(url);   
	      HttpURLConnection urlConnection= null;   
	      urlConnection=(HttpURLConnection)urlconn.openConnection();   
	      urlConnection.setRequestMethod("GET");   
	      //urlConnection.setDoOutput(true);   
	      //urlConnection.setDoInput(true);   
	      urlConnection.setRequestProperty   
	      (   
	        "User-Agent","Mozilla/4.0"+   
	        " (compatible; MSIE 6.0; Windows 2000)"   
	      );   
	         
	      urlConnection.setRequestProperty   
	      ("Content-type","text/html; charset="+strEncoding);        
	      urlConnection.setConnectTimeout(1000*intTimeout);
	      urlConnection.setReadTimeout(8000);	//��ȡ��ʱ
	      urlConnection.connect();   
	      if (urlConnection.getResponseCode() == 200)   
	      { 
	    	  return true;   
	      }else{   
	        return false;   
	      }   
	    }   
	    catch (Exception e)   
	    {   
	      e.printStackTrace();   
	      return false;   
	    }   
	
	}

}
