package carpool.remoteservice;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;

public class GMDirectionService {
	private final String baseUrl = "http://maps.google.com/maps/api/directions/json?";

	private List<GeoPoint> rltPath = new ArrayList<GeoPoint>();
	private GeoPoint origin;

	public GeoPoint getOrigin() {
		return origin;
	}

	public void setOrigin(GeoPoint origin) {
		this.origin = origin;
	}

	public GeoPoint getDestination() {
		return destination;
	}

	public void setDestination(GeoPoint destination) {
		this.destination = destination;
	}

	public String getSensor() {
		return sensor;
	}

	public void setSensor(String sensor) {
		this.sensor = sensor;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	private GeoPoint destination;
	private String sensor;
	private String mode;

	public List<GeoPoint> getRltPath() {
		String requestStr = this.baseUrl + "origin=" + this.origin.getLatitudeE6()/1E6
				+ "," + this.origin.getLongitudeE6()/1E6 + "&destination="
				+ this.destination.getLatitudeE6()/1E6 + ","
				+ this.destination.getLongitudeE6()/1E6 + "&" + "sensor="
				+ this.sensor + "&" + "mode=" + this.mode;
		HttpGet get = new HttpGet(requestStr);
		try {
			HttpParams parameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(parameters, 3000);
			HttpClient httpClient = new DefaultHttpClient(parameters);
			HttpResponse response = httpClient.execute(get);
			if (response.getStatusLine().getStatusCode() == 200) {
				String strRlt = EntityUtils.toString(response.getEntity());
				JSONObject jsonObject = new JSONObject(strRlt);
				JSONArray routeArray = jsonObject.getJSONArray("routes");
				String polyline = routeArray.getJSONObject(0)
						.getJSONObject("overview_polyline").getString("points");
				if (polyline.length() > 0) {
					decodePolylines(polyline);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rltPath;
	}

	private void decodePolylines(String poly) {
		int len = poly.length();
		int index = 0;
		int lat = 0;
		int lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = poly.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = poly.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			GeoPoint p = new GeoPoint((int) (((double) lat / 1E5) * 1E6),
					(int) (((double) lng / 1E5) * 1E6));
			this.rltPath.add(p);

		}
	}

}
