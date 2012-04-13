package carpool.v3;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import carpool.prototype.Taxi;
import carpool.ui.LocationItemOverlay;
import carpool.ui.chooseRideItemOverlay;
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
		mc.setZoom(18);

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
		CPRequest request = new CPRequest("pinTest", args);
		CPResponse response = request.request();
		JSONArray results;
		JSONObject dvrstatus, dvr;
		try {
			results = response.getJson().getJSONArray("results");
			if (results.length() == 0) {
				LayoutInflater factory = LayoutInflater
						.from(Pinche3Activity.this);
				final View getnearDlgView = factory.inflate(
						R.layout.getnear_taxi_view, null);
				Builder builder = new AlertDialog.Builder(Pinche3Activity.this);
				builder.setIcon(R.drawable.login);
				builder.setTitle("拼车测试结果");
				builder.setView(getnearDlgView);
				builder.setPositiveButton(R.string.getnear_2,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								Map<String, String> args = new HashMap<String, String>();
								CPRequest request = new CPRequest(
										"getNearTaxi", args);
								CPResponse response = request.request();
								System.out.println(response.getJson()
										.toString());
								JSONArray results;
								String driver, seats;
								try {
									if (response.getJson().get("errorMsg")
											.toString().compareTo("null") == 0) {
										results = response.getJson()
												.getJSONArray("nearTaxies");
										List<OverlayItem> overlayitems = new ArrayList<OverlayItem>();
										for (int i = 0; i < results.length(); i++) {
											driver = results.getJSONObject(i)
													.getString("id");
											seats = results.getJSONObject(i)
													.getString("seats");
											String position = results
													.getJSONObject(i)
													.getString("position");
											double lat = Double
													.valueOf(position
															.split(",")[0]);
											double lng = Double
													.valueOf(position
															.split(",")[1]);
											GeoPoint gp = new GeoPoint(
													(int) (lat * 1E6),
													(int) (lng * 1E6));
											overlayitems.add(new OverlayItem(
													gp, null, null));
											CPSession.candidateTaxi.add(new Taxi(
													null, driver, Integer
															.parseInt(seats),
													null));
										}
										Drawable marker = getResources()
												.getDrawable(R.drawable.taxi);
										chooseRideItemOverlay taxis = new chooseRideItemOverlay(
												marker, overlayitems,
												Pinche3Activity.this);
										mapView.getOverlays().clear();
										mapView.getOverlays().add(taxis);

									} else {
										Toast.makeText(Pinche3Activity.this,
												"出错了，骚年", Toast.LENGTH_SHORT)
												.show();

									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
				builder.setNegativeButton(R.string.getnear_3,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								Intent intent = new Intent();
								intent.setClass(Pinche3Activity.this,
										HomeActivity.class);
								startActivity(intent);

							}
						});
				AlertDialog dlg = builder.create();
				dlg.show();

			} else {
				dvrstatus = results.getJSONObject(0).getJSONObject("dvrstatus");
				String taxiPosition = dvrstatus.get("position").toString();
				String path = dvrstatus.get("route").toString();
				String[] pts = path.split("@");
				for (int i = 0; i < pts.length; i++) {
					CPSession.points
							.add(new GeoPoint((int) (Double.valueOf(pts[i]
									.split(",")[0]) * 1E6), (int) (Double
									.valueOf(pts[i].split(",")[1]) * 1E6)));
				}
				dvr = results.getJSONObject(0).getJSONObject("dvr");
				String name = dvr.get("id").toString();
				String phone = dvr.get("phone").toString();
				String carNo = dvr.get("carNo").toString();
				String carVolume = dvr.get("carVolume").toString();
				double lat = Double.valueOf(taxiPosition.split(",")[0]);
				double lng = Double.valueOf(taxiPosition.split(",")[1]);
				GeoPoint gp = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
				CPSession.taxiPoint = gp;
				CPSession.taxi = new Taxi(carNo, name,
						Integer.parseInt(carVolume), phone);
				mc = mapView.getController();
				mc.animateTo(gp);
				List<OverlayItem> overlayitems = new ArrayList<OverlayItem>();
				overlayitems.add(new OverlayItem(gp, null, null));
				Drawable marker = getResources().getDrawable(R.drawable.taxi);
				LocationItemOverlay locs = new LocationItemOverlay(marker,
						overlayitems, true, this);
				mapView.getOverlays().clear();
				mapView.getOverlays().add(locs);
			}
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
