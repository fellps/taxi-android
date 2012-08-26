package tcc.iesgo.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import tcc.iesgo.activity.R;

public class RecordsActivity extends ListActivity implements LocationListener {

	LocationManager lm;
	HttpClient hc = new DefaultHttpClient();
	Button btnVoltar;
	ProgressDialog progressDialog;
	Handler mHandler = new Handler();
	DecimalFormat formattedDist = new DecimalFormat("#.#");
	String pontos;
	HttpPost post;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_item);
		// Obtem uma instacia do servico de gerenciador de localizacao
		
		init();
	}
	
	public void init() {
		// Seta a nova lista
		// Cria uma lista com os dados informados
		ArrayList<Map<String, String>> list = buildData(pontos);

		String[] hashmap = { "nome", "distancia", "ponto_id" };
		int[] campos = { R.id.text1, R.id.text2 };

		SimpleAdapter adapter = new SimpleAdapter(RecordsActivity.this, list,
				R.layout.list_style, hashmap, campos);

		setListAdapter(adapter);
	}

	//Cria um ArrayList a partir da string informada
	private ArrayList<Map<String, String>> buildData(String pontos) {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		list.add(putData("João Pedro dos Santos","22/08/2012", "5"));
		list.add(putData("José Gomes da Costa","26/08/2012", "1"));
		
		return list;
	}

	// Retorna um HashMap a partir dos dados informados na lista
	private HashMap<String, String> putData(String nome, String distancia, String id) {
		HashMap<String, String> item = new HashMap<String, String>();
		item.put("nome", nome);
		item.put("distancia", distancia);
		item.put("ponto_id", id);
		return item;
	}

	@Override
	protected void onListItemClick(final ListView l, View v, final int position, long id) {
		super.onListItemClick(l, v, position, id);
		// Objetos na lista
	}

	
	@Override
	public void onBackPressed() {
		finish();
	}

	// Chamado quando a activity comeca a interagir com o usuario
	@Override
	protected void onResume() {
		super.onResume();
		init();
	}

	// Chamado quando o sistema esta prestes a retomar uma activity anterior
	@Override
	protected void onPause() {
		super.onPause();	
	}

	// Chamado quando a activity ja nao e visivel para o usuario
	@Override
	protected void onStop() {
		super.onStop();
		//finish();
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