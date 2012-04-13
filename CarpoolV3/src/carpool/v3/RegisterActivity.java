package carpool.v3;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import carpool.webcom.CPRequest;
import carpool.webcom.CPResponse;
import carpool.webcom.CPSession;

public class RegisterActivity extends Activity {
	EditText unameText, upwdText, upwdAssureText;
	Button submitButton, resetButton;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_view);
		submitButton = (Button) findViewById(R.id.register_submit);
		resetButton = (Button) findViewById(R.id.register_reset);
		unameText = (EditText) findViewById(R.id.register_username);
		upwdText = (EditText) findViewById(R.id.register_password);
		upwdAssureText = (EditText) findViewById(R.id.register_pwdAssure);


		submitButton.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String username = unameText.getText().toString();
				String password = upwdText.getText().toString();
				String pwdAssure = upwdAssureText.getText().toString();
				if (username.length() < 1) {
					Toast toast = Toast.makeText(RegisterActivity.this,
							" 请填写用户名", Toast.LENGTH_SHORT);
					LinearLayout toastView = (LinearLayout) toast.getView();
					toastView.setOrientation(LinearLayout.HORIZONTAL);
					ImageView imageNotice = new ImageView(
							getApplicationContext());
					imageNotice.setImageResource(R.drawable.tag);
					toastView.addView(imageNotice, 0);
					toast.show();
				} else if (password.length() < 1) {
					Toast toast = Toast.makeText(RegisterActivity.this,
							"请填写密码", Toast.LENGTH_SHORT);
					LinearLayout toastView = (LinearLayout) toast.getView();
					toastView.setOrientation(LinearLayout.HORIZONTAL);
					ImageView imageNotice = new ImageView(
							getApplicationContext());
					imageNotice.setImageResource(R.drawable.tag);
					toastView.addView(imageNotice, 0);
					toast.show();
				} else if (password.compareTo(pwdAssure) != 0) {
					Toast toast = Toast.makeText(RegisterActivity.this,
							"密码与确认密码不匹配", Toast.LENGTH_SHORT);
					LinearLayout toastView = (LinearLayout) toast.getView();
					toastView.setOrientation(LinearLayout.HORIZONTAL);
					ImageView imageNotice = new ImageView(
							getApplicationContext());
					imageNotice.setImageResource(R.drawable.tag);
					toastView.addView(imageNotice, 0);
					toast.show();
				} else {
					CPSession.user.setUsername(username);
					CPSession.user.setPassword(password);
					Map<String, String> args = new HashMap<String, String>();
					args.put("nickname", username);
					args.put("password", password);
					args.put("isdriver", "false");
					if (CPSession.sina_token != null) {
						args.put("accessid", CPSession.sina_token.getToken());
						args.put("accesskey", Long
								.toString(CPSession.sina_token.getExpiresIn()));
						args.put("accessscrt", CPSession.sina_token.getSecret());
						args.put("oauthtype", "SINA");
					}
					CPRequest request = new CPRequest("register", args);
					CPResponse response = request.request();
					System.out.println(response.getJson().toString());
					Toast.makeText(RegisterActivity.this, "注册成功",
							Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					intent.setClass(RegisterActivity.this, HomeActivity.class);
					startActivity(intent);
					RegisterActivity.this.finish();

				}
			}
		});
		resetButton.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				unameText.setText("");
				upwdText.setText("");
				upwdAssureText.setText("");

			}
		});
	}

}
