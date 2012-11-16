package tcc.iesgo.overlay;

import tcc.iesgo.activity.R;
import tcc.iesgo.activity.RequestFilterActivity;
import tcc.iesgo.http.connection.HttpClientFactory;

import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
 
public class CustomItemizedOverlay extends BalloonItemizedOverlay<OverlayItem> {
 
    private final ArrayList<OverlayItem> mapOverlays = new ArrayList<OverlayItem>();
    
    ProgressDialog progressDialog;
    Handler mHandler = new Handler();
    
    JSONObject jObject;
 
    private Context context;
	MapView mapView;
	
	HttpClient httpclient = HttpClientFactory.getThreadSafeClient();
 
    public CustomItemizedOverlay(Drawable defaultMarker, MapView mapView) {
        super(boundCenter(defaultMarker), mapView);
        this.mapView = mapView;
    }
 
    public CustomItemizedOverlay(Drawable defaultMarker, Context context, MapView mapView) {
        this(defaultMarker, mapView);
        this.mapView = mapView;
        this.context = context;
    }
        
	@Override
	protected boolean onBalloonTap(int index, OverlayItem item) {
		final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		
		dialog.setTitle(item.getTitle());
		dialog.setMessage(R.string.balloon_call_taxi); //item.getSnippet()
		dialog.setIcon(android.R.drawable.ic_menu_myplaces);
		dialog.setCancelable(false);
		
		dialog.setPositiveButton(R.string.ad_button_yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				String[] id_taxi = getFocus().getTitle().split(" - ");
				String[] taxi_id = id_taxi[0].split("RG: ");
				//RequestTaxi request = new RequestTaxi();
				//request.execute(taxi_id[1]);
				Intent intent = new Intent(mapView.getContext(), RequestFilterActivity.class);
				intent.putExtra("taxiid", taxi_id);
				context.startActivity(intent);
				hideAllBalloons();
			}
		});
		dialog.setNegativeButton(R.string.ad_button_no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		dialog.show();
		return true;
	}
 
    @Override
    protected OverlayItem createItem(int i) {
        return mapOverlays.get(i);
    }
 
    @Override
    public int size() {
        return mapOverlays.size();
    }
 
    public void addOverlay(OverlayItem overlay) {
        mapOverlays.add(overlay);
        this.populate();
    }
    
    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        if(!shadow) {
            super.draw(canvas, mapView, false);
        }
    }
}