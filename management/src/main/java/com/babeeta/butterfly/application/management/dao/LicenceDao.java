package com.babeeta.butterfly.application.management.dao;

import java.io.Serializable;
import java.util.Map;

import com.babeeta.butterfly.application.management.entity.Licence;

public interface LicenceDao<T, K extends Serializable> extends IDao<T, K> {

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
	
}
