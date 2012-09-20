package com.xushun.util;

import java.util.regex.Pattern;

public class SeuNewsURLFilter implements URLFilter {
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g" + "|png|tiff?|mid|mp2|mp3|mp4"
            + "|wav|avi|mov|mpeg|ram|m4v|pdf" + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
	
	
	@Override
	public boolean shoudVisit(String url) {
		
		String href = url.toLowerCase();
       
		return !FILTERS.matcher(href).matches() && (href.indexOf("news.seu.edu.cn") != -1) &&(href.length() < 120);
		
	}

}
