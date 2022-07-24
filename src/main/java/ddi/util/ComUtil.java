package ddi.util;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.Cipher;
import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.codec.binary.Base64;

public class ComUtil {

	/**
	 * request 파라미터 추출(순서없음)
	 * @param request
	 * @return
	 */
	public static Map getParameterMap(HttpServletRequest request) {
		// 저장할 맵
		Map paramMap = new HashMap();
	
		// 파라미터 이름
		Enumeration paramNames = request.getParameterNames();
		
		// 맵에 저장
		while(paramNames.hasMoreElements()) {
			String name	 = paramNames.nextElement().toString();
			String value = request.getParameter(name);
	
			paramMap.put(name, value);
		}
		
		// 결과반환
		return paramMap;
	}
	
	/**
	 * 클라이언트 IP 리턴
	 * @param request
	 * @return
	 */
	public static String getAddress(HttpServletRequest request) {
	
		String ip = request.getHeader("X-FORWARDED-FOR") == null ? request.getRemoteAddr() : request.getHeader("X-FORWARDED-FOR");
		
		return ip;
	}
	
	/**
	 * 난수발생
	 * @return
	 */
	public static String getRandomKey() {
		return getRandomKey(20);
	}
	public static String getRandomKey(int len) {
		
		StringBuffer temp = new StringBuffer();
		Random rnd = new Random();
		
		for (int i = 0; i < len; i++) {
		    int rIndex = rnd.nextInt(3);
		    switch (rIndex) {
		    case 0:
		        // a-z
		        temp.append((char) ((int) (rnd.nextInt(26)) + 97));
		        break;
		    case 1:
		        // A-Z
		        temp.append((char) ((int) (rnd.nextInt(26)) + 65));
		        break;
		    case 2:
		        // 0-9
		        temp.append((rnd.nextInt(10)));
		        break;
		    }
		}

		return temp.toString();
	}
	
	/**
	 * RSA 키생성
	 */
	public static HttpServletRequest getKeyPair(HttpServletRequest request) throws NoSuchAlgorithmException{
		
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(1024, new SecureRandom());
		
		KeyPair keyPair = keyPairGenerator.genKeyPair();
		
		Key publicKey = keyPair.getPublic();
		Key privateKey = keyPair.getPrivate();
		
		request.getSession().setAttribute("publicKey", java.util.Base64.getEncoder().encodeToString(publicKey.getEncoded()));
		request.getSession().setAttribute("privateKey", java.util.Base64.getEncoder().encodeToString(privateKey.getEncoded()));
		
			
		return request;
	}
	
	/**
	 * RSA 복호화
	 */
	public static String decrypt(String p_privateKey, String val) throws Exception {
	   	 
 		byte[] bPrivateKey = Base64.decodeBase64(p_privateKey.getBytes());
 
 		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
 
 		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(bPrivateKey);
 		PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
 
 		
 		byte[] blogin_pw = Base64.decodeBase64(val.getBytes());
 		Cipher cipher = Cipher.getInstance("RSA");
 		cipher.init(Cipher.DECRYPT_MODE, privateKey);
 		byte[] blogin_pw2 = cipher.doFinal(blogin_pw);
 		val = new String(blogin_pw2);
 
 		return val;
 
 	}
	/**
	 * 암호화키(salt) 생성 
	 */
	public static String getSalt(){
		
		SecureRandom random;
		String salt = null;
		
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
		
			byte[] bytes = new byte[16];
			random.nextBytes(bytes);
			salt = new String(java.util.Base64.getEncoder().encode(bytes));
		
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return salt;
	}
	/**
	 * SHA-512 암호화
	 */
	public static String getSHA512(String param, String salt){
		
		MessageDigest md;
		String hex= null;
		
		try {
			md = MessageDigest.getInstance("SHA-512");
			md.reset();
			md.update(salt.getBytes("UTF-8"));
			md.update(param.getBytes("UTF-8"));
			hex = String.format("%0128x", new BigInteger(1, md.digest()));
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hex;
	}
}
