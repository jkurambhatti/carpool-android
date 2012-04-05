package carpool.v3;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import carpool.ui.LocationItemOverlay;
import carpool.ui.LongPressOverlay;
import carpool.webcom.CPSession;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class PincheActivity extends MapActivity {
	public MapView mapView;
	LocationManager locationManager;
	String bestProvider;
	MapController mc;
	Button myLoc, chooseLoc, goBack, goNext;

	@Override
	protected void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		super.onCreate(icicle);
		setContentView(R.layout.pinche_1);

		mapView = (MapView) findViewById(R.id.pc1_map);
		mapView.setBuiltInZoomControls(true);
		mc = mapView.getController();
		mc.setZoom(14);

		myLoc = (Button) findViewById(R.id.pc1_myloc);
		chooseLoc = (Button) findViewById(R.id.pc1_chooseloc);
		goBack = (Button) findViewById(R.id.pc1_goback);
		goNext = (Button) findViewById(R.id.pc1_next);
		goNext.setEnabled(false);
		goNext.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(PincheActivity.this, Pinche2Activity.class);
				startActivity(intent);
			}
		});

		myLoc.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CPSession.startPoint = getMyLocation();
				goNext.setEnabled(true);
			}
		});

		chooseLoc.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LongPressOverlay lgOverlay = new LongPressOverlay(mapView);
				mapView.getOverlays().clear();
				mapView.getOverlays().add(lgOverlay);
			}

		});

		goBack.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PincheActivity.this.finish();
			}
		});

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		bestProvider = locationManager.getBestProvider(criteria, false);
		locationManager.requestLocationUpdates(bestProvider, 0, 0,
				mLocationListener);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(bestProvider, 1000, 0,
				mLocationListener);
	}

	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(mLocationListener);
	}

	private GeoPoint getMyLocation() {
		Location location = locationManager.getLastKnownLocation(bestProvider);

		double lat = location.getLatitude();
		double lng = location.getLongitude();
		GeoPoint gp = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
		mc = mapView.getController();
		mc.animateTo(gp);
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
			mc = mapView.getController();
			mc.animateTo(gp);
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

}
