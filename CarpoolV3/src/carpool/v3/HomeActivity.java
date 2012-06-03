package carpool.v3;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import carpool.webcom.CPActivity;
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
//		CPSession.socket.activity = this;
		if (CPSession.user.getPortrait() != "null") {
			portrait.setImageBitmap(returnBitmap(CPSession.user.getPortrait()));
		}
		GridView gridview = (GridView) findViewById(R.id.home_gridview);
		gridview.setAdapter(new ImageAdapter(this));

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

	class ImageAdapter extends BaseAdapter {
		private Context mContext;

		public ImageAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			return mThumbIds.length;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		// create a new ImageView for each item referenced by the Adapter
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			if (convertView == null) { // if it's not recycled, initialize some
										// attributes
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new GridView.LayoutParams(160, 160));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(8, 8, 8, 8);
			} else {
				imageView = (ImageView) convertView;
			}

			imageView.setImageResource(mThumbIds[position]);
			switch (position) {
			case 0:
				imageView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Toast.makeText(HomeActivity.this, "个人资料修改",
								Toast.LENGTH_SHORT).show();
						Intent intent = new Intent();
						intent.setClass(HomeActivity.this,
								ProfileActivity.class);
						startActivity(intent);

					}
				});
				break;
			case 1:
				imageView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Toast.makeText(HomeActivity.this, "同步个人资料",
								Toast.LENGTH_SHORT).show();
						Intent intent = new Intent();
						intent.setClass(HomeActivity.this,
								ProfileActivity.class);
						startActivity(intent);

					}
				});
				break;
			case 2:
				imageView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Toast.makeText(HomeActivity.this, "自助打车",
								Toast.LENGTH_SHORT).show();
						Intent intent = new Intent();
						intent.setClass(HomeActivity.this,
								PincheProcessActivity.class);
						startActivity(intent);

					}
				});
				break;
			case 3:
				imageView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Toast.makeText(HomeActivity.this, "拼车服务",
								Toast.LENGTH_SHORT).show();
						Intent intent = new Intent();
						intent.setClass(HomeActivity.this,
								PincheProcessActivity.class);
						startActivity(intent);

					}
				});
				break;
			case 4:
				imageView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Toast.makeText(HomeActivity.this, "地理信息服务",
								Toast.LENGTH_SHORT).show();
						Intent intent = new Intent();
						intent.setClass(HomeActivity.this, GISActivity.class);
						startActivity(intent);

					}
				});
				break;
			case 5:
				imageView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Toast.makeText(HomeActivity.this, "网络拼车信息",
								Toast.LENGTH_SHORT).show();
						Intent intent = new Intent();
						intent.setClass(HomeActivity.this,
								PincheInfoActivity.class);
						startActivity(intent);

					}
				});
				break;
			case 6:
				imageView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Toast.makeText(HomeActivity.this, "新浪微博",
								Toast.LENGTH_SHORT).show();
						Intent intent = new Intent();
						intent.setClass(HomeActivity.this,
								SinaWeiboActivity.class);
						startActivity(intent);

					}
				});
				break;
			case 7:
				imageView.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Toast.makeText(HomeActivity.this, "人人网",
								Toast.LENGTH_SHORT).show();
						Intent intent = new Intent();
						intent.setClass(HomeActivity.this,
								RenrenAuthActivity.class);
						startActivity(intent);

					}
				});
				break;

			}
			return imageView;
		}

		// references to our images
		private Integer[] mThumbIds = { R.drawable.p1, R.drawable.p2,
				R.drawable.p3, R.drawable.p4, R.drawable.p5, R.drawable.p6,
				R.drawable.p7, R.drawable.p8, };
	}
}
