package tcc.iesgo.overlay;

import tcc.iesgo.activity.R;
import tcc.iesgo.overlay.RoutePath.connectAsyncTask;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
 
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
 
public class CustomItemizedOverlay extends BalloonItemizedOverlay<OverlayItem> {
 
    private final ArrayList<OverlayItem> mapOverlays = new ArrayList<OverlayItem>();
 
    private Context context;
	MapView mapView;
 
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
}