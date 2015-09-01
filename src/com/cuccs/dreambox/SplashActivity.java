package com.cuccs.dreambox;

import com.cuccs.dreambox.utils.AppSettings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.WindowManager;
import android.widget.TextView;

public class SplashActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
	//使用SharedPreferences来记录程序的使用次数
    SharedPreferences spreferences;
    
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splashscreen);

        //**********显示版本名称
        /*PackageManager pm = getPackageManager();
        try {
            PackageInfo packi = pm.getPackageInfo("com.cuccs.dreambox", 0);
            TextView versionNumber = (TextView) findViewById(R.id.versionNumber);
            versionNumber.setText("Version " + packi.versionName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }*/
        TextView textview = (TextView) findViewById(R.id.versionNumber);
        textview.setText("Dream  Box");
                       
        
        new Handler().postDelayed(new Runnable() {
            public void run() {
            	//读取SharedPreferences中需要的数据
                spreferences = getSharedPreferences("App_StartCount",MODE_WORLD_READABLE);
                int count = spreferences.getInt("count", 0);
            	//判断程序与第几次运行，如果是第一次运行则跳转到引导页面
                if (count == 0) {
					//createSystemSwitcherShortCut(getApplicationContext());	//创建快捷方式的图标
                	AppSettings.writeDefaultSetup(getApplicationContext());	//默认设置
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(),Whatsnew.class);
                    intent.putExtra("whereFrom", "MainActivity");
                    startActivity(intent);
                    finish();
                }else{
                	startActivity(new Intent(SplashActivity.this, Homepage.class));
                    finish();
                }              
                Editor editor = spreferences.edit();        
                editor.putInt("count", 1);	//存入数据
                editor.commit();	//提交修改
            }
        }, 1500);

    }
	
	// 创建快捷方式的图标
	public static void createSystemSwitcherShortCut(Context context) {
		final Intent addIntent = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		final Parcelable icon = Intent.ShortcutIconResource.fromContext(
				context, R.drawable.ic_launcher); // 获取快捷键的图标
		addIntent.putExtra("duplicate", false);		//不允许重复创建  
		final Intent myIntent = new Intent(context,
				SplashActivity.class);
		myIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				context.getString(R.string.app_name));// 快捷方式的标题
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);// 快捷方式的图标
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, myIntent);// 快捷方式的动作
		context.sendBroadcast(addIntent);
	}
}