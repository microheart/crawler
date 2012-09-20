package com.xushun.crawler;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class Scheduler {

	private static Queue<URL> unvisitedURLs = new LinkedList<URL>();  // 可以用阻塞队列 ArrayBlockingQueue
	private static HashSet<URL> visitedURLs = new HashSet<URL>();
	
	public Scheduler(ArrayList<URL> urls) {    
		for(URL url: urls) {
			unvisitedURLs.offer(url);
		}
	}    
	
	public synchronized URL getURL()		
	{
		//堆栈无数据，不能出栈
		while(unvisitedURLs.isEmpty()){ 
			try{ 
				wait(); // 等待生产者写入数据 
			} catch (InterruptedException e) { 
				e.printStackTrace(); 
			} 
		}
		
		this.notify(); 
		URL url = unvisitedURLs.poll();
		visitedURLs.add(url);
		
	    return url; 
	}

	public synchronized void insert(URL url)
	{
		if(!visitedURLs.contains(url) && !unvisitedURLs.contains(url))
			unvisitedURLs.add(url);
	}

	public synchronized void insert(ArrayList<URL> analyzedURL)
	{
		for(URL url : analyzedURL)
		{
			if(!visitedURLs.contains(url) && !unvisitedURLs.contains(url))
				unvisitedURLs.add(url);
		}
	}
    
}
