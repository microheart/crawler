package com.xushun.crawler;

import java.net.URL;

public class Gather implements Runnable {

	private static final int MAX_DOWNLOADED_PER_THREAD = 10000;
	
	private Scheduler scheduler;
	private String ID;
	
	public Gather(String ID, Scheduler scheduler)
	{
		this.ID = ID;
		this.scheduler = scheduler;
		
	}
	
	public void run() {
		
		int counter = 0;
		while(counter++ < MAX_DOWNLOADED_PER_THREAD)		
		{
			URL url = scheduler.getURL();
			System.out.println("in running thread ID: " + ID + " ; url: " + url.toString());
			
			URLAnalyzer urlAnalyzer = new URLAnalyzer(scheduler, url);
			urlAnalyzer.analyzer();
			
		}
		
	}

}
