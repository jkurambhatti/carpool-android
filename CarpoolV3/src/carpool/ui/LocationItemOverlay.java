package carpool.ui;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import carpool.v3.HomeActivity;
import carpool.v3.WaitActivity;
import carpool.v3.R;

import carpool.webcom.CPSession;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class LocationItemOverlay extends ItemizedOverlay<OverlayItem> {

	private List<OverlayItem> items = null;
	private Drawable marker = null;
	private boolean isTaxi = false;
	private Context context;

	public LocationItemOverlay(Drawable defaultMarker, List<OverlayItem> items) {
		super(defaultMarker);
		this.marker = defaultMarker;
		this.items = items;
		populate();
		// TODO Auto-generated constructor stub
	}

	public LocationItemOverlay(Drawable defaultMarker, List<OverlayItem> items,
			boolean isTaxi, Context context) {
		super(defaultMarker);
		this.marker = defaultMarker;
		this.items = items;
		this.isTaxi = isTaxi;
		this.context = context;
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return items.get(i);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return items.size();
	}

	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		boundCenterBottom(marker);
	}

	public boolean onTap(int i) {
		if (this.isTaxi) {
			LayoutInflater factory = LayoutInflater.from(this.context);
			final View pincheView = factory
					.inflate(R.layout.lio_dlg_view, null);
			Builder builder = new AlertDialog.Builder(this.context);
			builder.setIcon(R.drawable.login);
			builder.setTitle("推荐出租车信息");
			builder.setView(pincheView);
			TextView t1 = (TextView) pincheView.findViewById(R.id.lio_name);
			TextView t2 = (TextView) pincheView.findViewById(R.id.lio_carno);

			TextView t3 = (TextView) pincheView
					.findViewById(R.id.lio_carvolume);
			TextView t4 = (TextView) pincheView.findViewById(R.id.lio_phone);
			t1.setText(CPSession.taxi.getDriver());
			t2.setText(CPSession.taxi.getCarno());
			t3.setText(CPSession.taxi.getCarvolume().toString());
			t4.setText(CPSession.taxi.getPhone());

			builder.setPositiveButton(R.string.pinche,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							CPSession.waitTime = 10;
							Intent intent = new Intent();
							intent.setClass(context, WaitActivity.class);
							context.startActivity(intent);
						}
					});
			builder.setNegativeButton(R.string.dache,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							CPSession.waitTime = 0;
							// TODO Auto-generated method stub
							Intent intent = new Intent();
							intent.setClass(context, HomeActivity.class);
							context.startActivity(intent);

						}
					});
			AlertDialog dlg = builder.create();
			dlg.show();

			return true;
		} else {
			Toast.makeText(context,
					items.get(i).getTitle() + "\n" + items.get(i).getSnippet(),
					Toast.LENGTH_LONG).show();
			return true;

		}
	}

}
