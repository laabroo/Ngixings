package com.android.laabroo.ngixings;

public class Tempat {
	public double lat = 0;
	public double lng = 0;
	public int category = 0;
	public String lokname = "";
	public String address = "";

	public Tempat(double plat, double plng, int pcategory, String sname,
			String saddress) {
		this.lat = plat;
		this.lng = plng;
		this.category = pcategory;

		this.lokname = sname;
		this.address = saddress;
	}
}