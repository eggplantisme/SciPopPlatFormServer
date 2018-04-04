package main;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import bean.SciPopBase;
import bean.SciPopInfo;
import bean.TempUser;
import bean.User;
import dao.SciPopBaseDao;
import dao.SciPopInfoDao;
import dao.TempUserDao;
import dao.UserDao;
import net.sf.json.JSON;
import net.sf.json.JSONObject;



@Controller
public class GeneralController {
	
	
	/**
	 * ������url
	 * @param model
	 * @return
	 */
	@RequestMapping(value={"/index", "/"})
	@ResponseBody
    public Object index_jsp(Model model, HttpSession session){
        model.addAttribute("str", "Hello world");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("get", "yes");
        jsonObject.put("satisfied", 1);
        
        session.setAttribute("name", "unknown");
        return jsonObject;
    }
	/**
	 * ������ע��ҳ��
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/regist", method = RequestMethod.GET)
    public String regist_jsp(Model model){
    	return "regist";
    }
	
	/**
	 * ע��
	 * @param user
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping(value={"/regist"}, method = RequestMethod.POST)
	@ResponseBody
	public Object regist(User user, Model model, HttpSession session) {
		String username = user.getUsername();
		String password = user.getPassword();
		String phone = user.getPhone();
		int userclass = user.getUserclass();
		Object temp = matchAndJsonReturn(username, password, phone, userclass);
		if (!temp.equals(true)) return temp;
		else {
			UserDao userDao = new UserDao();
			//��ֹע��һ���͹���Աͬ�����û�
			if (userDao.queryUserbyName(username) || username.equals(Admin.AdminName)) {
				JSONObject result = new JSONObject();
				result.put("repeated", "yes");
				result.put("info", 0);
				result.put("class", userclass);
				return result;
			} else {
				user.setScore(0);
				//����ר�һ�Ա�ͻ��ػ�Ա��ע������ͨ��
				//��������Ҫ������ʱ�û���ȴ����
				if(user.getUserclass() != 1 && user.getUserclass() != 2) {
					userDao.addUser(user);
					JSONObject result = new JSONObject();
					result.put("repeated", "no");
					result.put("info", 1);
					result.put("class", userclass);
					
					//����session
					session.setAttribute("username", user.getUsername());
					session.setAttribute("class", userclass);
					return result;
				} else {
					TempUserDao tempUserDao = new TempUserDao();
					tempUserDao.addUser(user);
					JSONObject result = new JSONObject();
					result.put("repeated", "no");
					result.put("info", 8);
					result.put("class", userclass);
					return result;
				}
			}
		}
	}
	
	/**
	 * ��ע���¼��Ϣ����ģʽ���
	 * ������Ϣinfo��ʽ����
	 * 0��repeated
	 * 1:success
	 * 2:name is not correct
	 * 3:pass is not correct
	 * 4:phone is not correct
	 * 5:class is not correct
	 * @param username
	 * @param password
	 * @param phone
	 * @param userclass
	 * @return
	 */
	public Object matchAndJsonReturn(String username, String password, String phone, int userclass) {
		JSONObject result = new JSONObject();
		//���ָ�ʽƥ�䣬���Ȳ�����100
		String usernamePattern = "^.{1,100}$";
		Pattern pattern = Pattern.compile(usernamePattern);
		Matcher matcher = pattern.matcher(username);
		if (!matcher.matches()) {
			result.put("repeated", "no");
			result.put("info", 2);
			result.put("class", userclass);
			return result;
		} else {
			//������Ϣƥ��
			String passwordPattern = "^.{6,100}$";
			pattern = Pattern.compile(passwordPattern);
			matcher = pattern.matcher(password);
			if (!matcher.matches()) {
				result.put("repeated", "no");
				result.put("info", 3);
				result.put("class", userclass);
				return result;
			} else {
				//�绰��Ϣƥ��
				String phonePattern = "^\\d{11}$";
				pattern = Pattern.compile(phonePattern);
				matcher = pattern.matcher(phone);
				if (!matcher.matches()) {
					result.put("repeated", "no");
					result.put("info", 4);
					result.put("class", userclass);
					return result;
				} else {
					//���ƥ��
					String classPattern = "^(0|1|2|3)$";
					pattern = Pattern.compile(classPattern);
					matcher = pattern.matcher(userclass + "");
					if (!matcher.matches()) {
						result.put("repeated", "no");
						result.put("info", 5);
						result.put("class", userclass);
						return result;
					}
				}
			}
		}
		return true;
	}

	/**
	 * ��¼
	 * @param user
	 * @param model
	 * @param session
	 * @return result
	 */
	@RequestMapping(value={"/login"}, method = RequestMethod.POST)
	@ResponseBody
	public Object login(User user, Model model, HttpSession session) {
		String username = user.getUsername();
		String password = user.getPassword();
		int userClass = user.getUserclass();
		//����Ա�û�������ʱ�û��б�
		if (username.equals(Admin.AdminName) && password.equals(Admin.AdminPass)) {
			TempUserDao tempUserDao = new TempUserDao();
			List<TempUser> tempUsers = tempUserDao.getUserList();
			//����session
			session.setAttribute("username", "eggplant");
			session.setAttribute("class", 3);
			return tempUsers;
		} else {
			String phone = "12345678901";//ռ����ֵ
			int userclass = user.getUserclass();
			//����ʽ
			Object temp = matchAndJsonReturn(username, password, phone, userclass);
			if (!temp.equals(true)) return temp;
			else {
				UserDao userDao = new UserDao();
				if (!userDao.queryUserbyName(username)) {
					JSONObject result = new JSONObject();
					result.put("repeated", "no");
					result.put("info", 6);
					result.put("class", userclass);
					return result;
				} else {
					User temp_user = userDao.getUserbyName(username);
					if (temp_user.getPassword().equals(password)) {
						if (temp_user.getUserclass() == userClass) {
							JSONObject result = new JSONObject();
							result.put("repeated", "no");
							result.put("info", 1);
							result.put("class", userclass);
							
							//����session
							session.setAttribute("username", user.getUsername());
							session.setAttribute("class", userclass);
							return result;
						} else {
							JSONObject result = new JSONObject();
							result.put("repeated", "no");
							result.put("info", 13);
							result.put("class", userclass);
							return result;
						}
					} else {
						JSONObject result = new JSONObject();
						result.put("repeated", "no");
						result.put("info", 7);
						result.put("class", userclass);
						return result;
					}
				}
			}
		}
		
	}
	
	/**
	 * ��ȡ����ר�һ�Ա�б�������л�Ա��������
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping(value = {"/adminProList"}, method = RequestMethod.GET)
	@ResponseBody
	public Object AdminProtList(Model model, HttpSession session) {
		if (("" + session.getAttribute("class")).equals("3")) {
			UserDao userDao = new UserDao();
			List<User> users = userDao.getUsersbyClass("1");
			return users;
		} else {
			return null;
		}
	}
	
	/**
	 * ��ȡ�����б�
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping(value = {"/adminList"}, method = RequestMethod.GET)
	@ResponseBody
	public Object AdminList(Model model, HttpSession session) {
		if (("" + session.getAttribute("class")).equals("3")) {
			TempUserDao tempUserDao = new TempUserDao();
			List<TempUser> tempUsers = tempUserDao.getUserList();
			return tempUsers;
		} else {
			return null;
		}
	}
	
	/**
	 * ����Աͬ��ר�һ���ػ�Աע��ĺ���
	 * ����ֵinfo����
	 * 0��ʾ�ɹ�
	 * 1��ʾȨ�޲��ǹ���ԱȨ��
	 * @param tempUser
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping(value={"/adminAgree/{tempUserName}"}, method = RequestMethod.GET)
	@ResponseBody
	public Object AdminAgree(@PathVariable String tempUserName, Model model, HttpSession session) {
		if (("" + session.getAttribute("class")).equals("3")) {
			UserDao userDao = new UserDao();
			TempUserDao tempUserDao = new TempUserDao();
			User user = tempUserDao.getUserbyName(tempUserName);
			userDao.addUser(user);
			
			tempUserDao.DeleteUser(tempUserName);
			JSONObject result = new JSONObject();
			result.put("agree", "true");
			result.put("info", 1);
			return result;
		} else {
			JSONObject result = new JSONObject();
			result.put("agree", "false");
			result.put("info", 12);
			return result;
		}
	}
	
	/**
	 * ɾ����ʱ�û�
	 * ����ֵinfo����
	 * 0��ʾ�ɹ�
	 * 1��ʾȨ�޲��ǹ���ԱȨ��
	 * @param tempUser
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping(value={"/adminDelete/{tempUserName}"}, method = RequestMethod.GET)
	@ResponseBody
	public Object AdminDelete(@PathVariable String tempUserName, Model model, HttpSession session) {
		if (("" + session.getAttribute("class")).equals("3")) {
			TempUserDao tempUserDao = new TempUserDao();
			tempUserDao.DeleteUser(tempUserName);
			JSONObject result = new JSONObject();
			result.put("delete", "true");
			result.put("info", 1);
			return result;
		} else {
			JSONObject result = new JSONObject();
			result.put("delete", "false");
			result.put("info", 12);
			return result;
		}
	}
	
	
	/**
	 * ��ÿ�����Ϣ������б�
	 * @param model
	 * @param session
	 * @return titleList
	 */
	@RequestMapping(value = {"/getTitleList"}, method=RequestMethod.GET)
	@ResponseBody
	public Object getTitleList(Model model, HttpSession session) {
		SciPopInfoDao sciPopInfoDao = new SciPopInfoDao();
		List<JSONObject> titleList = new ArrayList<JSONObject>();
		List<SciPopInfo> sciPopInfos = sciPopInfoDao.getListSciPopInfo();
		for (SciPopInfo sciPopInfo : sciPopInfos) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", sciPopInfo.getInfoId());
			jsonObject.put("title", sciPopInfo.getTitle());
			titleList.add(jsonObject);
		}
		return titleList;
	}
	
	/**
	 * ����id��ȡ��Ӧ�Ŀ�����Ϣ
	 * @param id
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping(value = {"/getSciPopInfo/{id}"}, method = RequestMethod.GET)
	@ResponseBody
	public Object getSciPopInfoById(@PathVariable int id, Model model, HttpSession session) {
		SciPopInfoDao sciPopInfoDao = new SciPopInfoDao();
		SciPopInfo sciPopInfo = sciPopInfoDao.getSciPopInfo(id);
		return sciPopInfo;
	}
	
	@RequestMapping(value = {"/getSciPopInfos/{name}"}, method = RequestMethod.GET)
	@ResponseBody
	public Object getSciInfosByName(@PathVariable String name, Model model, HttpSession session) {
		SciPopInfoDao sciPopInfoDao = new SciPopInfoDao();
		List<SciPopInfo> sciPopInfos = sciPopInfoDao.getListSciPopInfoByName(name);
		return sciPopInfos;
	}
	
	/**
	 * ����sessionȨ����ӿ�����Ϣ
	 * @param sciPopInfo
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping(value={"/addSciPopInfo"},method = RequestMethod.POST)
	@ResponseBody
	public Object addSciPopInfo(SciPopInfo sciPopInfo, Model model, HttpSession session) {
		JSONObject jsonObject = new JSONObject();
		if(session.getAttribute("class") == null) {
			jsonObject.put("update", "no");
			jsonObject.put("reason", "1");
			return jsonObject;
		} else {
			String userclass = String.valueOf(session.getAttribute("class"));
			if (userclass.equals("1") || userclass.equals("3")) {
				SciPopInfoDao sciPopInfoDao = new SciPopInfoDao();
				sciPopInfoDao.addSciPopInfo(sciPopInfo);
				jsonObject.put("update", "yes");
				jsonObject.put("reason", "0");
				return jsonObject;
			} else {
				jsonObject.put("update", "no");
				jsonObject.put("reason", "1");
				return jsonObject;
			}
		}
	}

	/**
	 * ���¿�����Ϣ
	 * @param sciPopInfo
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping(value={"/updateSciPopInfo"}, method= RequestMethod.POST)
	@ResponseBody
	public Object updateSciPopInfo(SciPopInfo sciPopInfo, Model model, HttpSession session) {
		JSONObject jsonObject = new JSONObject();
		if(session.getAttribute("class") == null) {
			jsonObject.put("update", "no");
			jsonObject.put("reason", "1");
			return jsonObject;
		} else {
			String userclass = String.valueOf(session.getAttribute("class"));
			if (userclass.equals("1") || userclass.equals("3")) {
				SciPopInfoDao sciPopInfoDao = new SciPopInfoDao();
				sciPopInfoDao.updateSciPopInfo(sciPopInfo);
				jsonObject.put("update", "yes");
				jsonObject.put("reason", "0");
				return jsonObject;
			} else {
				jsonObject.put("update", "no");
				jsonObject.put("reason", "1");
				return jsonObject;
			}
		}
	}

	/**
	 * ɾ��������Ϣ
	 * @param infoId
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping(value={"/deleteSciPopInfo/{infoId}"},method = RequestMethod.GET)
	@ResponseBody
	public Object deleteSciPopInfo(@PathVariable int infoId, Model model, HttpSession session) {
		JSONObject jsonObject = new JSONObject();
		if(session.getAttribute("class") == null) {
			jsonObject.put("update", "no");
			jsonObject.put("reason", "1");
			return jsonObject;
		} else {
			String userclass = String.valueOf(session.getAttribute("class"));
			if (userclass.equals("1") || userclass.equals("3")) {
				SciPopInfoDao sciPopInfoDao = new SciPopInfoDao();
				sciPopInfoDao.deleteSciPopInfo(infoId);
				jsonObject.put("update", "yes");
				jsonObject.put("reason", "0");
				return jsonObject;
			} else {
				jsonObject.put("update", "no");
				jsonObject.put("reason", "1");
				return jsonObject;
			}
		}
	}

	/**
	 * ��ӿ��ջ�����Ϣ
	 * @param sciPopBase
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping(value={"/addSciPopBase"}, method = RequestMethod.POST)
	@ResponseBody
	public Object addSciPopBase(SciPopBase sciPopBase, Model model, HttpSession session) {
		JSONObject jsonObject = new JSONObject();
		if(session.getAttribute("class") == null) {
			jsonObject.put("update", "no");
			jsonObject.put("reason", "1");
			return jsonObject;
		} else {
			String userclass = String.valueOf(session.getAttribute("class"));
			if (userclass.equals("2") || userclass.equals("3")) {
				SciPopBaseDao sciPopBaseDao = new SciPopBaseDao();
				sciPopBaseDao.addSciPopBase(sciPopBase);
				jsonObject.put("update", "yes");
				jsonObject.put("reason", "0");
				return jsonObject;
			} else {
				jsonObject.put("update", "no");
				jsonObject.put("reason", "1");
				return jsonObject;
			}
		}
	}
	
	/**
	 * ��ȡ���ջ��ص��б�
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping(value = {"/getBaseList"}, method=RequestMethod.GET)
	@ResponseBody
	public Object getSciPopBaseList(Model model, HttpSession session) {
		SciPopBaseDao sciPopBaseDao = new SciPopBaseDao();
		List<JSONObject> bases = new ArrayList<JSONObject>();
		List<SciPopBase> sciPopBases = sciPopBaseDao.getListSciPopBase();
		for (SciPopBase sciPopBase : sciPopBases) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", sciPopBase.getBaseId());
			jsonObject.put("title", sciPopBase.getBaseName());
			bases.add(jsonObject);
		}
		return bases;
		
	}
	
	@RequestMapping(value = {"/getSciBaseList/{name}"}, method = RequestMethod.GET)
	@ResponseBody
	public Object getSciBaseByName(@PathVariable String name, Model model, HttpSession session) {
		SciPopBaseDao sciPopBaseDao = new SciPopBaseDao();
		List<SciPopBase> sciPopBases = sciPopBaseDao.getListSciPopBaseByName(name);
		return sciPopBases;
	}
	/**
	 * 
	 * ����id��ȡ���ջ�����Ϣ
	 * @param baseId
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping(value = {"/getSciPopBase/{baseId}"}, method = RequestMethod.GET)
	@ResponseBody
	public Object getSciPopBaseById(@PathVariable int baseId, Model model, HttpSession session) {
		SciPopBaseDao sciPopBaseDao = new SciPopBaseDao();
		SciPopBase sciPopBase = sciPopBaseDao.getSciPopBaseById(baseId);
		return sciPopBase;
	}
	
	/**
	 * ���¿��ջ�����Ϣ
	 * @param sciPopBase
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping(value={"/updateSciPopBase"}, method= RequestMethod.POST)
	@ResponseBody
	public Object updateSciPopBase(SciPopBase sciPopBase, Model model, HttpSession session) {
		JSONObject jsonObject = new JSONObject();
		if(session.getAttribute("class") == null) {
			jsonObject.put("update", "no");
			jsonObject.put("reason", "1");
			return jsonObject;
		} else {
			String userclass = String.valueOf(session.getAttribute("class"));
			if (userclass.equals("2") || userclass.equals("3")) {
				SciPopBaseDao sciPopBaseDao = new SciPopBaseDao();
				sciPopBaseDao.updateSciPopBase(sciPopBase);
				jsonObject.put("update", "yes");
				jsonObject.put("reason", "0");
				return jsonObject;
			} else {
				jsonObject.put("update", "no");
				jsonObject.put("reason", "1");
				return jsonObject;
			}
		}
	}
	
	/**
	 * ɾ��������Ϣ
	 * @param baseId
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping(value={"/deleteSciPopBase/{baseId}"}, method = RequestMethod.GET)
	@ResponseBody
	public Object deleteSciPopBase(@PathVariable int baseId, Model model, HttpSession session) {
		JSONObject jsonObject = new JSONObject();
		if(session.getAttribute("class") == null) {
			jsonObject.put("update", "no");
			jsonObject.put("reason", "1");
			return jsonObject;
		} else {
			String userclass = String.valueOf(session.getAttribute("class"));
			if (userclass.equals("2") || userclass.equals("3")) {
				SciPopBaseDao sciPopBaseDao = new SciPopBaseDao();
				sciPopBaseDao.deleteSciPopBaseById(baseId);
				jsonObject.put("update", "yes");
				jsonObject.put("reason", "0");
				return jsonObject;
			} else {
				jsonObject.put("update", "no");
				jsonObject.put("reason", "1");
				return jsonObject;
			}
		}
	}
	
	@RequestMapping(value={"/addScore/{name}"}, method = RequestMethod.GET)
	@ResponseBody
	public Object addScore(@PathVariable String name, Model model, HttpSession session) {
		JSONObject jsonObject = new JSONObject();
		if(session.getAttribute("class") == null) {
			jsonObject.put("info", 14);
			return jsonObject;
		} else {
			String userclass = String.valueOf(session.getAttribute("class"));
			if (userclass.equals("2") || userclass.equals("3")) {
				UserDao userDao = new UserDao();
				if (userDao.queryUserbyName(name)) {
					User user = userDao.getUserbyName(name);
					int score = user.getScore();
					score++;
					user.setScore(score);
					userDao.UpdateUser(user);
					
					jsonObject.put("info", 1);
					return jsonObject;
				} else {
					jsonObject.put("info", 6);
					return jsonObject;
				}
				
			} else {
				jsonObject.put("info", 14);
				return jsonObject;
			}
		}
	}
	@RequestMapping(value={"/getScore/{name}"}, method = RequestMethod.GET)
	@ResponseBody
	public Object getScore(@PathVariable String name, Model model, HttpSession session) {
		JSONObject jsonObject = new JSONObject();
		if(session.getAttribute("username") == null) {
			jsonObject.put("info", 14);
			return jsonObject;
		} else {
			UserDao userDao = new UserDao();
			int score = userDao.getScore(name);
			jsonObject.put("info", 1);
			jsonObject.put("score", score);
			return jsonObject;
		}
	}
}
