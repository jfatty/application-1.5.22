package com.licence.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 用户密码加密解密
 * @author xuyawei
 * @since 2011-05-10
 */
public class Crypt {
	public static String KEYSTRING_GGGGGGGGGGGGGGGGGGG="c892ba238c98835d4d53a3faed43ee52";//"202cb962ac59075b964b07152d234b70";
	
	private String key="";
	
	public Crypt(String key)
	{
		this.key=key;
	}
	// --------------------------------------------------------------------------------------------
	// 获得密钥
	public SecretKey getKey(String s) throws Exception {
		// s ="g8TlgLEc6oqZxdwGe6pDiKB8Y";
//		System.out.println("s==" + s);
		char[] ss = s.toCharArray();
		String sss = "";
		for (int i = 0; i < ss.length; i = i + 2) {
			sss = sss + ss[i];
		} 
		SecretKeyFactory kf = SecretKeyFactory.getInstance("DES");
		DESKeySpec ks = new DESKeySpec(sss.substring(0, 8).getBytes());
		SecretKey kd = kf.generateSecret(ks);
		return kd;
	}

	// --------------------------------------------------------------------------------------------------
	// 返回加密后的字符串
	// key是用于生成密钥的字符串，input是要加密的字符串
	public String getEncryptedString(String key, String input) {
		String base64 = "";
		try {
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, getKey(key));
//			System.out.print("getKey(key)===" + getKey(key) + "key==" + key);
			byte[] inputBytes = input.getBytes();
			byte[] outputBytes = cipher.doFinal(inputBytes);
			BASE64Encoder encoder = new BASE64Encoder();
			base64 = encoder.encode(outputBytes);
		} catch (Exception e) {
			base64 = e.getMessage();
		}
		return base64;
	}

	// --------------------------------------------------------------------------------------------------
	// 返回解密后的字符串
	// key是用于生成密钥的字符串，input是要解密的字符串
	public String getDecryptedString(String key, String input) {
		String result = null;
		try {
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, getKey(key));
			BASE64Decoder decoder = new BASE64Decoder();
			byte[] raw = decoder.decodeBuffer(input);
			byte[] stringBytes = cipher.doFinal(raw);
			result = new String(stringBytes, "UTF8");
		} catch (Exception e) {
			result = e.getMessage();
		}
		return result;
	}

	public String getKeyByResource(){
		return this.key;
//		String str ="c892ba238c98835d4d53a3faed43ee52";//"202cb962ac59075b964b07152d234b70";// ApplicationResource.getValueByKey("password.key");
//		if(str!=null && !str.equals("")){
//			return str;
//		}else{
//			return KEYSTRING;
//		}
	}
	/**
	 * 加密
	 * @param input 加密前的字符串
	 * @return
	 */
	public String getEncryptedString(String input){
		return getEncryptedString(getKeyByResource(),input);
	}
	/**
	 * 解密
	 * @param input 加密后的字符串
	 * @return
	 */
	public String getDecryptedString(String input) {
		return getDecryptedString(getKeyByResource(),input);
	}
	
	
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public static void main(String[] args) {
		Crypt mycrypt = new Crypt("aaaao8hhsda");
		try {
			// SecretKey skey = mycrypt.getKey("g8TlgLEc6oqZxdwGe6pDiKB8Y");
			System.out.println("key= "+mycrypt.getKey());
			String ss = mycrypt.getEncryptedString("key:c892ba238c98835d4d53a3faed43ee52,company:oppo,c740e4dda8926837");
			//ss=mycrypt.getEncryptedString("oppo:250");
			System.out.println("ss==" + ss);
			String ss2 = mycrypt.getDecryptedString("KHID8kDVErHzwTO36AmNiuABev2dMcAaFYU7FqndzOnceMHg8wOM8+oL/xoO6r7Q+sFG/htX1VIDoOgqDuJL7EhT9MGznN3+");
			System.out.println("ss2==" + ss2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
