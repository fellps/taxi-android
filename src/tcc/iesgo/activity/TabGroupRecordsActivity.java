package tcc.iesgo.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class TabGroupRecordsActivity extends TabGroupActivity{
	ProgressDialog progressDialog;
	Handler mHandler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		progressDialog = ProgressDialog.show(getParent(), getString(R.string.pd_title),
				getString(R.string.pd_content_loading));
		progressDialog.setIcon(R.drawable.progress_dialog);

		new Thread(new Runnable() {
			@Override
			public void run() {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						startChildActivity("RecordsActivity", new Intent(getParent(), RecordsActivity.class));
						progressDialog.dismiss();
					}
				});
			}
		}).start();
	}
}
