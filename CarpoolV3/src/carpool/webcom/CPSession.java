package carpool.webcom;

import java.util.ArrayList;
import java.util.List;

import carpool.prototype.Passenger;
import carpool.prototype.Taxi;

import com.google.android.maps.GeoPoint;

public class CPSession {
	public static Passenger user = new Passenger();
	public static int waitTime = 0;
	public static GeoPoint startPoint = null;
	public static GeoPoint endPoint = null;
	public static int pinAmount = 0;
	public static GeoPoint taxiPoint = null;
	public static Taxi taxi = null;
	public static List<GeoPoint> points = new ArrayList<GeoPoint>();
}
