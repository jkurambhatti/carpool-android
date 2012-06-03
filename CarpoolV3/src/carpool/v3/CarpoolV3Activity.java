package carpool.v3;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;
import carpool.ui.CarpoolScrollView;
import carpool.webcom.CPActivity;
import carpool.webcom.CPConstants;
import carpool.webcom.CPRequest;
import carpool.webcom.CPResponse;
import carpool.webcom.CPSession;
import carpool.webcom.CPSocket;

import com.weibo.net.AccessToken;
import com.weibo.net.DialogError;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboDialogListener;
import com.weibo.net.WeiboException;

public class CarpoolV3Activity extends CPActivity implements OnGestureListener,
		OnTouchListener {

	private TextView date_TextView;
	private ImageButton set_ImageButton, regist_ImageButton, login_ImageButton;
	private ViewFlipper viewFlipper;
	private boolean showNext = true;
	private boolean isRun = true;
	private int currentPage = 0;
	private final int SHOW_NEXT = 0011;
	private static final int FLING_MIN_DISTANCE = 50;
	private static final int FLING_MIN_VELOCITY = 0;
	private GestureDetector mGestureDetector;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		date_TextView = (TextView) findViewById(R.id.home_date_tv);
		date_TextView.setText(getDate());
		RelativeLayout r1 = (RelativeLayout) findViewById(R.id.sinaweibo);
		r1.setOnClickListener(new RelativeLayout.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Weibo weibo = Weibo.getInstance();
				weibo.setupConsumerConfig(CPSession.CONSUMER_KEY,
						CPSession.CONSUMER_SECRET);
				weibo.setRedirectUrl("http://www.sina.com");
				weibo.authorize(CarpoolV3Activity.this,
						new AuthDialogListener());
			}
		});
		RelativeLayout r2 = (RelativeLayout) findViewById(R.id.renren);
		r2.setOnClickListener(new RelativeLayout.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(CarpoolV3Activity.this,
						RenrenAuthActivity.class);
				startActivity(intent);

			}
		});
		set_ImageButton = (ImageButton) findViewById(R.id.title_set_bn);
		set_ImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutInflater factory = LayoutInflater
						.from(CarpoolV3Activity.this);
				final View settingDlgView = factory.inflate(
						R.layout.settings_view, null);
				TextView tv = (TextView) settingDlgView
						.findViewById(R.id.settings_tv);
				tv.setText("是否使用GPS定位:");
				ToggleButton tb = (ToggleButton) settingDlgView
						.findViewById(R.id.settings_tb);
				SharedPreferences useGPS = getSharedPreferences("use_GPS", 0);
				Boolean ifUseGPS = useGPS.getBoolean("useGPS", false);
				tb.setChecked(ifUseGPS);
				Builder builder = new AlertDialog.Builder(
						CarpoolV3Activity.this);
				builder.setIcon(R.drawable.login);
				builder.setTitle("环境设置");
				builder.setView(settingDlgView);
				builder.setPositiveButton(R.string.submit,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								ToggleButton tb = (ToggleButton) settingDlgView
										.findViewById(R.id.settings_tb);
								SharedPreferences useGPS = getSharedPreferences(
										"use_GPS", 0);
								if (tb.isChecked() == true) {
									useGPS.edit().putBoolean("useGPS", true)
											.commit();
								} else {
									useGPS.edit().putBoolean("useGPS", false)
											.commit();
								}

							}
						});
				builder.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.cancel();
							}

						});
				AlertDialog dlg = builder.create();
				dlg.show();

			}
		});

		regist_ImageButton = (ImageButton) findViewById(R.id.home_bn_regist);
		regist_ImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(CarpoolV3Activity.this, RegisterActivity.class);
				startActivity(intent);
			}
		});
		login_ImageButton = (ImageButton) findViewById(R.id.home_bn_login);
		login_ImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutInflater factory = LayoutInflater
						.from(CarpoolV3Activity.this);
				final View loginDlgView = factory.inflate(
						R.layout.login_dlg_view, null);
				EditText uname = (EditText) loginDlgView
						.findViewById(R.id.login_username);
				EditText upwd = (EditText) loginDlgView
						.findViewById(R.id.login_password);
				SharedPreferences userInfo = getSharedPreferences("user_info",
						0);
				uname.setText(userInfo.getString("name", ""));
				upwd.setText(userInfo.getString("pass", ""));
				Builder builder = new AlertDialog.Builder(
						CarpoolV3Activity.this);
				builder.setIcon(R.drawable.login);
				builder.setTitle("欢迎登录");
				builder.setView(loginDlgView);
				builder.setPositiveButton(R.string.login_tv,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								EditText uname = (EditText) loginDlgView
										.findViewById(R.id.login_username);
								EditText upwd = (EditText) loginDlgView
										.findViewById(R.id.login_password);
								String suname = uname.getText().toString();
								String supwd = upwd.getText().toString();
								if (suname.compareTo("") == 0
										|| supwd.compareTo("") == 0) {
									Toast.makeText(CarpoolV3Activity.this,
											"请填写内容", Toast.LENGTH_LONG).show();
									Field field;
									try {
										field = dialog.getClass()
												.getSuperclass()
												.getDeclaredField("mShowing");
										field.setAccessible(true);
										field.set(dialog, false);
									} catch (SecurityException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (NoSuchFieldException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IllegalArgumentException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IllegalAccessException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								} else {
									Map<String, String> args = new HashMap<String, String>();
									args.put("nickname", suname);
									args.put("password", supwd);

									CPRequest request = new CPRequest("login",
											args);
									CPResponse response = request.request();
									System.out.println(response.getJson()
											.toString());
									try {
										if (response.getJson().get("errorMsg")
												.toString().compareTo("null") != 0) {
											Toast.makeText(
													CarpoolV3Activity.this,
													"用户名/密码错误",
													Toast.LENGTH_SHORT).show();

										} else {
											JSONObject rlt = response.getJson();
											JSONObject user = rlt
													.getJSONObject("user");
											CPSession.user.setPortrait(user
													.getString("portrait"));
											CPSession.user.setUsername(suname);
											CPSession.user.setPassword(supwd);
											SharedPreferences userInfo = getSharedPreferences(
													"user_info", 0);
											userInfo.edit()
													.putString("name", suname)
													.commit();
											userInfo.edit()
													.putString("pass", supwd)
													.commit();
											CPSession.socket = new CPSocket(
													CPConstants.serverIP, 8000);
											CPSession.socket.activity = CarpoolV3Activity.this;
											CPSession.socket.sendMsg("REGISTER"
													+ suname);
											Intent intent = new Intent();
											intent.setClass(
													CarpoolV3Activity.this,
													HomeActivity.class);
											// 关闭dialog
											Field field = dialog
													.getClass()
													.getSuperclass()
													.getDeclaredField(
															"mShowing");
											field.setAccessible(true);
											field.set(dialog, true);
											startActivity(intent);

										}
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (UnknownHostException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (SecurityException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (NoSuchFieldException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IllegalArgumentException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IllegalAccessException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
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
		viewFlipper = (ViewFlipper) findViewById(R.id.mViewFliper_vf);
		mGestureDetector = new GestureDetector(this);
		viewFlipper.setOnTouchListener(this);
		viewFlipper.setLongClickable(true);
		viewFlipper.setOnClickListener(clickListener);
		displayRatio_selelct(currentPage);

		CarpoolScrollView CarpoolScrollView = (CarpoolScrollView) findViewById(R.id.viewflipper_scrollview);
		CarpoolScrollView.setOnTouchListener(onTouchListener);
		CarpoolScrollView.setGestureDetector(mGestureDetector);

		thread.start();
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			toastInfo("点击事件");
		}
	};
	private OnTouchListener onTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			return mGestureDetector.onTouchEvent(event);
		}
	};

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case SHOW_NEXT:
				if (showNext) {
					showNextView();
				} else {
					showPreviousView();
				}
				break;

			default:
				break;
			}
		}

	};

	private String getDate() {
		Date date = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		int w = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0) {
			w = 0;
		}
		String mDate = c.get(Calendar.YEAR) + "年" + c.get(Calendar.MONTH) + "月"
				+ c.get(Calendar.DATE) + "日  " + weekDays[w];
		return mDate;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE
				&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
			showNextView();
			showNext = true;
			// return true;
		} else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE
				&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
			showPreviousView();
			showNext = false;
			// return true;
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return mGestureDetector.onTouchEvent(event);
	}

	Thread thread = new Thread() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (isRun) {
				try {
					Thread.sleep(1000 * 8);
					Message msg = new Message();
					msg.what = SHOW_NEXT;
					mHandler.sendMessage(msg);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	};

	private void showNextView() {

		viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_left_in));
		viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_left_out));
		viewFlipper.showNext();
		currentPage++;
		if (currentPage == viewFlipper.getChildCount()) {
			displayRatio_normal(currentPage - 1);
			currentPage = 0;
			displayRatio_selelct(currentPage);
		} else {
			displayRatio_selelct(currentPage);
			displayRatio_normal(currentPage - 1);
		}
		Log.e("currentPage", currentPage + "");

	}

	private void showPreviousView() {
		displayRatio_selelct(currentPage);
		viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_right_in));
		viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_right_out));
		viewFlipper.showPrevious();
		currentPage--;
		if (currentPage == -1) {
			displayRatio_normal(currentPage + 1);
			currentPage = viewFlipper.getChildCount() - 1;
			displayRatio_selelct(currentPage);
		} else {
			displayRatio_selelct(currentPage);
			displayRatio_normal(currentPage + 1);
		}
		Log.e("currentPage", currentPage + "");
	}

	private void displayRatio_selelct(int id) {
		int[] ratioId = { R.id.home_ratio_img_04, R.id.home_ratio_img_03,
				R.id.home_ratio_img_02, R.id.home_ratio_img_01 };
		ImageView img = (ImageView) findViewById(ratioId[id]);
		img.setSelected(true);
	}

	private void displayRatio_normal(int id) {
		int[] ratioId = { R.id.home_ratio_img_04, R.id.home_ratio_img_03,
				R.id.home_ratio_img_02, R.id.home_ratio_img_01 };
		ImageView img = (ImageView) findViewById(ratioId[id]);
		img.setSelected(false);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			isRun = false;
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		finish();
		super.onDestroy();
	}

	private void toastInfo(String string) {
		Toast.makeText(CarpoolV3Activity.this, string, Toast.LENGTH_SHORT)
				.show();
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
			Intent intent = new Intent();
			intent.setClass(CarpoolV3Activity.this, RegisterActivity.class);
			startActivity(intent);
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

	@Override
	public void append(String str) {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
