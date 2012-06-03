package carpool.remoteservice;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;

public class GMGeoCodeService {
	private final String baseUrl = "http://maps.googleapis.com/maps/api/geocode/json?";

	private String jsonStr = null;

	private GeoPoint point = null;

	public GMGeoCodeService(GeoPoint point) {
		this.point = point;
	}

	private String getFullUrl() {
		String url = baseUrl;
		url += "latlng=" + (point.getLatitudeE6() / 1E6) + ","
				+ (point.getLongitudeE6() / 1E6);
		url += "&sensor=true";
		url += "&language=zh";
		return url;
	}

	private void getResult() {
		String url = getFullUrl();
		HttpGet get = new HttpGet(url);
		try {
			HttpParams parameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(parameters, 3000);
			HttpClient httpClient = new DefaultHttpClient(parameters);
			HttpResponse response = httpClient.execute(get);
			if (response.getStatusLine().getStatusCode() == 200) {
				String strRlt = EntityUtils.toString(response.getEntity());
				this.jsonStr = strRlt;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getAddr() {
		this.getResult();
		try {
			JSONObject rlt = new JSONObject(this.jsonStr);
			String status = rlt.getString("status");
			if (status.compareTo("OK") == 0) {
				JSONArray addrs = rlt.getJSONArray("results");
				String longname = addrs.getJSONObject(0).getString(
						"formatted_address");
				if (longname.indexOf("邮政编码") > 0) {
					longname = longname.substring(0,
							longname.indexOf("邮政编码") - 1);

				}
				return longname;
			} else if (status.compareTo("ZERO_RESULTS") == 0) {
				return null;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
