package carpool.webcom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Map.Entry;

public class CPRequest {
	public static String url = "http://"+CPConstants.serverIP+":8080/CarpoolOnAndroid2/api/";

	private String requestType;

	private Map<String, String> requestArgs;

	public CPRequest(String requestType, Map<String, String> requestArgs) {
		this.setRequestType(requestType);
		this.setRequestArgs(requestArgs);
	}

	private String toCPString() {
		String request = url + requestType + "?";
		for (Entry<String, String> entry : requestArgs.entrySet()) {
			request += entry.getKey();
			request += "=";
			request += entry.getValue();
			request += "&";
		}
		return request.substring(0, request.length() - 1);
	}

	public CPResponse request() {
		String url = this.toCPString();
		Reader reader = null;
		StringBuilder buffer = null;
		try {
			URL aryURI = new URL(url);
			URLConnection conn = aryURI.openConnection();
			conn.connect();
			InputStream iStream = conn.getInputStream();
			reader = new BufferedReader(new InputStreamReader(iStream), 4000);
			buffer = new StringBuilder(iStream.toString().length());
			char[] tmp = new char[1024];
			int l;
			while ((l = reader.read(tmp)) != -1) {
				buffer.append(tmp, 0, l);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String responseString = buffer.toString();

		return new CPResponse(responseString);
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public Map<String, String> getRequestArgs() {
		return requestArgs;
	}

	public void setRequestArgs(Map<String, String> requestArgs) {
		this.requestArgs = requestArgs;
	}

}
