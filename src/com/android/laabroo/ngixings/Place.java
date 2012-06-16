package com.android.laabroo.ngixings;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Place implements Serializable {

	public double lat;
	public double lon;
	public double rad;
	public String name;
	public String address;
	
	public Place() {
		// TODO Auto-generated constructor stub
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double getRad() {
		return rad;
	}

	public void setRad(double rad) {
		this.rad = rad;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
