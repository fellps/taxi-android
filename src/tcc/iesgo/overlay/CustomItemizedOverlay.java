package tcc.iesgo.overlay;

import tcc.iesgo.activity.R;
import tcc.iesgo.http.connection.HttpClientFactory;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;

import android.widget.Toast;

import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
 
public class CustomItemizedOverlay extends BalloonItemizedOverlay<OverlayItem> {
 
    private final ArrayList<OverlayItem> mapOverlays = new ArrayList<OverlayItem>();
    
    ProgressDialog progressDialog;
    Handler mHandler = new Handler();
    
    JSONObject jObject;
 
    private Context context;
	MapView mapView;
	
	private String result, resultStatus;
	private String taxi_id;
	
	HttpClient httpclient = HttpClientFactory.getThreadSafeClient();
 
    public CustomItemizedOverlay(Drawable defaultMarker, MapView mapView) {
        super(boundCenter(defaultMarker), mapView);
        this.mapView = mapView;
    }
 
    public CustomItemizedOverlay(Drawable defaultMarker, Context context, MapView mapView) {
        this(defaultMarker, mapView);
        this.mapView = mapView;
        this.context = context;
    }
    
    
    private class RequestTaxi extends AsyncTask<String, String, String> {
    	
        @Override
        protected void onPreExecute() {
        	progressDialog = ProgressDialog.show(context, context.getString(R.string.pd_title), context.getString(R.string.cm_pd_request_msg));
        	progressDialog.setIcon(R.drawable.progress_dialog);
        }

		@Override
		protected String doInBackground(String... id_taxis) {
			for(final String id_taxi : id_taxis){
				taxi_id = id_taxi;
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						try {
							String status = requestTaxi(id_taxi);
							if(status.equals("0")){ //se status = 2 (pendente)
					    		statusRequest sr = new statusRequest(context);	
					    		sr.execute(0, 0, 90);
							} else {
								final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
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
							Toast.makeText(context, context.getString(R.string.login_error_connection), Toast.LENGTH_SHORT).show();
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
	    				resultStatus = requestTaxi(taxi_id);
	    			}
					
					if(resultStatus.equals("0")){ //se status = 2 (pendente)
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
	        
    		if(resultStatus.equals("0")){ //2
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
				HttpPost post = new HttpPost(context.getString(R.string.url_webservice) + context.getString(R.string.url_request_taxi) + 
					 id_taxi + "/" + context.getString(R.string.form_id_request_taxi));
				
				HttpResponse rp = httpclient.execute(post);

				if (rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
					this.result = EntityUtils.toString(rp.getEntity());
				
				jsonResult = getJsonResult(this.result, "result");
				
			} catch (Exception e) {
				Toast.makeText(context, context.getString(R.string.login_error_connection), Toast.LENGTH_SHORT).show();
			}
    	}
		return jsonResult;
    }
    /*
    private String statusRequest(String id_taxi){
    	String jsonResult = "";
    	
    	if(!id_taxi.equals("")){
			try {
				HttpPost post = new HttpPost(context.getString(R.string.url_webservice) + context.getString(R.string.url_request_taxi) 
					+ context.getString(R.string.form_id_request_taxi));
				
				HttpResponse rp = httpclient.execute(post);
			
				if (rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
					this.result = EntityUtils.toString(rp.getEntity());
				
				jsonResult = getJsonResult(this.result, "result");
				
			} catch (Exception e) {
				Toast.makeText(context, context.getString(R.string.login_error_connection), Toast.LENGTH_SHORT).show();
			}
    	}
		return jsonResult;
    }
    */
	//Retorna um resultado a partir de um campo específico
	private String getJsonResult(String response, String option) throws JSONException {
		jObject = new JSONObject(response);
		return jObject.getString(option);
	}
    
	@Override
	protected boolean onBalloonTap(int index, OverlayItem item) {
		final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		
		dialog.setTitle(item.getTitle());
		dialog.setMessage(R.string.balloon_call_taxi); //item.getSnippet()
		dialog.setIcon(android.R.drawable.ic_menu_myplaces);
		dialog.setCancelable(false);

		dialog.setPositiveButton(R.string.ad_button_yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				String[] id_taxi = getFocus().getTitle().split(" - ");
				String[] taxi_id = id_taxi[0].split("RG: ");
				RequestTaxi request = new RequestTaxi();
				request.execute(taxi_id[1]);
				hideAllBalloons();
			}
		});
		dialog.setNegativeButton(R.string.ad_button_no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		dialog.show();
		return true;
	}
 
    @Override
    protected OverlayItem createItem(int i) {
        return mapOverlays.get(i);
    }
 
    @Override
    public int size() {
        return mapOverlays.size();
    }
 
    public void addOverlay(OverlayItem overlay) {
        mapOverlays.add(overlay);
        this.populate();
    }
    
    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        if(!shadow) {
            super.draw(canvas, mapView, false);
        }
    }
}