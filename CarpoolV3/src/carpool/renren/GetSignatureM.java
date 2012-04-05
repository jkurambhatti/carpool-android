package carpool.renren;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;

public class GetSignatureM {
	public String getSignature(List<String> paramList,String secret){

		 Collections.sort(paramList);
		 StringBuffer buffer = new StringBuffer();
		 for (String param : paramList) {
		     buffer.append(param);  //将参数键值对，以字典序升序排列后，拼接在一起
		 }
		 buffer.append(secret);  //符串末尾追加上应用的Secret Key
		 try {            //下面是将拼好的字符串转成MD5值，然后返回
		    java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
		    StringBuffer result = new StringBuffer();
		    try {
		        for (byte b : md.digest(buffer.toString().getBytes("UTF-8"))) {
		            result.append(Integer.toHexString((b & 0xf0) >>> 4));
		            result.append(Integer.toHexString(b & 0x0f));
		        }
		    } catch (UnsupportedEncodingException e) {
		        for (byte b : md.digest(buffer.toString().getBytes())) {
		            result.append(Integer.toHexString((b & 0xf0) >>> 4));
		            result.append(Integer.toHexString(b & 0x0f));
		        }
		    }
		    return result.toString();
		} catch (java.security.NoSuchAlgorithmException ex) {
		    
		}
		return null;
   	}
}
