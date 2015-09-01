package com.cuccs.dreambox;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

public class ContactUs extends Activity{
	Button Btn_Back;
	RelativeLayout RLayout_01;
	RelativeLayout RLayout_02;
	RelativeLayout RLayout_03;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_us);
		
		RLayout_01 = (RelativeLayout)findViewById(R.id.contact_us_relativelayout01);
        RLayout_01.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();        
				intent.setAction("android.intent.action.VIEW");    
				Uri content_url = Uri.parse("http://weixin.qq.com/r/wnxMVNHEB4x4rZU39ynQ");   
				intent.setData(content_url);  
				startActivity(intent);
			}
		});
		RLayout_02 = (RelativeLayout)findViewById(R.id.contact_us_relativelayout02);
        RLayout_02.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();        
				intent.setAction("android.intent.action.VIEW");    
				Uri content_url = Uri.parse("http://www.weibo.com/xueyubowen");   
				intent.setData(content_url);  
				startActivity(intent);
			}
		});
		RLayout_03 = (RelativeLayout)findViewById(R.id.contact_us_relativelayout03);
        RLayout_03.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ContactUs.this, Website_Dreamanster.class);  
		        startActivity(intent);
		        overridePendingTransition(R.anim.translate_right_enter, R.anim.translate_left_exit);       //设定新的Activity进入和当前Activity退出时的动画。
			}
		});
		Btn_Back = (Button) findViewById(R.id.contact_us_icon_back);  
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
        finish();
        overridePendingTransition(R.anim.translate_left_enter, R.anim.translate_right_exit);
        return super.onKeyDown(keyCode, event);       
    }   
}
