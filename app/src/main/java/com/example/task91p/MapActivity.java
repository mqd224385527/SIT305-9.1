package com.example.task91p;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseHelper databaseHelper;
    private static final String TAG = "MapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        databaseHelper = new DatabaseHelper(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        displayItems();
    }

    private void displayItems() {
        List<Item> items = databaseHelper.getAllItems();
        
        if (items.isEmpty()) {
            Log.d(TAG, "No items to display on map");
            return;
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        boolean hasValidCoordinates = false;

        for (Item item : items) {
            double lat = item.getLatitude();
            double lng = item.getLongitude();
            
            if (lat != 0 && lng != 0) {
                LatLng position = new LatLng(lat, lng);
                float markerColor = item.getType().equals("Lost") ? 
                        BitmapDescriptorFactory.HUE_RED : BitmapDescriptorFactory.HUE_GREEN;
                
                mMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(item.getType() + ": " + item.getName())
                        .snippet(item.getDescription())
                        .icon(BitmapDescriptorFactory.defaultMarker(markerColor)));
                
                builder.include(position);
                hasValidCoordinates = true;
            }
        }

        if (hasValidCoordinates) {
            LatLngBounds bounds = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } else {
            // Default to Melbourne, Australia if no valid coordinates
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-37.8136, 144.9631), 10));
        }
    }
} 