package application.DTO;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class AuthHandler {
	private AuthHandler() {}
	
	public static String getPasswordHash(String password) {
		try {
			byte[] bytesOfMessage = password.getBytes("UTF-8");
			
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] theMD5digest = md.digest(bytesOfMessage);
			
			StringBuilder sb = new StringBuilder();
	        for (byte b : theMD5digest) {
	            sb.append(String.format("%02x", b));
	        }
	        
	        return sb.toString();			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} 
		
		return null;
	}
}
