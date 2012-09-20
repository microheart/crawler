package com.xushun.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.xushun.model.PageInfo;


public class PageDAO {
	
    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g" + "|png|tiff?|mid|mp2|mp3|mp4"
            + "|wav|avi|mov|mpeg|ram|m4v|pdf" + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
	
	private static Connection conn = null;

	public static PageInfo getPageInfo(int pageId) {
		
		PageInfo page = null;
		
		try{
			conn = DBConnection.getConnectionInstance();
			String sql = "select url, title, saved_name, plain from page where pageid = ?";
			
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, pageId);
			
			int cnt = 0;
			ResultSet rs = preparedStatement.executeQuery();
			page = new PageInfo();
			while(rs.next()) {
				++cnt;
				page.setUrl(rs.getString(1));
				page.setTitle(rs.getString(2));
				page.setSavedName(rs.getString(3));
				page.setPlain(rs.getString(4));
				page.setDocID(pageId);
			}
			
			if(cnt == 0)
				return null;
			
		}
		catch(Exception e) {
			
			System.out.println("get failure !");
			e.printStackTrace();
		}
		
		return page;
		
	}
	
	
public static List<PageInfo> getAllPage() {
		
		PageInfo page = null;
		List<PageInfo> pageList = new ArrayList<PageInfo>();
		try{
			conn = DBConnection.getConnectionInstance();
			String sql = "select url, title, saved_name, plain, pageid from page";
			
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			
			ResultSet rs = preparedStatement.executeQuery();
			
			while(rs.next()) {
				page = new PageInfo();
				page.setUrl(rs.getString(1));
				page.setTitle(rs.getString(2));
				page.setSavedName(rs.getString(3));
				page.setPlain(rs.getString(4));
				page.setDocID(rs.getInt(5));
				
				pageList.add(page);
			}
			
		}
		catch(Exception e) {
			
			System.out.println("get failure !");
			e.printStackTrace();
		}
		
		if(pageList.size() == 0)
			return null;
		return pageList;
		
	}
	
	
	public static List<PageInfo> getPageList(List<Integer> pageIds) {
		
		List<PageInfo> pageList = new ArrayList<PageInfo>();
		
		for(Integer pageId: pageIds) {
			PageInfo page = getPageInfo(pageId);
			if(page != null) {
				pageList.add(page);
			}
		}
		
		return pageList;
	}
	
public static void addPage(PageInfo page) {
		
		try {
			conn = DBConnection.getConnectionInstance();
			
			String sql = "insert into page(docid, url, domain, path, sub_domain, parent_url, content_encoding, content_type, charset, saved_name, plain, title) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, page.getDocID());
			preparedStatement.setString(2, page.getUrl());
			preparedStatement.setString(3, page.getDomain());
			preparedStatement.setString(4, page.getPath());
			preparedStatement.setString(5, page.getSubDomain());
			preparedStatement.setString(6, page.getParentUrl());
			preparedStatement.setString(7, page.getContentEncoding());
			preparedStatement.setString(8, page.getContentType());
			preparedStatement.setString(9, page.getCharset());
			preparedStatement.setString(10, page.getSavedName());
			preparedStatement.setString(11, page.getPlain());
			preparedStatement.setString(12, page.getTitle());
			
			preparedStatement.executeUpdate();
			
		}
		catch(Exception e) {
			
			System.out.println("insert failure !");
			e.printStackTrace();
		}
		
	}
	
}
