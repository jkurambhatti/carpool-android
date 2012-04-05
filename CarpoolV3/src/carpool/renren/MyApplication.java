package carpool.renren;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.content.SharedPreferences;

public class MyApplication extends Application{
	public static MyApplication myApplication;
	public boolean tokenIsExist;
	public Map<String,String> tokenMap;
	public String code="";
	public String session_key="";
	public String Secret_key="b294491f90074acb84c608363ce22c36";
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		myApplication=this;
		SharedPreferences preferences=getSharedPreferences("oauth", MODE_WORLD_WRITEABLE);
		String access_token=preferences.getString("access_token", null);
		if(access_token==null){
			tokenIsExist=false;
		}else{
			tokenIsExist=true;
			tokenMap=new HashMap<String, String>();
		    tokenMap.put("access_token", preferences.getString("access_token", null));
		    tokenMap.put("expires_in", preferences.getString("expires_in", null));
		    tokenMap.put("refresh_token", preferences.getString("refresh_token", null));
		    
		}
		
	}
	
	public static MyApplication getInstance(){
		return myApplication;
	}

}
