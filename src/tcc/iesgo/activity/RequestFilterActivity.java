package tcc.iesgo.activity;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import tcc.iesgo.activity.R;
import tcc.iesgo.http.connection.HttpClientFactory;
import tcc.iesgo.persistence.SQLiteAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

public class RequestFilterActivity extends Activity {
	
	Spinner payment;
	ToggleButton animals;
	Spinner passengers;
	ToggleButton needs;
	EditText volume;
	Button btnSend;
	ImageButton btnHelp;
	
	SQLiteAdapter mySQLiteAdapter;
	HttpClient httpclient = HttpClientFactory.getThreadSafeClient();
	
	Handler mHandler = new Handler();
	ProgressDialog progressDialog;
	
	ArrayAdapter<CharSequence> adapter;
	ArrayAdapter<CharSequence> adapter2;
	
	private JSONObject jObject;
	
	private String[] taxiId;
	
	ArrayList<String> data = new ArrayList<String>();
	
	String result, resultStatus = "0";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.request_filter); //Layout da Activity
		
		// Recupera os extras da intent anterior
		Bundle extras = getIntent().getExtras();

		// Extrai o conteudo do extra
		this.taxiId = extras.getStringArray("taxiid");

		//Instância dos componentes do layout
		payment = (Spinner) findViewById(R.id.sp_payment);
		animals = (ToggleButton) findViewById(R.id.tb_animals);
		passengers = (Spinner) findViewById(R.id.sp_passengers);
		needs = (ToggleButton) findViewById(R.id.tb_needs);
		volume = (EditText) findViewById(R.id.et_volume);
		btnSend = (Button) findViewById(R.id.bt_send);
		btnHelp = (ImageButton) findViewById(R.id.ib_help);
		
		//Inicializa o Spinner
		adapter = ArrayAdapter.createFromResource(this, R.array.spinner_array_pay, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		payment.setAdapter(adapter);
		
		adapter = ArrayAdapter.createFromResource(this, R.array.spinner_array_pas, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		passengers.setAdapter(adapter);
		
		//Botão de ajuda
		btnHelp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(), HelpActivity.class);
				startActivity(i);
			}
		});
	
		//Botão de registro
		btnSend.setOnClickListener(new View.OnClickListener() {
			//Verifica se todos os campos foram preenchidos corretamente
			@Override
			public void onClick(View view) {
				if(payment.getSelectedItem().toString().equals("Dinheiro")){ data.add("0"); }
				else { data.add("1"); }
				if(animals.getText().toString().equals("Sim")){ data.add("1"); }
				else { data.add("0"); }
				data.add(passengers.getSelectedItem().toString());
				if(needs.getText().toString().equals("Sim")){ data.add("1"); }
				else { data.add("0"); }
				if(volume.getText().toString().equals("")) { data.add("0"); }
				else { data.add(volume.getText().toString()); }

				RequestTaxi request = new RequestTaxi();
				request.execute(taxiId);
			}
		});
	}
		
    private class RequestTaxi extends AsyncTask<String, String, String> {
    	
        @Override
        protected void onPreExecute() {
        	progressDialog = ProgressDialog.show(RequestFilterActivity.this, RequestFilterActivity.this.getString(R.string.pd_title), 
        			RequestFilterActivity.this.getString(R.string.cm_pd_request_msg));
        	progressDialog.setIcon(R.drawable.progress_dialog);
        }

		@Override
		protected String doInBackground(String... id_taxis) {
			for(final String id_taxi : taxiId){
				
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						try {
							String status = requestTaxi(id_taxi);
							if(status.equals("2")){ //se status = 2 (pendente)
					    		statusRequest sr = new statusRequest(RequestFilterActivity.this);	
					    		sr.execute(0, 0, 90);
							} else if (status.equals("0")) {
								final AlertDialog.Builder dialog = new AlertDialog.Builder(RequestFilterActivity.this);
								dialog.setTitle(R.string.ad_title_error);
								dialog.setMessage(R.string.cm_error_request_taxi); //item.getSnippet()
								dialog.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
								dialog.setCancelable(false);
								dialog.setPositiveButton(R.string.ad_button_positive, new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
									}
								});
								dialog.show();
							}
						} catch (Exception e) {
							Toast.makeText(RequestFilterActivity.this, RequestFilterActivity.this.getString(R.string.login_error_connection), Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
			return null;
		}
		
	    @Override
	    protected void onPostExecute(String result) {
	        progressDialog.dismiss();
	    }
	    
	    @Override
	    protected void onProgressUpdate(String... values) {
	        //Atualiza mensagem
	        progressDialog.setMessage(values[0]);
	    }
    }
    
	
	public class statusRequest extends AsyncTask<Integer, String, Integer>{
	
	    private ProgressDialog progress;
	    private Context context;
	
	    public statusRequest(Context context) {
	        this.context = context;
	    }
	
	    @Override
	    protected void onPreExecute() {
	        //Cria novo um ProgressDialogo e exibe
			progress = ProgressDialog.show(context, context.getString(R.string.cm_pd_request_taxi_title_sucess), context.getString(R.string.cm_pd_wait_request));
			progress.setIcon(android.R.drawable.ic_menu_myplaces);
	        progress.show();
	    }
	
	    @Override
	    protected Integer doInBackground(Integer... paramss) {
	    	for (final int param : paramss){
	    		for (int i = param; i > 0; i--) {
	    			if(i==10||i==20||i==30||i==40||i==50||i==60||i==70||i==80||i==90){
	    				resultStatus = statusRequestTaxi(taxiId[0]);
	    			}
					
					if(resultStatus.equals("2")){ //se status = 2 (pendente)
						try {
							Thread.sleep(1000);
							publishProgress(context.getString(R.string.cm_pd_wait_request) + " " + i + " " + context.getString(R.string.cm_pd_wait_request_seconds));
						} catch (InterruptedException e) {
							Toast.makeText(context, context.getString(R.string.cm_ad_request_taxi_title_error), Toast.LENGTH_SHORT).show();
						}
					} else if(resultStatus.equals("1")){ //se status = 1 (aprovado)
						final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
						dialog.setTitle(R.string.cm_ad_request_taxi_title_sucess);
						dialog.setMessage(R.string.cm_ad_request_taxi_content_sucess); //item.getSnippet()
						dialog.setIcon(android.R.drawable.ic_menu_myplaces);
						dialog.setCancelable(false);
						dialog.setPositiveButton(R.string.ad_button_positive, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
						dialog.show();
						break;
					}
	    		}
	    	}
	        return 1;
	    }
	
	    @Override
	    protected void onPostExecute(Integer result) {
	        //Cancela progressDialogo
	        progress.dismiss();
	        
    		if(resultStatus.equals("2")){ //2
				final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
				dialog.setTitle(R.string.cm_ad_request_taxi_title_no_sucess);
				dialog.setMessage(R.string.cm_ad_response_taxi_no_success); //item.getSnippet()
				dialog.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
				dialog.setCancelable(false);
				dialog.setPositiveButton(R.string.ad_button_positive, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				dialog.show();
    		}
	    }
	
	    @Override
	    protected void onProgressUpdate(String... values) {
	        //Atualiza mensagem
	        progress.setMessage(values[0]);
	    }
	}

    //Faz uma requisição a um taxista
    private String requestTaxi(String id_taxi){
    	String jsonResult = "";
    	
    	if(!id_taxi.equals("")){
			try {
				HttpPost post = new HttpPost(RequestFilterActivity.this.getString(R.string.url_webservice) + RequestFilterActivity.this.getString(R.string.url_request_taxi) + 
					 id_taxi + "/" + data.get(0) + "/" + data.get(1) + "/" + data.get(2) + "/" + data.get(3) + "/" + data.get(4) + "/" + RequestFilterActivity.this.getString(R.string.form_id_request_taxi));

				HttpResponse rp = httpclient.execute(post);

				if (rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
					this.result = EntityUtils.toString(rp.getEntity());
				
				jsonResult = getJsonResult(this.result, "result");
				
			} catch (Exception e) {
				Toast.makeText(RequestFilterActivity.this, RequestFilterActivity.this.getString(R.string.login_error_connection), Toast.LENGTH_SHORT).show();
			}
    	}
		return jsonResult;
    }
    
    private String statusRequestTaxi(String id_taxi){
    	String jsonResult = "";
    	
    	if(!id_taxi.equals("")){
			try {
				HttpPost post = new HttpPost(RequestFilterActivity.this.getString(R.string.url_webservice) + RequestFilterActivity.this.getString(R.string.url_status_request) +
					id_taxi + "/" + RequestFilterActivity.this.getString(R.string.form_id_status_request));
				
				HttpResponse rp = httpclient.execute(post);
			
				if (rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
					this.result = EntityUtils.toString(rp.getEntity());
				
				jsonResult = getJsonResult(this.result, "result");
				
			} catch (Exception e) {
				Toast.makeText(RequestFilterActivity.this, RequestFilterActivity.this.getString(R.string.login_error_connection), Toast.LENGTH_SHORT).show();
			}
    	}
		return jsonResult;
    }
    
	//Retorna um resultado a partir de um campo específico
	private String getJsonResult(String response, String option) throws JSONException {
		jObject = new JSONObject(response);
		return jObject.getString(option);
	}
    
	@Override
	public void onBackPressed() {
		RequestFilterActivity.this.finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		//RequestFilterActivity.this.finish();
	}
}