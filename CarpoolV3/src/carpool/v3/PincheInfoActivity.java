package carpool.v3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import carpool.prototype.PincheInfo;
import carpool.webcom.CPRequest;
import carpool.webcom.CPResponse;

public class PincheInfoActivity extends Activity {

	private List<PincheInfo> piList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pincheinfo_view);
		Map<String, String> args = new HashMap<String, String>();
		args.put("type", "58");
		args.put("index", "1");
		args.put("size", "10");
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
				pi.setDepartureTime(Long.valueOf(departureTime));
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
				piList.add(pi);
				PincheInfoAdapter adapater = new PincheInfoAdapter();

				ListView Msglist = (ListView) findViewById(R.id.pincheinfo_list);
				Msglist.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View view,
							int arg2, long arg3) {
						Object obj = view.getTag();
						if (obj != null) {
							String id = obj.toString();
							Toast.makeText(PincheInfoActivity.this, id,
									Toast.LENGTH_SHORT).show();
						}
					}

				});
				Msglist.setAdapter(adapater);

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public class PincheInfoAdapter extends BaseAdapter {
		TextView tv1, tv2, tv3, tv4, tv5, tv6;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return piList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return piList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			arg1 = LayoutInflater.from(getApplicationContext()).inflate(
					R.layout.pincheinfo_single, null);
			tv1 = (TextView) findViewById(R.id.pi_tv1);
			tv2 = (TextView) findViewById(R.id.pi_tv2);
			tv3 = (TextView) findViewById(R.id.pi_tv3);
			tv4 = (TextView) findViewById(R.id.pi_tv4);
			tv5 = (TextView) findViewById(R.id.pi_tv5);
			tv6 = (TextView) findViewById(R.id.pi_tv6);
			PincheInfo pi = piList.get(arg0);

			tv1.setText("起点：");
			tv2.setText(pi.getOrigin());
			tv3.setText("终点：");
			tv4.setText(pi.getDestination());
			tv5.setText("拼车类型：");
			tv6.setText(pi.getCarType());

			return arg1;
		}

	}
}
