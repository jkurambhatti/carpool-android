package carpool.renren;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;




public class Users {
	String v1="1.0";
	String api_key1="0049faf6b6d045c18dbed333a0f38e3c";
	String format1="JSON";
	String session_key1=MyApplication.getInstance().session_key;
	public String getLoggedInUser(){
		GetSignatureM mGetSignatureM=new GetSignatureM();
		List<String> paramList=new ArrayList<String>();
		paramList.add("v="+v1);
		paramList.add("api_key="+api_key1);
		paramList.add("format="+format1);
		paramList.add("session_key="+session_key1);
		paramList.add("method=users.getLoggedInUser");
		String sig1=mGetSignatureM.getSignature(paramList, "b294491f90074acb84c608363ce22c36");
		try {
			String method=URLEncoder.encode("users.getLoggedInUser","UTF-8");
			String session_key=URLEncoder.encode(session_key1,"UTF-8");
			String api_key=URLEncoder.encode(api_key1,"UTF-8");
			String format=URLEncoder.encode(format1,"UTF-8");
			String v=URLEncoder.encode(v1,"UTF-8");
			String sig=URLEncoder.encode(sig1,"UTF-8");
			HttpPost mHttpPost=new HttpPost();
			String url="http://api.renren.com/restserver.do";
			String parmar="method="+method+"&session_key="+session_key+"&api_key="+api_key+"&format="+format+"&v="+v+"&sig="+sig;
			InputStream in=mHttpPost.doPost(url, parmar);
			byte[] b=new byte[1024];
			int a=in.read(b);
			String line="["+new String(b, 0, a,"UTF-8")+"]";
			return line;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
