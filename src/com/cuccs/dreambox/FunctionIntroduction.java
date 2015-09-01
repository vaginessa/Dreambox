package com.cuccs.dreambox;

import com.cuccs.dreambox.layouts.MyVideoView;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class FunctionIntroduction  extends Activity{
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//取消标题栏
		getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
		              WindowManager.LayoutParams. FLAG_FULLSCREEN);//全屏
		setContentView(R.layout.function_introduce);

		/* 创建VideoView对象 */
		final MyVideoView videoView = (MyVideoView) findViewById(R.id.VideoView01);
		/* 设置路径 */
		videoView.setVideoURI(Uri.parse("android.resource://com.cuccs.dreambox/"+R.raw.intro));
		/* 设置模式-播放进度条 */
		videoView.setMediaController(null);
		videoView.requestFocus();
		
		/* 开始播放 */
		videoView.start();
	}
}
