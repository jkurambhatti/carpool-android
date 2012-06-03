package carpool.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import carpool.v3.R;
import carpool.webcom.CPSession;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class WaitOverlay extends Overlay {
	private Context context;
	private Bitmap pMarker, tMarker;
	private MapView mapView;
	private Paint paint;

	public WaitOverlay(Context context, MapView mapView) {
		this.context = context;
		this.mapView = mapView;
		Resources r = context.getResources();
		this.pMarker = BitmapFactory.decodeResource(r, R.drawable.mylocation);
		this.tMarker = BitmapFactory.decodeResource(r, R.drawable.taxi);
		paint = new Paint();
		paint.setColor(Color.BLUE);
		paint.setAlpha(150);
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setStrokeWidth(4);
	}

	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if (!shadow) {
			Projection projection = mapView.getProjection();
			Point paintPoint = new Point();
			projection.toPixels(
					CPSession.mylocs.get(CPSession.mylocs.size() - 1),
					paintPoint);
			canvas.drawBitmap(this.pMarker, paintPoint.x - 16,
					paintPoint.y - 32, paint);
			projection.toPixels(
					CPSession.taxilocs.get(CPSession.taxilocs.size() - 1),
					paintPoint);
			canvas.drawBitmap(this.tMarker, paintPoint.x - 16,
					paintPoint.y - 32, paint);
		}
	}

}
