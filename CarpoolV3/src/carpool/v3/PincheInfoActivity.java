package carpool.v3;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import carpool.prototype.PincheInfo;
import carpool.webcom.CPRequest;
import carpool.webcom.CPResponse;

public class PincheInfoActivity extends Activity implements OnScrollListener {
	private PaginationAdapter adapter;
	private Button loadMoreButton;
	private ImageButton ganjiBtn, p58Btn;
	private ListView listView;
	private View loadMoreView;
	private Handler handler = new Handler();
	private String type;
	private int size;
	private int page;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pincheinfo_view);
		loadMoreView = getLayoutInflater()
				.inflate(R.layout.loadmore_view, null);
		loadMoreButton = (Button) loadMoreView
				.findViewById(R.id.loadMoreButton);
		ganjiBtn = (ImageButton) findViewById(R.id.ganji_btn);
		p58Btn = (ImageButton) findViewById(R.id.p58_btn);
		adapter = new PaginationAdapter();

		loadMoreButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				loadMoreButton.setText("正在加载中...");
				handler.postDelayed(new Runnable() {
					public void run() {
						loadMoreData();
						adapter.notifyDataSetChanged();
						loadMoreButton.setText("查看更多...");
					}
				}, 5000);
			}
		});
		ganjiBtn.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (type.compareTo("ganji") == 0) {
					return;
				}
				adapter.clear();
				initializeAdapter("ganji");
				size = 10;
				page = 1;

			}
		});
		p58Btn.setOnClickListener(new ImageButton.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (type.compareTo("58") == 0) {
					return;
				}
				adapter.clear();
				initializeAdapter("58");
				size = 10;
				page = 1;

			}

		});

		listView = (ListView) findViewById(R.id.pincheinfo_list);
		listView.addFooterView(loadMoreView);
		initializeAdapter("58");
		type = "58";
		size = 10;
		page = 1;

		listView.setAdapter(adapter);
		listView.setOnScrollListener(this);
	}

	private void loadMoreData() {
		adapter.addItems(this.retrieveNewInfos(size, page + size, type));
	}

	private void initializeAdapter(String type) {
		adapter.addItems(this.retrieveNewInfos(10, 1, type));
	}

	class PaginationAdapter extends BaseAdapter {
		TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8, tv9, tv10, tv11, tv12,
				tv13, tv14;
		List<PincheInfo> infos = new ArrayList<PincheInfo>();

		public int getCount() {
			return infos.size();
		}

		public Object getItem(int position) {
			return infos.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public void addItems(List<PincheInfo> infos) {
			this.infos.addAll(infos);
		}

		public void clear() {
			this.infos.clear();
		}

		public View getView(int arg0, View arg1, ViewGroup arg2) {
			arg1 = LayoutInflater.from(getApplicationContext()).inflate(
					R.layout.pincheinfo_single, null);
			tv1 = (TextView) arg1.findViewById(R.id.pi_tv1);
			tv2 = (TextView) arg1.findViewById(R.id.pi_tv2);
			tv3 = (TextView) arg1.findViewById(R.id.pi_tv3);
			tv4 = (TextView) arg1.findViewById(R.id.pi_tv4);
			tv5 = (TextView) arg1.findViewById(R.id.pi_tv5);
			tv6 = (TextView) arg1.findViewById(R.id.pi_tv6);
			tv6 = (TextView) arg1.findViewById(R.id.pi_tv6);
			tv7 = (TextView) arg1.findViewById(R.id.pi_tv7);
			tv8 = (TextView) arg1.findViewById(R.id.pi_tv8);
			tv9 = (TextView) arg1.findViewById(R.id.pi_tv9);
			tv10 = (TextView) arg1.findViewById(R.id.pi_tv10);
			tv11 = (TextView) arg1.findViewById(R.id.pi_tv11);
			tv12 = (TextView) arg1.findViewById(R.id.pi_tv12);
			tv13 = (TextView) arg1.findViewById(R.id.pi_tv13);
			tv14 = (TextView) arg1.findViewById(R.id.pi_tv14);
			PincheInfo pi = infos.get(arg0);

			tv1.setText("起点：");
			tv2.setText(pi.getOrigin());
			tv3.setText("终点：");
			tv4.setText(pi.getDestination());
			tv5.setText("拼车类型：");
			tv6.setText(pi.getCarType());
			tv7.setText("发布人：");
			tv8.setText(pi.getPublisher());
			tv9.setText("发布时间：");
			if (pi.getDepartureTime() != null) {
				tv10.setText((new Date(pi.getDepartureTime())).toLocaleString());
			} else {
				tv10.setText("无");
			}
			tv11.setText("联系电话：");
			tv12.setText(pi.getPhone());
			tv13.setText("备注：");
			tv14.setText(pi.getBackup());
			return arg1;
		}
	}

	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
	}

	public List<PincheInfo> retrieveNewInfos(int size, int page, String type) {
		List<PincheInfo> infos = new ArrayList<PincheInfo>();
		Map<String, String> args = new HashMap<String, String>();
		args.put("type", type);
		args.put("index", String.valueOf(page));
		args.put("size", String.valueOf(size));
		CPRequest request = new CPRequest("getOutterInfo", args);
		CPResponse response = request.request();
		try {
			JSONArray results = response.getJson().getJSONArray("outterInfo");
			for (int i = 0; i < results.length(); i++) {
				String backup = results.getJSONObject(i).getString("backup");
				String carType = results.getJSONObject(i).getString("carType");
				String departureTime = results.getJSONObject(i).getString(
						"departureTime");
				String departureTime2 = results.getJSONObject(i).getString(
						"departureTime2");
				String destination = results.getJSONObject(i).getString(
						"destination");
				String id = results.getJSONObject(i).getString("id");
				String origin = results.getJSONObject(i).getString("origin");
				String path = results.getJSONObject(i).getString("path");
				String phone = results.getJSONObject(i).getString("phone");
				String publishTime = results.getJSONObject(i).getString(
						"publishTime");
				String publisher = results.getJSONObject(i).getString(
						"publisher");
				String url = results.getJSONObject(i).getString("url");
				String website = results.getJSONObject(i).getString("website");
				PincheInfo pi = new PincheInfo();
				pi.setBackup(backup);
				pi.setCarType(carType);
				if (departureTime.compareTo("null") == 0) {
					pi.setDepartureTime(null);

				} else {
					pi.setDepartureTime(Long.valueOf(departureTime));

				}
				pi.setDepartureTime2(departureTime2);
				pi.setDestination(destination);
				pi.setId(Integer.parseInt(id));
				pi.setOrigin(origin);
				pi.setPath(path);
				pi.setPhone(phone);
				pi.setPublishTime(publishTime);
				pi.setPublisher(publisher);
				pi.setUrl(url);
				pi.setWebsite(website);
				infos.add(pi);
			}
			return infos;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
