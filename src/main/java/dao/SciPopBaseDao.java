package dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import bean.SciPopBase;

public class SciPopBaseDao extends DataBuild {
	PreparedStatement cps = null;
	String create_SciPopBase_table = "create table if not exists SciPopBase(baseId int AUTO_INCREMENT primary key, "
			+ "address TEXT, "
			+ "contactNumber varchar(1000), "
			+ "baseInfo TEXT"
			+ "basename TEXT"
			+ "foreign key(baseAdminName) references User(username))";
	String add_SciPopBase = "insert into SciPopBase(address, contactNumber, baseInfo, basename, baseAdminName) values (?, ?, ?, ?, ?)";
	String get_SciPopBase_ById = "select * from SciPopBase where baseId = ?";
	String update_ScipopBase_ById = "update SciPopBase set address = ?, contactNumber = ?, baseInfo = ?, basename = ? where baseId = ?";
	String delete_SciPopBase_ById = "delete from SciPopBase where baseId = ?";
	/**
	 * 与数据库进行连接并创建表
	 */
	public SciPopBaseDao() {
		openCon();
		try {
			cps = con.prepareStatement(create_SciPopBase_table);
			cps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加科普基地
	 * @param sciPopBase
	 * @return
	 */
	public String addSciPopBase(SciPopBase sciPopBase) {
		openCon();
		try {
			ps = con.prepareStatement(add_SciPopBase);
			ps.setString(1, sciPopBase.getAddress());
			ps.setString(2, sciPopBase.getContactNumber());
			ps.setString(3, sciPopBase.getBaseInfo());
			ps.setString(4, sciPopBase.getBaseName());
			ps.setString(5, sciPopBase.getBaseAdminName());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.closePs();
			this.closeCon();
		}
		return sciPopBase.getBaseName();
	}

	/**
	 * 根据id获得科普基地信息
	 * @param baseId
	 * @return
	 */
	public SciPopBase getSciPopBaseById(int baseId) {
		openCon();
		SciPopBase temp_base = new SciPopBase();
		try {
			ps=con.prepareStatement(get_SciPopBase_ById);
			ps.setInt(1, baseId);
			rs = ps.executeQuery();
			while(rs.next()) {
				temp_base.setBaseId(rs.getInt("baseId"));
				temp_base.setAddress(rs.getString("address"));
				temp_base.setBaseName(rs.getString("basename"));
				temp_base.setBaseInfo(rs.getString("baseInfo"));
				temp_base.setContactNumber(rs.getString("contactNumber"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			this.closeRs();
			this.closePs();
			this.closeCon();
		}
		return temp_base;
	}

	/**
	 * 更新科普基地信息
	 * @param sciPopBase
	 * @return
	 */
	public boolean updateSciPopBase(SciPopBase sciPopBase) {
		openCon();
		boolean bool=false;
		try {
			ps=con.prepareStatement(update_ScipopBase_ById);
			ps.setString(1, sciPopBase.getAddress());
			ps.setString(2, sciPopBase.getContactNumber());
			ps.setString(3, sciPopBase.getBaseInfo());
			ps.setString(4, sciPopBase.getBaseName());
			ps.setInt(5, sciPopBase.getBaseId());
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

	public boolean deleteSciPopBaseById(int baseId) {
		openCon();
		boolean bool=false;
		try {
			ps= con.prepareStatement(delete_SciPopBase_ById);
			ps.setInt(1, baseId);
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
