package carpool.v3;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import carpool.webcom.CPActivity;
import carpool.webcom.CPRequest;
import carpool.webcom.CPResponse;
import carpool.webcom.CPSession;
import carpool.webcom.MessageHandler;

public class HomeActivity extends CPActivity {
	public MessageHandler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_view);
		TextView tv = (TextView) findViewById(R.id.home_textView2);
		tv.setText(CPSession.user.getUsername());
		RelativeLayout r1 = (RelativeLayout) findViewById(R.id.rl_profile);
		ImageView portrait = (ImageView) findViewById(R.id.home_headpic_iv);
		Looper looper = Looper.myLooper();
		handler = new MessageHandler(looper, this);
		CPSession.socket.activity = this;
		if (CPSession.user.getPortrait() != "null") {
			portrait.setImageBitmap(returnBitmap(CPSession.user.getPortrait()));
		}
		// Map<String, String> args = new HashMap<String, String>();
		// args.put("nickname", CPSession.user.getUsername());
		// args.put("type", "SINA");
		// CPRequest request = new CPRequest("getOauth", args);
		// CPResponse response = request.request();
		// try {
		// String errMsg = response.getJson().getString("errorMsg");
		// if (errMsg == "null") {
		// String token = response.getJson().getString("accessId");
		// String expiresin = response.getJson().getString("accessKey");
		// String secret = response.getJson().getString("accessScrt");
		// CPSession.sina_token = new AccessToken(token, secret);
		// CPSession.sina_token.setExpiresIn(expiresin);
		// }
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		r1.setOnClickListener(new RelativeLayout.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(HomeActivity.this, "个人资料修改", Toast.LENGTH_SHORT)
						.show();
				Intent intent = new Intent();
				intent.setClass(HomeActivity.this, ProfileActivity.class);
				startActivity(intent);

			}
		});
		RelativeLayout r2 = (RelativeLayout) findViewById(R.id.rl_pinche);
		r2.setOnClickListener(new RelativeLayout.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(HomeActivity.this, "拼车服务", Toast.LENGTH_SHORT)
						.show();
				Intent intent = new Intent();
				intent.setClass(HomeActivity.this, PincheActivity.class);
				startActivity(intent);
			}
		});
		RelativeLayout r3 = (RelativeLayout) findViewById(R.id.rl_gis);
		r3.setOnClickListener(new RelativeLayout.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(HomeActivity.this, "地理信息服务", Toast.LENGTH_SHORT)
						.show();
				Intent intent = new Intent();
				intent.setClass(HomeActivity.this, GISActivity.class);
				startActivity(intent);
			}
		});
		RelativeLayout r4 = (RelativeLayout) findViewById(R.id.rl_osn);
		r4.setOnClickListener(new RelativeLayout.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(HomeActivity.this, "社交网络扩展", Toast.LENGTH_SHORT)
						.show();
				Intent intent = new Intent();
				intent.setClass(HomeActivity.this, SinaWeiboActivity.class);
				startActivity(intent);

			}
		});
		RelativeLayout r5 = (RelativeLayout) findViewById(R.id.rl_logout);
		r5.setOnClickListener(new RelativeLayout.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(HomeActivity.this, "谢谢使用Carpool",
						Toast.LENGTH_SHORT).show();
				HomeActivity.this.finish();

			}
		});

	}

	public Bitmap returnBitmap(String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try {
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	@Override
	public void append(String str) {
		// TODO Auto-generated method stub

		Message msg = Message.obtain();

		if (str.equals("[0]GETON")) {
			msg.obj = "[0]GETON";

		} else if (str.equals("[0]GETOFF")) {
			msg.obj = "[0]GETOFF";
		}
		handler.sendMessage(msg);

	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
