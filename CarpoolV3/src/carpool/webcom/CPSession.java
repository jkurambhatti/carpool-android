package carpool.webcom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import carpool.prototype.Passenger;
import carpool.prototype.Taxi;

import com.google.android.maps.GeoPoint;
import com.weibo.net.AccessToken;

public class CPSession {
	public static Passenger user = new Passenger();
	public static int waitTime = 0;
	public static GeoPoint startPoint = null;
	public static GeoPoint endPoint = null;
	public static int pinAmount = 0;
	public static GeoPoint taxiPoint = null;
	public static Taxi taxi = null;
	public static List<GeoPoint> points = new ArrayList<GeoPoint>();
	public static Map<String,String> OAuthData = new HashMap<String,String>();
	public static AccessToken sina_token = null;
	public static String uid = null;
	public static String CONSUMER_KEY = "2828113772";
	public static String CONSUMER_SECRET = "e92cde1ec6c07dd328050fc2f2ca847f";
	public static CPSocket socket = null;
	public static int requestId =  0;
	public static List<Taxi> candidateTaxi = new ArrayList<Taxi>();
	public static String flag = "WAITING";
}
