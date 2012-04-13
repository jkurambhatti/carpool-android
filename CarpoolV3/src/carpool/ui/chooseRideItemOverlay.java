package carpool.ui;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

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
import carpool.prototype.Taxi;
import carpool.v3.HomeActivity;
import carpool.v3.R;
import carpool.v3.WaitActivity;
import carpool.webcom.CPRequest;
import carpool.webcom.CPResponse;
import carpool.webcom.CPSession;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class chooseRideItemOverlay extends ItemizedOverlay<OverlayItem> {

	private List<OverlayItem> items = null;
	private Drawable marker = null;
	private Context context;
	private int taxiId;
	
	public chooseRideItemOverlay(Drawable defaultMarker,
			List<OverlayItem> items, Context context) {
		super(defaultMarker);
		this.marker = defaultMarker;
		this.items = items;
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
		Taxi taxi = CPSession.candidateTaxi.get(i);
		taxiId = i;
		LayoutInflater factory = LayoutInflater.from(this.context);
		final View pincheView = factory.inflate(R.layout.lio_dlg_view, null);
		Builder builder = new AlertDialog.Builder(this.context);
		builder.setIcon(R.drawable.login);
		builder.setTitle("推荐出租车信息");
		builder.setView(pincheView);
		TextView t1 = (TextView) pincheView.findViewById(R.id.lio_name);
		TextView t2 = (TextView) pincheView.findViewById(R.id.lio_carno);

		TextView t3 = (TextView) pincheView.findViewById(R.id.lio_carvolume);
		TextView t4 = (TextView) pincheView.findViewById(R.id.lio_phone);
		t1.setText(taxi.getDriver());
		t2.setText(taxi.getCarno());
		t3.setText(taxi.getCarvolume().toString());
		t4.setText(taxi.getPhone());

		builder.setPositiveButton(R.string.pinche,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						CPSession.taxi = CPSession.candidateTaxi.get(taxiId);
						Map<String, String> args = new HashMap<String, String>();
						args.put("passenger", CPSession.user.getUsername());
						args.put("driver", CPSession.taxi.getDriver());
						args.put("amount", String.valueOf(CPSession.pinAmount));
						args.put("status", "sending");
						args.put(
								"origin",
								Math.round(CPSession.startPoint.getLatitudeE6() / 1000.00)
										/ 1000.0
										+ ","
										+ Math.round(CPSession.startPoint
												.getLongitudeE6() / 1000.00)
										/ 1000.0);
						args.put(
								"destination",
								Math.round(CPSession.endPoint.getLatitudeE6() / 1000.00)
										/ 1000.0
										+ ","
										+ Math.round(CPSession.endPoint
												.getLongitudeE6() / 1000.00)
										/ 1000.0);
						args.put("time", String.valueOf(new Date().getTime()));
						CPRequest request = new CPRequest("sendRequest", args);
						CPResponse response = request.request();
						try {
							if (response.getJson().getString("errorMsg") == "null") {
								CPSession.requestId = Integer.parseInt(response
										.getJson().getString("requestid"));
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						Intent intent = new Intent();
						intent.setClass(context, WaitActivity.class);
						context.startActivity(intent);
					}
				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				});
		AlertDialog dlg = builder.create();
		dlg.show();

		return true;
	}

}
