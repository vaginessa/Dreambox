package com.cuccs.dreambox;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class TermsofService extends Activity{
	Button Btn_Back;
	WebView webview;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.termsofservice);
		
		webview = (WebView) findViewById(R.id.termsofservice_webview); 
        webview.getSettings().setJavaScriptEnabled(true); 
        webview.loadUrl("file:///android_asset/setup.html"); 
        webview.setWebViewClient(new WebViewClient(){
        	public boolean shouldOverrideUrlLoading(WebView view, String url) {
        		view.loadUrl(url);   //在当前的webview中跳转到新的url
				return true;
        	}
        });
        
        Btn_Back = (Button) findViewById(R.id.termsofservice_icon_back);  
        Btn_Back.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
                //设定新的Activity进入和当前Activity退出时的动画。
                //在startActivity（）或finish（）后，调用overridePendingTransition方法.
                overridePendingTransition(R.anim.translate_left_enter, R.anim.translate_right_exit);      
            }  
        });
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {       
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {       
            webview.goBack();       
            return true;       
        }
        finish();
        overridePendingTransition(R.anim.translate_left_enter, R.anim.translate_right_exit);
        return super.onKeyDown(keyCode, event);       
    }   
}
