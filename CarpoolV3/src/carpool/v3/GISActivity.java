package carpool.v3;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKPoiInfo;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.PoiOverlay;
import com.baidu.mapapi.RouteOverlay;

public class GISActivity extends MapActivity {
	Button mBtnSearch = null;
	boolean isBusLineSearch = true;
	MapView mMapView = null;
	MKSearch mSearch = null;
	String mCityName = null;
	View dlgView;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gis_view);

		ImageButton ib = (ImageButton) findViewById(R.id.imageButton1);
		registerForContextMenu(ib);
		BMap app = (BMap) this.getApplication();
		if (app.mBMapMan == null) {
			app.mBMapMan = new BMapManager(getApplication());
			app.mBMapMan.init(app.mStrKey, new BMap.MyGeneralListener());
		}
		app.mBMapMan.start();
		super.initMapActivity(app.mBMapMan);

		mMapView = (MapView) findViewById(R.id.bmapView);
		mMapView.setBuiltInZoomControls(true);
		mMapView.setDrawOverlayWhenZooming(true);

		mSearch = new MKSearch();
		mSearch.init(app.mBMapMan, new MKSearchListener() {

			public void onGetPoiResult(MKPoiResult res, int type, int error) {
				if (isBusLineSearch) {

					if (error != 0 || res == null) {
						Toast.makeText(GISActivity.this, "抱歉，未找到结果",
								Toast.LENGTH_LONG).show();
						return;
					}

					MKPoiInfo curPoi = null;
					int totalPoiNum = res.getNumPois();
					for (int idx = 0; idx < totalPoiNum; idx++) {
						Log.d("busline", "the busline is " + idx);
						curPoi = res.getPoi(idx);
						if (2 == curPoi.ePoiType) {
							break;
						}
					}

					mSearch.busLineSearch(mCityName, curPoi.uid);
				} else {

					if (res == null) {
						Log.d("onGetPoiResult", "the onGetPoiResult res is "
								+ type + "__" + error);
					} else
						Log.d("onGetPoiResult",
								"the onGetPoiResult res is "
										+ res.getCurrentNumPois() + "__"
										+ res.getNumPages() + "__"
										+ res.getNumPois() + "__" + type + "__"
										+ error);

					if (error != 0 || res == null) {
						Log.d("onGetPoiResult", "the onGetPoiResult res 0 ");
						Toast.makeText(GISActivity.this, "抱歉，未找到结果",
								Toast.LENGTH_LONG).show();
						return;
					}

					ArrayList<MKPoiResult> poiResult = res.getMultiPoiResult();
					if (poiResult != null)
						Log.d("onGetPoiResult", "the onGetPoiResult res 1__"
								+ poiResult.size());
					if (res.getCurrentNumPois() > 0) {
						Log.d("onGetPoiResult", "the onGetPoiResult res 2");
						PoiOverlay poiOverlay = new PoiOverlay(
								GISActivity.this, mMapView);
						poiOverlay.setData(res.getAllPoi());
						mMapView.getOverlays().clear();
						mMapView.getOverlays().add(poiOverlay);
						mMapView.invalidate();
						mMapView.getController().animateTo(res.getPoi(0).pt);
					} else if (res.getCityListNum() > 0) {
						Log.d("onGetPoiResult", "the onGetPoiResult res 3");
						String strInfo = "在";
						for (int i = 0; i < res.getCityListNum(); i++) {
							strInfo += res.getCityListInfo(i).city;
							strInfo += ",";
						}
						strInfo += "找到结果";
						Toast.makeText(GISActivity.this, strInfo,
								Toast.LENGTH_LONG).show();
					}

					Log.d("onGetPoiResult", "the onGetPoiResult res 4");
				}
			}

			public void onGetDrivingRouteResult(MKDrivingRouteResult res,
					int error) {
			}

			public void onGetTransitRouteResult(MKTransitRouteResult res,
					int error) {
			}

			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
					int error) {
			}

			public void onGetAddrResult(MKAddrInfo res, int error) {
			}

			public void onGetBusDetailResult(MKBusLineResult result, int iError) {
				if (iError != 0 || result == null) {
					Toast.makeText(GISActivity.this, "抱歉，未找到结果",
							Toast.LENGTH_LONG).show();
					return;
				}

				RouteOverlay routeOverlay = new RouteOverlay(GISActivity.this,
						mMapView);
				routeOverlay.setData(result.getBusRoute());
				mMapView.getOverlays().clear();
				mMapView.getOverlays().add(routeOverlay);
				mMapView.invalidate();

				mMapView.getController().animateTo(
						result.getBusRoute().getStart());
			}

		});
	}

	@Override
	protected void onPause() {
		BMap app = (BMap) this.getApplication();
		app.mBMapMan.stop();
		super.onPause();
	}

	@Override
	protected void onResume() {
		BMap app = (BMap) this.getApplication();
		app.mBMapMan.start();
		super.onResume();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("地理信息服务");
		// add context menu item
		menu.add(0, 1, Menu.NONE, "公交线路查询");
		menu.add(0, 2, Menu.NONE, "兴趣点查询");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		Builder builder;
		AlertDialog dlg;
		switch (item.getItemId()) {
		case 1:
			// do something
			dlgView = LayoutInflater.from(this).inflate(R.layout.buslinesearch,
					null);
			builder = new AlertDialog.Builder(this);
			builder.setIcon(R.drawable.login);
			builder.setTitle("公交线路查询");
			builder.setView(dlgView);
			builder.setPositiveButton(R.string.submit,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							isBusLineSearch = true;
							EditText editCity = (EditText) dlgView
									.findViewById(R.id.city);
							EditText editSearchKey = (EditText) dlgView
									.findViewById(R.id.searchkey);
							mCityName = editCity.getText().toString();
							mSearch.poiSearchInCity(mCityName, editSearchKey
									.getText().toString());
						}
					});
			builder.setNegativeButton(R.string.goback,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.cancel();
						}
					});

			dlg = builder.create();
			dlg.show();

			break;
		case 2:
			// do something
			dlgView = LayoutInflater.from(this).inflate(R.layout.poisearch,
					null);
			builder = new AlertDialog.Builder(this);
			builder.setIcon(R.drawable.login);
			builder.setTitle("兴趣点查询");
			builder.setView(dlgView);
			builder.setPositiveButton(R.string.submit,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							isBusLineSearch = false;
							EditText editCity = (EditText) dlgView
									.findViewById(R.id.city);
							EditText editSearchKey = (EditText) dlgView
									.findViewById(R.id.searchkey);
							mSearch.poiSearchInCity(editCity.getText()
									.toString(), editSearchKey.getText()
									.toString());

						}
					});
			builder.setNegativeButton(R.string.goback,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.cancel();
						}
					});
			dlg = builder.create();
			dlg.show();

			break;
		default:
			return super.onContextItemSelected(item);
		}
		return true;
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
