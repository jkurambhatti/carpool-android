package carpool.v3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.renren.api.connect.android.Renren;
import com.renren.api.connect.android.exception.RenrenAuthError;
import com.renren.api.connect.android.status.StatusSetRequestParam;
import com.renren.api.connect.android.view.RenrenAuthListener;

public class RenrenActivity extends Activity {

	private static final String APP_ID = "182965";
	// 应用的API Key
	private static final String API_KEY = "abef8621eb034f9ea6068c13fa0433b4";
	// 应用的Secret Key
	private static final String SECRET_KEY = "b294491f90074acb84c608363ce22c36";

	private Renren renren;
	private Handler handler;
	final RenrenAuthListener listener = new RenrenAuthListener() {
		@Override
		public void onComplete(Bundle values) {
			Log.d("test", values.toString());
		}

		@Override
		public void onRenrenAuthError(RenrenAuthError renrenAuthError) {
			handler.post(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(RenrenActivity.this, "认证失败",
							Toast.LENGTH_SHORT).show();
				}
			});
		}

		@Override
		public void onCancelLogin() {
		}

		@Override
		public void onCancelAuth(Bundle values) {
		}

	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.renren_view);
		renren = new Renren(API_KEY, SECRET_KEY, APP_ID, RenrenActivity.this);
		Button pubBtn = (Button) findViewById(R.id.renren_btn_send);
		pubBtn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				renren.publishStatus(RenrenActivity.this,
						new StatusSetRequestParam(""));
			}

		});
		handler = new Handler();
		renren.authorize(RenrenActivity.this, listener);
	}

}