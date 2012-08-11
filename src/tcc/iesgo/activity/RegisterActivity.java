package tcc.iesgo.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import tcc.iesgo.activity.R;
import tcc.iesgo.persistence.SQLiteAdapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class RegisterActivity extends Activity {
/*
	Button btnLogin;
	ImageButton btnHelp;
	EditText inputUsuario;
	EditText inputEmail;
	EditText inputCod;
	TextView textUsuario;
	TextView textEmail;
	TextView textCod;
	TextView textLang;
	Integer listNum = 0;
	TextView registerErrorMsg;
	SQLiteAdapter mySQLiteAdapter;
	HttpClient httpclient = new DefaultHttpClient();
	ProgressDialog progressDialog;
	Spinner idiomas;
	ArrayAdapter<CharSequence> adapter;
	ArrayAdapter<CharSequence> adapter2;

	String lang = "pt";
	String resultCod = "0";

	public final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
			"[a-zA-Z0-9+._%-+]{1,256}" + "@"
			+ "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" + "(" + "."
			+ "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" + ")+");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.registrar);

		idiomas = (Spinner) findViewById(R.id.idiomas);
		inputUsuario = (EditText) findViewById(R.id.loginUsuario);
		inputEmail = (EditText) findViewById(R.id.emailUsuario);
		inputCod = (EditText) findViewById(R.id.codVerificador);
		textUsuario = (TextView) findViewById(R.id.TextView2);
		textEmail = (TextView) findViewById(R.id.TextView4);
		textCod = (TextView) findViewById(R.id.TextView5);
		textLang = (TextView) findViewById(R.id.TextView3);
		
		adapter = ArrayAdapter.createFromResource(
				this, R.array.spinner_array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		idiomas.setAdapter(adapter);
		if (Locale.getDefault().toString().equals("pt_BR"))
			idiomas.setSelection(0);
		else if (Locale.getDefault().toString().equals("en_US"))
			idiomas.setSelection(1);
		else if (Locale.getDefault().toString().equals("es_ES"))
			idiomas.setSelection(2);

		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnHelp = (ImageButton) findViewById(R.id.help);
		registerErrorMsg = (TextView) findViewById(R.id.login_error);
		
		
		btnHelp.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(),
						HelpActivity.class);
					startActivity(i);

				
			}
		});

	
		btnLogin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				if (inputUsuario.getText().toString().equals(""))
					Toast.makeText(getApplicationContext(), getString(R.string.registrar_error_campo_nome), Toast.LENGTH_SHORT).show();
				else if (inputEmail.getText().toString().equals(""))
					Toast.makeText(getApplicationContext(), getString(R.string.registrar_error_campo_email), Toast.LENGTH_SHORT).show();
				else if(!checkEmail(inputEmail.getText().toString()))
					Toast.makeText(getApplicationContext(), getString(R.string.registrar_error_campo_email_invalid), Toast.LENGTH_SHORT).show();
				else if(inputCod.getText().toString().equals(""))
					Toast.makeText(getApplicationContext(), getString(R.string.registrar_error_campo_cod_invalid), Toast.LENGTH_SHORT).show();
				else
					register(inputUsuario.getText().toString(), inputEmail.getText().toString(), lang, inputCod.getText().toString());
			}
		});
		
		idiomas.setOnItemSelectedListener(new OnItemSelectedListener() {
			  @Override
			  public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				  lang = parentView.getItemAtPosition(position).toString();
				  Locale appLoc = null;
			
				  if(lang.equals("Português") || lang.equals("Portuguese") || lang.equals("Portugués"))
				    	appLoc = new Locale("pt_BR");
				  else   if(lang.equals("English") || lang.equals("Inglês") || lang.equals("Inglés"))
				    	appLoc = new Locale("en_US");
				  else if(lang.equals("Espanhol") || lang.equals("Spanish") || lang.equals("Español"))
				    	appLoc = new Locale("es_ES");
				  else 
					  	appLoc = new Locale(Locale.getDefault().toString());
				  
				  lang = appLoc.toString();
				  
				    if (!Locale.getDefault().toString().equals(appLoc.toString()) && listNum > 0) {
				    	Locale.setDefault(appLoc);
				    	Configuration appConfig = new Configuration();
				    	appConfig.locale = appLoc;
				    	getBaseContext().getResources().updateConfiguration(appConfig,
						getBaseContext().getResources().getDisplayMetrics());
				    	btnLogin.setText(R.string.registrar_button);
				    	textUsuario.setText(R.string.registrar_usuario);
					    textEmail.setText(R.string.registrar_email);
					    textCod.setText(R.string.registrar_cod_ver);
					    textLang.setText(R.string.registrar_lang);
					
						adapter2 = ArrayAdapter.createFromResource(
								parentView.getContext(), R.array.spinner_array,
								android.R.layout.simple_spinner_item);
						adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						idiomas.setAdapter(adapter2);
						idiomas.setSelection(position); // needed
						
				    }
				    listNum++;
		      }
		   
			  @Override
		      public void onNothingSelected(AdapterView<?> arg0)
		      {
		        // TODO Auto-generated method stub
		      }
		});
		
	}
		
	
	public void register(final String myName, final String email,
			final String lang, final String codVer) {
		
		progressDialog = ProgressDialog.show(RegisterActivity.this,
				getString(R.string.registrar_pd_title),
				getString(R.string.registrar_pd_content));
		new Thread() {
			public void run() {
				try {

				

					// Autentica como admin
					HttpPost httppost = new HttpPost(
							getString(R.string.url_ecotrans_autenticacao));
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
								2);
					nameValuePairs.add(new BasicNameValuePair("name",
								getString(R.string.login_name)));
					nameValuePairs.add(new BasicNameValuePair("pass",
							getString(R.string.login_pass)));
					nameValuePairs.add(new BasicNameValuePair("form_id",
								getString(R.string.register_form_id_login)));
					try {
							// Add your data
							httppost.setEntity(new UrlEncodedFormEntity(
										nameValuePairs));
			
							// Execute HTTP Post Request
							// HttpResponse response = httpclient.execute(httppost);
							httpclient.execute(httppost);
					} catch (IOException e) {
						registerErrorMsg.setText(getString(R.string.registrar_error_sistema));					}
					// Fim autentica como admin
								
					// Verifica o código		
								
					try {
						HttpPost post = new HttpPost(getString(R.string.url_ecotrans)
								+ getString(R.string.url_cod_verificador)
								+ codVer);
						HttpResponse rp = httpclient.execute(post);
						if (rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
								resultCod = EntityUtils.toString(rp.getEntity());
						}catch (Exception e) {
							registerErrorMsg
								.setText(getString(R.string.registrar_error_sistema));
						}
								
						if(resultCod.equals("1")){
			
							// Cria usuario					
			
							Random password = new Random();
							String password_str = password.toString();
				
							HttpPost post = new HttpPost(
									getString(R.string.url_ecotrans_criar_usuario));
							List<NameValuePair> userValuePairs = new ArrayList<NameValuePair>(
											2);
							userValuePairs.add(new BasicNameValuePair("name", myName));
							userValuePairs.add(new BasicNameValuePair("pass", password_str));
							userValuePairs.add(new BasicNameValuePair("email",  email));
							userValuePairs.add(new BasicNameValuePair("form_id",
									getString(R.string.register_form_id_new)));
			
							try {
								// Salva usuário na nuvem
								post.setEntity(new UrlEncodedFormEntity(userValuePairs));
								HttpResponse rp = httpclient.execute(post);
								String user = EntityUtils.toString(rp.getEntity());
								// txtUsuario.setText(user);
								// Salva usuário no DB do Aplicativo
								mySQLiteAdapter = new SQLiteAdapter(
										getApplicationContext());
								mySQLiteAdapter.openToWrite();
								mySQLiteAdapter.insert(user, password_str, lang);
			
								mySQLiteAdapter.close();
				
								gotohome();
										
									
							} catch (IOException e) {
								registerErrorMsg
								.setText(getString(R.string.registrar_error_sistema));							}
					}else{
						registerErrorMsg
						.setText(getString(R.string.registrar_error_campo_cod_invalid));
					}
			
				} catch (Exception e) {
					e.printStackTrace();
				}
				progressDialog.dismiss();
			}
		}.start();

	}
	
	public void gotohome() {
		Intent i = new Intent(getApplicationContext(),
			MainTabActivity.class);
		startActivity(i);
	}
	
    private boolean checkEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }
    
	@Override
	public void onBackPressed() {
		RegisterActivity.this.finish();
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

*/
}
