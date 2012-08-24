package tcc.iesgo.overlay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
 
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
 
public class RouteOverlay extends Overlay{
    private GeoPoint gp1;
    private GeoPoint gp2;
    private int mode=0;
    private int defaultColor;
    Context mContext;
    
    //private String text="";
    //private Bitmap img = null;
 
    public RouteOverlay(Context context,GeoPoint gp1,GeoPoint gp2,int mode){
        this.gp1 = gp1;
        this.gp2 = gp2;
        this.mode = mode;
        this.mContext = context;
        defaultColor = 999; //sem cor padrão
        
    }
 
    public RouteOverlay(GeoPoint gp1,GeoPoint gp2,int mode, int defaultColor){
        this.gp1 = gp1;
        this.gp2 = gp2;
        this.mode = mode;
        this.defaultColor = defaultColor;
    }
    
    public int getMode(){
        return mode;
    } 
    /*
    public void setText(String t){
        this.text = t;
    }
    
    public void setBitmap(Bitmap bitmap){
        this.img = bitmap;
    }
    */
    @Override
    public boolean draw (Canvas canvas, MapView mapView, boolean shadow, long when){
        Projection projection = mapView.getProjection();
        if (shadow == false){
        	
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            Point point = new Point();
            projection.toPixels(gp1, point);
            
            if(mode==1){
                if(defaultColor==999)
                    paint.setColor(Color.BLUE);
                else
                    paint.setColor(defaultColor);                
                //ponto inicial
            }
            else if(mode==2){
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
            else if(mode==3){
                if(defaultColor==999)
                    paint.setColor(Color.GREEN);
                else
                    paint.setColor(defaultColor);
                
                Point point2 = new Point();
                projection.toPixels(gp2, point2);
                paint.setStrokeWidth(5);
                paint.setAlpha(120);
                canvas.drawLine(point.x, point.y, point2.x,point2.y, paint);
                //ponto de destino
            }
        }
        return super.draw(canvas, mapView, shadow, when);
    }
}