package com.cuccs.dreambox;

import com.cuccs.dreambox.utils.CheckNetwork;
import com.cuccs.dreambox.utils.send_post;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class RegisterActivity extends Activity implements OnGestureListener{
	Context mContext;
	Dialog loadingDialog;
	Button btn_reg;
	EditText mUsernameEdit, mPasswordEdit, mConfirmEdit;
	String Username = "";
	String Password = "";
	String Repassword = "";
	GestureDetector mGestureDetector;
	
	private Handler mHandler = new Handler() {  
        public void handleMessage(Message msg) {  
        	btn_reg.setEnabled(true);
        	btn_reg.setText("注 册");
            switch (msg.what) {   
                case 1:
					Toast.makeText(getApplicationContext(), "EmailisExist...", MODE_APPEND).show();
					break;
                case 2:
					Toast.makeText(getApplicationContext(), "PswdShort!", MODE_APPEND).show();
					break;
                case 3:
					Toast.makeText(getApplicationContext(), "email", MODE_APPEND).show();
					break;
                case 4:
					Toast.makeText(getApplicationContext(), "EmailNoexist!", MODE_APPEND).show();
					break;
                case 5:
                	Toast.makeText(getApplicationContext(), "Oops ServiceERROR!!", MODE_APPEND).show();
					break;
                case 6:
                	Toast.makeText(getApplicationContext(), "SUCCESS!", MODE_APPEND).show();
                	SharedPreferences mSpreferences = getApplicationContext().getSharedPreferences("My_account",0);
            		Editor editor = mSpreferences.edit();        
                    editor.putString("username", Username);
                    editor.putString("password", Password);
                    editor.commit();
                	break;
                case 7:
                	Toast.makeText(getApplicationContext(), "Network Error注册失败,可能网络异常", MODE_APPEND).show();
					break;
            }  
        };  
    };
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_activity);
		mContext = this.getApplicationContext();
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);	//键盘弹出时界面上移
		mGestureDetector = new GestureDetector((OnGestureListener) this); 
		
		mUsernameEdit = (EditText) findViewById(R.id.register_activity_email);
		mPasswordEdit = (EditText) findViewById(R.id.register_activity_password);
		mConfirmEdit = (EditText) findViewById(R.id.register_activity_confirm);
		btn_reg = (Button) findViewById(R.id.register_activity_btn_Reg);
		
		loadingDialog = new Dialog(this, R.style.Theme_ShareDialog);
		loadingDialog.setContentView(R.layout.loading);
		loadingDialog.setCancelable(false);
		btn_reg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) { 
				Username = mUsernameEdit.getText().toString();
				Password = mPasswordEdit.getText().toString();
				Repassword = mConfirmEdit.getText().toString();
				if(Username.equals("")){
					Toast.makeText(getApplicationContext(), "请输入你的手机号码!", MODE_APPEND).show();
				}else if(Password.equals("")){
					Toast.makeText(getApplicationContext(), "请您设置密码!", MODE_APPEND).show();
				}else if(Repassword.equals("")){
					Toast.makeText(getApplicationContext(), "您尚未确认密码!", MODE_APPEND).show();
				}else if(Password.equals(Repassword) == false){
					Toast.makeText(getApplicationContext(), "您两次输入的密码不一致!", MODE_APPEND).show();
				}else{
					loadingDialog.show();
					btn_reg.setEnabled(false);
					btn_reg.setText("注册中...");
					Http_register hthread = new Http_register();
					hthread.start();
				}
			}
		});
	}

	
	public boolean onKeyDown(int keyCode, KeyEvent event){    
	    if(keyCode == KeyEvent.KEYCODE_BACK 
	            && event.getAction() == KeyEvent.ACTION_DOWN){   
	    	Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);  
	    	startActivity(intent);
	        overridePendingTransition(R.anim.translate_left_enter, R.anim.translate_right_exit);
	        this.finish();
	        return true;   
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	public boolean dispatchTouchEvent(MotionEvent ev) {    
       mGestureDetector.onTouchEvent(ev);  
       return super.dispatchTouchEvent(ev);  
	}  
	@Override
	public boolean onDown(MotionEvent e){
		return false;
	}
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (e2.getX() - e1.getX() > 80 && Math.abs(velocityX) > 40) {      
			Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);  
	    	startActivity(intent);
	        overridePendingTransition(R.anim.translate_left_enter, R.anim.translate_right_exit);
	        this.finish();
	    }
		return false;
	}
	@Override
	public void onLongPress(MotionEvent e) {}
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,float distanceY) {
		return false;
	}
	@Override
	public void onShowPress(MotionEvent e) {}
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
	
	
	
	private class Http_register extends Thread{
		@Override
		public void start(){
			CheckNetwork checknet = new CheckNetwork(getApplicationContext());		//检查网络连接是否正常
			if(checknet.isConnectingToInternet() == false){
				loadingDialog.dismiss();
				return;
			}
			super.start();
		}
		
		@Override
		public void run(){
			String value = null;
			String response = "";
			Message msg_login = new Message();
			Username = mUsernameEdit.getText().toString();
			Password = mPasswordEdit.getText().toString();
			
			Log.v("LoginActivity", "value::"+value);
			response = send_post.phclient_login(mContext, "http://www.dreamanster.com/test_reg.php",Username,Password);
			Log.i("LoginSendfinish", "response----> "+response);
			if(response == null){
				msg_login.what = 7;
				mHandler.sendMessage(msg_login);
				loadingDialog.dismiss();
				return;
			}
			if(response.contains("EmailisExist")){
				msg_login.what = 1;
			}else if(response.contains("PswdShort")){
				msg_login.what = 2;
			}else if(response.contains("EmailERROR")){
				msg_login.what = 3;
			}else if(response.contains("EmailNoexist")){
				msg_login.what = 4;
			}else if(response.contains("ServiceERROR")){
				msg_login.what = 5;
			}else if(response.contains("ok")){
				msg_login.what = 6;	
			}else{
				msg_login.what = 5;		
			}
			
			Log.v("LoginActivit_msg_login.what", "msg_login.what----> "+msg_login.what);
			mHandler.sendMessage(msg_login);
			loadingDialog.dismiss();
			return;
		}
	}
}