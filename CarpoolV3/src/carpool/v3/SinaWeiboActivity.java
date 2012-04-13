package carpool.v3;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import carpool.prototype.SinaWeiboInfo;
import carpool.ui.SinaWeiboHolder;
import carpool.v3.AsyncImageLoader.ImageCallback;
import carpool.webcom.CPSession;

import com.weibo.net.AccessToken;
import com.weibo.net.DialogError;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboDialogListener;
import com.weibo.net.WeiboException;
import com.weibo.net.WeiboParameters;

public class SinaWeiboActivity extends Activity {
	Weibo weibo;
	TextView sinaweibo_tv;
	private List<SinaWeiboInfo> wbList;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (CPSession.sina_token == null) {
			weibo = Weibo.getInstance();
			weibo.setupConsumerConfig(CPSession.CONSUMER_KEY,
					CPSession.CONSUMER_SECRET);
			weibo.setRedirectUrl("http://www.sina.com");
			weibo.authorize(SinaWeiboActivity.this, new AuthDialogListener());

		}
		setContentView(R.layout.sinaweibo_view);
		Button sinaweibo_get = (Button) findViewById(R.id.sinaweibo_btn_get);
		Button sinaweibo_send = (Button) findViewById(R.id.sinaweibo_btn_send);
		sinaweibo_get.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String url = Weibo.SERVER + "statuses/friends_timeline.json";
				WeiboParameters bundle = new WeiboParameters();
				bundle.add("source", CPSession.CONSUMER_KEY);

				try {
					String rlt = weibo.request(SinaWeiboActivity.this, url,
							bundle, "GET", weibo.getAccessToken());
					JSONArray data = new JSONObject(rlt)
							.getJSONArray("statuses");
					for (int i = 0; i < data.length(); i++) {
						JSONObject d = data.getJSONObject(i);
						if (d != null) {
							JSONObject u = d.getJSONObject("user");
							if (d.has("retweeted_status")) {
								JSONObject r = d
										.getJSONObject("retweeted_status");
							}

							// 微博id
							String id = d.getString("id");
							String userId = u.getString("id");
							String userName = u.getString("screen_name");
							String userIcon = u.getString("profile_image_url");
							String time = d.getString("created_at");
							String text = d.getString("text");
							Boolean haveImg = false;
							if (d.has("thumbnail_pic")) {
								haveImg = true;
							}

							if (wbList == null) {
								wbList = new ArrayList<SinaWeiboInfo>();
							}
							SinaWeiboInfo w = new SinaWeiboInfo();
							w.setId(id);
							w.setUserId(userId);
							w.setUserName(userName);
							w.setTime(time);
							w.setText(text);
							w.setHaveImage(haveImg);
							w.setUserIcon(userIcon);
							wbList.add(w);
						}

					}
					System.out.println(rlt);
				} catch (WeiboException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (wbList != null) {
					WeiBoAdapater adapater = new WeiBoAdapater();
					ListView Msglist = (ListView) findViewById(R.id.sinaweibo_list);
					Msglist.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View view,
								int arg2, long arg3) {
							Object obj = view.getTag();
							if (obj != null) {
								String id = obj.toString();
								Toast.makeText(SinaWeiboActivity.this, id, Toast.LENGTH_SHORT).show();
							}
						}

					});
					Msglist.setAdapter(adapater);
				}

			}

		});

		sinaweibo_send.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutInflater factory = LayoutInflater
						.from(SinaWeiboActivity.this);
				final View sendDlgView = factory.inflate(
						R.layout.sina_send_dlg_view, null);

				Builder builder = new AlertDialog.Builder(
						SinaWeiboActivity.this);
				builder.setIcon(R.drawable.login);
				builder.setTitle("发布微博");
				builder.setView(sendDlgView);
				builder.setPositiveButton(R.string.sinaweibo_send,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								EditText et_send = (EditText) sendDlgView
										.findViewById(R.id.sinaweibo_et_send);
								String content = et_send.getText().toString();
								String url = Weibo.SERVER
										+ "statuses/update.json";
								WeiboParameters bundle = new WeiboParameters();
								bundle.add("source", CPSession.CONSUMER_KEY);
								bundle.add("status", content);
								try {
									String rlt = weibo.request(
											SinaWeiboActivity.this, url,
											bundle, "POST",
											weibo.getAccessToken());
									sinaweibo_tv.setText(rlt);
									System.out.println(rlt);
								} catch (WeiboException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}
						});
				builder.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

							}
						});
				AlertDialog dlg = builder.create();
				dlg.show();

			}

		});
	}

	class AuthDialogListener implements WeiboDialogListener {

		@Override
		public void onComplete(Bundle values) {
			String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			AccessToken accessToken = new AccessToken(token,
					CPSession.CONSUMER_SECRET);
			accessToken.setExpiresIn(expires_in);
			CPSession.sina_token = accessToken;
			Weibo.getInstance().setAccessToken(accessToken);
			String url = Weibo.SERVER + "account/get_uid.json";
			WeiboParameters bundle = new WeiboParameters();
			bundle.add("source", CPSession.CONSUMER_KEY);
			try {
				if (CPSession.uid == null) {
					String rlt = weibo.request(SinaWeiboActivity.this, url,
							bundle, "GET", weibo.getAccessToken());
					JSONObject rlt_json = new JSONObject(rlt);
					CPSession.uid = rlt_json.getString("uid");
					System.out.println(rlt);

				}
			} catch (WeiboException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		@Override
		public void onError(DialogError e) {
			Toast.makeText(getApplicationContext(),
					"Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
		}

		@Override
		public void onCancel() {
			Toast.makeText(getApplicationContext(), "Auth cancel",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(getApplicationContext(),
					"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}

	}

	// 微博列表Adapater
	public class WeiBoAdapater extends BaseAdapter {

		private AsyncImageLoader asyncImageLoader;

		@Override
		public int getCount() {
			return wbList.size();
		}

		@Override
		public Object getItem(int position) {
			return wbList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			asyncImageLoader = new AsyncImageLoader();
			convertView = LayoutInflater.from(getApplicationContext()).inflate(
					R.layout.sinaweibo_single, null);
			SinaWeiboHolder wh = new SinaWeiboHolder();
			wh.wbicon = (ImageView) convertView.findViewById(R.id.wbicon);
			wh.wbtext = (TextView) convertView.findViewById(R.id.wbtext);
			wh.wbtime = (TextView) convertView.findViewById(R.id.wbtime);
			wh.wbuser = (TextView) convertView.findViewById(R.id.wbuser);
			wh.wbimage = (ImageView) convertView.findViewById(R.id.wbimage);
			SinaWeiboInfo wb = wbList.get(position);
			if (wb != null) {
				convertView.setTag(wb.getId());
				wh.wbuser.setText(wb.getUserName());
				wh.wbtime.setText(wb.getTime());
				wh.wbtext.setText(wb.getText(), TextView.BufferType.SPANNABLE);
				textHighlight(wh.wbtext, new char[] { '#' }, new char[] { '#' });
				textHighlight(wh.wbtext, new char[] { '@' }, new char[] { ':',
						' ' });
				textHighlight2(wh.wbtext, "http://", " ");

				if (wb.getHaveImage()) {
					wh.wbimage.setImageResource(R.drawable.ic_action_user);
				}
				Drawable cachedImage = asyncImageLoader.loadDrawable(
						wb.getUserIcon(), wh.wbicon, new ImageCallback() {

							@Override
							public void imageLoaded(Drawable imageDrawable,
									ImageView imageView, String imageUrl) {
								imageView.setImageDrawable(imageDrawable);
							}

						});
				if (cachedImage == null) {
					wh.wbicon.setImageResource(R.drawable.ic_action_user);
				} else {
					wh.wbicon.setImageDrawable(cachedImage);
				}
			}

			return convertView;
		}

		private void textHighlight(TextView textView, char[] start, char[] end) {
			Spannable sp = (Spannable) textView.getText();
			String text = textView.getText().toString();
			int n = 0;
			int s = -1;
			int e = -1;
			while (n < text.length()) {
				s = text.indexOf(start[0], n);
				if (s != -1) {
					e = text.indexOf(end[0], s + 1);
					if (e != -1) {
						e = e + 1;
					} else {
						e = text.length();
					}
					n = e;
					sp.setSpan(new ForegroundColorSpan(Color.BLUE), s, e,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					s = e = -1;
				} else {
					n = text.length();
				}
			}
		}

		private void textHighlight2(TextView textView, String start, String end) {
			Spannable sp = (Spannable) textView.getText();
			String text = textView.getText().toString();
			int n = 0;
			int s = -1;
			int e = -1;
			while (n < text.length()) {
				s = text.indexOf(start, n);
				if (s != -1) {
					e = text.indexOf(end, s + start.length());
					if (e != -1) {
						e = e + end.length();
					} else {
						e = text.length();
					}
					n = e;
					sp.setSpan(new ForegroundColorSpan(Color.BLUE), s, e,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					s = e = -1;
				} else {
					n = text.length();
				}
			}
		}
	}
}
