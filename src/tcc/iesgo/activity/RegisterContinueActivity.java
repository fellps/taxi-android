package tcc.iesgo.activity;

import tcc.iesgo.activity.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class RegisterContinueActivity extends Activity {
	

	Button btnNext;
	ImageButton btnHelp;
	
	private String email, password;

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
		Intent i = new Intent(RegisterContinueActivity.this, MainTabActivity.class);
		i.putExtra("email", email);
		i.putExtra("pass", password);
		startActivity(i);
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