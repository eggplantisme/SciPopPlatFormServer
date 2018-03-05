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
    public Object index_jsp(Model model){
        model.addAttribute("str", "Hello world");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("get", "yes");
        jsonObject.put("satisfied", 1);
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
			if (userDao.queryUserbyName(username)) {
				JSONObject result = new JSONObject();
				result.put("repeated", "yes");
				result.put("info", 0);
				result.put("class", userclass);
				return result;
			} else {
				user.setScore(0);
				//����ר�һ�Ա�ͻ��ػ�Ա��ע������ͨ��
				//��������Ҫ������ʱ�û���ȴ����
				if(user.getUserclass() != 2 && user.getUserclass() != 3) {
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
					result.put("info", 6);
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
		//����Ա�û�������ʱ�û��б�
		if (username == "eggplant" && password == "eggplant") {
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
					result.put("repeated", "yes");
					result.put("info", 6);
					result.put("class", userclass);
					return result;
				} else {
					User temp_user = userDao.getUserbyName(username);
					if (temp_user.getPassword().equals(password)) {
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
						result.put("info", 7);
						result.put("class", userclass);
						return result;
					}
				}
			}
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
	@RequestMapping(value={"/adminAgree"}, method = RequestMethod.GET)
	@ResponseBody
	public Object AdminAgree(TempUser tempUser, Model model, HttpSession session) {
		if ((String)session.getAttribute("class") == "3") {
			User user = tempUser;
			UserDao userDao = new UserDao();
			userDao.addUser(user);
			TempUserDao tempUserDao = new TempUserDao();
			tempUserDao.DeleteUser(user.getUsername());
			JSONObject result = new JSONObject();
			result.put("agree", "true");
			result.put("info", 0);
			return result;
		} else {
			JSONObject result = new JSONObject();
			result.put("agree", "false");
			result.put("info", 1);
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
	@RequestMapping(value={"/adminDelete"}, method = RequestMethod.GET)
	@ResponseBody
	public Object AdminDelete(TempUser tempUser, Model model, HttpSession session) {
		if ((String)session.getAttribute("class") == "3") {
			TempUserDao tempUserDao = new TempUserDao();
			tempUserDao.DeleteUser(tempUser.getUsername());
			JSONObject result = new JSONObject();
			result.put("delete", "true");
			result.put("info", 0);
			return result;
		} else {
			JSONObject result = new JSONObject();
			result.put("delete", "false");
			result.put("info", 1);
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
			String userclass = (String)session.getAttribute("class");
			if (userclass == "1" || userclass == "3") {
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
			String userclass = (String)session.getAttribute("class");
			if (userclass == "1" || userclass == "3") {
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
			String userclass = (String)session.getAttribute("class");
			if (userclass == "1" || userclass == "3") {
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
			String userclass = (String)session.getAttribute("class");
			if (userclass == "2" || userclass == "3") {
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
			String userclass = (String)session.getAttribute("class");
			if (userclass == "1" || userclass == "3") {
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
			String userclass = (String)session.getAttribute("class");
			if (userclass == "1" || userclass == "3") {
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
}
