package com.babeeta.butterfly.application.management.service.impl;


import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.babeeta.butterfly.application.management.dao.LicenceDao;
import com.babeeta.butterfly.application.management.entity.Licence;
import com.babeeta.butterfly.application.management.service.LicenceService;
import com.babeeta.butterfly.application.management.util.Contast;
import com.babeeta.butterfly.application.management.util.Crypt;
import com.babeeta.butterfly.application.management.util.DesUtils;
import com.babeeta.butterfly.application.management.util.LicenceFile;

/***
 * licence管理
 * @author zeyong.xia
 * @date 2011-12-30
 */
public class LicenceServiceImpl implements LicenceService {

	private LicenceDao<Licence,String> licenceDaoImpl;
	
	private String appCountHost;
	
	private final static Logger logger = LoggerFactory
	.getLogger(LicenceServiceImpl.class);
	/***
	 * 查询应用总数
	 * @return
	 */
	public int appCount()
	{
		logger.debug("[LicenceServiceImpl] appCount start");
		Licence li=this.getLicence();
		if(li==null)
		{
			logger.debug("[LicenceServiceImpl] appCount fail, licence class is null");
			return 0;
		}
		/*
		LicenceFile lf=new LicenceFile();
		lf.setKey(Contast.KEY);*/
		String res=li.getLicence();
		if(res==null||res.equals(""))
		{
			logger.debug("[LicenceServiceImpl] appCount fail, licence is null");
			return 0;
		}
		logger.debug("[LicenceServiceImpl] appCount,licence is{}",res);
		String[] result=this.desLicence(res);
		if(result==null||result.length!=3)
		{
			return 0;
		}
		String str= result[2].split(":")[1];
	
		if(str==null||str.equals(""))
		{
			logger.debug("[LicenceServiceImpl] appCount fail,split by : error");
			return 0;
		}
		try{
			logger.debug("[LicenceServiceImpl] appCount success");
			return Integer.valueOf(str);
		}
		catch(Exception e )
		{
			logger.debug("[LicenceServiceImpl] appCount fail,exception is {}",e.getLocalizedMessage());
			return 0;
		}
		
	}
	
	/***
	 * 得到licence信息
	 * @return
	 */
	public Licence getLicence()
	{
		return this.licenceDaoImpl.getLicence();
	}
	
	/***
	 * 调用外部接口
	 * @return
	 */
	public int queryAppFromInter()
	{
		//TODO
		logger.debug("[LicenceServiceImpl] queryAppFromInter start appCountHost is {}",appCountHost);
		HttpGet request = new HttpGet(appCountHost);
		DefaultHttpClient client = new DefaultHttpClient();
		request.setHeader(new BasicHeader("Content-type", " application/octet-stream"));
		try {
			HttpResponse response = client.execute(request);
			String result=EntityUtils.toString(response.getEntity());
			int num=Integer.valueOf(result);
			logger.debug("[LicenceServiceImpl] queryAppFromInter success,result is {}",result);
			return num;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.debug("[LicenceServiceImpl] queryAppFromInter fail,exception  is {}",e.getLocalizedMessage());
		} 
		return 200;
	}
	/**
	 * 更新licence
	 * @param licence
	 * @return
	 */
	public boolean updateLicence(String id,Map<String,Object> map)
	{
		return this.licenceDaoImpl.updateLicence(id,map);
	}
	
	public boolean addLicence(Licence licence)
	{
		try{
			this.licenceDaoImpl.save(licence);
		}
		catch(Exception e)
		{
			logger.debug("[LicenceServiceImpl] addLicence error ,exception is {}",e.getLocalizedMessage());
			return false;
		}
		return true;
	}
	
	private String[] desLicence(String licence)
	{
		try {
			String[] result=new String[3];
			DesUtils defaultDes=new DesUtils();
			String firstMing=defaultDes.decrypt(licence);
			String[] split=firstMing.split(",");
			if(split==null||split.length!=3)
			{
				System.out.println("解密出错");
				return null;
			}
			String keyMing=defaultDes.decrypt(split[0]);
			result[0]=keyMing;
			String key=keyMing.split(":")[1];
			System.out.println("解密后的原始key= "+key);
			key=defaultDes.encrypt(key);
			System.out.println("使用的key= "+key);
			DesUtils des=new DesUtils(key);
			String mingCom=des.decrypt(split[1]);
			result[1]=mingCom;
			System.out.println("解密后的公司信息= "+mingCom);
			String mingNum=des.decrypt(split[2]);
			result[2]=mingNum;
			System.out.println("解密后的数量="+mingNum);
			return result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}

	public LicenceDao<Licence,String> getLicenceDaoImpl() {
		return licenceDaoImpl;
	}

	public void setLicenceDaoImpl(LicenceDao<Licence,String> licenceDaoImpl) {
		this.licenceDaoImpl = licenceDaoImpl;
	}

	public String getAppCountHost() {
		return appCountHost;
	}

	public void setAppCountHost(String appCountHost) {
		this.appCountHost = appCountHost;
	}
	
	
}
