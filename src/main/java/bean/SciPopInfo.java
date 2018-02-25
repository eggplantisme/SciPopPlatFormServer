package bean;

import java.sql.Date;

/**
 * @author lenovo1
 * ø∆∆’–≈œ¢
 */
public class SciPopInfo {
	private int infoId;
	private String title;
	private String writterName;
	private String content;
	private Date lastTime;

	/**
	 * @return the infoId
	 */
	public int getInfoId() {
		return infoId;
	}

	/**
	 * @param infoId the infoId to set
	 */
	public void setInfoId(int infoId) {
		this.infoId = infoId;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the writterName
	 */
	public String getWritterName() {
		return writterName;
	}

	/**
	 * @param writterName the writterName to set
	 */
	public void setWritterName(String writterName) {
		this.writterName = writterName;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the lastTime
	 */
	public Date getLastTime() {
		return lastTime;
	}

	/**
	 * @param lastTime the lastTime to set
	 */
	public void setLastTime(Date lastTime) {
		this.lastTime = lastTime;
	}
}
