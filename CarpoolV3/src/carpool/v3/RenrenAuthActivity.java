package carpool.v3;

import android.app.Activity;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import carpool.renren.MyApplication;

public class RenrenAuthActivity extends Activity {
	WebView webview;
	String url1 = "";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.authorizationactivity);
		webview = (WebView) findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setBuiltInZoomControls(true);
		webview.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				// TODO Auto-generated method stub
				handler.proceed();
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				url1 = webview.getUrl();
				if (url1 != null) {
					String tString;
					if (url1.contains("code=")) {
						tString = url1.substring(url1.indexOf("code=") + 5,
								url1.length());
						Toast.makeText(RenrenAuthActivity.this, tString,
								Toast.LENGTH_SHORT).show();
						MyApplication.getInstance().code = tString;
						if (MyApplication.getInstance().code != "") {
							Intent intent = new Intent(RenrenAuthActivity.this,
									HomeActivity.class);
							startActivity(intent);
						}
					}
				}
				super.onPageFinished(view, url);
			}
		});
		String url = "https://graph.renren.com/oauth/authorize?client_id=abef8621eb034f9ea6068c13fa0433b4&response_type=code&redirect_uri=http://graph.renren.com/oauth/login_success.html";
		webview.loadUrl(url);

	}
}