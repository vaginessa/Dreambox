package com.cuccs.dreambox.utils;


import java.lang.ref.SoftReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

/**
 * @author TimeTraveler
 *异步图片加载
 */
public class AsyncImageLoader {
	
	public SoftReference<Bitmap> loadImageFromSrcPath(final String imageSourcePath, final ImageCallback imageCallback){
		
		final Handler handler = new Handler() {
            public void handleMessage(Message message) {
                imageCallback.NotifyImageLoaded(null, (Bitmap) message.obj);
            }
        };
        new Thread() {
            @Override
            public void run() {
            	// 从图片路径中读取图片资源
        		Bitmap bitmap = null;
        		try {
        			BitmapFactory.Options options = new BitmapFactory.Options();		//生成缩略图
        			/**
        			 * *  设置inJustDecodeBounds为true后，decodeFile并不分配空间，但可计算出原始图片的长度和宽度，即opts.width和opts.height。设置了此属性一定要记得将值设置为false
        			 * */
        			options.inJustDecodeBounds = false;
        			int be = options.outHeight/40;
        			if (be <= 0) {
        				be = 5;
        			}
        			options.inSampleSize = be;
        			
        			bitmap = BitmapFactory.decodeFile(imageSourcePath,options);
        			} catch (Exception e) {
        				e.printStackTrace();
        			}
                
                Message message = handler.obtainMessage(0, bitmap);
                handler.sendMessage(message);
            }
        }.start();
        return null;
	}
	
	public interface ImageCallback {
        public void NotifyImageLoaded(ImageView imageview,Object data);
    }
}