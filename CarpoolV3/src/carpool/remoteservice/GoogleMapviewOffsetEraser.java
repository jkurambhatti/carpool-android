package carpool.remoteservice;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import carpool.webcom.CPRequest;
import carpool.webcom.CPResponse;

import com.google.android.maps.GeoPoint;

public class GoogleMapviewOffsetEraser {

	private double lngToPixel(double lng, int zoom) {
		return (lng + 180) * (256L << zoom) / 360;
	}

	private double pixelToLng(double pixelX, int zoom) {
		return pixelX * 360 / (256L << zoom) - 180;
	}

	private double latToPixel(double lat, int zoom) {
		double siny = Math.sin(lat * Math.PI / 180);
		double y = Math.log((1 + siny) / (1 - siny));
		return (128 << zoom) * (1 - y / (2 * Math.PI));
	}

	private double pixelToLat(double pixelY, int zoom) {
		double y = 2 * Math.PI * (1 - pixelY / (128 << zoom));
		double z = Math.pow(Math.E, y);
		double siny = (z - 1) / (z + 1);
		return Math.asin(siny) * 180 / Math.PI;
	}

	public GeoPoint erase(GeoPoint point) {
		int xlat = (int) (point.getLatitudeE6() / 1E4 + 0.5);
		int xlng = (int) (point.getLongitudeE6() / 1E4 + 0.5);
		Map<String, String> args = new HashMap<String, String>();
		args.put("lgn", String.valueOf(xlng));
		args.put("lat", String.valueOf(xlat));
		CPRequest request = new CPRequest("getOffset", args);
		CPResponse response = request.request();
		int latoffset, lngoffset;
		try {
			latoffset = Integer.parseInt(response.getJson().getString(
					"lat_offset"));
			lngoffset = Integer.parseInt(response.getJson().getString(
					"lgn_offset"));
			double lat = point.getLatitudeE6() / 1E6;
			double lng = point.getLongitudeE6() / 1E6;
			double lat_zoom18 = latToPixel(lat, 18);
			double lng_zoom18 = lngToPixel(lng, 18);
			double newlat = pixelToLat(lat_zoom18 + latoffset, 18);
			double newlng = pixelToLng(lng_zoom18 + lngoffset, 18);
			return new GeoPoint((int) (newlat * 1E6), (int) (newlng * 1E6));

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
}
