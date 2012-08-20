package tcc.iesgo.overlay;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import tcc.iesgo.activity.R;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class RouteOverLay extends Overlay {

	private GeoPoint gp1;
	private GeoPoint gp2;
	private int mode = 0;
	private int defaultColor;
	//private int mRadius = 6;

	public RouteOverLay(GeoPoint gp1, GeoPoint gp2, int mode){
		this.gp1 = gp1;
		this.gp2 = gp2;
		this.mode = mode;
		defaultColor = 999; // sem cor pre definida
	}

	public RouteOverLay(GeoPoint gp1, GeoPoint gp2, int mode, int defaultColor) {
		this.gp1 = gp1;
		this.gp2 = gp2;
		this.mode = mode;
		this.defaultColor = defaultColor;
	}

	public int getMode() {
		return mode;
	}

	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow,	long when) {

		Projection projection = mapView.getProjection();
		if (shadow == false) {
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			Point point = new Point();
			projection.toPixels(gp1, point);

			if (mode == 1) {
				if (defaultColor == 999) {
					paint.setColor(Color.BLUE);
				} else {
					paint.setColor(defaultColor);
				}
			// Ponto de Inicio
			//	RectF oval = new RectF(point.x - mRadius, point.y - mRadius, point.x + mRadius, point.y + mRadius);
			//	canvas.drawOval(oval, paint);
			} 
			else if (mode == 2) {
				if (defaultColor == 999) {
					paint.setColor(Color.RED);
				} else {
					paint.setColor(defaultColor);
				}
				Point point2 = new Point();
				projection.toPixels(gp2, point2);
				paint.setStrokeWidth(5);
				paint.setAlpha(120);
				canvas.drawLine(point.x, point.y, point2.x, point2.y, paint);
			} else if (mode == 3) {
				if (defaultColor == 999) {
					paint.setColor(Color.GREEN);
				} else {
					paint.setColor(defaultColor);
				}
				Point point2 = new Point();
				projection.toPixels(gp2, point2);
				paint.setStrokeWidth(5);
				paint.setAlpha(120);
				canvas.drawLine(point.x, point.y, point2.x, point2.y, paint);
				
				Bitmap ponto = BitmapFactory.decodeResource(mapView.getResources(), R.drawable.icon_taxi);
				canvas.drawBitmap(ponto, point2.x - (ponto.getWidth()/2), point2.y - (ponto.getHeight()), null);
				ponto.recycle();
				// Ponto de Chegada
				//RectF oval = new RectF(point2.x - mRadius, point2.y - mRadius, point2.x + mRadius, point2.y + mRadius);
				//paint.setAlpha(255);
				//canvas.drawOval(oval, paint);
			}
		}
		return super.draw(canvas, mapView, shadow, when);
	}
}