package tcc.iesgo.activity;

import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import tcc.iesgo.activity.RegisterActivity;
import tcc.iesgo.http.connection.HttpClientFactory;
import tcc.iesgo.persistence.SQLiteAdapter;

public class LoginActivity extends Activity {
	
	EditText inputEmail;
	EditText inputPassword;
	TextView registerErrorMsg;
	CheckBox cbRemember;
	Button btnLogin;
	ImageButton btnHelp;
	
	ProgressDialog progressDialog;
	Handler mHandler = new Handler();
	
	HttpClient httpclient = HttpClientFactory.getThreadSafeClient();
	
	RegisterActivity register = new RegisterActivity();
	
	private JSONObject jObject;
	
	private String resultHttp = "0";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.login); //Layout da Activity
		
		inputEmail = (EditText) findViewById(R.id.et_email);
		inputEmail.setHint(getString(R.string.login_email_description));
		inputPassword = (EditText) findViewById(R.id.et_pw);
		inputPassword.setHint(getString(R.string.register_pw));
		cbRemember = (CheckBox) findViewById(R.id.cb_remember);
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
				getString(R.string.pd_title), getString(R.string.pd_content_login));
		progressDialog.setIcon(R.drawable.progress_dialog);
		
		new Thread(new Runnable() {
			@Override
			public void run() {

						try {
							
							HttpPost httppost = new HttpPost(getString(R.string.url_webservice) + getString(R.string.url_login_user)
									+ email + "/" + password + "/" + getString(R.string.form_id_login));

							HttpResponse rp = httpclient.execute(httppost);
							
							if (rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
								resultHttp = EntityUtils.toString(rp.getEntity());

							String result = getJsonResult(resultHttp, "result"); //Json
							
							if(result.equals("1")){

				            	if(cbRemember.isChecked()){
				            		saveCredentials(email, password);
				            	}
				            	gotoMap(email, password);

							} else {
			    				mHandler.post(new Runnable() {
			    					@Override
			    					public void run() {
					            		Toast.makeText(LoginActivity.this, getString(R.string.login_error_authentication), Toast.LENGTH_SHORT).show();
							    	    registerErrorMsg.setText(getString(R.string.login_error_authentication));
			    					}
			    				});
							}
						} catch (Exception e) {
		    				mHandler.post(new Runnable() {
		    					@Override
		    					public void run() {
				            		Toast.makeText(LoginActivity.this, getString(R.string.register_error_off), Toast.LENGTH_SHORT).show();
						    	    registerErrorMsg.setText(getString(R.string.register_error_off));
		    					}
		    				});
						}
						progressDialog.dismiss();
			}
		}).start();
	}
	
	private void saveCredentials(String email, String password){
		SQLiteAdapter mySQLiteAdapter;
		
		mySQLiteAdapter = new SQLiteAdapter(getApplicationContext());
		
        mySQLiteAdapter.openToRead(); //Abre para leitura
        
        Cursor cursor = mySQLiteAdapter.queueAll(); //Dados salvos no banco
        startManagingCursor(cursor);
        
        if(cursor.getCount() == 0){ //Caso seja encontrado algum registro
			mySQLiteAdapter.openToWrite();
			mySQLiteAdapter.insert(email, password, Locale.getDefault().toString()); //Salva os dados
        }
		mySQLiteAdapter.close();
	}
	
	private void gotoHome() {
		Intent i = new Intent(LoginActivity.this, MainActivity.class);
		startActivity(i);
	}
	
	private void gotoMap(String email, String password) {
		Intent i = new Intent(LoginActivity.this, MainTabActivity.class);
		i.putExtra("email", email);
		i.putExtra("pass", password);
		startActivity(i);
	}
	
	private String getJsonResult(String response, String option) throws JSONException{
		jObject = new JSONObject(response);
		return jObject.getString(option);
	}
	
	@Override
	public void onBackPressed() {
		LoginActivity.this.finish();
		gotoHome();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		LoginActivity.this.finish();
	}
}