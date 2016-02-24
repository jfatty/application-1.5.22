package com.babeeta.butterfly.application.management.service.impl;


import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.babeeta.butterfly.application.management.dao.AppDao;
import com.babeeta.butterfly.application.management.entity.App;
import com.babeeta.butterfly.application.management.service.AppService;
/***
 * app逻辑处理
 * @author zeyong.xia
 * @date 2011-12-10
 */
public class AppServiceImpl implements AppService {

	private AppDao<App,String> appDaoImpl;
	
	private static final String HOST="http://accounts.app/api/register/";
	
	private String appRegisterHost;
	
	private final static Logger logger = LoggerFactory
	.getLogger(AppServiceImpl.class);
	
	/***
	 * 新增应用
	 * @param app
	 * @return
	 */
	public App addApp(App app)
	{
		this.appDaoImpl.save(app);
		return app;
	}
	
	/***
	 * 查询应用
	 * @param id
	 * @return
	 */
	public App queryById(String id)
	{
		return this.appDaoImpl.findById(id);
	}
	
	/***
	 * 更新应用
	 * @param app
	 * @return
	 */
	public App updateApp(App app)
	{
		this.appDaoImpl.updateApp(app);
		return app;
	}
	
	/***
	 * 删除
	 * @param id
	 * @return
	 */
	public void deleteApp(String id)
	{
		this.appDaoImpl.deleteByEntityId(id);
	}
	
	/***
	 * 总数
	 * @return
	 */
	public int queryAppCount()
	{
		return this.appDaoImpl.queryCount();
	}
	
	/***
	 * 分页查询应用
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<App> queryAppList(int offset,int limit)
	{
		return this.appDaoImpl.queryList(offset, limit);
	}
	/***
	 * 查询某用户的应用
	 * @param email
	 * @param offset
	 * @param limit
	 * @return
	 */
	@Override
	public List<App> queryMyAppList(int offset, int limit, String userEmail) {
		// TODO Auto-generated method stub
		return this.appDaoImpl.queryAppByEmail(userEmail, offset, limit);
	}
	
	public void setAppDaoImpl(AppDao<App,String> appDaoImpl) {
		this.appDaoImpl = appDaoImpl;
	}

	/***
	 * 查询某用户应用列表
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<App> queryAppList(int offset,int limit,String userEmail,List<String> list)
	{
		return this.appDaoImpl.queryAppList(offset, limit, userEmail, list);
	}
	
	/***
	 * 应用注册
	 * @param map
	 * @return
	 */
	public String registerApp(App app)
	{
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(HOST);
		post.setHeader("Content-type", "application/json");
//		StringBuffer json=new StringBuffer("{\"id\":\"\",\"expiredMsgFeedbackUrl\":\""+app.getCallBackUrl()+"\",\"extra\":{");
		StringBuffer json=new StringBuffer("");
		json.append("{\"id\":\"\",\"expiredMsgFeedbackUrl\":\"").append(app.getCallBackUrl()).append("\",\"extra\":{");
		if(app.getMessageLifeCycle()!=null&&app.getMessageLifeCycle().length>0)
		{		
			String[] it=app.getMessageLifeCycle();
			StringBuffer sb=new StringBuffer("");
			for(int i=0;i<it.length;i++)
			{
				String value=it[i];
				sb.append(value).append(",");
			}
			json.append("\"life\":\"");
		    json.append(sb.delete(sb.length()-1, sb.length()).toString())	
		    .append("\"");
			//json.substring(0,json.length()-2);
			json.append("}}");
			logger.debug("[AppServiceImpl] registerApp,json is {}",json.toString());
			post.setEntity(new ByteArrayEntity(json.toString().getBytes()));
		}
		try {
			HttpResponse response = client.execute(post);
			logger.debug("[AppServiceImpl] registerApp,response status is {}",response.getStatusLine());
			String responseString = EntityUtils.toString(response
					.getEntity());
			//logger.debug("response: {}", responseString);
			if (response.getStatusLine().getStatusCode() == 200) {
				JSONObject obj=JSONObject.fromObject(responseString);
				String id=obj.getString("id");
				String key=obj.getString("secureKey");
				return id+":"+key;
			} else {
				return "";
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	
	
	}

	/***
	 * 查询未审核的app总数
	 * @return
	 */
	public int queryNotCheckAppCount()
	{
		return this.appDaoImpl.queryNotCheckAppCount();
	}
	
	/***
	 * 查询应用
	 * @param email
	 * @return
	 */
	public List<App> queryAppList(Map<String,Object> map,int offset,int limit)
	{
		return this.appDaoImpl.queryList(offset, limit, map);
	}
	
	/***
	 * 查询用用总数
	 * @param map
	 * @return
	 */
	public int queryAppCount(Map<String,Object> map)
	{
		return this.appDaoImpl.queryCount(map);
	}

	public String getAppRegisterHost() {
		return appRegisterHost;
	}

	public void setAppRegisterHost(String appRegisterHost) {
		this.appRegisterHost = appRegisterHost;
	}
	
	
}
