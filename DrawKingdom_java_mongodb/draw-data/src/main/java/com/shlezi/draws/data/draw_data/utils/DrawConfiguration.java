package com.shlezi.draws.data.draw_data.utils;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

public class DrawConfiguration {

	private DrawConfiguration(){
		
	}
	
	
	public static DrawConfiguration getInstance() {
		return instance;
	}
	private static DrawConfiguration instance = new DrawConfiguration();
	private static Properties properties = null;
	static {
		properties = new Properties();
		try {
			properties.load(new FileReader(new File("C:\\Users\\Ishai\\workspace\\draw-data\\draw.properties")));
		} catch (Exception e) {
			System.out.println("failed initializing config");
			throw new RuntimeException(e);		}
	}

	
	public String getProperty(String key){
		if(key == null){
			throw new IllegalArgumentException();
		}
		return properties.getProperty(key);
	}
	
}
