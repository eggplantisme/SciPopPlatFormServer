package bean;

/**
 * @author lenovo1
 *
 */
public class User {
	private String username;
	private String password;
	private String phone;
	private int score;
	/**
	 * ��ʾ�û����
	 * 0 -- ��ͨ��Ժ
	 * 1 -- ר�һ�Ա
	 * 2 -- ���ػ�Ա
	 * 3 -- ƽ̨����Ա 
	 */
	private int userclass;

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(int score) {
		this.score = score;
	}

	/**
	 * @return the userclass
	 */
	public int getUserclass() {
		return userclass;
	}

	/**
	 * @param userclass the userclass to set
	 */
	public void setUserclass(int userclass) {
		this.userclass = userclass;
	}
}
