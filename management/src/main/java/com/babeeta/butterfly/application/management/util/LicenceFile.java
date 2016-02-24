package com.babeeta.butterfly.application.management.util;

import java.security.*;  
import javax.crypto.*;  

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.*;
/**       
    *   使用DES加密与解密,可对byte[],String类型进行加密与解密  
    *   密文可使用String,byte[]存储.   
    *   方法:  
    *   void getKey(String   strKey)从strKey的字条生成一个Key     
    *   String getEncString(String strMing)对strMing进行加密,返回String密文  
    *   String getDesString(String strMi)对strMin进行解密,返回String明文  
    *   byte[] getEncCode(byte[] byteS)byte[]型的加密  
    *   byte[] getDesCode(byte[] byteD)byte[]型的解密  
    */      
public class LicenceFile{
	private final static Logger logger = LoggerFactory
	.getLogger(LicenceFile.class);
	
  private Key key;
  private byte[] byteMi = null;
  private byte[] byteMing = null;
  private String strMi= "";
  private String strM= ""; 
  //  根据参数生成KEY   
  public void setKey(String strKey){ 
   try{  
        KeyGenerator _generator = KeyGenerator.getInstance("DES");  
        _generator.init(new SecureRandom(strKey.getBytes()));  
        this.key = _generator.generateKey();  
        _generator=null;
        }
    catch(Exception e){
     e.printStackTrace();
     }
   
    }  
  //  加密String明文输入,String密文输出  
  public String setEncString(String strMing){
   BASE64Encoder base64en = new BASE64Encoder();  
    try {
     this.byteMing = strMing.getBytes("UTF8");  
      this.byteMi = this.getEncCode(this.byteMing);  
      this.strMi = base64en.encode(this.byteMi);
      
     }  
    catch(Exception e)
    {
     e.printStackTrace();
     }
   finally
     {
 
      this.byteMing = null;  
      this.byteMi = null;
      }
   return this.strMi;
  }  
  //加密以byte[]明文输入,byte[]密文输出    
  private byte[] getEncCode(byte[] byteS){
	  
	  logger.debug("[LicenceFile] getEncCode start");
   byte[] byteFina = null;  
    Cipher cipher;  
    try
     {
      cipher = Cipher.getInstance("DES");  
      cipher.init(Cipher.ENCRYPT_MODE,key);  
      byteFina = cipher.doFinal(byteS);
      }  
    catch(Exception e)
     {
      e.printStackTrace();
      }  
    finally
    {
     cipher = null;
     }
       
   return byteFina;
  } 
// 解密:以String密文输入,String明文输出   
  @SuppressWarnings("restriction")
public String setDesString(String strMi){  
   BASE64Decoder base64De = new BASE64Decoder();
   logger.debug("[LicenceFile] setDesString start,strMi is {}",strMi);
    try
    {
    // this.byteMi = base64De.decodeBuffer(strMi);
    this.byteMi= Base64.decodeBase64(strMi);
     logger.debug("[LicenceFile] setDesString ,byteMi is {}",byteMi);
      this.byteMing = this.getDesCode(byteMi);  
      this.strM = new String(byteMing,"UTF8"); 
      logger.debug("[LicenceFile] setDesString ,strM is {}",strM);
      }  
    catch(Exception e)
     {
    	logger.debug("[LicenceFile] setDesString fail ,exception  is {}",e.getLocalizedMessage());
     // e.printStackTrace();
      }  
    finally
     {
      base64De = null;  
      byteMing = null;  
      byteMi = null;
      }  
    logger.debug("[LicenceFile] setDesString success,strM is {}",strM);
    return strM;
  }
  // 解密以byte[]密文输入,以byte[]明文输出    
 private byte[] getDesCode(byte[] byteD){
   Cipher cipher;  
    byte[] byteFina=null;  
    try{
     cipher = Cipher.getInstance("DES");  
      cipher.init(Cipher.DECRYPT_MODE,key);  
      byteFina = cipher.doFinal(byteD);
      }
   catch(Exception e)
     {
      e.printStackTrace();
      }
   finally
     {
      cipher=null;
      }  
    return byteFina;
  } 
  //返回加密后的密文strMi  
  public String getStrMi()
  {
   return strMi;
  }
  //返回解密后的明文
  public String getStrM()
  {
   return strM;
  }
  
  public static void main(String[] args)
  {
	  LicenceFile t=new LicenceFile();
	  t.setKey("key");
	 //String mi= t.setEncString("aaa");
	 //.out.println(mi);
	  t.setDesString("evy9xyUO/RI=");
  }
 } 
