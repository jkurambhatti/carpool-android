package carpool.v3;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import carpool.webcom.CPRequest;
import carpool.webcom.CPResponse;
import carpool.webcom.CPSession;

import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;
import com.weibo.net.WeiboParameters;

public class ProfileActivity extends Activity {
	private EditText et_name, et_phone;
	private RadioGroup rg_gender;
	private RadioButton rb_male, rb_female;
	private String headPic;
	private String gender;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.profile_layout);
		Button SinaBtn = (Button) findViewById(R.id.profile_btn_sina);
		Button RenrenBtn = (Button) findViewById(R.id.profile_btn_renren);
		Button submitBtn = (Button) findViewById(R.id.profile_btn_submit);
		Button resetBtn = (Button) findViewById(R.id.profile_btn_reset);
		et_name = (EditText) findViewById(R.id.profile_et_name);
		et_phone = (EditText) findViewById(R.id.profile_et_phone);
		rg_gender = (RadioGroup) findViewById(R.id.profile_rg_gender);
		rb_male = (RadioButton) findViewById(R.id.rb_male);
		rb_female = (RadioButton) findViewById(R.id.rb_female);

		SinaBtn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Weibo weibo = Weibo.getInstance();
				// weibo.setAccessToken(CPSession.sina_token);
				String url = Weibo.SERVER + "users/show.json";
				WeiboParameters bundle = new WeiboParameters();
				bundle.add("source", CPSession.CONSUMER_KEY);
				bundle.add("uid", CPSession.uid);
				try {
					String rlt = weibo.request(ProfileActivity.this, url,
							bundle, "GET", weibo.getAccessToken());
					JSONObject rlt_json = new JSONObject(rlt);
					String nickname = rlt_json.getString("screen_name");
					et_name.setText(nickname);
					headPic = rlt_json.getString("profile_image_url");
					System.out.println(rlt);
				} catch (WeiboException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		});

		submitBtn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String nickname = et_name.getText().toString();
				String phone = et_phone.getText().toString();
				Map<String, String> args = new HashMap<String, String>();
				args.put("nickname", CPSession.user.getUsername());
				if (nickname != "") {
					args.put("name", nickname);
				}
				if (phone != "") {
					args.put("phone", phone);
				}
				if (gender != null) {
					args.put("gender", gender);
				}
				if (headPic != null) {
					args.put("portrait", headPic);
				}
				CPRequest request = new CPRequest("editInfo", args);
				CPResponse response = request.request();
				try {
					if (response.getJson().getString("errorMsg") == "null") {
						Toast.makeText(ProfileActivity.this, "更新成功",
								Toast.LENGTH_SHORT).show();
						CPSession.user.setPortrait(headPic);
						Intent intent = new Intent();
						intent.setClass(ProfileActivity.this,
								HomeActivity.class);
						startActivity(intent);

					}
					System.out.println(response.getContent());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});

		rg_gender
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO Auto-generated method stub
						if (checkedId == rb_male.getId()) {
							gender = "m";
						} else if (checkedId == rb_female.getId()) {
							gender = "f";
						}
					}
				});

	}
}
