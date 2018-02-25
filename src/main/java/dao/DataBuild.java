package dao;

import java.sql.*;

public class DataBuild {
	public Connection con = null;
	public PreparedStatement ps = null;
	public ResultSet rs = null;
	public String driver = "com.mysql.jdbc.Driver";
	public String url = "jdbc:mysql://localhost:3306/scipop";
	public String cd_url = "jdbc:mysql://localhost:3306";
	public String user = "root";
	public String pass = "root";
	public void openCon() {
		try {
			Class.forName(driver);
			//创建数据库的尝试，自己事先创建了tickets数据库
			Connection cd_con = DriverManager.getConnection(cd_url, user, pass);
			String cd_sql = "CREATE DATABASE if not exists scipop";
			cd_con.createStatement().executeUpdate(cd_sql);
			con = DriverManager.getConnection(url, user, pass);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public void closeCon()
	{
		if(con!=null)
		{
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void closePs()
	{
		if(ps!=null)
		{
			try {
				ps.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void closeRs()
	{
		if(rs!=null)
		{
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
