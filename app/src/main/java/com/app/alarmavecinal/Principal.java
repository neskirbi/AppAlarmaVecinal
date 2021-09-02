package com.app.alarmavecinal;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.view.Menu;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.app.alarmavecinal.FuncionAlertas.AlertasLista;
import com.app.alarmavecinal.FuncioneAvisos.AvisosLista;
import com.app.alarmavecinal.ChatFb.SalaChat;
import com.app.alarmavecinal.EditarInfo.Datos;
import com.app.alarmavecinal.Estructuras.Emergencias;
import com.app.alarmavecinal.Grupos.Grupo;
import com.app.alarmavecinal.LoginPack.Login;
import com.app.alarmavecinal.Mapas.MapaEmergencia;
import com.app.alarmavecinal.Sugerencias.SubirSugerencia;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class Principal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ImageButton emergencia;
    long then = 0;
    Funciones funciones;
    Boolean vi = false;
    View headerView;
    TextView nombre,nota,direccion;
    Context context;
    double lat=0,lon=0;
    ImageView localizar_on;
    SwitchMaterial switchgps;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    LocationManager mLocationManager = null;
    LocationListener locationListener = null;
    private String proovedor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;
        funciones = new Funciones(context);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        localizar_on = findViewById(R.id.localizar_on);
        nota = findViewById(R.id.nota);
        switchgps = findViewById(R.id.switchgps);
        direccion=findViewById(R.id.direccion);
        direccion.setText(funciones.GetDireccionAntes());




        //borrar el registro del grupo si  esta en ""  por que guardaba el grupo vacio
        if (funciones.GetIdGrupo().replace(" ", "").length() != 32) {
            funciones.SalirGrupo();
        }


        MobileAds.initialize(this);
        AdView m = findViewById(R.id.banner);
        AdRequest adRequest = null;
        if (BuildConfig.DEBUG) {
            adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
            funciones.Logo("anuncio", "debug");
        } else {
            adRequest = new AdRequest.Builder().build();
        }
        m.loadAd(adRequest);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);
        nombre = headerView.findViewById(R.id.nombre);
        if (!funciones.Check_Log()) {
            startActivity(new Intent(this, Login.class));
        }
        setNombre();

        funciones.VerificarServicios();

        emergencia = findViewById(R.id.emergencia);

        then = 0;
        emergencia.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    vi = true;
                    Vibrando();
                    then = (Long) System.currentTimeMillis();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    vi = false;
                    if (((Long) System.currentTimeMillis() - then) > 1500) {
                        if (funciones.GetIdGrupo() != "") {
                            Toast.makeText(context, "Se activa Emergencia...", Toast.LENGTH_SHORT).show();
                            String latt="",lont="";
                            try {
                                if(funciones.GetUbicacion().replace(" ","").length()>0){
                                    JSONObject ubicacion=new JSONObject(funciones.GetUbicacion());
                                    latt=ubicacion.getString("lat");
                                    lont=ubicacion.getString("lon");
                                }


                                //funciones.Conexion("{\"id_usuario\":\""+funciones.GetIdUsuario()+"\",\"id_grupo\":\""+funciones.GetIdGrupo()+"\",\"tipo\":\"1\"}",funciones.GetUrl()+getString(R.string.url_SetEmergencia));
                                firebaseDatabase = FirebaseDatabase.getInstance();
                                databaseReference = firebaseDatabase.getReference("EmergenciaVecinos-" + funciones.GetIdGrupo());//Sala de chat
                                databaseReference.push().setValue(new Emergencias(funciones.GetUIID(), funciones.GetIdUsuario(), funciones.GetNombre(), "Emergencia!!! \n", "{\"direccion\":\"" + funciones.GetDireccion() + "\",\"ubicacion\":\"" + latt + "," + lont + "\"}", funciones.GetDate()));
                            } catch (JSONException e) {
                                e.printStackTrace();
                                funciones.Logo("Errorfb",e.getMessage());
                            }

                        } else {
                            Toast.makeText(context, "¡Primero debes crear ó unirte a un grupo!", Toast.LENGTH_SHORT).show();
                        }

                        return true;
                    } else {
                        Toast.makeText(context, "Precionar por 2 segundos para activar.", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        localizar_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funciones.Vibrar(funciones.VibrarPush());
                localizar_on.setEnabled(false);


                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    PedirPermisoLocation();
                } else {

                    VerificarUbicacion();
                    mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    locationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            lat=location.getLatitude();
                            lon=location.getLongitude();
                            funciones.SetUbicacion("{\"lat\":\""+lat+"\",\"lon\":\""+lon+"\"}");
                            if(funciones.GetUbicacion().replace(" ","").length()>0){
                                nota.setText("Ubicación guardada.");
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    nota.setTextColor(getColor(R.color.colorPrimary));
                                }
                            }

                            direccion.setText(funciones.GetDireccionAhora(lat,lon));

                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {



                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                            VerificarUbicacion();
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                            VerificarUbicacion();
                        }
                    };

                    if (locationListener != null) {
                        proovedor=LocationManager.GPS_PROVIDER;
                        mLocationManager.requestLocationUpdates(proovedor, 0, 0, locationListener);
                    } else {
                        Toast.makeText(context, "listener null", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        switchgps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(context, "Encendido.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Apagado.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }





    void Vibrando() {
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                int cont = 0;
                while (vi) {
                    if (cont >= 20) {
                        vi = false;
                    }
                    cont++;
                    Thread tr = new Thread();
                    try {
                        tr.sleep(20);
                        funciones.Vibrar(funciones.VibrarPush());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };

        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        funciones.ForzarEnviador();
        VerificarUbicacion();
        localizar_on.setEnabled(true);
    }

    private void VerificarUbicacion() {
        if(!funciones.VerificarSwitchGPS()){
            nota.setText("Encender GPS.");
            Toast.makeText(context, "Encender GPS.", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                nota.setTextColor(getColor(R.color.error));
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locationListener != null)
            mLocationManager.removeUpdates(locationListener);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        funciones.Vibrar(funciones.VibrarPush());


        if (id == R.id.nav_datos) {
            startActivity(new Intent(this, Datos.class));
        } else if (id == R.id.nav_chat) {
            if (funciones.GetIdGrupo() != "") {
                //startActivity(new Intent(this, ChatWindow.class));
                startActivity(new Intent(this, SalaChat.class));
            } else {
                funciones.SinGrupo();
            }

        } else if (id == R.id.nav_alertas) {
            if (funciones.GetIdGrupo() != "") {
                startActivity(new Intent(this, AlertasLista.class));
            } else {
                funciones.SinGrupo();
            }
        } else if (id == R.id.nav_grupo) {
            startActivity(new Intent(this, Grupo.class));
        }
        else if (id == R.id.nav_emergencias) {
            if (funciones.GetIdGrupo() != "") {
                startActivity(new Intent(this, MapaEmergencia.class));
            }else{
                funciones.SinGrupo();
            }
        }else if (id == R.id.nav_avisos) {
            if (funciones.GetIdGrupo() != "") {
                startActivity(new Intent(this, AvisosLista.class));
            } else {
                funciones.SinGrupo();
            }

        } else if (id == R.id.nav_sugerencia) {
            startActivity(new Intent(this, SubirSugerencia.class));
        } else if (id == R.id.nav_propinas) {

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/donate?hosted_button_id=HM368K4HZ4RC6"));
            startActivity(browserIntent);
        }
        else if (id == R.id.nav_salir) {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setTitle("Salir");
            alertDialog.setMessage("Si cierras sesión no recibirás notificaciones, ¿Desea salir?");


            alertDialog.setIcon(R.drawable.boton_alerta);

            alertDialog.setPositiveButton("Salir",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            funciones.LogOut();
                            startActivity(new Intent(context, Login.class));

                        }
                    });

            alertDialog.setNegativeButton("Cancelar",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            alertDialog.show();
        }/*else if (id == R.id.nav_camara) {
            if(funciones.GetIdGrupo()!=""){
                if(PedirPermisoCamara()) {
                    startActivity(new Intent(this, ListaTransmision.class));
                }
            }else{
                funciones.SinGrupo();
            }
        }*/


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void setNombre() {
        nombre.setText(funciones.GetNombre());
    }

    public Boolean PedirPermisoLocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permsRequestCode = 100;
            String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};

            int camara = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

            if (camara == PackageManager.PERMISSION_GRANTED) {

                return true;
            } else {

                requestPermissions(perms, permsRequestCode);
                return false;
            }

        }
        return true;
    }




}

