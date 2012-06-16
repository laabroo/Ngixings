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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class Interaktif extends MapActivity {

	private MapView mapView;
	private LocationManager locManager;
	private LocationListener locListener;
	double latitude, longitude;
	int radius = 2;

	private ArrayList<Place> list_lokasi = new ArrayList<Place>();
	private HttpClient httpClient;
	private HttpGet httpGet;
	private HttpResponse response;
	private Place place;
	private Location myLocation;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		initPosition();
		initMap();
		// initLocationManager();
	}

	private void initPosition() {
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locListener = new LocationListener() {

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub

			}

			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub

			}

			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub

			}

			public void onLocationChanged(Location location) {
				latitude = location.getLatitude();
				longitude = location.getLongitude();
				getAll(latitude, longitude, radius);
				tampilkanPosisikeMap(location);

			}
		};
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				1000, locListener);
	}

	public String loadData(double lat, double lon, double rad) {
		httpGet = new HttpGet("http://laabroo.cu.cc/lokasi.php?lat=" + lat
				+ "&lon=" + lon + "&rad=" + rad);
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

	private void getAll(double lat, double lon, double rad) {
		String posisis = loadData(lat, lon, rad);
		Log.i("Posisi : ", posisis);
		try {
			JSONObject obj = new JSONObject(posisis);
			JSONArray jsonArray = obj.getJSONArray("items");

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);

				String name = jsonObject.getString("name");
				String latitude = jsonObject.getString("latitude");
				String longitude = jsonObject.getString("longitude");
				String address = jsonObject.getString("address");
				String radius = jsonObject.getString("distance");

				Log.i("Data Hasil : ", name + "," + latitude + "," + longitude
						+ ", " + radius);
				lat = new Double(latitude);
				lon = new Double(longitude);
				rad = new Double(radius);

				place = new Place();
				place.setAddress(address);
				place.setLat(Double.valueOf(latitude));
				place.setLon(Double.valueOf(longitude));
				place.setName(name);
				place.setRad(Double.valueOf(radius));
				Log.i("Place : ", place.toString());
				list_lokasi.add(place);

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

	private Location lonlan() {

		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locListener = new LocationListener() {

			public void onProviderDisabled(String arg0) {
			}

			public void onProviderEnabled(String arg0) {
			}

			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			}

			public void onLocationChanged(Location location) {
				myLocation = new Location(location);
				myLocation.setLatitude(location.getLatitude());
				myLocation.setLatitude(location.getLatitude());
				
			}

		};
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				1000, locListener);

		return myLocation;

	}

	protected void tampilkanPosisikeMap(Location newLocation) {
		List<Overlay> overlays = mapView.getOverlays();

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
		OverlayItem item = new OverlayItem(geopoint, "Me", "Posisi saat ini.");

		overlay.addItem(item);
		mapView.getOverlays().add(overlay);

		for (int i = 0; i < list_lokasi.size(); i++) {
			geopoint = new GeoPoint((int) (list_lokasi.get(i).getLat() * 1E6),
					(int) (list_lokasi.get(i).getLon() * 1E6));
			locationB.setLatitude(geopoint.getLatitudeE6() / 1E6);
			locationB.setLongitude(geopoint.getLongitudeE6() / 1E6);

			icon = getResources().getDrawable(R.drawable.marker);

			icon.setBounds(0, 0, icon.getIntrinsicWidth(),
					icon.getIntrinsicHeight());
			overlay = new MyItemizedOverlay(icon, this);

			item = new OverlayItem(geopoint, list_lokasi.get(i).getName(),
					"Posisi : " + list_lokasi.get(i).getAddress()
							+ "\n Jarak : " + list_lokasi.get(i).getRad()
							+ " km");
			overlay.addItem(item);

			Log.i("Jumlah : ", new String(String.valueOf(overlays.size())));
			mapView.getOverlays().add(overlay);

		}
		// move to location
		mapView.getController().animateTo(myposition);
		mapView.getController().zoomIn();
		mapView.getController().zoomOut();

		// redraw map
		mapView.postInvalidate();
	}

	private void initMap() {
		mapView = (MapView) findViewById(R.id.mymap);
		mapView.displayZoomControls(true);
		mapView.getController().setZoom(16);

	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_baru, menu);
		return true;

	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add:
			addNew();
			break;
		case R.id.rad:
			reload();
			break;
		}
		return true;
	}

	private void reload() {
		alertDialog();
	}

	private void addNew() {
		Intent intent = new Intent(this, AddPlace.class);
		startActivity(intent);
	}

	private void alertDialog() {
		final CharSequence[] items = { "1", "2", "3", "4", "5" };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// builder.setTitle("MENU");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				String a = items[item].toString();
//				myLocation = lonlan();
//				Toast.makeText(
//						getApplicationContext(),
//						a + myLocation.getLatitude()
//								+ myLocation.getLongitude(), Toast.LENGTH_LONG)
//						.show();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

}
