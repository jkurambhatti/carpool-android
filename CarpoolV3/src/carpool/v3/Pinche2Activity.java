package carpool.v3;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import carpool.ui.LongPressOverlay;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class Pinche2Activity extends MapActivity {
	public MapView mapView;
	MapController mc;

	@Override
	protected void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		super.onCreate(icicle);
		setContentView(R.layout.pinche_2);
		mapView = (MapView) findViewById(R.id.pc2_map);
		mapView.setBuiltInZoomControls(true);
		mc = mapView.getController();
		mc.setZoom(18);
		LongPressOverlay lpOverlay = new LongPressOverlay(mapView, false);
		mapView.getOverlays().clear();
		mapView.getOverlays().add(lpOverlay);

		Button goBack = (Button) findViewById(R.id.pc2_goback);
		goBack.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Pinche2Activity.this.finish();
			}

		});
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
