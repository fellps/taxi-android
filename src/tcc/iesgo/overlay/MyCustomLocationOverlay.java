package tcc.iesgo.overlay;

import tcc.iesgo.activity.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Handler;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Projection;

/**
 * Classe responsável em customizar o ícone 
 * de localização do usuário. (MyLocationOverlay)
 */

public class MyCustomLocationOverlay extends MyLocationOverlay {
 
     private Paint accuracyPaint;
     private Point center;
     private Point left;
     private Drawable[] slips;
     private final MapView mapView;
     private int currSlip = 0;
     Point point = new Point();
     Rect rect = new Rect();
     private Handler handler = new Handler();
     private Runnable overlayAnimationTask;

 public MyCustomLocationOverlay(Context context, MapView mapView) {
	 super(context, mapView);
  
	 this.mapView = mapView;
	 
     slips = new Drawable[4];
     slips[0] = context.getResources().getDrawable(R.drawable.icon_user);
     slips[1] = context.getResources().getDrawable(R.drawable.icon_user);
     slips[2] = context.getResources().getDrawable(R.drawable.icon_user);
     slips[3] = context.getResources().getDrawable(R.drawable.icon_user);
     overlayAnimationTask = new Runnable() {
         public void run() {
             currSlip = (currSlip + 1) % slips.length;
             MyCustomLocationOverlay.this.mapView.invalidate();
             handler.removeCallbacks(overlayAnimationTask);
             handler.postDelayed(overlayAnimationTask, 200);
         }
     };
     handler.removeCallbacks(overlayAnimationTask);
     handler.postAtTime(overlayAnimationTask, 100);
 }
 
  @Override
     protected void drawMyLocation(Canvas canvas, MapView mapView,
                     Location lastFix, GeoPoint myLocation, long when) {
   
                     accuracyPaint = new Paint();
                     accuracyPaint.setAntiAlias(true);
                     accuracyPaint.setStrokeWidth(2.0f);
                     center = new Point();
                     left = new Point();
                     
                     Projection projection = mapView.getProjection();
                     double latitude = lastFix.getLatitude();
                     double longitude = lastFix.getLongitude();
                     float accuracy = lastFix.getAccuracy();
                     
                     float[] result = new float[1];
                     
                     Location.distanceBetween(latitude, longitude, latitude, longitude + 1, result);
                     float longitudeLineDistance = result[0];
                     
                     GeoPoint leftGeo = new GeoPoint((int)(latitude*1e6), (int)((longitude-accuracy/longitudeLineDistance)*1e6));
                     projection.toPixels(leftGeo, left);
                     projection.toPixels(myLocation, center);
                     int radius = center.x - left.x;
                     
                     accuracyPaint.setColor(0xff6666ff);
                     accuracyPaint.setStyle(Style.STROKE);
                     canvas.drawCircle(center.x, center.y, radius, accuracyPaint);
                     
                     accuracyPaint.setColor(0x186666ff);
                     accuracyPaint.setStyle(Style.FILL);
                     canvas.drawCircle(center.x, center.y, radius, accuracyPaint);
   
                     mapView.getProjection().toPixels(myLocation, point);
                     rect.left = point.x - slips[currSlip].getIntrinsicWidth() / 2;
                     rect.top = point.y - slips[currSlip].getIntrinsicHeight() / 2;
                     rect.right = point.x + slips[currSlip].getIntrinsicWidth() / 2;
                     rect.bottom = point.y + slips[currSlip].getIntrinsicHeight() / 2;
                     slips[currSlip].setBounds(rect);
                     slips[currSlip].draw(canvas);         
     }
}