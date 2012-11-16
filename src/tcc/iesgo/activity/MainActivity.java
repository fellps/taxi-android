package tcc.iesgo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends Activity {
	
	Button btnLogin;
	Button btnRegisterClient;
	Button btnRegisterTaxi;
	Button btnExit;
	ImageButton btnHelp;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main); //Layout da Activity
		
		btnLogin = (Button) findViewById(R.id.bt_login);
		btnRegisterClient = (Button) findViewById(R.id.bt_register_client);
		btnRegisterTaxi = (Button) findViewById(R.id.bt_register_taxi);
		btnExit = (Button) findViewById(R.id.bt_exit);
		btnHelp = (ImageButton) findViewById(R.id.ib_help);
		
		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(), LoginActivity.class);
				startActivity(i);
			}
		});
		
		btnRegisterClient.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(), RegisterClientActivity.class);
				startActivity(i);
			}
		});
		
		btnRegisterTaxi.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(), RegisterTaxiActivity.class);
				startActivity(i);
			}
		});
		
		btnExit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				MainActivity.this.finish();
			}
		});
		
		btnHelp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(), HelpActivity.class);
				startActivity(i);
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		MainActivity.this.finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		MainActivity.this.finish();
	}
}