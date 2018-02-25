package dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.SciPopInfo;

public class SciPopInfoDao extends DataBuild {
	PreparedStatement cps = null;
	String create_SciPopInfo_table = "create table if not exists SciPopInfo(infoId int AUTO_INCREMENT primary key, "
			+ "title varchar(1000), "
			+ "writtername varchar(1000), "
			+ "content TEXT, "
			+ "time DATE)";
	String add_SciPopInfo = "insert into SciPopInfo(title, writtername, content, time) values (?, ?, ?, ?)";
	String get_SciPopInfo_byId = "select * from SciPopInfo where infoId = ?";
	String update_SciPopInfo_byId = "update SciPopInfo set title = ?, writtername = ?, content = ?, time = ? where infoId = ?";
	String delete_SciPopInfo_byId = "delete from SciPopInfo where infoId = ?";
	
	/**
	 * 与数据库进行连接并创建表
	 */
	public SciPopInfoDao() {
		openCon();
		try {
			cps = con.prepareStatement(create_SciPopInfo_table);
			cps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 添加科普信息
	 * @param sciPopInfo
	 * @return
	 */
	public String addSciPopInfo(SciPopInfo sciPopInfo) {
		openCon();
		try {
			ps = con.prepareStatement(add_SciPopInfo);
			ps.setString(1, sciPopInfo.getTitle());
			ps.setString(2, sciPopInfo.getWritterName());
			ps.setString(3, sciPopInfo.getContent());
			ps.setDate(4, sciPopInfo.getLastTime());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.closePs();
			this.closeCon();
		}
		return sciPopInfo.getTitle();
	}
	/**
	 * 根据id获得科普信息
	 * @param infoId
	 * @return
	 */
	public SciPopInfo getSciPopInfo(int infoId) {
		openCon();
		SciPopInfo temp_info = new SciPopInfo();
		try {
			ps=con.prepareStatement(get_SciPopInfo_byId);
			ps.setInt(1, infoId);
			rs = ps.executeQuery();
			while(rs.next()) {
				temp_info.setInfoId(rs.getInt("infoId"));
				temp_info.setTitle(rs.getString("title"));
				temp_info.setWritterName(rs.getString("writtername"));
				temp_info.setContent(rs.getString("content"));
				temp_info.setLastTime(rs.getDate("time"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			this.closeRs();
			this.closePs();
			this.closeCon();
		}
		return temp_info;
	}
	
	String getAllSciPopInfo = "select * from SciPopInfo";
	/**
	 * 获得科普信息的列表
	 * @return
	 */
	public List<SciPopInfo> getListSciPopInfo() {
		openCon();
		ArrayList<SciPopInfo> sciPopInfos = new ArrayList<SciPopInfo>();
		try {
			ps=con.prepareStatement(getAllSciPopInfo);
			rs = ps.executeQuery();
			while (rs.next()) {
				SciPopInfo sciPopInfo = new SciPopInfo();
				sciPopInfo.setInfoId(rs.getInt("infoId"));
				sciPopInfo.setContent(rs.getString("content"));
				sciPopInfo.setTitle(rs.getString("title"));
				sciPopInfo.setLastTime(rs.getDate("time"));
				sciPopInfo.setWritterName(rs.getString("writtername"));
				sciPopInfos.add(sciPopInfo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			this.closeRs();
			this.closePs();
			this.closeCon();
		}
		return sciPopInfos;
	}
	/**
	 * 更新科普内容
	 * @param tempInfo
	 * @return
	 */
	public boolean updateSciPopInfo(SciPopInfo tempInfo) {
		openCon();
		boolean bool=false;
		try {
			ps=con.prepareStatement(update_SciPopInfo_byId);
			ps.setString(1, tempInfo.getTitle());
			ps.setString(2, tempInfo.getWritterName());
			ps.setString(3, tempInfo.getContent());
			ps.setDate(4, tempInfo.getLastTime());
			ps.setInt(5, tempInfo.getInfoId());
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
	 * 根据id删除科普信息
	 * @param infoId
	 * @return
	 */
	public boolean deleteSciPopInfo(int infoId) {
		openCon();
		boolean bool=false;
		try {
			ps= con.prepareStatement(delete_SciPopInfo_byId);
			ps.setInt(1, infoId);
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
