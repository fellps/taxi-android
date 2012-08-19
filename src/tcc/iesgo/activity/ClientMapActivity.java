package tcc.iesgo.activity;

import java.util.List;

import tcc.iesgo.activity.R;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class ClientMapActivity extends MapActivity implements LocationListener {
	
	MapView mapView; //Mapa
	List<Overlay> mapOverlays; //Objetos do mapa
	MapController mc; //Controlador do mapa
	LocationManager lm;
	MyLocationOverlay myLocationOverlay; //Overlay do usuário
	
	Drawable dTaxi;
	
	TextView mapInfo;
	
	GeoPoint geoActual;
	
	ProgressDialog progressDialog;
	Handler mHandler = new Handler();
	
	private int minLatitude, maxLatitude, minLongitude, maxLongitude;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }
    
    private void init(){
		//Layout da aplicacao
		setContentView(R.layout.map);
		
		//Instancia o mapView da aplicacao
		mapView = (MapView) findViewById(R.id.mapview);
		//Controladores de zoom
		mapView.setBuiltInZoomControls(true);
		//Instancia de overlays para o mapa
		mapOverlays = mapView.getOverlays();

		//Controlador do mapa
		mc = mapView.getController();
		mc.setZoom(16);
		
		//Obtem uma instacia do servico de gerenciador de localizacao
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		//Ativa os updates para receber notificacoes de localizacao (periodicamente)
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 120000, 100f, ClientMapActivity.this);

		//Útimo local conhecido
		Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		// Cria um overlay mostrando a posicao do dispositivo
		myLocationOverlay = new MyLocationOverlay(ClientMapActivity.this,mapView);
		// Habilita o ponto azul de localizacao na tela
		myLocationOverlay.enableMyLocation();
		// Habilita atualizacoes do sensor
		myLocationOverlay.enableCompass();
		
		// Adiciona o overlay no mapa
		mapOverlays.add(myLocationOverlay);
		
		dTaxi = ClientMapActivity.this.getResources().getDrawable(R.drawable.icon_taxi);

		mapInfo = (TextView) findViewById(R.id.map_info);

		showMap(location);
    }
    
    private class UpdateLocation extends AsyncTask<Location, Void, Integer> {

        @Override
        protected void onPreExecute() {
        	progressDialog = ProgressDialog.show(ClientMapActivity.this, getString(R.string.pd_title), getString(R.string.map_pd_update_map));
        }
        
		@Override
		protected Integer doInBackground(Location... locations) {
			for(final Location location : locations){
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						updateMap(location);
					}
				});
			}
			return 1;
		}
		
        @Override
        protected void onPostExecute(Integer result) {
            progressDialog.dismiss();
        }

    }

	//Chamando quando a posição do gps é alterada
	@Override
	public void onLocationChanged(Location location) {
		showMap(location);
	}

	public void showMap(Location location) {
		if (location == null)
			location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER); //Último local registrado

		minLatitude = Integer.MAX_VALUE;
		maxLatitude = Integer.MIN_VALUE;
		minLongitude = Integer.MAX_VALUE;
		maxLongitude = Integer.MIN_VALUE;
		
		UpdateLocation updateLocation = new UpdateLocation();
		updateLocation.execute(location);
	}

	//Chamado quando o GPS esta desativado (abre as conf. do GPS)
	@Override
	public void onProviderDisabled(String arg0) {
		final AlertDialog.Builder dialog = new AlertDialog.Builder(getParent());
		dialog.setTitle(getString(R.string.gps_disabled));
		dialog.setMessage(getString(R.string.gps_disabled_message));
		dialog.setCancelable(false);
		
		dialog.setPositiveButton(getString(R.string.ad_button_positive),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,	int id) {
						Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(intent);
					}
				});
			
		dialog.setNegativeButton(
				getString(R.string.ad_button_negative), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,	int id) {
						dialog.cancel();
						finish();
					}
		});
		dialog.show();
	}
	
	//Atualiza o mapa com a posicao atual
	private void updateMap(Location location) {
		if (location != null) {
			Double geoLat = location.getLatitude() * 1E6;
			Double geoLng = location.getLongitude() * 1E6;
			
			geoActual = new GeoPoint(geoLat.intValue(), geoLng.intValue());	
			
			mapView.invalidate(); // atualiza o mapa
			
			mc.animateTo(geoActual);
		}
	}

	//Chamado quando o GPS esta ativado
	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	//Chamado quando o status do GPS e alterado
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
        case LocationProvider.OUT_OF_SERVICE:
                //Log.v(tag, "Status alterado: Fora de serviço");
                break;
        case LocationProvider.TEMPORARILY_UNAVAILABLE:
                //Log.v(tag, "Status alterado: Temporariamente indisponível");
                break;
        case LocationProvider.AVAILABLE:
                //Log.v(tag, "Status alterado: Disponível");
                break;
        }
	}

	//Retornar true se a aplicação estiver traçando rotas ou violara os termos de uso
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	// Chamado quando a activity comeca a interagir com o usuario
	@Override
	protected void onResume() {
		super.onResume();
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 120000, 100f, ClientMapActivity.this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		PendingIntent origPendingIntent = PendingIntent.getBroadcast(getBaseContext(),0, getIntent(), PendingIntent.FLAG_UPDATE_CURRENT);
		lm.removeUpdates(origPendingIntent);
	}
	
	//Chamado quando a activity ja nao e visivel para o usuario
	@Override
	protected void onStop() {
		super.onStop();
		PendingIntent origPendingIntent = PendingIntent.getBroadcast(getBaseContext(),0, getIntent(), PendingIntent.FLAG_UPDATE_CURRENT);
		lm.removeUpdates(origPendingIntent);
		System.exit(0);
	}
}