package com.app.alarmavecinal.Yo.Datos;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;

public class DatosView extends AppCompatActivity implements OnMapReadyCallback, Datos.DatosView {
    EditText nombres, apellidos, direccion,ubicacion;
    Context context;
    Funciones funciones;
    ProgressDialog dialog;
    GoogleMap googleMap;
    boolean unavez = true;
    DatosPresenter datosPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos);
        setTitle("Editar Info.");
        context = this;
        funciones = new Funciones(context);
        datosPresenter=new DatosPresenter(this,context);
        datosPresenter.GetDatos();

        nombres = findViewById(R.id.nombre);
        apellidos = findViewById(R.id.apellidos);
        direccion = findViewById(R.id.direccion);

        ubicacion = findViewById(R.id.ubicacion);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);


    }



    public void Actualizar(View view){

        funciones.Vibrar(funciones.VibrarPush());
        datosPresenter.Actualizar(nombres.getText().toString(),apellidos.getText().toString(),direccion.getText().toString(),ubicacion.getText().toString());
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

            @Override
            public void onMyLocationChange(Location arg0) {

                if(unavez){
                    CamaraPosition(arg0.getLatitude(),arg0.getLongitude());
                    ubicacion.setText("{\"lat\":\""+arg0.getLatitude()+"\",\"lon\":\""+arg0.getLongitude()+"\"}");
                    unavez=false;
                }

            }
        });
    }

    public void CamaraPosition(double latitude, double longitude){

        if(latitude!=0.0 && longitude!=0.0 ){
            LatLng ubicacion = new LatLng(latitude, longitude);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(ubicacion)      // Sets the center of the map to Mountain View
                    .zoom(15)                   // Sets the zoom
                    .bearing((float) 0)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

    }

    @Override
    public void MostrarDatos(JsonArray body) {
        nombres.setText(funciones.GetIndex(body,"nombres"));
        apellidos.setText(funciones.GetIndex(body,"apellidos"));
        direccion.setText(funciones.GetIndex(body,"direccion"));
    }
}
