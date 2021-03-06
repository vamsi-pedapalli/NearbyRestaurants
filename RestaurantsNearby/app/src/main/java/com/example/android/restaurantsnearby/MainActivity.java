package com.example.android.restaurantsnearby;

import android.Manifest;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {

    public static final String LOG_TAG = MainActivity.class.getName();

    private StringBuilder sampleURL = new StringBuilder("https://developers.zomato.com/api/v2.1/geocode?lat=29.862&lon=77.8953");
    private String locationProvider;
    private RestaurantAdapter mAdapter;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    private static double userLatitude;
    private static double userLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationProvider = LocationManager.GPS_PROVIDER;
//        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
//        updateURL(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());

        RestaurantsAsynTask task = new RestaurantsAsynTask();
        task.execute(sampleURL.toString());


        locationManager.requestLocationUpdates(locationProvider, 5000, 500, this);
//        task.execute(sampleURL.toString());

        ListView restaurantListView = (ListView) findViewById(R.id.list);
        mAdapter = new RestaurantAdapter(this, new ArrayList<Restaurants>());
        restaurantListView.setAdapter(mAdapter);


        restaurantListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                      @Override
                                                      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                          long viewId = view.getId();

                                                          if (viewId == R.id.loca) {
                                                              Restaurants location = mAdapter.getItem(position);
//                                                              Toast.makeText(getApplicationContext(), "loc is " +location.getLatitude() + location.getLongitude() , Toast.LENGTH_SHORT).show();
//                                                              Uri gmmIntentUri = Uri.parse("geo:"+location.getLatitude()+","+location.getLongitude());
//                                                              Uri gmmIntentUri = Uri.parse("geo:19.0330,73.0297");

                                                              Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?daddr=" + location.getLatitude() + "," + location.getLongitude());
                                                              Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                                              mapIntent.setPackage("com.google.android.apps.maps");
                                                              if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                                                  startActivity(mapIntent);
                                                              } else {
                                                                  Toast.makeText(getApplicationContext(), "no maps app, go to" + location.getLatitude() + "," + location.getLongitude(), Toast.LENGTH_SHORT).show();

                                                              }

                                                          } else {
                                                              {
//                                                                  Toast.makeText(getApplicationContext(), "Restaurant item clicked" + viewId , Toast.LENGTH_SHORT).show();

                                                                  Restaurants presentRestaurant = mAdapter.getItem(position);

                                                                  Uri restaurantUri = Uri.parse(presentRestaurant.getProfileURL());

                                                                  Intent websiteIntent = new Intent(Intent.ACTION_VIEW, restaurantUri);

                                                                  startActivity(websiteIntent);
                                                              }
                                                          }

                                                      }
                                                  }

        );
    }


    @Override
    public void onLocationChanged(Location location) {
        userLatitude = location.getLatitude();
        userLongitude = location.getLongitude();
        updateURL(userLatitude, userLongitude);
        RestaurantsAsynTask task = new RestaurantsAsynTask();
        task.execute(sampleURL.toString());

}

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }


    void updateURL(double lat, double lon) {
        int k = sampleURL.indexOf("=");
        int len = sampleURL.length();
        String kat = lat + "&lon=" + lon;
        sampleURL.replace(k + 1, len + 1, kat);
    }

    public static double getUserLatitude() {
        return userLatitude;
    }

    public static double getUserLongitude() {
        return userLongitude;
    }

    private class RestaurantsAsynTask extends AsyncTask<String, Void, List<Restaurants>> {
        @Override
        protected List<Restaurants> doInBackground(String... URLs) {


            if (URLs.length < 1 || URLs[0] == null) {
                return null;
            }

            List<Restaurants> result = QueryHandler.fetchRestaurantData(URLs[0]);
            return result;
        }

        @Override
        protected void onPostExecute(List<Restaurants> data) {
            mAdapter.clear();
            if (data != null && !data.isEmpty()) {
                mAdapter.addAll(data);
            }
        }
    }
}


