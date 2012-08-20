package tcc.iesgo.overlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
 
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
 
public class MyOverlay extends Overlay
{
    private GeoPoint gp1;
    private GeoPoint gp2;
    //private int mRadius=6;
    private int mode=0;
    private int defaultColor;
    private String text="";
    private Bitmap img = null;
    Context mContext;
 
    public MyOverlay(Context context,GeoPoint gp1,GeoPoint gp2,int mode) // GeoPoint is a int. (6E)
    {
        this.gp1 = gp1;
        this.gp2 = gp2;
        this.mode = mode;
        this.mContext = context;
        defaultColor = 999; // no defaultColor
 
    }
 
    public MyOverlay(GeoPoint gp1,GeoPoint gp2,int mode, int defaultColor)
    {
        this.gp1 = gp1;
        this.gp2 = gp2;
        this.mode = mode;
        this.defaultColor = defaultColor;
    }
    public void setText(String t)
    {
        this.text = t;
    }
    public void setBitmap(Bitmap bitmap)
    {
        this.img = bitmap;
    }
    public int getMode()
    {
        return mode;
    }
 
    @Override
    public boolean draw
    (Canvas canvas, MapView mapView, boolean shadow, long when)
    {
        Projection projection = mapView.getProjection();
        if (shadow == false)
        {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            Point point = new Point();
            projection.toPixels(gp1, point);
            // mode=1&#65306;start
            if(mode==1)
            {
                if(defaultColor==999)
                    paint.setColor(Color.BLUE);
                else
                    paint.setColor(defaultColor);                
                // start point
            }
            // mode=2&#65306;path
            else if(mode==2)
            {
                if(defaultColor==999)
                    paint.setColor(Color.RED);
                else
                    paint.setColor(defaultColor);
                Point point2 = new Point();
                projection.toPixels(gp2, point2);
                paint.setStrokeWidth(5);
                paint.setAlpha(120);
                canvas.drawLine(point.x, point.y, point2.x,point2.y, paint);
 
            }
            /* mode=3&#65306;end */
            else if(mode==3)
            {
                /* the last path */
 
                if(defaultColor==999)
                    paint.setColor(Color.GREEN);
                else
                    paint.setColor(defaultColor);
                Point point2 = new Point();
                projection.toPixels(gp2, point2);
                paint.setStrokeWidth(5);
                paint.setAlpha(120);
                canvas.drawLine(point.x, point.y, point2.x,point2.y, paint);
                /* end point */
 
            }
        }
        return super.draw(canvas, mapView, shadow, when);
    }
 
}