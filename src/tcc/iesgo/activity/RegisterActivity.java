package tcc.iesgo.activity;

import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import tcc.iesgo.activity.R;
import tcc.iesgo.persistence.SQLiteAdapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
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
	
	Spinner language;
	EditText inputName;
	EditText inputEmail;
	EditText inputPhone;
	EditText inputPassword;
	TextView registerErrorMsg;
	Button btnRegister;
	ImageButton btnHelp;
	
	TextView textLang;
	TextView textName;
	TextView textEmail;
	TextView textPhone;
	TextView textPassword;
	
	SQLiteAdapter mySQLiteAdapter;
	HttpClient httpclient = new DefaultHttpClient();
	
	Handler mHandler = new Handler();
	ProgressDialog progressDialog;
	
	ArrayAdapter<CharSequence> adapter;
	ArrayAdapter<CharSequence> adapter2;
	
	private Integer listNum = 0;
	private String lang = "pt";
	private JSONObject jObject;

	//Verifica se o e-mail informado é válido
	public final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
			  "[a-zA-Z0-9+._%-+]{1,256}" + "@"
			+ "[a-zA-Z0-9][a-zA-Z0-9-]{1,64}" + "(" + "."
			+ "[a-zA-Z0-9][a-zA-Z0-9-]{1,25}" + ")+");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.register); //Layout da Activity

		//Instância dos componentes do layout
		language = (Spinner) findViewById(R.id.sp_lang);
		inputName = (EditText) findViewById(R.id.et_name);
		inputName.setHint(getString(R.string.register_name_description));
		inputEmail = (EditText) findViewById(R.id.et_email);
		inputEmail.setHint(getString(R.string.register_email_description));
		inputPhone = (EditText) findViewById(R.id.et_phone);
		inputPhone.setHint(getString(R.string.register_phone_description));
		inputPassword = (EditText) findViewById(R.id.et_pw);
		btnRegister = (Button) findViewById(R.id.bt_register);
		btnHelp = (ImageButton) findViewById(R.id.ib_help);
		registerErrorMsg = (TextView) findViewById(R.id.tv_error);
		textLang = (TextView) findViewById(R.id.tv_lang);
		textName = (TextView) findViewById(R.id.tv_name);
		textEmail = (TextView) findViewById(R.id.tv_email);
		textPhone = (TextView) findViewById(R.id.tv_phone);
		textPassword = (TextView) findViewById(R.id.tv_pw);
		
		//Inicializa o Spinner
		adapter = ArrayAdapter.createFromResource(this, R.array.spinner_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		language.setAdapter(adapter);
		
		if (Locale.getDefault().toString().equals("pt_BR"))
			language.setSelection(0);
		else if (Locale.getDefault().toString().equals("en_US"))
			language.setSelection(1);
		else if (Locale.getDefault().toString().equals("es_ES"))
			language.setSelection(2);
		
		//Botão de ajuda
		btnHelp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(), HelpActivity.class);
				startActivity(i);
			}
		});
	
		//Botão de registro
		btnRegister.setOnClickListener(new View.OnClickListener() {
			//Verifica se todos os campos foram preenchidos corretamente
			@Override
			public void onClick(View view) {
				String[] data = {inputName.getText().toString(), inputEmail.getText().toString(), 
						 inputPhone.getText().toString(), inputPassword.getText().toString(), lang};
				
				if (inputName.getText().toString().length() <= 3)
					Toast.makeText(getApplicationContext(), getString(R.string.register_error_name), Toast.LENGTH_SHORT).show();
				else if(!checkEmail(inputEmail.getText().toString()))
					Toast.makeText(getApplicationContext(), getString(R.string.register_error_email), Toast.LENGTH_SHORT).show();
				else if(inputPhone.getText().toString().length() < 8)
					Toast.makeText(getApplicationContext(), getString(R.string.register_error_phone), Toast.LENGTH_SHORT).show();
				else if(inputPassword.getText().toString().length() < 5)
					Toast.makeText(getApplicationContext(), getString(R.string.register_error_pw), Toast.LENGTH_SHORT).show();
				else
					register(data);
			}
		});
		
		//Caso o idioma seja trocado
		language.setOnItemSelectedListener(new OnItemSelectedListener(){
			  @Override
			  public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				  
				  lang = parentView.getItemAtPosition(position).toString();
				  Locale appLoc = null;
			
				  //Troca o idioma local do aplicativo pela selecionada
				  if(lang.equals("Português") || lang.equals("Portuguese") || lang.equals("Portugués"))
				    	appLoc = new Locale("pt_BR");
				  else   if(lang.equals("Inglês") || lang.equals("English") || lang.equals("Inglés"))
				    	appLoc = new Locale("en_US");
				  else if(lang.equals("Espanhol") || lang.equals("Spanish") || lang.equals("Español"))
				    	appLoc = new Locale("es_ES");
				  else 
					  	appLoc = new Locale(Locale.getDefault().toString());
				  
				  lang = appLoc.toString();
				  
				  //Se o idioma corrente for diferente do selecionado
				  if (!Locale.getDefault().toString().equals(appLoc.toString()) && listNum > 0) {
				    	Locale.setDefault(appLoc);
				    	Configuration appConfig = new Configuration();
				    	appConfig.locale = appLoc;
				    	getBaseContext().getResources().updateConfiguration(appConfig,
						getBaseContext().getResources().getDisplayMetrics());
				    	//Efetua as alterações dos textos, botões etc.
				    	textLang.setText(R.string.register_lang);
				    	textName.setText(R.string.register_name);
					    textEmail.setText(R.string.register_email);
					    textPhone.setText(R.string.register_phone);
					    textPassword.setText(R.string.register_pw);
					    btnRegister.setText(R.string.register_button);
					    
						adapter2 = ArrayAdapter.createFromResource(parentView.getContext(), R.array.spinner_array,	android.R.layout.simple_spinner_item);
						adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						language.setAdapter(adapter2);
						language.setSelection(position); //Obrigatório
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
		
	
	public void register(final String[] data) {
		
		progressDialog = ProgressDialog.show(RegisterActivity.this, 
				getString(R.string.pd_title), getString(R.string.pd_content_register));
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					HttpPost httppost = new HttpPost(getString(R.string.url_webservice) + getString(R.string.url_create_user)
							+ data[0].replace(" ", "%20") + "/" + data[1] + "/" + data[2] + "/" + data[3] + "/" + data[4] + "/" + getString(R.string.form_id_new));

						//Salva usuário na nuvem
						HttpResponse rp = httpclient.execute(httppost);
						
						String response = "0";

						if (rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
							response = EntityUtils.toString(rp.getEntity());

						String result = getJsonResult(response, "result"); //Json

						if(result.equals("1")){
							//Salva usuário no DB do Aplicativo
							mySQLiteAdapter = new SQLiteAdapter(getApplicationContext());
							mySQLiteAdapter.openToWrite();
							mySQLiteAdapter.insert(data[0], data[4], lang);
							mySQLiteAdapter.close();
							
							//Abre o mapa
							gotoMap();
						} else if(result.equals("2")){
							mHandler.post(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(RegisterActivity.this, getString(R.string.register_error_duplicate), Toast.LENGTH_SHORT).show();
									registerErrorMsg.setText(getString(R.string.register_error_duplicate));
								}
							});
						} else {
							mHandler.post(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(RegisterActivity.this, getString(R.string.register_error_param), Toast.LENGTH_SHORT).show();
									registerErrorMsg.setText(getString(R.string.register_error_param));
								}
							});
						}

				} catch (Exception e) {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(getApplicationContext(), getString(R.string.register_error_off), Toast.LENGTH_SHORT).show();
							registerErrorMsg.setText(getString(R.string.register_error_off));
						}
					});
				}
				progressDialog.dismiss();
			}
		}).start();
	}
	
	private void gotoHome() {
		Intent i = new Intent(RegisterActivity.this, MainActivity.class);
		startActivity(i);
	}
	
	private void gotoMap() {
		Intent i = new Intent(RegisterActivity.this, MainTabActivity.class);
		startActivity(i);
	}
	
	private String getJsonResult(String response, String option) throws JSONException{
		jObject = new JSONObject(response);
		return jObject.getString(option);
	}

    public boolean checkEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }
    
	@Override
	public void onBackPressed() {
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