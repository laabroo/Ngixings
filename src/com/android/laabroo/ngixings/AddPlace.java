package com.android.laabroo.ngixings;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import android.content.DialogInterface.OnClickListener;

import com.google.android.maps.GeoPoint;

public class AddPlace extends Activity {
	private EditText editJalan;
	private EditText editNama;
	String lon;
	String lat;
	String nama;
	String jalan;
	private HttpClient client = new DefaultHttpClient();
	private HttpPost post;
	private HttpResponse response;
	LocationManager locationManager;
	private LocationListener locListener;
	String provider;
	private Location lokasi;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add);

		initLocationManager();

		editNama = (EditText) findViewById(R.id.editName);
		editJalan = (EditText) findViewById(R.id.editJalan);
		Button submit = (Button) findViewById(R.id.btnSubmit);

		Button cancel = (Button) findViewById(R.id.btnCancel);

		submit.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				addPosition();
			}
		});

		cancel.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				finish();

			}
		});

	}

	private void initLocationManager() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locListener = new LocationListener() {

			public void onProviderDisabled(String arg0) {
			}

			public void onProviderEnabled(String arg0) {
			}

			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			}

			public void onLocationChanged(Location location) {
				location.getLatitude();
				location.getLongitude();
				lon = new String(String.valueOf(location.getLongitude()));
				lat = new String(String.valueOf(location.getLatitude()));
				Log.i("onlocationChange Longitude dan Latitude : ", lat + ","
						+ lon);
				// Log.i("Longitude dan Latitude : ", location.getLongitude()
				// + "," + location.getLatitude());

			}

		};
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				1000, locListener);

	}

	private void addPosition() {
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locListener = new LocationListener() {
			
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				// TODO Auto-generated method stub
				
			}
			
			public void onProviderEnabled(String arg0) {
				// TODO Auto-generated method stub
				
			}
			
			public void onProviderDisabled(String arg0) {
				// TODO Auto-generated method stub
				
			}
			
			public void onLocationChanged(Location loc) {
				// TODO Auto-generated method stub
				loc.getLatitude();
				loc.getLongitude();
				lat = new String(String.valueOf(loc.getLatitude()));
				lon = new String(String.valueOf(loc.getLongitude()));
				
				Log.i("onlocationChange Longitude dan Latitude : ", lat + ","
						+ lon);
				
			}
		};
		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				1000, locListener);

		String longi =lon;
		String lati =  lat;
		nama = editNama.getText().toString();
		jalan = editJalan.getText().toString();

		Log.i("Lat-Lon : ", lati + "," + longi);
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
				4);
		nameValuePairs.add(new BasicNameValuePair("name", nama));
		nameValuePairs.add(new BasicNameValuePair("address", jalan));
		nameValuePairs.add(new BasicNameValuePair("longitude", longi));
		nameValuePairs.add(new BasicNameValuePair("latitude", lati));

		Log.i("Data : ", nama + "," + jalan + "," + longi + "," + lati);

		client = new DefaultHttpClient();
		post = new HttpPost("http://laabroo.cu.cc/add.php");

		try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			response = client.execute(post);
			InputStream inputStream = response.getEntity().getContent();
			BufferedInputStream bis = new BufferedInputStream(inputStream);
			ByteArrayBuffer baf = new ByteArrayBuffer(400);

			int current;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}

			String byteData = new String(baf.toByteArray());
			String respondServer = response.getStatusLine().getReasonPhrase();
			int statusCode = response.getStatusLine().getStatusCode();
			String code = new String(String.valueOf(statusCode));

			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Information");
			alertDialog.setMessage(byteData.toString());
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {

				// @Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			alertDialog.show();
			Log.i("StatusCode : ", byteData);
			Log.i("respondServer : ", respondServer);
			Log.i("StatusCode : ", code);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location loc) {
			// TODO Auto-generated method stub
			loc.getLatitude();
			loc.getLongitude();

			Log.i("My locationlistener new Lat-Lon : ", loc.getLatitude() + ","
					+ loc.getLongitude());
		}

		public Location getLon(Location location) {
			location.getLongitude();
			return location;
		}

		public Location getLat(Location location) {
			location.getLatitude();
			return location;
		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(), "GPS Disable",
					Toast.LENGTH_SHORT).show();
		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(), "GPS Enable",
					Toast.LENGTH_SHORT).show();

		}

		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub

		}

	}

}
