package tcc.iesgo.activity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
 
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
 
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
 
import tcc.iesgo.activity.R;
import tcc.iesgo.overlay.RouteOverlay;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
 
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
 
public class TaximeterActivity extends MapActivity implements LocationListener{
 
    private MapView mapView;
    private TaximeterActivity taximeterActivity;
    private GeoPoint origGeoPoint,destGeoPoint;
    private Drawable origDrawable, destDrawable;
    private CustomTaximeterItemizedOverlay origItemizedOverlay, destItemizedOverlay;
    private OverlayItem orign, destiny;
    private LocationManager lm;
    private Document doc = null;
    private static List<Overlay> mOverlays;

    private Button btAction;
    private TextView mapInfo;
    
    private String distance, duration;
    private boolean process = false;
    private boolean processFinish = false;
    private boolean processStatus = false;
    private int distanceMeters, durationSeconds;
    private float flag1 = 1.80f;
    private float flag2 = 2.34f;
    private float downtime = 0.41f;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_taximeter);
        taximeterActivity = TaximeterActivity.this;
 
        mapView = (MapView) findViewById(R.id.mapview_taximeter);
    	
		//Obtem uma instacia do servico de gerenciador de localizacao
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		//Ativa os updates para receber notificacoes de localizacao (periodicamente)
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 120000, 100f, TaximeterActivity.this);

		//Útimo local conhecido
		Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
 
		//GeoPoint da posição atual do usuário
		origGeoPoint = new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));
		destGeoPoint = new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));
		
        mOverlays = mapView.getOverlays();
        
        origDrawable = this.getResources().getDrawable(R.drawable.icon_orign);
        destDrawable = this.getResources().getDrawable(R.drawable.icon_destiny);
        
        origItemizedOverlay = new CustomTaximeterItemizedOverlay(this.origDrawable, TaximeterActivity.this, this.mapView);
        
        OverlayItem origOverlayItem = new OverlayItem(origGeoPoint, "", "");
        
        origItemizedOverlay.addOverlay(origOverlayItem);
        
        mOverlays.add(origItemizedOverlay);

        mapView.setBuiltInZoomControls(true);
        mapView.displayZoomControls(true);
        
        //mOverlays = mapView.getOverlays();
        mapView.getController().animateTo(origGeoPoint);
        mapView.getController().setZoom(15);
        
        mapInfo = (TextView)  findViewById(R.id.map_info_taximeter);
        btAction = (Button) findViewById(R.id.bt_action);
        
        btAction.setOnClickListener(new View.OnClickListener() {
			//Verifica se todos os campos foram preenchidos corretamente
			@Override
			public void onClick(View view) {
				if(btAction.getText().equals(getString(R.string.map_taximeter_next))){ //Se for prosseguir
					btAction.setText(getString(R.string.map_taximeter_finish));
					mapInfo.setText(getString(R.string.map_taximeter_info_next));
					process = true;
				} else if(btAction.getText().equals(getString(R.string.map_taximeter_finish))) {
					if(processStatus){
				        connectAsyncTask _connectAsyncTask = new connectAsyncTask(TaximeterActivity.this);
				        _connectAsyncTask.execute(); 
				        processFinish = true;
				        btAction.setText(getString(R.string.map_taximeter_restart));
					} else {
						final AlertDialog.Builder dialog = new AlertDialog.Builder(getParent());
						dialog.setTitle(getString(R.string.ad_title_alert));
						dialog.setMessage(getString(R.string.map_taximeter_error_marker));
						dialog.setIcon(android.R.drawable.ic_menu_myplaces);
						dialog.setCancelable(false);
						
						dialog.setPositiveButton(getString(R.string.ad_button_positive),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,	int id) {
										dialog.cancel();
									}
								});
						dialog.show();
					}
				} else if(btAction.getText().equals(getString(R.string.map_taximeter_restart))){
					process = false;
					processFinish = false;
					processStatus = false;
					mapInfo.setText(getString(R.string.map_taximeter_info));
					btAction.setText(getString(R.string.map_taximeter_next));
					clearAllOverlays();
					mOverlays.add(origItemizedOverlay);
					mapView.invalidate();
				}
			}
		});
    }
 
    public class connectAsyncTask extends AsyncTask<Void, Void, Void>{
    	
    	private ProgressDialog progress;
    	private Context context;
    	
    	public connectAsyncTask (Context context){
    		this.context = context;
    	}
    	
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
			progress = ProgressDialog.show(this.context, this.context.getString(R.string.pd_title), this.context.getString(R.string.pd_content_loading));
			progress.setIcon(android.R.drawable.ic_menu_myplaces);
	        progress.show();
        }
        
        @Override
        protected Void doInBackground(Void... params) {
            fetchData();
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);            
            if(doc!=null){
                Overlay ol = new RouteOverlay(taximeterActivity, origGeoPoint, origGeoPoint, 1);
                mOverlays.add(ol);
                NodeList _nodelist = doc.getElementsByTagName("status");
                Node node1 = _nodelist.item(0);
                String _status1  = node1.getChildNodes().item(0).getNodeValue();
                if(_status1.equalsIgnoreCase("OK")){

                	//Extrai a distancia
                	NodeList listDist = doc.getElementsByTagName("distance");
                	
            	    for (int k = 0; k<listDist.getLength(); k++) {
            	        NodeList childs = listDist.item(k).getChildNodes();

            	        for (int l = 0; l < childs.getLength(); l++) {
            	            if (childs.item(l).getNodeName().equals("text")) {
            	            	Node nodeDistance = childs.item(l);
            	            	Element _nodelist_distance = (Element)nodeDistance;
            	            	distance = _nodelist_distance.getChildNodes().item(0).getNodeValue();
            	            }       
            	        }
            	        
            	        for (int l = 0; l < childs.getLength(); l++) {
            	            if (childs.item(l).getNodeName().equals("value")) {
            	            	Node nodeDistance = childs.item(l);
            	            	Element _nodelist_distance = (Element)nodeDistance;
            	            	distanceMeters = Integer.parseInt(_nodelist_distance.getChildNodes().item(0).getNodeValue());
            	            }       
            	        }
            	    }
            	    
                	//Extrai a duração
                	NodeList listDuration = doc.getElementsByTagName("duration");
                	
            	    for (int k = 0; k<listDuration.getLength(); k++) {
            	        NodeList childs = listDuration.item(k).getChildNodes();

            	        for (int l = 0; l < childs.getLength(); l++) {
            	            if (childs.item(l).getNodeName().equals("text")) {
            	            	Node nodeDistance = childs.item(l);
            	            	Element _nodelist_distance = (Element)nodeDistance;
            	            	duration = _nodelist_distance.getChildNodes().item(0).getNodeValue();
            	            }

            	            if (childs.item(l).getNodeName().equals("value")) {
            	            	Node nodeDistance = childs.item(l);
            	            	Element _nodelist_distance = (Element)nodeDistance;
            	            	durationSeconds = Integer.parseInt(_nodelist_distance.getChildNodes().item(0).getNodeValue());
            	            }       
            	        }
            	    }
                	
                	//Extrai as coordenadas
                    NodeList _nodelist_path = doc.getElementsByTagName("overview_polyline");
                    Node node_path = _nodelist_path.item(0);
                    Element _status_path = (Element)node_path;
                    NodeList _nodelist_destination_path = _status_path.getElementsByTagName("points");
                    Node _nodelist_dest = _nodelist_destination_path.item(0);
                    String _path  = _nodelist_dest.getChildNodes().item(0).getNodeValue();
                    
                    List<GeoPoint> _geopoints = decodePoly(_path);
                    GeoPoint gp1;
                    GeoPoint gp2;
                    gp2 = _geopoints.get(0);
                    for(int i=1;i<_geopoints.size();i++) // the last one would be crash
                    {
                        gp1 = gp2;
                        gp2 = _geopoints.get(i);
                        Overlay ol1 = new RouteOverlay(gp1,gp2,2,Color.BLUE);
                        mOverlays.add(ol1);
                    }
                    Overlay ol2 = new RouteOverlay(taximeterActivity, destGeoPoint, destGeoPoint, 3);
                    mOverlays.add(ol2);
 
                }else{
                    // showAlert AS "Unable to find the route"
                }
 
                Overlay ol2 = new RouteOverlay(taximeterActivity,destGeoPoint,destGeoPoint,3);
                mOverlays.add(ol2);

            }else{
              //  showAlert AS "Unable to find the route"
            }
            mapView.invalidate();
            
	        mapInfo.setText(getString(R.string.map_taximeter_flag1) + " " + round(((distanceMeters/1000)*flag1)+((durationSeconds/60)*downtime)) + 
	        		"\n" + getString(R.string.map_taximeter_flag2) + " " + round(((distanceMeters/1000)*flag2)+((durationSeconds/60)*downtime)) + 
	        		"\n" + getString(R.string.map_taximeter_distance) + " " + distance + " - " + getString(R.string.map_taximeter_duration) + " " + duration);
	        
	        //Cancela progressDialog
	        progress.dismiss();
        }
    }
    
    public void fetchData(){
    	
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.google.com/maps/api/directions/xml?origin=");
        urlString.append( Double.toString((double)origGeoPoint.getLatitudeE6()/1.0E6 ));
        urlString.append(",");
        urlString.append( Double.toString((double)origGeoPoint.getLongitudeE6()/1.0E6 ));
        urlString.append("&destination=");
        urlString.append( Double.toString((double)destGeoPoint.getLatitudeE6()/1.0E6 ));
        urlString.append(",");
        urlString.append( Double.toString((double)destGeoPoint.getLongitudeE6()/1.0E6 ));
        urlString.append("&sensor=true&mode=driving");
        
        HttpURLConnection urlConnection= null;
        URL url = null;
        try{
            url = new URL(urlString.toString());
            urlConnection=(HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.connect();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = (Document) db.parse(urlConnection.getInputStream());//Util.XMLfromString(response);
        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }catch (ParserConfigurationException e){
            e.printStackTrace();
        }
        catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private List<GeoPoint> decodePoly(String encoded) {
 
        List<GeoPoint> poly = new ArrayList<GeoPoint>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
 
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
 
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
 
            GeoPoint p = new GeoPoint((int) (((double) lat / 1E5) * 1E6),
                    (int) (((double) lng / 1E5) * 1E6));
            poly.add(p);
        }
 
        return poly;
    }
    
    private double round(double value){
	    int x = 2; //quantidade de números depois da virgula
	    x = (int)Math.pow(10, x);
	    double num = (int)(value*x);
	    num = (double)(num/x);
	    return num;
    }
    
    private void clearAllOverlays(){
    	mapView.getOverlays().removeAll(mapView.getOverlays());
    	mapView.invalidate();
    }
    
    private void addOverlayMap(GeoPoint p, Geocoder geoCoder){
    	if(!process){
	    	try{
	    		this.origGeoPoint = p;
	    		
				mapView.getOverlays().remove(0);
				this.mapView.invalidate();
				
		    	orign = new OverlayItem(p, "", "");
		    	
		    	origItemizedOverlay = new CustomTaximeterItemizedOverlay(this.origDrawable, TaximeterActivity.this, this.mapView);
		    	origItemizedOverlay.addOverlay(orign);
		    	
		        mOverlays.add(origItemizedOverlay);
		        
		        List<Address> addresses = geoCoder.getFromLocation(p.getLatitudeE6()/1E6, p.getLongitudeE6()/1E6, 1);
		    	
		        String add = "";
		        if (addresses.size() > 0){
		            for (int i=0; i<addresses.get(0).getMaxAddressLineIndex(); i++)
		               add += addresses.get(0).getAddressLine(i) + "\n";
		        }
		        Toast.makeText(TaximeterActivity.this, add, Toast.LENGTH_SHORT).show();
	    	} catch (Exception e) {
	    		Toast.makeText(TaximeterActivity.this, getString(R.string.ad_content_error_off), Toast.LENGTH_SHORT).show();
	    	}
    	} else {
	    	try{
	    		this.destGeoPoint = p;
	    		
	    		if(mapView.getOverlays().size() >= 2)
	    			mapView.getOverlays().remove(1);
	    		
				this.mapView.invalidate();
				
				destiny = new OverlayItem(p, "", "");
		    	
		    	destItemizedOverlay = new CustomTaximeterItemizedOverlay(this.destDrawable, TaximeterActivity.this, this.mapView);
		    	destItemizedOverlay.addOverlay(destiny);
		    	
		        mOverlays.add(destItemizedOverlay);
		        
		        List<Address> addresses = geoCoder.getFromLocation(p.getLatitudeE6()/1E6, p.getLongitudeE6()/1E6, 1);
		    	
		        String add = "";
		        if (addresses.size() > 0){
		            for (int i=0; i<addresses.get(0).getMaxAddressLineIndex(); i++)
		               add += addresses.get(0).getAddressLine(i) + "\n";
		        }
		        Toast.makeText(TaximeterActivity.this, add, Toast.LENGTH_SHORT).show();
		        processStatus = true;
	    	} catch (Exception e) {
	    		Toast.makeText(TaximeterActivity.this, getString(R.string.ad_content_error_off), Toast.LENGTH_SHORT).show();
	    	}
    	}
    }
    
    class CustomTaximeterItemizedOverlay extends ItemizedOverlay<OverlayItem> {
    	 
        private final ArrayList<OverlayItem> mapOverlays = new ArrayList<OverlayItem>();
        private Context context;
    	private MapView mapView;
    	private Drawable marker;
     
        public CustomTaximeterItemizedOverlay(Drawable defaultMarker, MapView mapView) {
            super(defaultMarker);
            this.marker = defaultMarker;
            this.mapView = mapView;
        }
     
        public CustomTaximeterItemizedOverlay(Drawable defaultMarker, Context context, MapView mapView) {
            this(defaultMarker, mapView);
            this.mapView = mapView;
            this.context = context;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event, MapView mapView) {
        	return gestureDetector.onTouchEvent(event);
        }
        
        final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
        	@Override
            public void onLongPress(MotionEvent event) {
                GeoPoint p = mapView.getProjection().fromPixels((int) event.getX(), (int) event.getY());
                Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
                
        		if(!processFinish){ //Caso não tenha finalizado o processo
        			addOverlayMap(p, geoCoder);
        		}
            }
        });
        
        public void addOverlay(OverlayItem overlay) {
            mapOverlays.add(overlay);
            this.populate();
        }
     
        @Override
        protected OverlayItem createItem(int i) {
            return mapOverlays.get(i);
        }
     
        @Override
        public int size() {
            return mapOverlays.size();
        }
        
        @Override
        public void draw(Canvas canvas, MapView mapView, boolean shadow) {
	        super.draw(canvas, mapView, false);
	        boundCenterBottom(marker);
        }
    }
    
    @Override
    protected boolean isRouteDisplayed() {
        // TODO Auto-generated method stub
        return false;
    }

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
	}


	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
	}


	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
	}


	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
	}
}