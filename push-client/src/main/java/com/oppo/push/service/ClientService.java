package com.oppo.push.service;

import com.oppo.push.entity.Client;

public interface ClientService {

	public Client addClient(Client client);
	
	public Client queryCid(String key);
	
	public boolean queryKey(String key);
	
	public boolean updateClient(Client client);
}
