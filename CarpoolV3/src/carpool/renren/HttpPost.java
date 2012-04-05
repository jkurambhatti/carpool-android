package carpool.renren;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpPost {
	HttpURLConnection mHttpURLConnection;
	InputStream in=null;
	public InputStream doPost(String url,String parmar){
		try {
			URL url1=new URL(url);
			mHttpURLConnection=(HttpURLConnection)url1.openConnection();
			mHttpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			mHttpURLConnection.setDoInput(true);
			mHttpURLConnection.setDoOutput(true);
			OutputStream out=mHttpURLConnection.getOutputStream();
			out.write(parmar.getBytes());
			out.flush();
			in=mHttpURLConnection.getInputStream();
			return in;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return in;
	}
}
