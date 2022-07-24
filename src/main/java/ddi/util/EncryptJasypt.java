package ddi.util;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

/**
 * 
 * @author Administrator
 * jasypt 암호화 클래스
 * 프로퍼티에 암호화된 값을 넣을때 사용.(실제프로젝트에서는 사용안함)
 */
public class EncryptJasypt {

	public static void main(String[] args) {
		
		StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
		standardPBEStringEncryptor.setAlgorithm("PBEWithMD5AndDES");
		standardPBEStringEncryptor.setPassword("ddiPassword");
		String encoded = standardPBEStringEncryptor.encrypt("QnZYToEm2Z");
//		String decoded = standardPBEStringEncryptor.decrypt("0NsZRJG5NFRwGLh1kn2bww==");
		
		System.out.println("Encrypted : " + encoded);
//		System.out.println("Decrypted : " + decoded);
	}
}
