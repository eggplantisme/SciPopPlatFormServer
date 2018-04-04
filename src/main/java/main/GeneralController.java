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
	 * 测试用url
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
	 * 测试用注册页面
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/regist", method = RequestMethod.GET)
    public String regist_jsp(Model model){
    	return "regist";
    }
	
	/**
	 * 注册
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
			//防止注册一个和管理员同名的用户
			if (userDao.queryUserbyName(username) || username.equals(Admin.AdminName)) {
				JSONObject result = new JSONObject();
				result.put("repeated", "yes");
				result.put("info", 0);
				result.put("class", userclass);
				return result;
			} else {
				user.setScore(0);
				//不是专家会员和基地会员的注册正常通过
				//若是则需要进入临时用户表等待审查
				if(user.getUserclass() != 1 && user.getUserclass() != 2) {
					userDao.addUser(user);
					JSONObject result = new JSONObject();
					result.put("repeated", "no");
					result.put("info", 1);
					result.put("class", userclass);
					
					//设置session
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
	 * 对注册登录信息进行模式检测
	 * 出错信息info格式如下
	 * 0：repeated
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
		//名字格式匹配，长度不超过100
		String usernamePattern = "^.{1,100}$";
		Pattern pattern = Pattern.compile(usernamePattern);
		Matcher matcher = pattern.matcher(username);
		if (!matcher.matches()) {
			result.put("repeated", "no");
			result.put("info", 2);
			result.put("class", userclass);
			return result;
		} else {
			//密码信息匹配
			String passwordPattern = "^.{6,100}$";
			pattern = Pattern.compile(passwordPattern);
			matcher = pattern.matcher(password);
			if (!matcher.matches()) {
				result.put("repeated", "no");
				result.put("info", 3);
				result.put("class", userclass);
				return result;
			} else {
				//电话信息匹配
				String phonePattern = "^\\d{11}$";
				pattern = Pattern.compile(phonePattern);
				matcher = pattern.matcher(phone);
				if (!matcher.matches()) {
					result.put("repeated", "no");
					result.put("info", 4);
					result.put("class", userclass);
					return result;
				} else {
					//类别匹配
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
	 * 登录
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
		//管理员用户返回临时用户列表
		if (username.equals(Admin.AdminName) && password.equals(Admin.AdminPass)) {
			TempUserDao tempUserDao = new TempUserDao();
			List<TempUser> tempUsers = tempUserDao.getUserList();
			//设置session
			session.setAttribute("username", "eggplant");
			session.setAttribute("class", 3);
			return tempUsers;
		} else {
			String phone = "12345678901";//占参用值
			int userclass = user.getUserclass();
			//检测格式
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
							
							//设置session
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
	 * 获取所有专家会员列表，方便进行会员文章排序
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
	 * 获取申请列表
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
	 * 管理员同意专家或基地会员注册的函数
	 * 返回值info意义
	 * 0表示成功
	 * 1表示权限不是管理员权限
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
	 * 删除临时用户
	 * 返回值info意义
	 * 0表示成功
	 * 1表示权限不是管理员权限
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
	 * 获得科普信息标题的列表
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
	 * 根据id获取相应的科普信息
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
	 * 根据session权限添加科普信息
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
	 * 更新科普信息
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
	 * 删除科普信息
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
	 * 添加科普基地信息
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
	 * 获取科普基地的列表
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
	 * 根据id获取科普基地信息
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
	 * 更新科普基地信息
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
	 * 删除科普信息
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
