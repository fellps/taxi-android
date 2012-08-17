package tcc.iesgo.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import tcc.iesgo.activity.RegisterActivity;

public class LoginActivity extends Activity {
	
	EditText inputEmail;
	EditText inputPassword;
	TextView registerErrorMsg;
	Button btnLogin;
	ImageButton btnHelp;
	
	ProgressDialog progressDialog;
	Handler mHandler = new Handler();
	
	HttpClient httpclient = new DefaultHttpClient();
	
	RegisterActivity register = new RegisterActivity();
	
	private String resultHttp = "0";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.login); //Layout da Activity
		
		inputEmail = (EditText) findViewById(R.id.et_email);
		inputEmail.setHint(getString(R.string.login_email_description));
		inputPassword = (EditText) findViewById(R.id.et_pw);
		inputPassword.setHint(getString(R.string.register_pw));
		registerErrorMsg = (TextView) findViewById(R.id.tv_error);
		btnLogin = (Button) findViewById(R.id.bt_login);
		btnHelp = (ImageButton) findViewById(R.id.ib_help);
		
		btnLogin.setOnClickListener(new View.OnClickListener() {
			//Verifica se todos os campos foram preenchidos corretamente
			@Override
			public void onClick(View view) {
				if(!register.checkEmail(inputEmail.getText().toString()))
					Toast.makeText(getApplicationContext(), getString(R.string.register_error_email), Toast.LENGTH_SHORT).show();
				else if(inputPassword.getText().toString().length() < 5)
					Toast.makeText(getApplicationContext(), getString(R.string.register_error_pw), Toast.LENGTH_SHORT).show();
				else
					login(inputEmail.getText().toString(), inputPassword.getText().toString());
			}
		});
		
		//Botão de ajuda
		btnHelp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(), HelpActivity.class);
				startActivity(i);
			}
		});
	}
	
	private void login(final String email, final String password){
		progressDialog = ProgressDialog.show(LoginActivity.this, 
				getString(R.string.pd_title), getString(R.string.pd_content));
		
		new Thread(new Runnable() {
			@Override
			public void run() {

				mHandler.post(new Runnable() {
					@Override
					public void run() {
						try {
							// Autentica como admin
							HttpPost httppost = new HttpPost(getString(R.string.url_webservice) + getString(R.string.url_authentication));
							List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
							nameValuePairs.add(new BasicNameValuePair("name", email));
							nameValuePairs.add(new BasicNameValuePair("pass", password));
							nameValuePairs.add(new BasicNameValuePair("form_id", getString(R.string.form_id_login)));
		
							//Executa a requisição
							httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
							httpclient.execute(httppost);
							HttpResponse response = httpclient.execute(httppost);
		
				            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){ //Caso os dados estejam corretos, redirecona p/ o mapa.
				            	resultHttp = EntityUtils.toString(response.getEntity());
				            	Log.i("###########", resultHttp);
				            	if (resultHttp.equals("1")){
				            		gotoMap();
				            	} else {
				            		Toast.makeText(LoginActivity.this, getString(R.string.login_error_authentication), Toast.LENGTH_SHORT).show();
						    	    registerErrorMsg.setText(getString(R.string.login_error_authentication));
				            	}
				            } else { //Se não, exibe erro
				            	Toast.makeText(LoginActivity.this, getString(R.string.login_error_connection), Toast.LENGTH_SHORT).show();
					    	    registerErrorMsg.setText(getString(R.string.login_error_connection));
				            }
		
						} catch (IOException e) {
							Toast.makeText(LoginActivity.this, getString(R.string.login_error_connection), Toast.LENGTH_SHORT).show();
				    	    registerErrorMsg.setText(getString(R.string.login_error_connection));		
						}
						progressDialog.dismiss();
					}
				});
			}
		}).start();
	}
	
	private void gotoHome() {
		Intent i = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(i);
	}
	
	private void gotoMap() {
		Intent i = new Intent(getApplicationContext(), ClientMapActivity.class);
		startActivity(i);
	}
	
	@Override
	public void onBackPressed() {
		LoginActivity.this.finish();
		gotoHome();
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
}