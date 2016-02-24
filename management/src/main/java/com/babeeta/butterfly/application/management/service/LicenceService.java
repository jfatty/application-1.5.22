package com.babeeta.butterfly.application.management.service;

import java.util.Map;

import com.babeeta.butterfly.application.management.entity.Licence;

/***
 * licence管理
 * @author zeyong.xia
 * @date 2011-12-30
 */
public interface LicenceService {

	
	/***
	 * 查询应用总数
	 * @return
	 */
	public int appCount();
	
	/***
	 * 得到licence信息
	 * @return
	 */
	public Licence getLicence();
	
	/**
	 * 更新licence
	 * @param licence
	 * @return
	 */
	public boolean updateLicence(String id,Map<String,Object> map);
	
	/***
	 * 调用外部接口
	 * @return
	 */
	public int queryAppFromInter();
	
	public boolean addLicence(Licence licence);
}
