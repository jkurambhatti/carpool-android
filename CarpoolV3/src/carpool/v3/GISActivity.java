package carpool.v3;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class GISActivity extends MapActivity {
	MapView mapView;
	MapController mc;

	@Override
	public void onNewIntent(Intent newIntent) {
		// TODO Auto-generated method stub
		super.onNewIntent(newIntent);
		String queryStr = newIntent.getStringExtra(SearchManager.QUERY);
		if (Intent.ACTION_SEARCH.equals(newIntent.getAction())) {
			Bundle bundled = newIntent.getBundleExtra(SearchManager.APP_DATA);
			if (bundled != null) {
				String ttdata = bundled.getString("data");
				Toast.makeText(GISActivity.this, ttdata, Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gis_view);
		onSearchRequested();
		mapView = (MapView) findViewById(R.id.map);
		mapView.setBuiltInZoomControls(true);
		mc = mapView.getController();
		mc.setZoom(14);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onSearchRequested() {
		// TODO Auto-generated method stub
		return super.onSearchRequested();
	}

}
