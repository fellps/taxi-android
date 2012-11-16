package tcc.iesgo.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import tcc.iesgo.activity.R;
import tcc.iesgo.http.connection.HttpClientFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class RegisterContinueActivity extends Activity {
	

	Button btnNext;
	ImageButton btnHelp;
	
	private String email, password;
	
	HttpClient httpclient = HttpClientFactory.getThreadSafeClient();
	
	private JSONObject jObject;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.register_continue); //Layout da Activity
		
		// Recupera os extras da intent anterior
		Bundle extras = getIntent().getExtras();
		
		email = extras.getString("email");
		password = extras.getString("pass");

		//Instância dos componentes do layout
		btnNext = (Button) findViewById(R.id.bt_next);
		btnHelp = (ImageButton) findViewById(R.id.ib_help);
		
		//Botão de ajuda
		btnHelp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(), HelpActivity.class);
				startActivity(i);
			}
		});
	
		//Botão de registro
		btnNext.setOnClickListener(new View.OnClickListener() {
			//Verifica se todos os campos foram preenchidos corretamente
			@Override
			public void onClick(View view) {
				gotoMap(email, password);
			}
		});
	}
	
	private void gotoHome() {
		Intent i = new Intent(RegisterContinueActivity.this, MainActivity.class);
		startActivity(i);
	}
	
	private void gotoMap(String email, String password) {

        try {
	        // Autentica o usuário na nuvem
			HttpPost httppost = new HttpPost(getString(R.string.url_webservice) + getString(R.string.url_authentication));
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("name", email));
	        nameValuePairs.add(new BasicNameValuePair("pass", password));
	        nameValuePairs.add(new BasicNameValuePair("form_id", getString(R.string.form_id)));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				String status = getJsonResult(EntityUtils.toString(response.getEntity()), "result");
				if(status.equals("1")){
			        Intent i = new Intent(RegisterContinueActivity.this, MainTabActivity.class);
					startActivity(i);
				} else {
            		Toast.makeText(RegisterContinueActivity.this, getString(R.string.login_error_authentication), Toast.LENGTH_SHORT).show();
				}
			}
		} catch (Exception e) {
    		Toast.makeText(RegisterContinueActivity.this, getString(R.string.register_error_off), Toast.LENGTH_SHORT).show();
		}
	}
	
	private String getJsonResult(String response, String option) throws JSONException{
		jObject = new JSONObject(response);
		return jObject.getString(option);
	}

	@Override
	public void onBackPressed() {
		gotoHome();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		RegisterContinueActivity.this.finish();
	}
}