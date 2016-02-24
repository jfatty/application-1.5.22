package com.oppo.push.service.impl;

import com.oppo.push.dao.ClientDao;
import com.oppo.push.entity.Client;
import com.oppo.push.service.ClientService;

public class ClientServiceImpl implements ClientService {

	private ClientDao<Client, String> clientDaoImpl;
	
	public Client addClient(Client client)
	{
		if(!this.queryKey(client.getKey()))
		{
			this.clientDaoImpl.save(client);
		}
		return client;
	}
	
	public Client queryCid(String key)
	{
		return this.clientDaoImpl.queryClient(key);
	}
	
	public boolean queryKey(String key)
	{
		return this.clientDaoImpl.queryKey(key);
	}
	
	public boolean updateClient(Client client)
	{
		return this.clientDaoImpl.updateClient(client);
	}

	public ClientDao<Client, String> getClientDaoImpl() {
		return clientDaoImpl;
	}

	public void setClientDaoImpl(ClientDao<Client, String> clientDaoImpl) {
		this.clientDaoImpl = clientDaoImpl;
	}
	
	
}
