package carpool.v3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import carpool.webcom.CPSession;

public class HomeActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_view);
		TextView tv = (TextView) findViewById(R.id.home_textView2);
		tv.setText(CPSession.user.getUsername());
		RelativeLayout r1 = (RelativeLayout) findViewById(R.id.rl_profile);
		r1.setOnClickListener(new RelativeLayout.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(HomeActivity.this, "个人资料修改", Toast.LENGTH_SHORT)
						.show();

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

}
