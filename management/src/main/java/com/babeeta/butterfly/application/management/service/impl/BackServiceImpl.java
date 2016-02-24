package com.babeeta.butterfly.application.management.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.babeeta.butterfly.application.management.AbstractHttpRPCService;
import com.babeeta.butterfly.application.management.dao.AppRegisterDao;
import com.babeeta.butterfly.application.management.dao.RoleDao;
import com.babeeta.butterfly.application.management.dao.UserDao;
import com.babeeta.butterfly.application.management.entity.App;
import com.babeeta.butterfly.application.management.entity.Role;
import com.babeeta.butterfly.application.management.entity.User;
import com.babeeta.butterfly.application.management.service.BackService;

public class BackServiceImpl extends AbstractHttpRPCService implements BackService {

	private UserDao userDaoImpl;
	
	private AppRegisterDao appRegisterDaoImpl;
	
	private RoleDao roleDaoImpl;
	
	
	/***
	 * 添加用户
	 * @param user
	 * @return
	 */
	public User addUser(User user)
	{
		this.userDaoImpl.save(user);
		return user;
	}
	
	
	/***
	 * 删除用户
	 * @param user
	 */
	public void deleteUser(User user)
	{
		this.userDaoImpl.deleteByEntityId(user.getId());
	}
	
	/***
	 * 用户登录
	 * @param userAccount
	 * @param userPass
	 * @return
	 */
	public User login(String userAccount,String userPass)
	{
		return this.userDaoImpl.login(userAccount, userPass);
	}
	
	/***
	 * 查询用户
	 * @param user
	 * @return
	 */
	public User queryUserByAccount(String account)
	{
		 this.userDaoImpl.queryUserByEmail(account);
		 return null;
	}
	
	/***
	 * 更新用户
	 * @param user
	 * @return
	 */
	public User updateUser(User user)
	{
		return null;
	}
	
	/***
	 * 注册
	 * @param app
	 * @return
	 */
	public App addAppRegister(App app)
	{
		return this.appRegisterDaoImpl.addAppRegister(app);
	}
	
	/****
	 * 更新
	 * @param app
	 * @return
	 */
	public App updateAppRegister(App app)
	{
		return this.appRegisterDaoImpl.updateAppRegister(app);
	}
	
	/***
	 * 删除
	 * @param app
	 */
	public void deleteAppRegister(App app)
	{
		this.appRegisterDaoImpl.deleteAppRegister(app);
	}
	
	/***
	 * 通过id查询
	 * @param id
	 * @return
	 */
	public App queryById(String id)
	{
		return this.appRegisterDaoImpl.queryById(id);
	}
	
	/****
	 * 分页查询
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<App> queryAll(int offset,int limit)
	{
		return this.appRegisterDaoImpl.queryAll(offset, limit);
	}
	
	/****
	 * 通过用户查询
	 * @param userAccount
	 * @return
	 */
	public App queryAppByUserAccount(String userAccount)
	{
		return this.appRegisterDaoImpl.queryByUserAcount(userAccount);
	}
	
	/***
	 * 添加角色
	 * @param role
	 * @return
	 */
	public Role addRole(Role role)
	{
		return null;//this.roleDaoImpl.addRole(role);
	}
	
	/****
	 * 查询角色信息
	 * @param userAccount
	 * @return
	 */
	public Role queryByUserAccount(String userAccount)
	{
		System.out.println(this.roleDaoImpl+" "+userAccount);
		return this.roleDaoImpl.queryByUserAccount(userAccount);
	}

	
	/***
	 * 应用注册
	 * @param map
	 * @return
	 */
	public String registerApp(Map<String,Object> map)
	{
		System.out.println("-------");
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://124.207.12.162:9801/api/register/");
		post.setHeader("Content-type", "application/json");
		StringBuffer json=new StringBuffer("{\"id\":\"111\",\"expiredMsgFeedbackUrl\":\"222\",\"extra\":{");
		if(map!=null&&map.size()>0)
		{
		
			Iterator<String> it=map.keySet().iterator();
			while(it.hasNext())
			{
				String key=it.next();
				String value=(String)map.get(key);
				json.append("\""+key+"\":\""+value+"\"");
				json.append(",");
			}
			json.delete(json.length()-1,json.length());
			//json.substring(0,json.length()-2);
			json.append("}}");
			System.out.println(json);
			post.setEntity(new ByteArrayEntity(json.toString().getBytes()));
		}
		try {
			HttpResponse response = client.execute(post);
			System.out.println("    [response status]"
					+ response.getStatusLine());
			String responseString = EntityUtils.toString(response
					.getEntity());
			//logger.debug("response: {}", responseString);
			if (response.getStatusLine().getStatusCode() == 200) {
				System.out.println(responseString);
				return responseString;
			} else {
				return "";
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("[backServiceImpl] "
					+ post.getURI().getPath() + " failed.", e);
			return "";
		}
	
	
	}

	public void setUserDaoImpl(UserDao userDaoImpl) {
		this.userDaoImpl = userDaoImpl;
	}


	public void setAppRegisterDaoImpl(AppRegisterDao appRegisterDaoImpl) {
		this.appRegisterDaoImpl = appRegisterDaoImpl;
	}


	public void setRoleDaoImpl(RoleDao roleDaoImpl) {
		this.roleDaoImpl = roleDaoImpl;
	}


	@Override
	protected String getHost() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public static void main(String[] args)
	{
	   BackServiceImpl b=new BackServiceImpl();
	   Map<String,Object> map=new HashMap<String,Object>();
	   map.put("life", "default");
	   b.registerApp(map);
	}
}
