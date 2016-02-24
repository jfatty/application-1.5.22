package com.babeeta.butterfly.application.router.gateway.balance;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Hash相关算法的实现
 * 
 * @author Leon
 * 
 */

public class HashAlg {

	public static Long computeKeyHash(String key) {
		byte[] bKey = HashAlg.computeMD5(key);
		return ((long) (bKey[3] & 0xFF) << 24)
										| ((long) (bKey[2] & 0xFF) << 16)
										| ((long) (bKey[1] & 0xFF) << 8)
				| (bKey[0] & 0xFF);
	}

	public static byte[] computeMD5(String value) {
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("md5");
			md5.reset();
			return md5.digest(value.getBytes("utf-8"));

		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("MD5 not supported.", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("UTF-8 not supported.", e);
		}
	}

	public static Long computeNodeKey(byte[] digest, int h) {
		return ((long) (digest[3 + h * 4] & 0xFF) << 24)
						| ((long) (digest[2 + h * 4] & 0xFF) << 16)
						| ((long) (digest[1 + h * 4] & 0xFF) << 8)
						| (digest[h * 4] & 0xFF);
	}

}
