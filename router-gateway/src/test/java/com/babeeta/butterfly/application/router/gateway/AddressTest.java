package com.babeeta.butterfly.application.router.gateway;

import com.babeeta.butterfly.misc.Address;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AddressTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testBuildAddressString() {
		Address addr = new Address("cid.aid@domain.com");
		Assert.assertEquals("cid.aid@another.domain",
                addr.buildAddress("another.domain"));
	}

	@Test
	public void testBuildAddressStringString() {
		Address addr = new Address("cid.aid@domain.com");
		Assert.assertEquals("did.cid.aid@another.domain",
                addr.buildAddress("did", "another.domain"));
	}

	@Test
	public void test不带有DeviceId的地址() throws Exception {
		Address addr = new Address("cid.aid@domain.com");
		Assert.assertNull(addr.deviceId);
		Assert.assertEquals("cid", addr.clientId);
		Assert.assertEquals("aid", addr.applicationId);
		Assert.assertEquals("domain.com", addr.domain);
	}

	@Test
	public void test带有DeviceId的地址() throws Exception {
		Address addr = new Address("did.cid.aid@domain.dev");
		Assert.assertEquals("did", addr.deviceId);
		Assert.assertEquals("cid", addr.clientId);
		Assert.assertEquals("aid", addr.applicationId);
		Assert.assertEquals("domain.dev", addr.domain);
	}

}
