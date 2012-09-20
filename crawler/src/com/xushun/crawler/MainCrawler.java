package com.xushun.crawler;

import java.net.URL;
import java.util.ArrayList;


public class MainCrawler {

	private ArrayList<URL> urls;
	private static final int THREAD_NUM = 10; 
	
	public MainCrawler(){}
	
	public MainCrawler(ArrayList<URL> urls)
	{
		this.urls = urls;
	}
	
	/**
	 * 启动爬虫
	 */
	public void start() {
		Scheduler scheduler = new Scheduler(urls);
		for(int i = 0; i < THREAD_NUM; i++)
		{
			Thread gather = new Thread(new Gather(String.valueOf(i), scheduler));
			gather.start();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		ArrayList<URL> urls = new ArrayList<URL>();
		try {
			
			urls.add(new URL("http://news.seu.edu.cn/"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		MainCrawler spider = new MainCrawler(urls);
		spider.start();

	}

}
