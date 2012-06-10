package carpool.v3;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import carpool.remoteservice.GMGeoCodeService;
import carpool.remoteservice.GoogleMapviewOffsetEraser;
import carpool.ui.LocationItemOverlay;
import carpool.ui.LongPressOverlay;
import carpool.webcom.CPActivity;
import carpool.webcom.CPSession;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class PincheProcessActivity extends CPActivity {
	MapView mapView;
	View popView;
	MapController mapCtrl;
	LocationManager locationManager;
	String bestProvider;
	Button nextBtn;
	TextView startTv, endTv;
	Spinner spinner, spinner2;
	String[] startStr = { "选择起点", "我的当前位置" };
	String[] endStr = { "选择终点", "在地图上选择", "查找目的地" };
	ArrayAdapter<String> adapter, adapter2;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		CPSession.clear();
		setContentView(R.layout.pinche_process_view);

		mapView = (MapView) findViewById(R.id.pc_map);
		mapView.setBuiltInZoomControls(true);
		mapCtrl = mapView.getController();
		mapCtrl.setZoom(18);

		popView = View.inflate(this, R.layout.poiwindow_view, null);
		mapView.addView(popView, new MapView.LayoutParams(
				MapView.LayoutParams.WRAP_CONTENT,
				MapView.LayoutParams.WRAP_CONTENT, null,
				MapView.LayoutParams.BOTTOM_CENTER));
		popView.setVisibility(View.GONE);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		bestProvider = locationManager.getBestProvider(criteria, false);
		locationManager.requestLocationUpdates(bestProvider, 0, 0,
				mLocationListener);

		nextBtn = (Button) findViewById(R.id.ppv_next);
		startTv = (TextView) findViewById(R.id.ptv_tv1);
		endTv = (TextView) findViewById(R.id.ptv_tv2);
		spinner = (Spinner) findViewById(R.id.ptv_spinner);
		spinner2 = (Spinner) findViewById(R.id.ptv_spinner2);

		nextBtn.setText("提交拼车");
		startTv.setText("起点未选定");
		endTv.setText("终点未选定");

		nextBtn.setEnabled(false);

		spinner.setPrompt("选择起点");
		adapter = new ArrayAdapter<String>(PincheProcessActivity.this,
				android.R.layout.simple_spinner_item, startStr);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setSelection(0, true);
		spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				switch (arg2) {
				case 0:
					break;
				case 1:
					CPSession.startPoint = getMyLocation();
					GMGeoCodeService gmservice = new GMGeoCodeService(
							CPSession.startPoint);
					String loc = gmservice.getAddr();
					if (loc == null) {
						startTv.setText("起点:"
								+ (CPSession.startPoint.getLatitudeE6() / 1E6)
								+ ","
								+ (CPSession.startPoint.getLongitudeE6() / 1E6));
					} else {
						startTv.setText("起点:" + loc);
						CPSession.startAddr = loc;
					}
					startTv.setTextColor(Color.MAGENTA);
					spinner.setVisibility(View.INVISIBLE);
					spinner2.setVisibility(View.VISIBLE);
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub.

			}
		});

		spinner2.setPrompt("选择终点");
		adapter2 = new ArrayAdapter<String>(PincheProcessActivity.this,
				android.R.layout.simple_spinner_item, endStr);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(adapter2);
		spinner2.setSelection(0, true);
		spinner2.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				switch (arg2) {
				case 0:
					break;
				case 1:
					Toast.makeText(PincheProcessActivity.this, "在地图上长按选择目的地",
							Toast.LENGTH_LONG).show();
					Drawable marker = getResources().getDrawable(
							R.drawable.taxi);

					LongPressOverlay lpOverlay = new LongPressOverlay(marker,
							mapView, popView);

					mapView.getOverlays().add(lpOverlay);
					break;
				case 2:
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});
		spinner2.setVisibility(View.INVISIBLE);
	}

	@Override
	public void append(String str) {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	private GeoPoint getMyLocation() {
		SharedPreferences useGPS = getSharedPreferences("use_GPS", 0);
		Boolean ifUseGPS = useGPS.getBoolean("useGPS", false);
		Location location;
		if (ifUseGPS) {
			openGPSSettings();
			LocationManager mlocationManager = (LocationManager) this
					.getSystemService(Context.LOCATION_SERVICE);
			mlocationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 1 * 1000, 1,
					mLocationListener);
			location = mlocationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location == null) {
				Toast.makeText(PincheProcessActivity.this,
						"GPS信号无法获得，将采用其他定位方式", Toast.LENGTH_LONG).show();
				location = locationManager.getLastKnownLocation(bestProvider);
			}
		} else {
			location = locationManager.getLastKnownLocation(bestProvider);
		}
		double lat = location.getLatitude();
		double lng = location.getLongitude();
		GeoPoint gp = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
		Toast.makeText(PincheProcessActivity.this, gp.toString(),
				Toast.LENGTH_LONG).show();
		if (ifUseGPS) {
			GoogleMapviewOffsetEraser eraser = new GoogleMapviewOffsetEraser();
			gp = eraser.erase(gp);
			Toast.makeText(PincheProcessActivity.this, gp.toString(),
					Toast.LENGTH_LONG).show();
		}
		mapCtrl.animateTo(gp);
		List<OverlayItem> overlayitems = new ArrayList<OverlayItem>();
		overlayitems.add(new OverlayItem(gp, null, null));
		Drawable marker = getResources().getDrawable(R.drawable.mylocation);
		LocationItemOverlay locs = new LocationItemOverlay(marker, overlayitems);
		mapView.getOverlays().clear();
		mapView.getOverlays().add(locs);
		return gp;
	}

	private LocationListener mLocationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location arg0) {
			// TODO Auto-generated method stub
			double lat = arg0.getLatitude();
			double lng = arg0.getLongitude();
			GeoPoint gp = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
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

	private void openGPSSettings() {
		if (locationManager
				.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			Toast.makeText(this, "GPS模块正常", Toast.LENGTH_SHORT).show();
			return;
		}

		Toast.makeText(this, "请开启GPS！", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
		startActivityForResult(intent, 0);

	}

}
