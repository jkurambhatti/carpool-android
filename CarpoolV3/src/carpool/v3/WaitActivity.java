package carpool.v3;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import carpool.ui.PathOverlay;
import carpool.webcom.CPActivity;
import carpool.webcom.CPRequest;
import carpool.webcom.CPResponse;
import carpool.webcom.CPSession;
import carpool.webcom.MessageHandler;

import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class WaitActivity extends CPActivity {
	MapView mapView;
	MapController mc;
	public MessageHandler handler;

	@Override
	protected void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		super.onCreate(icicle);
		setContentView(R.layout.wait_view);
		mapView = (MapView) findViewById(R.id.wait_map);
		mapView.setBuiltInZoomControls(true);
		mc = mapView.getController();
		mc.setZoom(14);
//		PathOverlay pOverlay = new PathOverlay(this, mapView);
//		mapView.getOverlays().clear();
//		mapView.getOverlays().add(pOverlay);
		CPSession.socket.activity = this;
		Looper looper = Looper.myLooper();
		handler = new MessageHandler(looper, this);
		waitingResponseTask wrt = new waitingResponseTask(this);
		wrt.execute();

	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void append(String str) {
		// TODO Auto-generated method stub
		System.out.println(str);
		if (str.equals("[0]MSGRESPONSE")) {
			CPSession.flag = "ASSURE";
			Map<String, String> args = new HashMap<String, String>();
			CPRequest request = new CPRequest("getResponse", args);
			if (CPSession.requestId != 0) {
				args.put("id", String.valueOf(CPSession.requestId));
			}
			CPResponse response = request.request();
			try {
				JSONObject rlt = response.getJson();

				JSONObject jresponse = rlt.getJSONObject("response");
				if (jresponse.getString("backup").equals("agree")) {
					Message msg = Message.obtain();
					msg.obj = "[0]MSGRESPONSE";
					handler.sendMessage(msg);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	class waitingResponseTask extends AsyncTask {
		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			myDialog.dismiss();
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			myDialog = new ProgressDialog(activity);
			myDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			myDialog.setTitle("提示");
			myDialog.setMessage(getResources().getString(R.string.wait_1));
			myDialog.setIcon(R.drawable.login);
			myDialog.setIndeterminate(false);
			myDialog.setCancelable(true);
			myDialog.show();

		}

		@Override
		protected void onProgressUpdate(Object... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		ProgressDialog myDialog;
		CPActivity activity;

		public waitingResponseTask(CPActivity activity) {
			this.activity = activity;
		}

		@Override
		protected Object doInBackground(Object... arg0) {
			// TODO Auto-generated method stub
			boolean flag = true;
			while (flag) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (CPSession.flag.equals("ASSURE")) {
					flag = false;
				}
			}
			return null;
		}

	}

}
