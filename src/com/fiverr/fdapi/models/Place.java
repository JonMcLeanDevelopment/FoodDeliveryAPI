package com.fiverr.fdapi.models;

public class Place {
	
	private String name;
	private double latitude,longitude;
	private String countryCode, city;
	private String uuid;
	
	public Place(String name, double latitude, double longitude, String countryCode, String city, String uuid) {
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.countryCode = countryCode;
		this.city = city;
		this.uuid = uuid;
	}
	
	public String getName() {
		return name;
	}
	public double getLatitude() {
		return latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public String getCity() {
		return city;
	}
	
	public String getUniqueId() {
		return uuid;
	}
	
	

}
