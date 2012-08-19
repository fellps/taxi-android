package tcc.iesgo.activity;

import tcc.iesgo.activity.R;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class RecordsActivity extends Activity {
	
	WebView mWebView;
   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.help);

       mWebView = (WebView) findViewById(R.id.webview_help);
       mWebView.getSettings().setJavaScriptEnabled(true);
       mWebView.loadUrl(getString(R.string.url_help));
   }

}