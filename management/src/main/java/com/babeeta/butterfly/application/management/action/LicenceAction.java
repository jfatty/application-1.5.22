package com.babeeta.butterfly.application.management.action;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class LicenceAction extends BaseAction {

	
	/**
	 * @author zeyong.xia
	 * @date 2011-12-30
	 */
	private static final long serialVersionUID = 1L;
	
	private final static Logger logger = LoggerFactory
	.getLogger(LicenceAction.class);
	
	public File licenceFile;
	
	public String licenceFileContentType;
	
	public String licenceFileFileName;
	
	private String caption;
	
	private LicenceService licenceServiceImpl;
	
	private String allowTypes;
	
	private static final int MAX_NUM=1024;
	
	private String result="";
	
	
	/***
	 * 初始化licence
	 * @return
	 */
	public String initLicence()
	{
		logger.debug("[LicenceAction] initLicence");
		String result=request.getParameter("result");
		if(result==null)
		{
			result="";
		}
		int count=this.licenceServiceImpl.appCount();
		int app=this.licenceServiceImpl.queryAppFromInter();
		int last=count-app;
		request.setAttribute("count", count);
		request.setAttribute("app", app);
		request.setAttribute("last", last);
		request.setAttribute("result", result);
		logger.debug("[LicenceAction] initLicence count is {},app is {}",count,app);
		return SUCCESS;
	}

	/***
	 * 导入licence
	 * @return
	 */
	public String importLicenceFile()
	{
		logger.debug("[LicenceAction]importLiceneceFile,fileName is {}",this.licenceFileFileName);
		ByteArrayOutputStream bos=null;
		try{
			FileInputStream fis=new FileInputStream(getLicenceFile());
			byte[] b=new byte[MAX_NUM];
			int num=fis.read(b, 0, MAX_NUM);
			bos=new ByteArrayOutputStream();
			while(num>0)
			{
				bos.write(b,0,num);
				if(fis.available()>0){
				num=fis.read(b,0,MAX_NUM);
				}
				else{
					num=0;
				}
			}
			byte[] li=bos.toByteArray();
			String s=new String(li,"UTF-8");
			String[] results=this.desLicence(s);
			if(results==null)
			{
				logger.debug("[LicenceAction]importLiceneceFile fail");
				//导入失败
				request.setAttribute("result", "0");
				this.result="0";
			}
			String company=results[1].split(":")[1];
			String nums=results[2].split(":")[1];
			if(company==null||company==""||"".equals(company)||!company.equalsIgnoreCase("oppo"))
			{
				logger.debug("[LicenceAction]importLiceneceFile fail,licence not for oppo");
				//导入失败
				request.setAttribute("result", "0");
				this.result="0";
			}
			int n=Integer.valueOf(nums);
			/*
			Crypt mycrypt = new Crypt();
			String str2=mycrypt.getDecryptedString(s);
			logger.debug("[LicenceAction]importLiceneceFile,licence is{}",str2);
			int result=readLicence(str2);
			logger.debug("[LicenceAction]importLiceneceFile ,result is {}",result);
			*/
			if(n!=0)
			{
				//TODO导入成功
				Licence licence=this.licenceServiceImpl.getLicence();
				if(licence==null)
				{
					licence=new Licence();
					licence.setId(UUID.randomUUID().toString().replace("-", ""));
					licence.setLicence(s);
					licence.setCreateAt(new Date());
					this.licenceServiceImpl.addLicence(licence);
					request.setAttribute("result", "1");
					this.result="1";
				}
				else
				{
					Map<String,Object> map=new HashMap<String,Object>();
					map.put("licence", s);
					map.put("createAt", new Date());
					this.licenceServiceImpl.updateLicence(licence.getId(),map);
					request.setAttribute("result", "1");
					this.result="1";
				}
			}
			else
			{
				//导入失败
				request.setAttribute("result", "0");
				this.result="0";
			}
		
		}catch(Exception e)
		{
			logger.debug("[LicenceAction]importLiceneceFile error,exception is {} ",e.getMessage());
			request.setAttribute("result", "0");
			this.result="0";
			return "error";	
		}
		logger.debug("[LicenceAction]importLiceneceFile success ,file size is {}",this.licenceFile.length());
		
		return SUCCESS;
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
			if(split==null||split.length!=3)
			{
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
	
	private static int readLicence(String licence)
	{
		if(licence==null||licence.equals(""))
		{
			logger.debug("[LicenceAction]readLicence licence is null");
			return 0;
		}
		String[] strs=licence.split(":");
		if(strs==null||strs.length!=2)
		{
			logger.debug("[LicenceAction]readLicence split licence is null");
			return 0;
		}
		String oppo=strs[0];
		String num=strs[1];
		if(oppo==null||oppo.equals("")||!oppo.equals("oppo"))
		{
			logger.debug("[LicenceAction]readLicence licence is not for oppo");
			return 0;
		}
		try{
			int result=Integer.valueOf(num);
			logger.debug("[LicenceAction]readLicence success");
			return result;
		}
		catch(Exception e)
		{
			logger.debug("[LicenceAction]readLicence error,exception is {}",e.getMessage());
			return 0;
		}
	}

	public File getLicenceFile() {
		return licenceFile;
	}

	public void setLicenceFile(File licenceFile) {
		this.licenceFile = licenceFile;
	}

	public void setLicenceServiceImpl(LicenceService licenceServiceImpl) {
		this.licenceServiceImpl = licenceServiceImpl;
	}

	public String getLicenceFileContentType() {
		return licenceFileContentType;
	}

	public void setLicenceFileContentType(String licenceFileContentType) {
		this.licenceFileContentType = licenceFileContentType;
	}

	public String getLicenceFileFileName() {
		return licenceFileFileName;
	}

	public void setLicenceFileFileName(String licenceFileFileName) {
		this.licenceFileFileName = licenceFileFileName;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	

	public String getAllowTypes() {
		return allowTypes;
	}

	public void setAllowTypes(String allowTypes) {
		this.allowTypes = allowTypes;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	
}
