package com.babeeta.butterfly.application.router.gateway.balance;

import java.util.List;
import java.util.TreeMap;

public class KetamaHashServiceLocator implements ServiceLocator {

	private TreeMap<Long, String> resourceMap = new TreeMap<Long, String>();

	private static final int NUM_REPS = 160;

	public KetamaHashServiceLocator() {
	}

	public KetamaHashServiceLocator(List<String> secondaryDomainList) {
		this.updateServer(secondaryDomainList);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.babeeta.butterfly.router.balance.ResourceFactory#getResource(java
	 * .lang.String)
	 */
	@Override
	public String getDomain(String key) {
		final TreeMap<Long, String> resourceMap = this.resourceMap;
		Long k = HashAlg.computeKeyHash(key);
		if (resourceMap.containsKey(k)) {
			return resourceMap.get(k);
		} else {
			k = resourceMap.ceilingKey(k);
			if (k == null) {
				k = resourceMap.firstKey();
			}
			return resourceMap.get(k);
		}
	}

	/**
	 * 更新服务器列表
	 * 
	 * @param domainList
	 */
	void updateServer(List<String> domainList) {
		final TreeMap<Long, String> resourceMap = new TreeMap<Long, String>();
		for (String domain : domainList) {
			for (int i = 0; i < NUM_REPS / 4; i++) {
				byte[] digest = HashAlg.computeMD5(new StringBuilder(domain)
						.append("-")
						.append(i)
						.toString());
				for (int h = 0; h < 4; h++) {
					resourceMap.put(HashAlg.computeNodeKey(digest, h), domain);
				}
			}
		}
		this.resourceMap = resourceMap;
	}
}