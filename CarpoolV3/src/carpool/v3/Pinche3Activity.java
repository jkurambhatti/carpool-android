package carpool.v3;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import carpool.prototype.Taxi;
import carpool.ui.LocationItemOverlay;
import carpool.webcom.CPRequest;
import carpool.webcom.CPResponse;
import carpool.webcom.CPSession;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class Pinche3Activity extends MapActivity {
	MapView mapView;
	MapController mc;

	@Override
	protected void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		super.onCreate(icicle);
		setContentView(R.layout.pinche_3);
		mapView = (MapView) findViewById(R.id.pc3_map);
		mapView.setBuiltInZoomControls(true);
		mc = mapView.getController();
		mc.setZoom(14);

		Map<String, String> args = new HashMap<String, String>();
		args.put("passenger", CPSession.user.getUsername());
		args.put(
				"origin",
				Math.round(CPSession.startPoint.getLatitudeE6() / 1000.00)
						/ 1000.0
						+ ","
						+ Math.round(CPSession.startPoint.getLongitudeE6() / 1000.00)
						/ 1000.0);
		args.put(
				"destination",
				Math.round(CPSession.endPoint.getLatitudeE6() / 1000.00)
						/ 1000.0
						+ ","
						+ Math.round(CPSession.endPoint.getLongitudeE6() / 1000.00)
						/ 1000.0);
		args.put("time", String.valueOf(new Date().getTime()));
		args.put("amount", String.valueOf(CPSession.pinAmount));
		CPRequest request = new CPRequest("pinForLau", args);
		CPResponse response = request.request();
		JSONArray results;
		JSONObject dvrstatus, dvr;
		try {
			results = response.getJson().getJSONArray("results");
			dvrstatus = results.getJSONObject(0).getJSONObject("dvrstatus");
			String taxiPosition = dvrstatus.get("position").toString();
			String path = dvrstatus.get("route").toString();
			String[] pts = path.split("@");
			for (int i = 0; i < pts.length; i++) {
				CPSession.points.add(new GeoPoint((int)(Double.valueOf(pts[i].split(",")[0]) * 1E6),(int)(Double.valueOf(pts[i].split(",")[1]) * 1E6)));
			}
			dvr = results.getJSONObject(0).getJSONObject("dvr");
			String name = dvr.get("name").toString();
			String phone = dvr.get("phone").toString();
			String carNo = dvr.get("carNo").toString();
			String carVolume = dvr.get("carVolume").toString();
			double lat = Double.valueOf(taxiPosition.split(",")[0]);
			double lng = Double.valueOf(taxiPosition.split(",")[1]);
			GeoPoint gp = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
			CPSession.taxiPoint = gp;
			CPSession.taxi = new Taxi(carNo, name, Integer.parseInt(carVolume),
					phone);
			mc = mapView.getController();
			mc.animateTo(gp);
			List<OverlayItem> overlayitems = new ArrayList<OverlayItem>();
			overlayitems.add(new OverlayItem(gp, null, null));
			Drawable marker = getResources().getDrawable(R.drawable.taxi);
			LocationItemOverlay locs = new LocationItemOverlay(marker,
					overlayitems, true, this);
			mapView.getOverlays().clear();
			mapView.getOverlays().add(locs);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(response.getJson().toString());
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
