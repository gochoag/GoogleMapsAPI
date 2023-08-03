package com.example.googlemapsapi;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapClickListener {
    PolylineOptions lineas =new PolylineOptions();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        lineas.width(8);
        lineas.color(Color.RED);

    }
    GoogleMap mMap;
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        //satelite
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        //mover a ubicacion

        LatLng madrid = new LatLng(40.6893, -74.0446);

        CameraUpdate camUpd1 =
                CameraUpdateFactory
                        .newLatLngZoom(madrid, 15);
        mMap.moveCamera(camUpd1);


        CameraPosition camPos = new CameraPosition.Builder()
                .target(madrid)
                .zoom(19)
                .bearing(10) //noreste arriba
                .tilt(90) //punto de vista de la cámara 70 grados
                .build();
        CameraUpdate camUpd3 =
                CameraUpdateFactory.newCameraPosition(camPos);
        mMap.animateCamera(camUpd3);
        mMap.setOnMapClickListener(this);
    }
    MarkerOptions marcador;



    int i=1;
    @Override
    public void onMapClick(@NonNull LatLng latLng) {

        marcador = new MarkerOptions();
        marcador.position(latLng);
        marcador.title("Punto");

        mMap.addMarker(marcador);

        lineas.add(latLng);




        if(lineas.getPoints().size()==6){
           lineas.add(lineas.getPoints().get(0));

            mMap.addPolyline(lineas);
            lineas.getPoints().clear();
        }



    }
}