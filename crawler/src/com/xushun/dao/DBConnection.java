package com.xushun.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DBConnection 
{
    private static Connection conn = null;
    private Statement stmt = null;
    private PreparedStatement prepstmt = null;
    private static final String url = "jdbc:mysql://localhost/crawler"; // URL指向要访问的数据库名search_engine
    private static final String user = "root"; // MySQL配置时的用户名
    private static final String password = "xushun"; // MySQL配置时的密码
    
    static {
    	try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, password);  
    	}
    	catch(Exception e) {
    		System.out.println("connect da failure!");
    		e.printStackTrace();
    	}   	
    }
    
    public static Connection getConnectionInstance() throws Exception {
        return conn;     
    }
    
    public DBConnection()
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, password);
            stmt = conn.createStatement();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    public Connection getConnection() 
    {
        return conn;
    }
    
   
    
    public void close()     
    {
        try
        {
            if (stmt != null) 
            {
                stmt.close();
                stmt = null;
            }
            if (prepstmt != null) 
            {
                prepstmt.close();
                prepstmt = null;
            }
            conn.close();
            conn = null;
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}