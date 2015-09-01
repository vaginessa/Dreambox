package com.cuccs.dreambox.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 常用工具类
 * @Author TimeTraveler
 * @since http://www.cnblogs.com/hanyonglu/archive/2012/05/10/2494908.html
 */
public class Utils {
    
    /**
     * InputStream to byte
     * @param inStream
     * @return
     */
    public byte[] readInputStream(InputStream inStream) throws Exception { 
        byte[] buffer = new byte[1024]; 
        int len = -1; 
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        
        while ((len = inStream.read(buffer)) != -1) { 
            outStream.write(buffer, 0, len); 
        }
        
        byte[] data = outStream.toByteArray(); 
        outStream.close(); 
        inStream.close();
        
        return data; 
   } 
    
   /**
    * Byte to bitmap
    * @param bytes
    * @param opts
    */
   public Bitmap getBitmapFromBytes(byte[] bytes, BitmapFactory.Options opts) {
       if (bytes != null){
           if (opts != null){ 
               return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,opts); 
           }
           else{
               return BitmapFactory.decodeByteArray(bytes, 0, bytes.length); 
           }
       }
       
       return null; 
   } 
}