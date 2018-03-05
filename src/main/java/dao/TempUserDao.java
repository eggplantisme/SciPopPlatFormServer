package dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.TempUser;
import bean.User;

public class TempUserDao extends DataBuild {
	public PreparedStatement cps = null;
	String create_user_table = "create table if not exists TempUser(username varchar(1000) primary key, "
			+ "password varchar(1000), "
			+ "phone varchar(1000), "
			+ "score int, "
			+ "userclass int)";
	String add_user = "insert into TempUser(username, password, phone, score, userclass) values (?, ?, ?, ?, ?)";
	String query_user_byName = "select * from TempUser where username=?";
	String get_TempUserList = "select * from TempUser";
	String delete_user = "delete from TempUser where username = ?";
	/**
	 * 与数据库进行连接并创建用户表
	 */
	public TempUserDao() {
		openCon();
		try {
			cps = con.prepareStatement(create_user_table);
			cps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 添加用户
	 * @param user
	 * @return
	 */
	public String addUser(User user){
		openCon();
		try {
			ps= con.prepareStatement(add_user);
			ps.setString(1,user.getUsername());
			ps.setString(2,user.getPassword());
			ps.setString(3, user.getPhone());
			ps.setInt(4, user.getScore());
			ps.setInt(5, user.getUserclass());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.closePs();
			this.closeCon();
		}
		return user.getUsername();
	}
	
	public List<TempUser> getUserList() {
		openCon();
		ArrayList<TempUser> tempUsers = new ArrayList<TempUser>();
		try {
			ps=con.prepareStatement(get_TempUserList);
			rs = ps.executeQuery();
			while (rs.next()) {
				TempUser tempuser = new TempUser();
				tempuser.setUsername(rs.getString("username"));
				tempuser.setPassword(rs.getString("password"));
				tempuser.setPhone(rs.getString("phone"));
				tempuser.setScore(rs.getInt("score"));
				tempuser.setUserclass(rs.getInt("userclass"));
				tempUsers.add(tempuser);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			this.closeRs();
			this.closePs();
			this.closeCon();
		}
		return tempUsers;
	}
	
	/**
	 * 获得临时用户的信息
	 * @param name
	 * @return
	 */
	public User getUserbyName(String name){
		openCon();
		User user = new User();
		try {
			ps=con.prepareStatement(query_user_byName);
			ps.setString(1, name);
			rs = ps.executeQuery();
			while(rs.next()){
				user.setUsername(rs.getString("username"));
				user.setPassword(rs.getString("password"));
				user.setPhone(rs.getString("phone"));
				user.setScore(rs.getInt("score"));
				user.setUserclass(rs.getInt("userclass"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			this.closeRs();
			this.closePs();
			this.closeCon();
		}
		return user;
	}
	
	/**
	 * 注销用户
	 * @param username
	 * @return
	 */
	public boolean DeleteUser(String username) {
		openCon();
		boolean bool=false;
		try {
			ps= con.prepareStatement(delete_user);
			ps.setString(1, username);
			int num = ps.executeUpdate();
			if(num>0){
				bool=true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.closePs();
			this.closeCon();
		}
		return bool;
	}
}
