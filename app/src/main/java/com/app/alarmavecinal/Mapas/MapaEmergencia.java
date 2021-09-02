package com.app.alarmavecinal.Mapas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.alarmavecinal.Adapters.AdapterEmergencias;
import com.app.alarmavecinal.BuildConfig;
import com.app.alarmavecinal.Estructuras.Emergencias;
import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class MapaEmergencia extends AppCompatActivity implements OnMapReadyCallback, AdapterEmergencias.RecyclerItemClick {

    TextView tag_nombre;
    Funciones funciones = null;
    Context context;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener listener;
    RecyclerView emergencias_lista;
    ArrayList<Emergencias> emergencias = new ArrayList<>();
    private MapaEmergencia algo;
    private GoogleMap googleMap;
    String verlat = "", verlon = "";
    String vernombre = "", verdireccion = "";
    boolean switchcoor=true;

    FloatingActionButton verruta;
    LinearLayout contenedor_verruta;
    private ArrayList<Double> Alat;
    private ArrayList<Double> Alon;
    private ArrayList<String> Anombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_emergencia);
        setTitle("Emergencia");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        context = this;
        algo = this;
        funciones = new Funciones(context);
        emergencias_lista = findViewById(R.id.emergencias_lista);
        contenedor_verruta = findViewById(R.id.contenedor_verruta);
        verruta = findViewById(R.id.verruta);
        tag_nombre = findViewById(R.id.tag_nombre);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);


        MobileAds.initialize(this);
        AdView m = findViewById(R.id.banner);
        AdRequest adRequest = null;
        if (BuildConfig.DEBUG) {
            adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        } else {
            adRequest = new AdRequest.Builder().build();
        }
        m.loadAd(adRequest);


        verruta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funciones.Vibrar(funciones.VibrarPush());
                if(funciones.VerificarSwitchGPS()){
                    PedirPermisoLocation();
                }else{
                    Toast.makeText(context, "Encender GPS.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        ObtenerCoorVivo();

    }

    private void ObtenerCoorVivo() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }else{
            googleMap.setMyLocationEnabled(true);
            // Check if we were successful in obtaining the map.
            if (googleMap != null) {
                googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location coordenadas) {
                        if(switchcoor)
                            MoverYo(coordenadas.getLatitude(), coordenadas.getLongitude(), 10);
                        switchcoor=false;

                    }
                });
            }
        }

    }

    private void MoverYo(double latitude, double longitude, int altura) {
        LatLng coordenadas = new LatLng(latitude, longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordenadas, altura));
    }


    public void CargarListayMarcar(){
        ///Cargamos la lista de emergencias y llenamos un arreglo para marcar las ubicaciones

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("EmergenciaVecinos-"+funciones.GetIdGrupo());//Sala de chat

        listener=new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                funciones.Logo("Emergencia", snapshot.getValue()+"");
                Emergencias emergencia=snapshot.getValue(Emergencias.class);
                emergencias.add(0,new Emergencias(emergencia.getId_emergencia(),emergencia.getId_usuario(),emergencia.getNombre(),emergencia.getMensaje(),emergencia.getUbicacion(),emergencia.getFecha() ));
                emergencias_lista.setLayoutManager(new LinearLayoutManager(context));
                emergencias_lista.setAdapter(new AdapterEmergencias(emergencias,algo));

                //Marcando una por una
                JSONObject ubicaciont= null;
                try {
                    ubicaciont = new JSONObject(emergencia.getUbicacion());
                    Marcar(emergencia.getNombre(),ubicaciont.getString("ubicacion"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        };
        databaseReference.addChildEventListener(listener);
    }

    public Boolean PedirPermisoLocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            int permsRequestCode = 100;
            String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};

            int location = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

            if (location == PackageManager.PERMISSION_GRANTED ) {
                startActivity(new Intent(context,Ruta.class).putExtra("lat",verlat).putExtra("lon",verlon).putExtra("nombre",vernombre).putExtra("direccion",verdireccion));
                return true;
            } else {

                requestPermissions(perms, permsRequestCode);
                return false;
            }

        }
        return true;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        emergencias.clear();
        CargarListayMarcar();

    }

    @Override
    public void itemClick(Emergencias emergencia, int position) {
        funciones.Vibrar(funciones.VibrarPush());
        try {
            JSONObject ubicaciont=new JSONObject(emergencia.getUbicacion());

            Mover(emergencia.getNombre(),ubicaciont.getString("direccion"),ubicaciont.getString("ubicacion"), 16);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("coordenadas2","Error:" +e.getMessage());
        }


    }

    public void Marcar(String nombre, String ubicaciont){
        String[] ubicacion=ubicaciont.split(",");
        if(ubicacion.length==2) {
            String lat=ubicacion[0];
            String lon=ubicacion[1];
            LatLng coordenadas = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
            googleMap.addMarker(new MarkerOptions().position(coordenadas).title(nombre)).showInfoWindow();
        }

    }
    public void Mover(String nombre,String direccion,String ubicaciont,float altura){
        String[] ubicacion=ubicaciont.split(",");

        tag_nombre.setText(nombre);
        contenedor_verruta.setVisibility(View.VISIBLE);
        vernombre=nombre;
        verdireccion=direccion;

        if(ubicacion.length==2){
            String lat=ubicacion[0];
            String lon=ubicacion[1];
            LatLng coordenadas = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordenadas, altura));
            //Datos Para ir

            verlat=lat;
            verlon=lon;
        }else{
            Toast.makeText(context, "Sin coordenadas.", Toast.LENGTH_SHORT).show();
            //contenedor_verruta.setVisibility(View.GONE);
            verlat="";
            verlon="";
        }
    }

}