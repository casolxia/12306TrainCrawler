package com.crawler.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class PropertyUtil {
	
	private static PropertyUtil instants;
	private Properties prop = new Properties();
	
	private PropertyUtil(){	
		InputStream is = null;
		try{
			is =new  FileInputStream(FileUtil.getWebRoot()+"/conf/app/client.properties");
			prop.load(is);
			System.out.println("配置文件路径:"+FileUtil.getWebRoot()+"/conf/app/client.properties");
		}catch(Exception e){
			e.printStackTrace();
		}
		finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static  PropertyUtil getInstants() {
		if(instants ==null){
			instants = new PropertyUtil();
		}
		return instants;
	}
	
	public String getProp(String key){
		return prop.getProperty(key);
	}
	
	public static void main(String[] args) {
		System.out.println(PropertyUtil.getInstants().getProp("FtpUserName"));
	}
	
}
