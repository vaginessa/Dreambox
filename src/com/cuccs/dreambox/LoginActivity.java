package com.cuccs.dreambox;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.inf.iis.bcs.BaiduBCS;
import com.baidu.inf.iis.bcs.auth.BCSCredentials;
import com.baidu.inf.iis.bcs.model.BCSClientException;
import com.baidu.inf.iis.bcs.model.BCSServiceException;
import com.cuccs.dreambox.objects.BaiduCloud_BCS;
import com.cuccs.dreambox.utils.AppAccountInfo;
import com.cuccs.dreambox.utils.AppAutoConstants;
import com.cuccs.dreambox.utils.CheckNetwork;
import com.cuccs.dreambox.utils.downloadImage;
import com.cuccs.dreambox.utils.send_post;
import com.tencent.open.HttpStatusException;
import com.tencent.open.NetworkUnavailableException;
import com.tencent.tauth.Constants;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.sso.SsoHandler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnGestureListener,
		OnClickListener {
	Context mContext;
	ImageView headerImg;
	ImageButton btn_QQlogin, btn_Weibologin;
	EditText useredit;
	EditText passwordedit;
	Button btn_login;
	Button btn_register;
	String Username = "";
	String Password = "";
	String nickname, headerURL;
	boolean addcontentFlag = false;
	Dialog loadDialog;
	Animation loadAnimation;
	GestureDetector mGestureDetector;

	private static Tencent mTencent;
	private static final String APP_ID = AppAutoConstants.QQConstant.APP_ID;
	private static final String SCOPE = AppAutoConstants.QQConstant.SCOPE;// QQ登录权限

	private static Weibo mWeibo;
	private static SsoHandler mSsoHandler; /* SsoHandler */
	private Handler mhandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				downloadImage dim = new downloadImage(headerURL);
				dim.startdown();
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Bitmap bit = BitmapFactory.decodeFile("/mnt/sdcard/000000.jpg");
				headerImg.setImageBitmap(bit);
				headerImg.postInvalidate();
				Toast.makeText(getApplicationContext(), nickname + "OK",
						Toast.LENGTH_SHORT).show();
				break;
			case 2:
				Toast.makeText(getApplicationContext(), "PswdERROR!",
						MODE_APPEND).show();
				break;
			case 3:
				Toast.makeText(getApplicationContext(), "email", MODE_APPEND)
						.show();
				break;
			case 4:
				Toast.makeText(getApplicationContext(), "EmailNoexist!",
						MODE_APPEND).show();
				break;
			case 5:
				Toast.makeText(getApplicationContext(), "Oops shibai",
						MODE_APPEND).show();
				break;
			case 6:
				AppAccountInfo.setisLoginSuccess(mContext, true);	//登陆成功标志
				AppAccountInfo.setlastloginTime(mContext, new Date(System.currentTimeMillis()).getTime());
				Toast.makeText(getApplicationContext(), "OK,SUCCESS!", MODE_APPEND).show();
				Intent intent = new Intent(LoginActivity.this, Homepage.class);		//切换到Homepage
				startActivity(intent);
				overridePendingTransition(R.anim.translate_left_enter,
						R.anim.holding_anima);
				finish();
				break;
			case 7:
				Toast.makeText(getApplicationContext(), "登录失败，可能网络异常!",
						MODE_APPEND).show();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		mGestureDetector = new GestureDetector((OnGestureListener) this);
		mContext = this.getApplicationContext();

		mTencent = Tencent.createInstance(APP_ID, this.getApplicationContext());
		mWeibo = Weibo.getInstance(AppAutoConstants.WeiboConstant.APP_KEY,
				AppAutoConstants.WeiboConstant.REDIRECT_URL,
				AppAutoConstants.WeiboConstant.SCOPE);

		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		final LayoutInflater inflater = LayoutInflater.from(this);
		final Animation mWarningAnima = AnimationUtils.loadAnimation(this,
				R.anim.warning_anima);

		headerImg = (ImageView) findViewById(R.id.head_show);
		btn_QQlogin = (ImageButton) findViewById(R.id.btn_qqLogin);
		btn_Weibologin = (ImageButton) findViewById(R.id.btn_sinaLogin);
		useredit = (EditText) findViewById(R.id.editUser);
		passwordedit = (EditText) findViewById(R.id.editPassword);
		SharedPreferences mSpreferences = mContext.getSharedPreferences(
				"My_account", 0);
		Username = mSpreferences.getString("username", "");
		Password = mSpreferences.getString("password", "");
		useredit.setText(Username);
		passwordedit.setText(Password);
		btn_login = (Button) findViewById(R.id.loginButton);
		btn_register = (Button) findViewById(R.id.registerButton);

		btn_QQlogin.setOnClickListener(this);
		btn_Weibologin.setOnClickListener(this);

		useredit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				btn_login.setEnabled(false);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				String after = useredit.getText().toString();
				if (after.equals("")) {
					btn_login.setEnabled(false);
					passwordedit.setBackgroundResource(0);
					passwordedit.setHint("请输入密码");
				} else {
					btn_login.setEnabled(true);
				}
			}
		});
		passwordedit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				passwordedit.setBackgroundResource(0);
				passwordedit.setHint("请输入密码");
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String after = useredit.getText().toString();
				if (after.equals("")) {
					passwordedit.setBackgroundResource(0);
					passwordedit.setHint("请输入密码");
				} else {
					btn_login.setText("登 录");
					btn_login.setEnabled(true);
				}
			}
		});

		loadDialog = getDialog(this, "mShowing");
		loadDialog.setContentView(R.layout.loading);
		// 不关闭 Dialog的写法
		try {
			Field field = loadDialog.getClass().getSuperclass()
					.getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(loadDialog, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (Username.equals("") == true) {
			btn_login.setEnabled(false);
		}
		btn_login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Password = passwordedit.getText().toString();
				if (Password.equals("")) {
					passwordedit.setBackgroundResource(R.drawable.wrongpass);
					passwordedit.setHint("");
					passwordedit.setAnimation(mWarningAnima);
				} else {
					loadDialog.setCancelable(false);
					loadDialog.show();

					Http_Login hthread = new Http_Login();
					hthread.start();
				}
			}
		});
		btn_register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Toast.makeText(getApplicationContext(), "向右滑动返回登录页", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.translate_right_enter,
						R.anim.translate_left_exit);
				finish();
			}
		});
	}

	private class Http_Login extends Thread {
		@Override
		public void start() {
			CheckNetwork checknet = new CheckNetwork(getApplicationContext());
			if (checknet.isConnectingToInternet() == false) {
				loadDialog.dismiss();
				return;
			}
			super.start();
		}

		@Override
		public void run() {
			Looper.prepare();
			String value = null;
			String response = "";
			Message msg_login = new Message();
			Username = useredit.getText().toString();
			Password = passwordedit.getText().toString();

			Log.v("LoginActivity", "value::::" + value);
			response = send_post.phclient_login(mContext,
					"http://www.dreamanster.com/test_login.php", Username,
					Password);
			Log.i("LoginSendfinish", "response----> " + response);

			if (response == null) {
				msg_login.what = 7; // 请求登录失败
				mhandler.sendMessage(msg_login);
				loadDialog.dismiss();
				return;
			}
			if (response.contains("PswdERROR")) {
				msg_login.what = 2;
			} else if (response.contains("EmailERROR")) {
				msg_login.what = 3;
			} else if (response.contains("EmailNoexist")) {
				msg_login.what = 4;
			} else if (response.contains("ServiceERROR")) {
				msg_login.what = 5;
			} else if (response.contains("ok")) {
				AppAccountInfo.setUsername(mContext, Username); // 保存用户名
				SharedPreferences mSpreferences = mContext
						.getSharedPreferences("My_account", 0);
				Editor editor = mSpreferences.edit();
				editor.putString("username", Username);
				editor.putString("password", Password);
				editor.commit();
				// 开启新线程下载备份日志文件
				File dbFile = new File(
						"/data/data/com.cuccs.dreambox/databases/"
								+ AppAccountInfo.getUsername(mContext) + "_backuplog.db");
				BCSCredentials credentials = new BCSCredentials(AppAutoConstants.Baidu_BCS.accessKey,
						AppAutoConstants.Baidu_BCS.secretKey);
				BaiduBCS baiduBCS = new BaiduBCS(credentials,
						AppAutoConstants.Baidu_BCS.host);
				baiduBCS.setDefaultEncoding("UTF-8");
				if (!dbFile.exists()) {
					try {
						dbFile.createNewFile();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					try {
						//获取备份日志文件
						new BaiduCloud_BCS(mContext).getObjectWithDestFile(baiduBCS,
								"/" + AppAccountInfo.getUsername(mContext)+ "/Log_backup/" +
										AppAccountInfo.getUsername(mContext) + "_backuplog.db", dbFile);
					} catch (BCSServiceException e) {
						AppAutoConstants.Baidu_BCS.log.warn("Bcs return:"
								+ e.getBcsErrorCode() + ", "
								+ e.getBcsErrorMessage() + ", RequestId="
								+ e.getRequestId());
					} catch (BCSClientException e) {
						e.printStackTrace();
					}
				}
				//记录云端已用空间
				new BaiduCloud_BCS(mContext).listObject_getSpacesize(baiduBCS); 
				msg_login.what = 6; // 成功
			} else {
				msg_login.what = 5; // 失败
			}

			Log.v("LoginActivit_msg_login.what", "msg_login.what----> "
					+ msg_login.what);
			mhandler.sendMessage(msg_login);
			loadDialog.dismiss();
			Looper.loop();
		}
	}

	@Override
	public void onClick(View v) {
		CheckNetwork checknet = new CheckNetwork(this.getApplicationContext()); // 检查网络
		if (checknet.isConnectingToInternet() == false) {
			return;
		}
		switch (v.getId()) {
		case R.id.btn_qqLogin:
			onQQClickLogin();
			btn_QQlogin.setImageResource(R.drawable.btn_qqlog);
			break;
		case R.id.btn_sinaLogin:
			onWeiboClicklogin();
			btn_Weibologin.setImageResource(R.drawable.btn_sinalog);
			break;
		}
	}

	private void onQQClickLogin() {
		if (!mTencent.isSessionValid()) {
			IUiListener listener = new BaseUiListener() {
				@Override
				protected void doComplete(JSONObject values) {
					mTencent.requestAsync(Constants.GRAPH_USER_INFO, null,
							Constants.HTTP_GET, new BaseApiListener(
									"get_simple_userinfo", false), null);
				}
			};
			mTencent.login(this, SCOPE, listener);
		} else {
			mTencent.logout(this);
		}
	}

	private void onWeiboClicklogin() {
		mWeibo.anthorize(LoginActivity.this, new AuthDialogListener()); // ȡ��΢��code
		// mSsoHandler = new SsoHandler(this, mWeibo);
		// mSsoHandler.authorize(new AuthDialogListener(),null);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			Intent intent = new Intent(LoginActivity.this, Homepage.class);
			startActivity(intent);
			overridePendingTransition(R.anim.translate_left_enter,
					R.anim.holding_anima);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public static Dialog getDialog(final Context context, String name) {
		final Dialog dialog = new Dialog(context, R.style.Theme_ShareDialog);
		return dialog;
	}

	@Override
	/*****************************************************************/
	public boolean dispatchTouchEvent(MotionEvent ev) {
		mGestureDetector.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (e2.getX() - e1.getX() > 80 && Math.abs(velocityX) > 40) {
			Intent intent = new Intent(LoginActivity.this, Homepage.class);
			startActivity(intent);
			overridePendingTransition(R.anim.translate_left_enter,
					R.anim.holding_anima);
			finish();
		} else if (e1.getX() - e2.getX() > 80 && Math.abs(velocityX) > 40) {
			// 转到RegisterActivity
			Intent intent = new Intent(LoginActivity.this,
					RegisterActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.translate_right_enter,
					R.anim.translate_left_exit);
			finish();
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	private class BaseUiListener implements IUiListener {
		@Override
		public void onComplete(JSONObject response) {
			doComplete(response);
		}

		protected void doComplete(JSONObject values) {
		}

		@Override
		public void onError(UiError e) {
		}

		@Override
		public void onCancel() {
		}
	}

	private class BaseApiListener implements IRequestListener {
		private String mScope = "all";
		private Boolean mNeedReAuth = false;

		public BaseApiListener(String scope, boolean needReAuth) {
			mScope = scope;
			mNeedReAuth = needReAuth;
		}

		@Override
		public void onComplete(final JSONObject response, Object state) {
			Log.e("IRequestListener.onComplete:", response.toString());
			doComplete(response, state);
			new Thread() {
				@Override
				public void run() {
					try {
						nickname = response.getString("nickname");
						headerURL = response.getString("figureurl_qq_2");
						System.out.println("figureurl_qq_2>>>>>>:" + headerURL);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					mhandler.sendEmptyMessage(0);
				}
			}.start();
		}

		protected void doComplete(JSONObject response, Object state) {
			try {
				int ret = response.getInt("ret");
				if (ret == 100030) {
					if (mNeedReAuth) {
						Runnable r = new Runnable() {
							public void run() {
								mTencent.reAuth(LoginActivity.this, mScope,
										new BaseUiListener());
							}
						};
						LoginActivity.this.runOnUiThread(r);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Log.e("toddtest", response.toString());
			}

		}

		@Override
		public void onIOException(final IOException e, Object state) {
			Log.e("IRequestListener.onIOException:", e.getMessage());
		}

		@Override
		public void onMalformedURLException(final MalformedURLException e,
				Object state) {
			Log.e("IRequestListener.onMalformedURLException", e.toString());
		}

		@Override
		public void onJSONException(final JSONException e, Object state) {
			Log.e("IRequestListener.onJSONException:", e.getMessage());
		}

		@Override
		public void onConnectTimeoutException(ConnectTimeoutException arg0,
				Object arg1) {
			Log.e("IRequestListener.onConnectTimeoutException:",
					arg0.getMessage());

		}

		@Override
		public void onSocketTimeoutException(SocketTimeoutException arg0,
				Object arg1) {
			Log.e("IRequestListener.SocketTimeoutException:", arg0.getMessage());
		}

		@Override
		public void onUnknowException(Exception arg0, Object arg1) {
			Log.e("IRequestListener.onUnknowException:", arg0.getMessage());
		}

		@Override
		public void onHttpStatusException(HttpStatusException arg0, Object arg1) {
			Log.e("IRequestListener.HttpStatusException:", arg0.getMessage());
		}

		@Override
		public void onNetworkUnavailableException(
				NetworkUnavailableException arg0, Object arg1) {
			Log.e("IRequestListener.onNetworkUnavailableException:",
					arg0.getMessage());
		}
	}

	private class AuthDialogListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
			final String code = values.getString("code");
			if (code != null) {
				Toast.makeText(LoginActivity.this, "��֤code�ɹ�",
						Toast.LENGTH_SHORT).show();
				Log.i("code", code);
			}
			new Thread() {
				@Override
				public void run() {
					try {
						JSONObject jo, jo2;
						URL url = new URL(
								"https://api.weibo.com/oauth2/access_token?client_id="
										+ AppAutoConstants.WeiboConstant.APP_KEY
										+ "&client_secret="
										+ AppAutoConstants.WeiboConstant.APP_SECRET
										+ "&grant_type=authorization_code&redirect_uri="
										+ AppAutoConstants.WeiboConstant.REDIRECT_URL
										+ "&code=" + code);
						URLConnection connection = url.openConnection();
						connection.setDoOutput(true);
						OutputStreamWriter out = new OutputStreamWriter(
								connection.getOutputStream(), "utf-8");
						out.write("client_id="
								+ AppAutoConstants.WeiboConstant.APP_KEY
								+ "&client_secret="
								+ AppAutoConstants.WeiboConstant.APP_SECRET
								+ "&grant_type=aurhorization_token&code="
								+ code + "&redirect_uri="
								+ AppAutoConstants.WeiboConstant.REDIRECT_URL);
						out.flush();
						out.close();
						String sCurrentLine = "";
						String sTotalString = "";
						InputStream l_urlStream = connection.getInputStream();
						BufferedReader l_reader = new BufferedReader(
								new InputStreamReader(l_urlStream));
						while ((sCurrentLine = l_reader.readLine()) != null) {
							sTotalString += sCurrentLine;
						}

						jo = new JSONObject(sTotalString);
						System.out.println("uid:" + jo.getString("uid"));

						// ��ȡWeibo�û�ͷ��
						/********************** ------------------------- ************************/
						URL url_show = new URL(
								"https://api.weibo.com/2/users/show.json?uid="
										+ jo.getString("uid")
										+ "&access_token="
										+ jo.getString("access_token"));

						HttpURLConnection connection2 = (HttpURLConnection) url_show
								.openConnection();
						connection2.setRequestMethod("GET");
						sCurrentLine = "";
						sTotalString = "";
						InputStream is2 = connection2.getInputStream();
						BufferedReader l_reader2 = new BufferedReader(
								new InputStreamReader(is2));
						while ((sCurrentLine = l_reader2.readLine()) != null) {
							sTotalString += sCurrentLine;
						}
						Log.v("sTotalString>>>>", sTotalString);

						jo2 = new JSONObject(sTotalString);
						nickname = jo2.getString("screen_name");
						headerURL = jo2.getString("avatar_large");
						System.out.println("avatar_large_url>>>>>>:"
								+ headerURL);
					} catch (Exception e) {
						e.printStackTrace();
					}

					mhandler.sendEmptyMessage(0);
				}
			}.start();
		}

		@Override
		public void onError(WeiboDialogError e) {
			Toast.makeText(getApplicationContext(),
					"Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
		}

		@Override
		public void onCancel() {
			Toast.makeText(getApplicationContext(), "Auth cancel",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(getApplicationContext(),
					"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/**
		 * ��������ע�͵��Ĵ��룬����sdk֧��ssoʱ��Ч��
		 */
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
		mTencent.onActivityResult(requestCode, resultCode, data);
	}
}
