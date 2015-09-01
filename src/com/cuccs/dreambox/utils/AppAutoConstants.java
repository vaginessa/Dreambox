package com.cuccs.dreambox.utils;

import org.apache.commons.logging.LogFactory;

/**
 * @author TimeTraveler
 *QQ、新浪微博第三方登录App的各种常量
 */
public interface AppAutoConstants {
	
	public static final class Ali_OSS_Constant{
		public static final String ACCESS_ID = "ZPEUiBlAOqrgFbeP";
		public static final String ACCESS_KEY = "woT9XEs3QjfKByseQ42aJCeQLJGnxc";
		public static final String OSS_ENDPOINT = "http://oss.aliyuncs.com/";
	}
	
	public static final class Baidu_BCS{
		public static final org.apache.commons.logging.Log log = LogFactory
				.getLog(ProgressThread_Backup_cloud.class);
		public static final String host = "bcs.duapp.com";
		public static final String accessKey = "6b6f0e80d1d9b3b6499eceeb6c551e4c";
		public static final String secretKey = "C17a0dcb547a7d95153d80a67cb2b46d";
		public static final String bucket = "dreambox";
	}
	
	public static final class QQConstant{
		public static final String APP_ID = "100512008";
		public static final String SCOPE = "get_user_info, get_simple_userinfo, add_share";// QQ权限：读取用户信息并分享信息 
	}
	
	
	public static final class WeiboConstant{
		//新浪应用的APP_KEY
		public static final String APP_KEY="2591001980";
		public static final String APP_SECRET="9de94a2bad73ee6894375bba57fd6d33";
		//新浪的REDIRECT_URL
		public static final String REDIRECT_URL = "http://www.cuc.edu.cn";
		//传入多个SCOPE权限
		public static final String SCOPE = "email,direct_messages_read,direct_messages_write," +
				"friendships_groups_read,friendships_groups_write,statuses_to_me_read," +
					"follow_app_official_microblog";

		public static final String CLIENT_ID = "client_id";
		public static final String RESPONSE_TYPE = "response_type";
		public static final String USER_REDIRECT_URL = "redirect_uri";
		public static final String DISPLAY = "display";
		public static final String USER_SCOPE = "scope";
		public static final String PACKAGE_NAME = "packagename";
		public static final String KEY_HASH = "key_hash";
	}
	
}
