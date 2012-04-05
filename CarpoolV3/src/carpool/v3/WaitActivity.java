package carpool.v3;

import android.os.Bundle;
import carpool.ui.PathOverlay;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class WaitActivity extends MapActivity {
	MapView mapView;
	MapController mc;

	@Override
	protected void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		super.onCreate(icicle);
		setContentView(R.layout.wait_view);
		mapView = (MapView) findViewById(R.id.wait_map);
		mapView.setBuiltInZoomControls(true);
		mc = mapView.getController();
		mc.setZoom(14);
		PathOverlay pOverlay = new PathOverlay(this, mapView);
		mapView.getOverlays().clear();
		mapView.getOverlays().add(pOverlay);

	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
