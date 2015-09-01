package com.cuccs.dreambox.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.util.EncodingUtils;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.util.Xml;

public class WriteOperatingRecord {
	private Context mContext;
	private XmlSerializer mXmlSerializer;
	private String fileName = "OperatingRecord.xml"; // 文件名字

	public WriteOperatingRecord(Context mContext){
		this.mContext = mContext;
	}
	
	// 写数据
	public void writeFile(String fileName, String writestr) throws IOException {
		try {
			FileOutputStream fout = mContext.openFileOutput(fileName,0);	//0: MODE_PRIVATE
			mXmlSerializer = Xml.newSerializer();
			mXmlSerializer.setOutput(fout, "UTF-8");
			mXmlSerializer.startDocument("UTF-8", true);
			mXmlSerializer.startTag(null, "OperatingRecord");
			byte[] bytes = writestr.getBytes();
			fout.write(bytes);
			fout.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 读数据
	public String readFile(String fileName) throws IOException {
		String res = "";
		try {
			FileInputStream fin = mContext.openFileInput(fileName);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
			fin.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;

	}
}
