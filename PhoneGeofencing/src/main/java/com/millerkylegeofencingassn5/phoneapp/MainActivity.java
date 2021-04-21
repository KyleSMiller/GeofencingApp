package com.millerkylegeofencingassn5.phoneapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.OnLocationCameraTransitionListener;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.millerkylegeofencingassn5.api.Verify;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private boolean isFencing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_main);
        // Verify.verifyPhoneApp();

        // get GPS permissions
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // Map
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded(){
                    @Override
                    public void onStyleLoaded(@NonNull Style style){
                        // request geo location permissions
                        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        }

                        // display the user's location on the map
                        LocationComponent locationComponent = mapboxMap.getLocationComponent();
                        locationComponent.activateLocationComponent(
                                LocationComponentActivationOptions.builder(MainActivity.this, style)
                                        .useSpecializedLocationLayer(true)
                                        .build()
                        );
                        locationComponent.setLocationComponentEnabled(true);


                        // zoom in on the user at startup
                        locationComponent.setCameraMode(CameraMode.TRACKING_GPS_NORTH, new OnLocationCameraTransitionListener() {
                            @Override
                            public void onLocationCameraTransitionFinished(int cameraMode) {
                                locationComponent.zoomWhileTracking(17, 100);
                            }

                            @Override
                            public void onLocationCameraTransitionCanceled(int cameraMode) {

                            }
                        });
                        LocationComponentOptions options = locationComponent.getLocationComponentOptions().toBuilder()
                                //.trackingGesturesManagement(true)
                                .build();

                    }
                });
            }
        });

        // geo-fencing controls
        MaterialToolbar toolbar = findViewById(R.id.topBar);
        MaterialButton finishButton = new MaterialButton(this);
        finishButton.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_baseline_check_24));
        finishButton.setOnClickListener(view -> {
            // calculate area and display on screen
            // clear array
            isFencing = false;
            toolbar.removeView(finishButton);
            System.out.println("<-------------------- FINISHED AREA -------------------->");
        });

        isFencing = false;

        // start new geo-fence area
        FloatingActionButton pointButton = findViewById(R.id.pointButton);
        pointButton.setOnClickListener(view -> {
            if(!isFencing) {
                isFencing = true;
                toolbar.addView(finishButton);
                // create array
                System.out.println("<-------------------- STARTED AREA -------------------->");
            }
            else{
                // add point to array
                System.out.println("<-------------------- RECORDED NEW POINT -------------------->");
            }
        });

        // finish go-fence area


    }
}