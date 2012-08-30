package tcc.iesgo.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;	
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import tcc.iesgo.activity.R;
import tcc.iesgo.http.connection.HttpClientFactory;

public class RecordsActivity extends ListActivity implements LocationListener {

	LocationManager lm;
	HttpClient httpclient = HttpClientFactory.getThreadSafeClient();
	Button btnVoltar;
	ProgressDialog progressDialog;
	Handler mHandler = new Handler();
	DecimalFormat formattedDist = new DecimalFormat("#.#");
	HttpPost post;
	BaseAdapter adapter;
	
	Button btnRefresh;
	
	private String data[];
	
	private JSONObject jObject;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_item);
		
		btnRefresh = (Button) findViewById(R.id.button_refresh);
		
		init();
		
		btnRefresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				adapter.notifyDataSetChanged();
			}
		});
	}	
	
	public void init() {
		//Histórico de solicitações
		String records = getRecords();

		//Cria uma lista com os dados informados
		ArrayList<Map<String, String>> list = buildData(records);

		String[] hashmap = { "name", "date", "id_taxista" };
		int[] campos = { R.id.text1, R.id.text2 };

		adapter = new SimpleAdapter(RecordsActivity.this, list,
				R.layout.list_style, hashmap, campos);
		
		setListAdapter(adapter);
	}

	//Cria um ArrayList a partir da string informada
	private ArrayList<Map<String, String>> buildData(String records) {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		try {
			String[] taxiId = new String[getJsonResult(records, "id").length];
			taxiId = getJsonResult(records, "id");
			String[] name = new String[getJsonResult(records, "name").length];
			name = getJsonResult(records, "name");
			String[] date = new String[getJsonResult(records, "date").length];
			date = getJsonResult(records, "date");
			
			for(int i=0;i<name.length;i++){
				list.add(putData(name[i],date[i],taxiId[i]));
			}

		} catch (JSONException e) {
			Toast.makeText(RecordsActivity.this, getString(R.string.register_error_off), Toast.LENGTH_SHORT).show();
		}

		return list;
	}

	// Retorna um HashMap a partir dos dados informados na lista
	private HashMap<String, String> putData(String name, String date, String id) {
		HashMap<String, String> item = new HashMap<String, String>();
		item.put("name", name);
		item.put("date", date);
		item.put("id_taxista", id);
		return item;
	}

	@Override
	protected void onListItemClick(final ListView l, View v, final int position, long id) {
		super.onListItemClick(l, v, position, id);
		// Objetos na lista
	}
	
	private String getRecords(){
		String status = "";
		try {
			HttpPost httppost = new HttpPost(getString(R.string.url_webservice) + getString(R.string.url_records) +  getString(R.string.form_id_records));
			
			HttpResponse response = httpclient.execute(httppost);
			
			status = EntityUtils.toString(response.getEntity());
		
		} catch (Exception e) {
			Toast.makeText(RecordsActivity.this, getString(R.string.register_error_off), Toast.LENGTH_SHORT).show();
		}
		
		return status;
	}
	
	private String[] getJsonResult(String response, String option) throws JSONException{
		jObject = new JSONObject(response);
		JSONArray jArray = jObject.getJSONArray("records");
		data = new String[jArray.length()];
		for(int i=0;i<jArray.length();i++){
			this.data[i] = jArray.getJSONObject(i)
					.getString(option).toString();
		}
		return this.data;
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