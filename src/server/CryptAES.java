package server;

import java.nio.charset.Charset;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;


public class CryptAES {

	private String key;
	
	public CryptAES(String key){
		this.key = CryptAES.formatKey(key);
	}
	
	 public String encrypt(String s)
			 throws GeneralSecurityException{
		 return DatatypeConverter.printBase64Binary(CryptAES.encrypt(this.key, s));
		 
	 }
	 
	 
	 public String decrypt(String encrypted)
			 throws GeneralSecurityException{
	 return CryptAES.decrypt(this.key, DatatypeConverter.parseBase64Binary(encrypted));
	 }
	 
	 
	 //===============STATIC FUNCTIONS================================

	  public static byte[] encrypt(String key, String value)
	      throws GeneralSecurityException {

	    byte[] raw = key.getBytes(Charset.forName("UTF8"));
	    if (raw.length != 16) {
	      throw new IllegalArgumentException("Invalid key size.");
	    }

	    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    cipher.init(Cipher.ENCRYPT_MODE, skeySpec,
	        new IvParameterSpec(new byte[16]));
	    return cipher.doFinal(value.getBytes(Charset.forName("UTF8")));
	  }

	  public static String decrypt(String key, byte[] encrypted)
	      throws GeneralSecurityException {

	    byte[] raw = key.getBytes(Charset.forName("UTF8"));
	    if (raw.length != 16) {
	      throw new IllegalArgumentException("Invalid key size.");
	    }
	    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");

	    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    cipher.init(Cipher.DECRYPT_MODE, skeySpec,
	        new IvParameterSpec(new byte[16]));
	    byte[] original = cipher.doFinal(encrypted);

	    return new String(original, Charset.forName("UTF8"));
	  }
	  
	  public static String formatKey(String s){
		  String formatted;
		  int size = 16;
		  
		  if (s == null)
			  s = "";
		  if (s.length() < size){
			  formatted = s;
			  for (int i = s.length(); i < size; i++){
				  formatted += '0';
			  }
			  return formatted;
		  }
		  else{
			  formatted = s.substring(0, size);
			  return formatted;
		  }
		  
	  }
	  
	}
