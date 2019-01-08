package com.fiverr.fdapi.helper;

public class LocationHelper {
	
	public static boolean isCoordinateInRange(double range, double lat1, double long1, double lat2, double long2) {
		int r = 6731;
		
		System.out.println(lat2 - lat1);
		System.out.println(lat2 - lat1);
		
		double dLat = lat1- lat2;
		double dLong = long1 - long2;
		
		double d = ((r * Math.PI) / 180) * Math.sqrt(Math.pow(dLat, 2) + Math.pow(dLong, 2));
		
		if(d > range) {
			return false;
		}
		
		return true;
	}
	
	public static double getDistance(double lat1, double long1, double lat2, double long2) {
		int r = 6731;
		double dLat = lat1- lat2;
		double dLong = long1 - long2;
		
		return ((r * Math.PI) / 180) * Math.sqrt(Math.pow(dLat, 2) + Math.pow(dLong, 2));
	}
	
	
	
}
