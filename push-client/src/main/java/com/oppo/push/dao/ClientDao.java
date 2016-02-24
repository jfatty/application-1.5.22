package com.oppo.push.dao;

import java.io.Serializable;

import com.oppo.push.entity.Client;

public interface ClientDao<T, K extends Serializable> extends IDao<T, K> {

	
	public String queryCid(String key);
	
	public boolean queryKey(String key);
	
	public Client queryClient(String cid);
	
	public boolean updateClient(Client client);
}
