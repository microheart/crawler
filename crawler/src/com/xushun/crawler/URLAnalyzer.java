package com.xushun.crawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xushun.dao.PageDAO;
import com.xushun.model.PageInfo;
import com.xushun.util.StringUtil;

public class URLAnalyzer {

	private static URLFilter urlFilter = new DefaultURLFilter();
	
	private static Integer docID = 1;
	
	public static void setURLFilter(URLFilter filter) {
		urlFilter = filter;
	}
	
	private static final String DOWNLOAD_PATH = "snapshot";
	private String encoding = null;
	private Scheduler scheduler = null;
	private URL url = null;
	private String title = null;
	private String contentType = null;
	private String savedFileName = null;
	
	ArrayList<URL> links =  new ArrayList<URL>();
	
	private String htmlText = null;
	private String text = null;
	
	public URLAnalyzer(Scheduler scheduler, URL url)	{
		this.scheduler = scheduler;
		this.url = url;
	}
	
	
	public void analyzer() {
		getURLHtml();  // get html text
		writeToDisk();
		urlDetector();  // 获取页面链接
		updateLinks();
		parserText();   // 去掉html的标签
		writeToDB();
	}
	
	public void getURLHtml()
	{
		StringBuilder document = new StringBuilder();
		try {
			URLConnection conn = url.openConnection();
			contentType = conn.getContentType();
			String encoding = "utf-8";
			if(contentType != null)
				encoding = contentType.substring(contentType.indexOf("=")+1);
			System.out.println(encoding);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), encoding));
			
			String line = null;
			while((line = reader.readLine()) != null)
			{
				if(!line.trim().isEmpty())
					document.append(line).append("\n");
			}
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		htmlText =  document.toString();
	}
	
	
	public void writeToDisk() {

		BufferedWriter bfWriter = null;
		try {	
			
			savedFileName = StringUtil.getHTMLFileName();
			
			File file = new File(DOWNLOAD_PATH, savedFileName+".html");           //设定输出的文件名
			try {
				bfWriter = new BufferedWriter(new FileWriter(file));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//数据部分
			bfWriter.append(htmlText);
			
			bfWriter.flush();	
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if(bfWriter != null) {
				try{
					bfWriter.close();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}// end writeToDisk
	
	public void parserText() {
		Pattern p_script,p_style,p_html, p_title, p_filter;    
		Matcher m_script,m_style,m_html, m_title, m_filter;      
	          
	    try { 
	    	//定义script正则式{或<script[^>]*?>[\s\S]*?<\/script> } 
	    	String scriptRegExp = "<script[^>]*?>[\\s\\S]*?</script>";    
	    	//定义style正则式{或<style[^>]*?>[\s\S]*?<\/style> }    
	    	String styleRegExp = "<style[^>]*?>[\\s\\S]*?</style>"; 
	    	//定义HTML标签的正则表达式 
	    	String htmlRegExp = "<[^>]+>";
	    	
	    	String titleRegExp = "<title>.*</title>";
	        String[] filter = {"&quot;", "&nbsp;"};
	    	
	        p_title = Pattern.compile(titleRegExp,Pattern.CASE_INSENSITIVE);    
	        m_title = p_title.matcher(htmlText);  
	        if(m_title.find()) {
	        	title = m_title.group();
	        	int startIndex = title.indexOf(">") + 1;
	        	int endIndex = title.lastIndexOf("<") - 1;
	        	
	        	title = title.substring(startIndex, endIndex);
	        }
	        
	        p_script = Pattern.compile(scriptRegExp,Pattern.CASE_INSENSITIVE);    
	        m_script = p_script.matcher(htmlText);    
	        htmlText = m_script.replaceAll(""); //过滤script标签    
	   
	        p_style = Pattern.compile(styleRegExp,Pattern.CASE_INSENSITIVE);    
	        m_style = p_style.matcher(htmlText);    
	        htmlText = m_style.replaceAll(""); //过滤style标签    
	           
	        p_html = Pattern.compile(htmlRegExp,Pattern.CASE_INSENSITIVE);    
	        m_html = p_html.matcher(htmlText);    
	        htmlText = m_html.replaceAll(""); //过滤html标签    
	           
	        //过滤style标签    &quot; &nbsp;
	        for(int i = 0; i < filter.length; i++)
	        {
	        	p_filter = Pattern.compile(filter[i],Pattern.CASE_INSENSITIVE);    
	        	m_filter = p_filter.matcher(htmlText);    
		        htmlText = m_filter.replaceAll(""); 
	        }
	        
	        text = htmlText;    
	           
	    }catch(Exception e) {    
	    	e.printStackTrace();
	    }    
	          
	}
	
		//URL还需要做的工作，去除一些无用链接，修复一些相对路径的链接
		public void urlDetector()
		{
			final String patternString = "<[a|A]\\s+href=([^>]*\\s*>)";   		
			Pattern pattern = Pattern.compile(patternString,Pattern.CASE_INSENSITIVE);   
			
			Matcher matcher = pattern.matcher(htmlText);
			String tempURL;

			while(matcher.find())
			{
				try {
					
					tempURL = matcher.group();			
					tempURL = tempURL.substring(tempURL.indexOf("\"")+1);			
					if(!tempURL.contains("\""))
						continue;
					
					tempURL = tempURL.substring(0, tempURL.indexOf("\""));		
					//System.out.println(tempURL);
					//即使在之前的处理下，还是有可能发生意外的，比如，程序用的是相对的url
					//这样，这个字符串就不可以用于url的初始化，我们先把这部分省略不考虑
					//之后可以写一个补充host的方法将这些url补齐
					
					if(urlFilter != null) {
						if(urlFilter.shoudVisit(tempURL)) {
							links.add(new URL(tempURL));
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		public void writeToDB() {

            PageInfo pi = new PageInfo();
            
            synchronized(docID) {
            	docID++;
            }
            
            pi.setDocID(docID);
            pi.setUrl(url.getPath());
            pi.setDomain(null);
            pi.setPath(null);
            pi.setSubDomain(null);
            pi.setParentUrl(null);
            pi.setContentType(contentType);
            pi.setContentEncoding(null);
            pi.setCharset(encoding);
            pi.setSavedName(savedFileName);
            pi.setPlain(text);
            pi.setTitle(title);
            
            PageDAO.addPage(pi);
//            PageDAO.addLinks(docID.intValue(), url, links);
		}
		
	private void updateLinks() {
		for(URL url : links) {
			scheduler.insert(url);
		}
	}
	
}
