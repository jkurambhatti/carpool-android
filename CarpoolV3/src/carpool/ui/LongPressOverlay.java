package carpool.ui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import carpool.v3.Pinche2Activity;
import carpool.v3.Pinche3Activity;
import carpool.v3.R;
import carpool.webcom.CPSession;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class LongPressOverlay extends Overlay implements OnDoubleTapListener,
		OnGestureListener {
	private MapView mapView = null;
	private GestureDetector gestureScanner = new GestureDetector(this);
	private boolean isStart = true;

	public LongPressOverlay(MapView mapView) {
		this.mapView = mapView;
	}

	public LongPressOverlay(MapView mapView, boolean isStart) {
		this.mapView = mapView;
		this.isStart = isStart;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mv) {
		return gestureScanner.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		GeoPoint gp = mapView.getProjection().fromPixels((int) arg0.getX(),
				(int) arg0.getY());
		String latStr = Math.round(gp.getLatitudeE6() / 1000.00) / 1000.0 + "";
		String longStr = Math.round(gp.getLongitudeE6() / 1000.00) / 1000.0
				+ "";
		Toast.makeText(mapView.getContext(), latStr + ", " + longStr,
				Toast.LENGTH_SHORT).show();

		if (isStart) {
			CPSession.startPoint = gp;
			LayoutInflater factory = LayoutInflater.from(mapView.getContext());
			final View chooseSrcView = factory.inflate(R.layout.pinche_dlg_1,
					null);
			Builder builder = new AlertDialog.Builder(mapView.getContext());
			builder.setIcon(R.drawable.login);
			builder.setTitle("提交出发点");
			builder.setView(chooseSrcView);
			builder.setPositiveButton(R.string.submit,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Intent intent = new Intent();
							intent.setClass(mapView.getContext(),
									Pinche2Activity.class);
							mapView.getContext().startActivity(intent);
						}
					});
			builder.setNegativeButton(R.string.goback,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							CPSession.startPoint = null;

							dialog.cancel();
						}
					});
			AlertDialog dlg = builder.create();
			dlg.show();

		} else {
			CPSession.endPoint = gp;
			LayoutInflater factory = LayoutInflater.from(mapView.getContext());
			final View chooseDestView = factory.inflate(R.layout.pinche_dlg_2,
					null);
			Builder builder = new AlertDialog.Builder(mapView.getContext());
			builder.setIcon(R.drawable.login);
			builder.setTitle("提交目的地");
			builder.setView(chooseDestView);
			builder.setPositiveButton(R.string.submit,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							EditText pinAmount = (EditText) chooseDestView
									.findViewById(R.id.pinche2_amount);
							Integer amount = Integer.valueOf(pinAmount
									.getText().toString());
							CPSession.pinAmount = amount;

							Intent intent = new Intent();
							intent.setClass(mapView.getContext(),
									Pinche3Activity.class);
							mapView.getContext().startActivity(intent);
						}
					});
			builder.setNegativeButton(R.string.goback,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							CPSession.endPoint = null;

							dialog.cancel();
						}
					});
			AlertDialog dlg = builder.create();
			dlg.show();

		}

	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		// TODO Auto-generated method stub

		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

}
