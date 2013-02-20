

package yeb;

import java.io.FileInputStream;
import java.security.MessageDigest;

public class Digester 
{

    public static String calc(String algorithm, byte[] message)
		throws Exception
    {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        md.update(message);
		
        byte[] bytes = md.digest();
 
        StringBuilder hex = new StringBuilder();
    	for (int i=0;i<bytes.length;i++) {
    	  hex.append(Integer.toHexString(0xFF & bytes[i]));
    	}
 		return hex.toString();
    }
}

