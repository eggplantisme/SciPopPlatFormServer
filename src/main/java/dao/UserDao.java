package dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.User;

public class UserDao extends DataBuild {
	public PreparedStatement cps = null;
	String create_user_table = "create table if not exists User(username varchar(1000) primary key, "
			+ "password varchar(1000), "
			+ "phone varchar(1000), "
			+ "score int, "
			+ "userclass int)";
	String add_user = "insert into User(username, password, phone, score, userclass) values (?, ?, ?, ?, ?)";
	String query_user_byName = "select * from User where username=?";
	String update_userInfo = "update User set username=?, phone=?, score=? where username=?";
	String update_userPassword = "update User set password=? where username=?";
	String delete_user = "delete from User where username = ?";
	/**
	 * 与数据库进行连接并创建用户表
	 */
	public UserDao() {
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
	
	/**
	 * 查询用户
	 * @param name
	 * @return
	 */
	public boolean queryUserbyName(String name){
		openCon();
		boolean bool=false;
		try {
			ps=con.prepareStatement(query_user_byName);
			ps.setString(1, name);
			rs = ps.executeQuery();
			while(rs.next()){
				bool=true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			this.closeRs();
			this.closePs();
			this.closeCon();
		}
		return bool;
	}
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
	 * 更新用户
	 * @param user
	 * @return
	 * 
	 */
	public boolean UpdateUser(User user) {
		openCon();
		boolean bool=false;
		try {
			ps=con.prepareStatement(update_userInfo);
			ps.setString(1, user.getUsername());
			ps.setString(2, user.getPhone());
			ps.setInt(3, user.getScore());
			ps.setString(4, user.getUsername());
			int num = ps.executeUpdate();
			if (num > 0) {
				bool = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			this.closeRs();
			this.closePs();
			this.closeCon();
		}
		return bool;
	}
	public boolean UpdateUserpassword(String _password, String username) {
		openCon();
		boolean bool=false;
		try {
			ps=con.prepareStatement(update_userPassword);
			ps.setString(1, _password);
			ps.setString(2, username);
			int num = ps.executeUpdate();
			if (num > 0) {
				bool = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			this.closeRs();
			this.closePs();
			this.closeCon();
		}
		return bool;
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
	
	String get_user_byClass = "select * from User where userclass=?";
	public List<User> getUsersbyClass(String userclass){
		openCon();
		List<User> users = new ArrayList<User>();
		try {
			
			ps=con.prepareStatement(get_user_byClass);
			ps.setString(1, userclass);
			rs = ps.executeQuery();
			while(rs.next()){
				User user = new User();
				user.setUsername(rs.getString("username"));
				user.setPassword(rs.getString("password"));
				user.setPhone(rs.getString("phone"));
				user.setScore(rs.getInt("score"));
				user.setUserclass(rs.getInt("userclass"));
				users.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			this.closeRs();
			this.closePs();
			this.closeCon();
		}
		return users;
	}
	
	String getScore = "select score from User where username =?";
	public int getScore(String username){
		openCon();
		int score = 0;
		try {
			ps=con.prepareStatement(getScore);
			ps.setString(1, username);
			rs = ps.executeQuery();
			while(rs.next()){
				score = rs.getInt("score");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			this.closeRs();
			this.closePs();
			this.closeCon();
		}
		return score;
	}
	

	
}
