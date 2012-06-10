package carpool.v3;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

public class BMap extends Application {

	static BMap mApp;

	public BMapManager mBMapMan = null;
	public String mStrKey = "F7CBD0EFF200C1288878817C7821BD40CFD09089";
	boolean m_bKeyRight = true;

	public static class MyGeneralListener implements MKGeneralListener {
		@Override
		public void onGetNetworkState(int iError) {
			Toast.makeText(BMap.mApp.getApplicationContext(), "您的网络出错啦！",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onGetPermissionState(int iError) {
			Log.d("MyGeneralListener", "onGetPermissionState error is "
					+ iError);
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				// 授权Key错误：
				Toast.makeText(BMap.mApp.getApplicationContext(),
						"请在BMapApiDemoApp.java文件输入正确的授权Key！", Toast.LENGTH_LONG)
						.show();
				BMap.mApp.m_bKeyRight = false;
			}
		}

	}

	@Override
	public void onCreate() {
		mApp = this;
		mBMapMan = new BMapManager(this);
		mBMapMan.init(this.mStrKey, new MyGeneralListener());
		super.onCreate();
	}

	public void onTerminate() {
		// TODO Auto-generated method stub
		if (mBMapMan != null) {
			mBMapMan.destroy();
			mBMapMan = null;
		}
		super.onTerminate();
	}

}
