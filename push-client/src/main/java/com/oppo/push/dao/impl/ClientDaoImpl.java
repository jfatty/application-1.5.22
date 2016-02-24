package com.oppo.push.dao.impl;

import java.util.List;

import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.oppo.push.dao.ClientDao;
import com.oppo.push.dao.MorphiaDataStore;
import com.oppo.push.entity.Client;



public class ClientDaoImpl extends BaseDao<Client, String> implements ClientDao<Client, String> {

	public ClientDaoImpl(MorphiaDataStore ds)
	{
		super(Client.class, ds);
	}

	@Override
	public String queryCid(String key) {
		// TODO Auto-generated method stub
		Query<Client> query=this.ds.createQuery(Client.class).filter("key", key);
		List<Client> list=query.asList();
		if(list!=null&&list.size()>0)
		{
			return list.get(0).getKey();
		}
		return "85d2d73146084bdbbc7e83fde4ffc741";// "34c2ef5e5e1c4f25a4a44e8107590974";//"5e610a6f319a4203b752a8d910c6d5c4"//"46852304d7094e178da7efe0eaf70d7f"
	}

	@Override
	public boolean queryKey(String key) {
		// TODO Auto-generated method stub
		Query<Client> query=this.ds.createQuery(Client.class).filter("key", key);
		List<Client> list=query.asList();
		if(list!=null&&list.size()>0)
		{
			return true;
		}
		return false;
	}
	
	public Client queryClient(String cid)
	{
		// TODO Auto-generated method stub
		Query<Client> query=this.ds.createQuery(Client.class).filter("cid", cid);
		List<Client> list=query.asList();
		if(list!=null&&list.size()>0)
		{
			return list.get(0);
		}
		return null;
	
	}
	
	public boolean updateClient(Client client)
	{
		Query<Client> query=this.ds.createQuery(Client.class).filter("_id", client.getId());
		UpdateOperations<Client> ups=this.ds.createUpdateOperations(Client.class);
		ups.set("key", client.getKey());
		this.ds.update(query, ups);
		return true;
	}
	
	
}
