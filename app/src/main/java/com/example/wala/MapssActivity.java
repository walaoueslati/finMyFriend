package com.example.wala;

import androidx.fragment.app.FragmentActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.example.wala.databinding.ActivityMapssBinding;

public class MapssActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapssBinding binding;
    private double latitude = 0.0;
    private double longitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapssBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Vérifier permissions si tu veux récupérer localisation
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
        }

        // Récupérer les coordonnées depuis l'intent
        if (getIntent() != null) {
            latitude = getIntent().getDoubleExtra("latitude", 0.0);
            longitude = getIntent().getDoubleExtra("longitude", 0.0);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);
        else Toast.makeText(this, "Erreur : fragment map introuvable", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (latitude != 0.0 || longitude != 0.0) {
            LatLng location = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(location).title("Friend's Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
        } else {
            Toast.makeText(this, "Aucune coordonnée reçue", Toast.LENGTH_SHORT).show();
            LatLng defaultLocation = new LatLng(36.8, 10.2); // exemple Tunisie
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 6f));
        }
    }
}
