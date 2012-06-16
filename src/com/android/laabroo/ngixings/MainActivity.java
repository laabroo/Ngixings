package com.android.laabroo.ngixings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MainActivity extends MapActivity {
	/** Called when the activity is first created. */
	private MapView mapView;
	private LocationManager locManager;
	private LocationListener locListener;
	private ArrayList<Tempat> list_lokasi = new ArrayList<Tempat>();
	private HttpClient httpClient;
	private HttpGet httpGet;
	private HttpResponse response;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		try {
			// initLokasi();
			getAll();
			initMap();
			initLocationManager();
		} catch (Exception e) {
			Log.e("onCreate : ", e.getMessage());
		}
	}


	private void getAll() {
		String posisis = loadData();
		Log.i("Posisi : ", posisis);
		try {
			JSONArray jsonArray = new JSONArray(posisis);

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = (JSONObject) jsonArray.get(i);

				String name = jsonObject.getString("name");
				String latitude = jsonObject.getString("latitude");
				String longitude = jsonObject.getString("longitude");
				String address = jsonObject.getString("address");

				Log.i("Data Hasil : ", name + "," + latitude + "," + longitude);
				double lat = new Double(latitude);
				double lon = new Double(longitude);

				list_lokasi.add(new Tempat(lat, lon, i, name, address));

			}
		}

		catch (ClassCastException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addNew() {
		Intent intent = new Intent(this, AddPlace.class);
		startActivity(intent);
	}

	public String loadData() {
		httpGet = new HttpGet("http://laabroo.cu.cc/show.php");
		StringBuilder builder = new StringBuilder();
		httpClient = new DefaultHttpClient();
		try {
			response = httpClient.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statuscode = statusLine.getStatusCode();
			if (statuscode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));

				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
					Log.i("Data : ", line);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return builder.toString();
	}

	/**
	 * Initialize the map to the LinearLayout.
	 */
	private void initMap() {
		mapView = (MapView) findViewById(R.id.mymap);
		mapView.displayZoomControls(true);
		mapView.getController().setZoom(16);

	}

	/**
	 * Initialize the location manager.
	 */
	private void initLocationManager() {
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locListener = new LocationListener() {

			public void onProviderDisabled(String arg0) {
			}

			public void onProviderEnabled(String arg0) {
			}

			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			}

			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				tampilkanPosisikeMap(location);
			}

		};
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				1000, locListener);

	}


	protected void tampilkanPosisikeMap(Location newLocation) {
		List<Overlay> overlays = mapView.getOverlays();

		// first remove old overlay
		if (overlays.size() > 0) {
			for (Iterator<Overlay> iterator = overlays.iterator(); iterator
					.hasNext();) {
				iterator.next();
				iterator.remove();
			}
		}

		// transform the location to a geopoint
		GeoPoint geopoint = new GeoPoint(
				(int) (newLocation.getLatitude() * 1E6),
				(int) (newLocation.getLongitude() * 1E6));
		GeoPoint myposition = geopoint;
		Location locationA = new Location("point A");
		Location locationB = new Location("point B");
		locationA.setLatitude(geopoint.getLatitudeE6() / 1E6);
		locationA.setLongitude(geopoint.getLongitudeE6() / 1E6);
		// initialize icon
		Drawable icon = getResources().getDrawable(R.drawable.mappin);
		icon.setBounds(0, 0, icon.getIntrinsicWidth(),
				icon.getIntrinsicHeight());

		// create my overlay and show it
		MyItemizedOverlay overlay = new MyItemizedOverlay(icon, this);
		// OverlayItem item = new OverlayItem(geopoint, "Posisi saya", "ada "
		// + list_lokasi.size() + "toilet terdekat saat ini.\n");
		OverlayItem item = new OverlayItem(geopoint, "Me", "Posisi saat ini.");
		Log.i("Jumlah Overlays: ",new String(String.valueOf(overlays.size())));

		overlay.addItem(item);
		mapView.getOverlays().add(overlay);
		String a = new String(String.valueOf(list_lokasi.size()));
		Log.i("Jumlah posisi : ", a);
		for (int i = 0; i < list_lokasi.size(); i++) {
			geopoint = new GeoPoint((int) (list_lokasi.get(i).lat * 1E6),
					(int) (list_lokasi.get(i).lng * 1E6));
			locationB.setLatitude(geopoint.getLatitudeE6() / 1E6);
			locationB.setLongitude(geopoint.getLongitudeE6() / 1E6);

			double distance = locationA.distanceTo(locationB);

			if (distance < 1000) {

				icon = getResources().getDrawable(R.drawable.marker);

				icon.setBounds(0, 0, icon.getIntrinsicWidth(),
						icon.getIntrinsicHeight());
				overlay = new MyItemizedOverlay(icon, this);

				item = new OverlayItem(geopoint, list_lokasi.get(i).lokname,
						"Posisi : " + list_lokasi.get(i).address
								+ "\n Jarak : " + distance + " m");
				overlay.addItem(item);
			
				
				Log.i("Jumlah : ",new String(String.valueOf(overlays.size())));
				mapView.getOverlays().add(overlay);
			}

		}
		// move to location
		mapView.getController().animateTo(myposition);

		// redraw map
		mapView.postInvalidate();
	}

	protected boolean isRouteDisplayed() {
		return false;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;

	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add:
			addNew();
			break;
		}
		return true;
	}
}