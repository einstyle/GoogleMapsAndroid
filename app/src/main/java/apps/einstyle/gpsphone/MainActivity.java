package apps.einstyle.gpsphone;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, LocationListener {

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private String provider;
    double latitude; // latitude
    double longitude; // longitude
    private boolean canGetLocation;

    // The minimum distance to change Updates in meters
    //private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 1 meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;

    // The minimum time between updates in milliseconds
    //private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 60 minute
    private static final long MIN_TIME_BW_UPDATES = 1000 * 1 * 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }

        Location loc = null;
        Location location = getLocation(loc);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Set the long click listener as a way to exit the map.
        mMap.setOnMapLongClickListener(this);

        LatLng List = new LatLng(49.503004, 5.944756);
        mMap.addMarker(new MarkerOptions().position(List).title("LIST"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(List));
        mMap.moveCamera(CameraUpdateFactory.zoomTo( 8.0f ));
    }

    public Location getLocation(Location location) {
        try {
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            location = null;
            if (!isGPSEnabled && !isNetworkEnabled) {
                Log.d("No Network", "{LOGGING} !isGPSEnabled && !isNetworkEnabled");
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    Log.d("Network", "{LOGGING} Network Enabled true");
                    mLocationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (mLocationManager != null) {
                        location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        Log.d("Location Manager", "{LOGGING} mLocationManager not null");
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                } else {
                    Log.d("Network", "{LOGGING} Network Enabled false");
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    Log.d("isGPSEnabled", "{LOGGING} isGPSEnabled true");
                    if (location == null) {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return null;
                        }
                        mLocationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (mLocationManager != null) {
                            location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                LatLng myLocation = new LatLng(latitude, longitude);
                                mMap.addMarker(new MarkerOptions().position(myLocation).title("YOUR LOCATION"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                                mMap.moveCamera(CameraUpdateFactory.zoomTo( 20.0f ));
                            }
                        }
                    }
                } else {
                    Log.d("isGPSEnabled", "{LOGGING} isGPSEnabled false");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng myLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(myLocation).title("YOUR LOCATION"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        mMap.moveCamera(CameraUpdateFactory.zoomTo( 20.0f ));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled provider " + provider, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Enabled provider " + provider, Toast.LENGTH_SHORT).show();
    }

    protected void onPause() {
        super.onPause();
        mLocationManager.removeUpdates((LocationListener) this);
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Integer.parseInt(Manifest.permission.ACCESS_COARSE_LOCATION));

            } else {
                //location = mLocationManager.getLastKnownLocation(provider);
            }
            //location = mLocationManager.getLastKnownLocation(provider);
        } catch (Exception e) {
            Log.e("ERROR", "ERROR IN CODE. ");
            e.printStackTrace();
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }
}
