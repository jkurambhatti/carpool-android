package carpool.ui;

import java.util.ArrayList;
import java.util.List;

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

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class PathOverlay extends Overlay {
	private List<GeoPoint> points = new ArrayList<GeoPoint>();
	private MapView mapView;
	private Paint paint;
	private Context context;
	private Bitmap marker;

	public PathOverlay(Context context, MapView mapView) {
		this.context = context;
		this.mapView = mapView;
		this.points = CPSession.points;
		Resources r = context.getResources();
		this.marker = BitmapFactory.decodeResource(r, R.drawable.mylocation);
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
			if (points != null && points.size() >= 2) {
				Point start = new Point();
				projection.toPixels(points.get(0), start);
				for (int i = 1; i < points.size(); i++) {
					Point end = new Point();
					projection.toPixels(points.get(i), end);
					if (i == 1 || i == points.size() - 1) {
						canvas.drawBitmap(this.marker, start.x - 16,
								start.y - 32, paint);
					}
					canvas.drawLine(start.x, start.y, end.x, end.y, paint);
					start = end;
				}
				projection.toPixels(CPSession.startPoint, start);
				canvas.drawBitmap(this.marker, start.x - 16, start.y - 32,
						paint);

			}
		}
	}
}
