package carpool.v3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.widget.Toast;
import carpool.remoteservice.GoogleMapviewOffsetEraser;
import carpool.ui.LocationItemOverlay;
import carpool.ui.WaitOverlay;
import carpool.webcom.CPActivity;
import carpool.webcom.CPRequest;
import carpool.webcom.CPResponse;
import carpool.webcom.CPSession;
import carpool.webcom.MessageHandler;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class WaitActivity extends CPActivity {
	MapView mapView;
	MapController mapCtrl;
	LocationManager locationManager;
	String bestProvider;
	public MessageHandler handler;
	public boolean isRun = false;
	Location currentLocation;

	@Override
	protected void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		super.onCreate(icicle);
		setContentView(R.layout.wait_view);
		mapView = (MapView) findViewById(R.id.wait_map);
		mapCtrl = mapView.getController();
		CPSession.socket.activity = this;

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		bestProvider = locationManager.getBestProvider(criteria, false);
		locationManager.requestLocationUpdates(bestProvider, 0, 0,
				mLocationListener);

		Looper looper = Looper.myLooper();
		handler = new MessageHandler(looper, this);
		waitingResponseTask wrt = new waitingResponseTask(this);
		wrt.execute();

	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	private LocationListener mLocationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location arg0) {
			// TODO Auto-generated method stub
			double lat = arg0.getLatitude();
			double lng = arg0.getLongitude();
			GeoPoint gp = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
			mapCtrl.animateTo(gp);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

	};

	@Override
	public void append(String str) {
		// TODO Auto-generated method stub
		if (str.equals("[0]MSGRESPONSE")) {
			CPSession.flag = "ASSURE";
			Map<String, String> args = new HashMap<String, String>();
			CPRequest request = new CPRequest("getResponse", args);
			if (CPSession.requestId != 0) {
				args.put("id", String.valueOf(CPSession.requestId));
			}
			CPResponse response = request.request();
			try {
				JSONObject rlt = response.getJson();

				JSONObject jresponse = rlt.getJSONObject("response");
				if (jresponse.getString("backup").equals("agree")) {
					Message msg = Message.obtain();
					msg.obj = "[0]MSGRESPONSE";
					handler.sendMessage(msg);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			Message msg = Message.obtain();
			if (str.equals("[0]GETON")) {
				msg.obj = "[0]GETON";

			} else if (str.equals("[0]GETOFF")) {
				msg.obj = "[0]GETOFF";
				isRun = false;
			}
			handler.sendMessage(msg);
		}

	}

	class waitingResponseTask extends AsyncTask {
		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			isRun = true;
			statusThread.start();
			myDialog.dismiss();
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			myDialog = new ProgressDialog(activity);
			myDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			myDialog.setTitle("提示");
			myDialog.setMessage(getResources().getString(R.string.wait_1));
			myDialog.setIcon(R.drawable.login);
			myDialog.setIndeterminate(false);
			myDialog.setCancelable(false);
			myDialog.show();

		}

		@Override
		protected void onProgressUpdate(Object... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		ProgressDialog myDialog;
		CPActivity activity;

		public waitingResponseTask(CPActivity activity) {
			this.activity = activity;
		}

		@Override
		protected Object doInBackground(Object... arg0) {
			// TODO Auto-generated method stub
			boolean flag = true;
			while (flag) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (CPSession.flag.equals("ASSURE")) {
					flag = false;
				}
			}
			return null;
		}
	}

	Thread statusThread = new Thread() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (isRun) {
				try {
					SharedPreferences useGPS = getSharedPreferences("use_GPS",
							0);
					Boolean ifUseGPS = useGPS.getBoolean("useGPS", false);
					Location location;
					if (ifUseGPS) {
						LocationManager mlocationManager = (LocationManager) WaitActivity.this
								.getSystemService(Context.LOCATION_SERVICE);
						mlocationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER, 1 * 1000, 1,
								mLocationListener);
						location = mlocationManager
								.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						if (location == null) {
							location = locationManager
									.getLastKnownLocation(bestProvider);
						}
					} else {
						location = locationManager
								.getLastKnownLocation(bestProvider);
					}
					double lat = location.getLatitude();
					double lng = location.getLongitude();
					GeoPoint gp = new GeoPoint((int) (lat * 1E6),
							(int) (lng * 1E6));
					if (ifUseGPS) {
						GoogleMapviewOffsetEraser eraser = new GoogleMapviewOffsetEraser();
						gp = eraser.erase(gp);
					}

					lat = gp.getLatitudeE6() / 1E6;
					lng = gp.getLatitudeE6() / 1E6;
					CPSession.mylocs.add(gp);
					Map<String, String> args = new HashMap<String, String>();
					args.put("nickname", CPSession.user.getUsername());
					args.put("position",
							String.valueOf(lat) + "," + String.valueOf(lng));
					CPRequest request = new CPRequest("changeStatus", args);
					request.request();
					sleep(5000);
					args.clear();
					args.put("nickname", CPSession.taxi.getDriver());
					request = new CPRequest("getStatus", args);
					CPResponse response = request.request();
					if (!response.getJson().getString("errorMsg")
							.equals("null")) {
						System.out.println("出错了骚年");
					} else {
						JSONObject userstatus = response.getJson()
								.getJSONObject("userstatus");
						String position = userstatus.getString("position");
						double tLat = Double.valueOf(position.split(",")[0]);
						double tLng = Double.valueOf(position.split(",")[1]);
						GeoPoint tGp = new GeoPoint((int) (tLat * 1E6),
								(int) (tLng * 1E6));
						CPSession.taxilocs.add(tGp);
						mapView.getOverlays().clear();
						mapView.getOverlays().add(
								new WaitOverlay(WaitActivity.this, mapView));
					}
					sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	};

}
