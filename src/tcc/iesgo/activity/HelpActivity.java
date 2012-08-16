package tcc.iesgo.activity;

import tcc.iesgo.activity.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

public class HelpActivity extends Activity {
	
	WebView mWebView;
   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.help);

       mWebView = (WebView) findViewById(R.id.webview_help);
       mWebView.getSettings().setJavaScriptEnabled(true);
       mWebView.loadUrl(getString(R.string.url_help));
   }

   // Chamado quando a activity ja nao e visivel para o usuario
   protected void onStop() {
      super.onStop();	
      Intent i = new Intent(getApplicationContext(), MainActivity.class);
      startActivity(i);
   }
}