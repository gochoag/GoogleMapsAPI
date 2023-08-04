package com.example.googlemapsapi;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapClickListener {

    private final String API_KEY = "TOKEN";

    PolylineOptions lineas =new PolylineOptions();
    MarkerOptions marcador;
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

        LatLng UTEQ = new LatLng(1.274619, -78.813340);

        CameraUpdate camUpd1 =
                CameraUpdateFactory
                        .newLatLngZoom(UTEQ, 15);
        mMap.moveCamera(camUpd1);
        CameraPosition camPos = new CameraPosition.Builder()
                .target(UTEQ)
                .zoom(19)
                .bearing(10) //noreste arriba
                .tilt(90) //punto de vista de la cámara 70 grados
                .build();
        CameraUpdate camUpd3 =
                CameraUpdateFactory.newCameraPosition(camPos);
        mMap.animateCamera(camUpd3);
        mMap.setOnMapClickListener(this);
    }
    ArrayList<LatLng> ListPuntos = new ArrayList<>();
    int i=1;
    @Override
    public void onMapClick(@NonNull LatLng latLng) {

        marcador = new MarkerOptions();
        marcador.position(latLng);
        marcador.title("Punto");

        mMap.addMarker(marcador);

        lineas.add(latLng);
        ListPuntos.add(latLng);
        if(lineas.getPoints().size()==6){
           lineas.add(lineas.getPoints().get(0));
            mMap.addPolyline(lineas);

            calcularDistanciaTotal(ListPuntos);


            lineas.getPoints().clear();
        }
    }




    private void calcularDistanciaTotal(ArrayList<LatLng> puntos) {
        StringBuilder origins = new StringBuilder();
        StringBuilder destinations = new StringBuilder();
        for (LatLng punto : puntos) {
            origins.append(punto.latitude).append(",").append(punto.longitude).append("|");
            destinations.append(punto.latitude).append(",").append(punto.longitude).append("|");
        }
        origins.setLength(origins.length() - 1);
        destinations.setLength(destinations.length() - 1);

        // Construye la URL de la solicitud
        String urlString = "https://maps.googleapis.com/maps/api/distancematrix/json" +
                "?origins=" + origins +
                "&destinations=" + destinations +
                "&units=metric" +
                "&key=" + API_KEY;

        // Realiza la solicitud en segundo plano
        new DistanceMatrixTask().execute(urlString);
    }

    private class DistanceMatrixTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                connection.disconnect();
                return response.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                // Procesa la respuesta JSON y obtén la distancia total
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray rows = jsonResponse.getJSONArray("rows");
                    JSONObject row = rows.getJSONObject(0);
                    JSONArray elements = row.getJSONArray("elements");

                    int totalDistance = 0;
                    for (int i = 0; i < elements.length(); i++) {
                        JSONObject element = elements.getJSONObject(i);
                        JSONObject distance = element.getJSONObject("distance");
                        int distanceValue = distance.getInt("value");
                        totalDistance += distanceValue;
                    }

                    // Muestra un AlertDialog con la distancia total
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Distancia Total");
                    builder.setMessage("La distancia total es: " + totalDistance + " metros");
                    builder.setPositiveButton("Aceptar", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // Manejo de error
            }
        }
    }
}