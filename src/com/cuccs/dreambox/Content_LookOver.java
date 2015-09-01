package com.cuccs.dreambox;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Content_LookOver extends Activity{
	Button Btn_Back;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_mycloud);
		
		
		Btn_Back = (Button) findViewById(R.id.mycloud_icon_back);  
        Btn_Back.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.holding_anima, R.anim.translate_right_exit);
            }  
        });
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {     //返回键退出
	    if(keyCode == KeyEvent.KEYCODE_BACK 
	            && event.getAction() == KeyEvent.ACTION_DOWN){   
	    	finish();
            overridePendingTransition(R.anim.holding_anima, R.anim.contend_minify_rd);
	        return true;   
	    }
	    return super.onKeyDown(keyCode, event);
	}
}
