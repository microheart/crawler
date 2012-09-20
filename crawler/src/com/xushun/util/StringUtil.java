package com.xushun.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class StringUtil {

	/**
	 * 文件名是以当前时间字符串后加一个随机数组成
	 * @return
	 */
	public static String getHTMLFileName() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String currentTimeStr = sdf.format(new Date());
		Random random = new Random(System.currentTimeMillis());
		
		StringBuilder builder = new StringBuilder();
		builder.append(currentTimeStr).append(random.nextInt());
		
		//String imageName = currentTimeStr + random.nextInt(10000);
		
		return builder.toString();
	}
	
	public static String getTimeStr() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}

	public static List<Integer> strToList(String s) {
	
		List<Integer> list = new ArrayList<Integer>();
		String[] arr = s.split(",");

		for(int i =0; i < arr.length; ++i) {
			list.add(Integer.parseInt(arr[i]));
		}

		return list;
	}
}
