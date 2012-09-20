package com.xushun.crawler;

public class DefaultURLFilter implements URLFilter {

	@Override
	public boolean shoudVisit(String url) {
		
		return true;
	}

}
