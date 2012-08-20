package tcc.iesgo.overlay;

import tcc.iesgo.activity.R;
import tcc.iesgo.overlay.BalloonItemizedOverlay;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class ItemizedOverlay extends BalloonItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> m_overlays = new ArrayList<OverlayItem>();
	private Context c;
	
	LocationManager lm;
	GeoPoint geoActual;
	GeoPoint geoVan;
	GeoPoint src;
	GeoPoint geoDest;
	MapView mapView;
	ItemizedOverlay itemizedOverlay;
	List<Overlay> mapOverlays; //Objetos do mapa
	Context context;
	private RoutePath _activity = new RoutePath(context);
	
	private Document doc = null;

	double distance;
	
	public ItemizedOverlay(Drawable defaultMarker, MapView mapView, GeoPoint geoActual, GeoPoint geoDest, ItemizedOverlay itemizedOverlay) {
		super(boundCenter(defaultMarker), mapView);
		c = mapView.getContext();
		this.geoActual = geoActual;
		this.geoDest = geoDest;
		this.mapView = mapView;
		this.itemizedOverlay = itemizedOverlay;
	}

	public void addOverlay(OverlayItem overlay) {
	    m_overlays.add(overlay);
	    populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return m_overlays.get(i);
	}

	@Override
	public int size() {
		return m_overlays.size();
	}


	
	@Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow){
        if(!shadow){
            super.draw(canvas, mapView, false);
        }
    }
	


    private class connectAsyncTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            //SHOW YOU PROGRESS BAR HERE
        }
        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
        	fetchData();
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);            
            if(doc!=null){
                Overlay ol = new MyOverlay(_activity,geoDest,geoDest,1);
                mapOverlays.add(ol);
                NodeList _nodelist = doc.getElementsByTagName("status");
                Node node1 = _nodelist.item(0);
                String _status1  = node1.getChildNodes().item(0).getNodeValue();
                if(_status1.equalsIgnoreCase("OK")){
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
                        Overlay ol1 = new MyOverlay(gp1,gp2,2,Color.BLUE);
                        mapOverlays.add(ol1);
                    }
                    Overlay ol2 = new MyOverlay(_activity,geoActual,geoActual,3);
                    mapOverlays.add(ol2);
 
                    ///DISMISS PROGRESS BAR HERE
 
                }else{
                    // showAlert AS "Unable to find the route"
                }
 
                Overlay ol2 = new MyOverlay(_activity,geoActual,geoActual,3);
                mapOverlays.add(ol2);
                //DISMISS PROGRESS BAR HERE
            }else{
              //  showAlert AS "Unable to find the route"
            }
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
	
    private void fetchData(){
    	
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.google.com/maps/api/directions/xml?origin=");
        urlString.append( Double.toString((double)geoActual.getLatitudeE6()/1.0E6 ));
        urlString.append(",");
        urlString.append( Double.toString((double)geoActual.getLongitudeE6()/1.0E6 ));
        urlString.append("&destination=");
        urlString.append( Double.toString((double)geoDest.getLatitudeE6()/1.0E6 ));
        urlString.append(",");
        urlString.append( Double.toString((double)geoDest.getLongitudeE6()/1.0E6 ));
        urlString.append("&sensor=true&mode=driving");
        
        HttpURLConnection urlConnection= null;
        URL url = null;
        try
        {
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
	
}
