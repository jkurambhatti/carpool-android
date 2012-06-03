package carpool.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import carpool.remoteservice.GMGeoCodeService;
import carpool.v3.PincheResultsActivity;
import carpool.v3.R;
import carpool.webcom.CPSession;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class LongPressOverlay extends ItemizedOverlay<OverlayItem> implements
		OnDoubleTapListener, OnGestureListener {
	private static final int LAYER_FLAGS = Canvas.MATRIX_SAVE_FLAG
			| Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
			| Canvas.FULL_COLOR_LAYER_SAVE_FLAG
			| Canvas.CLIP_TO_LAYER_SAVE_FLAG;

	private MapView mapView = null;
	private View popView = null;
	private GestureDetector gestureScanner = new GestureDetector(this);
	private String tag = null;
	private GeoPoint point = null;
	private Builder builder;
	final private View dlgView;
	EditText pinAmount;

	public LongPressOverlay(Drawable defaultMarker, MapView mapView,
			View popView) {
		super(defaultMarker);
		this.mapView = mapView;
		this.popView = popView;
		dlgView = LayoutInflater.from(mapView.getContext()).inflate(
				R.layout.pinche_dlg_1, null);
		builder = new AlertDialog.Builder(mapView.getContext());
		builder.setIcon(R.drawable.login);
		builder.setTitle("选定终点");
		builder.setView(dlgView);
		builder.setPositiveButton(R.string.submit, new DlgListener());
		builder.setNegativeButton(R.string.goback,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						CPSession.endPoint = null;

						dialog.cancel();
					}
				});

	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		// TODO Auto-generated method stub
		if (!shadow) {
			if (this.point == null) {
				super.draw(canvas, mapView, shadow);
			} else {
				canvas.save(LAYER_FLAGS);
				Projection projection = mapView.getProjection();
				Point dpoint = new Point();
				Paint paint = new Paint();
				paint.setAntiAlias(true);
				projection.toPixels(this.point, dpoint);
				paint.setColor(Color.RED);
				canvas.drawCircle(dpoint.x, dpoint.y, 5, paint);
				MapView.LayoutParams geoLP = (MapView.LayoutParams) popView
						.getLayoutParams();
				geoLP.point = this.point;
				mapView.updateViewLayout(popView, geoLP);
				TextView tv = (TextView) popView.findViewById(R.id.pw_tv1);
				tv.setText(this.tag);
				popView.setVisibility(View.VISIBLE);
				popView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						TextView tv = (TextView) dlgView
								.findViewById(R.id.pcdlg1_tv1);
						tv.setText("是否将 " + tag + "设为终点？");
						AlertDialog dlg = builder.create();
						dlg.show();
					}
				});
			}

			canvas.restore();
			super.draw(canvas, mapView, shadow);

		}
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
		this.point = mapView.getProjection().fromPixels((int) arg0.getX(),
				(int) arg0.getY());
		Toast.makeText(mapView.getContext(), "正在查询，请稍等……", Toast.LENGTH_SHORT)
				.show();
		GMGeoCodeService gmservice = new GMGeoCodeService(this.point);
		tag = gmservice.getAddr();
		populate();
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

	class DlgListener implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			CPSession.endPoint = LongPressOverlay.this.point;
			CPSession.endAddr = LongPressOverlay.this.tag;
			TextView endTv = (TextView) ((Activity) (mapView.getContext()))
					.findViewById(R.id.ptv_tv2);
			endTv.setText("终点:" + tag);
			endTv.setTextColor(Color.MAGENTA);
			Button nextBtn = (Button) ((Activity) (mapView.getContext()))
					.findViewById(R.id.ppv_next);
			nextBtn.setOnClickListener(new Button.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					View dlg2View = LayoutInflater.from(mapView.getContext())
							.inflate(R.layout.pinche_dlg_2, null);
					TextView tv1 = (TextView) dlg2View
							.findViewById(R.id.pcdlg2_tv1);
					TextView tv2 = (TextView) dlg2View
							.findViewById(R.id.pcdlg2_tv2);
					TextView tv3 = (TextView) dlg2View
							.findViewById(R.id.pcdlg2_tv3);
					TextView tv4 = (TextView) dlg2View
							.findViewById(R.id.pcdlg2_tv4);
					tv1.setText("起点:");
					tv2.setText(CPSession.startAddr);
					tv3.setText("终点:");
					tv4.setText(CPSession.endAddr);
					pinAmount = (EditText) dlg2View
							.findViewById(R.id.pinche2_amount);
					Builder builder = new AlertDialog.Builder(mapView
							.getContext());
					builder.setIcon(R.drawable.login);
					builder.setTitle("提交拼车");
					builder.setView(dlg2View);
					builder.setPositiveButton(R.string.submit,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									if (pinAmount.getText() == null) {
										Toast.makeText(mapView.getContext(),
												"请输入拼车人数", Toast.LENGTH_SHORT)
												.show();
									} else {
										Integer amount = Integer
												.valueOf(pinAmount.getText()
														.toString());
										CPSession.pinAmount = amount;

										Intent intent = new Intent();
										intent.setClass(mapView.getContext(),
												PincheResultsActivity.class);
										mapView.getContext().startActivity(
												intent);
										((Activity) (mapView.getContext()))
												.finish();
									}

								}
							});
					builder.setNegativeButton(R.string.goback,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.cancel();
								}
							});
					AlertDialog dlg = builder.create();
					dlg.show();

				}
			});
			nextBtn.setEnabled(true);
			Spinner spinner2 = (Spinner) ((Activity) (mapView.getContext()))
					.findViewById(R.id.ptv_spinner2);
			CPSession.endPoint = point;
			CPSession.endAddr = tag;
			spinner2.setVisibility(View.INVISIBLE);
			popView.setVisibility(View.GONE);
			mapView.getOverlays().clear();
			dialog.dismiss();
			// TODO Auto-generated method stub
		}
	}

	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

}
