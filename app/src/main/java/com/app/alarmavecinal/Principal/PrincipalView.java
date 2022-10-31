package com.app.alarmavecinal.Principal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.view.Menu;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.app.alarmavecinal.BuildConfig;
import com.app.alarmavecinal.Alertas.AlertasLista;
import com.app.alarmavecinal.Avisos.AvisosLista;
import com.app.alarmavecinal.ChatFb.SalaChat;
import com.app.alarmavecinal.Models.Ordenes;
import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.Vecinos.GrupoView;
import com.app.alarmavecinal.Mapas.MapaEmergencia;
import com.app.alarmavecinal.R;
import com.app.alarmavecinal.Sugerencias.SubirSugerencia;
import com.app.alarmavecinal.Usuario.Login.Login;
import com.app.alarmavecinal.Usuario.Login.LoginView;
import com.app.alarmavecinal.Yo.YoView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;


public class PrincipalView extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Principal.PrincipalView {
    NavigationView navigationView;
    Menu nav_Menu;
    Toolbar toolbar;
    DrawerLayout drawer;
    ImageButton emergencia;
    long then = 0;
    Funciones funciones;
    Boolean vi = false;
    View headerView;
    TextView nombre,nota,direccion;
    Context context;
    double lat=0,lon=0;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;



    LocationListener locationListener = null;
    BottomNavigationView bottom_navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;
        funciones = new Funciones(context);
        bottom_navigation=findViewById(R.id.bottom_navigation);


        //funciones.PedirPermisoLocation(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        nota = findViewById(R.id.nota);
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

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        nav_Menu = navigationView.getMenu();
        //Oculatando botones si no se esta en un grupo
        CrearMenu();

        IniciandoFuncionDelmenu();
        VerificandoLogin();
        setNombre();


        funciones.VerificarServicios();

        emergencia = findViewById(R.id.emergencia);

        then = 0;
        BotonEmergencia();

        bottom_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                funciones.Vibrar(funciones.VibrarPush());
                switch(item.getItemId()){
                    case R.id.datos:
                        startActivity(new Intent(context, YoView.class));
                        break;
                    case R.id.chat:
                        if (funciones.GetIdGrupo() != "") {
                            //startActivity(new Intent(context, ChatWindow.class));
                            startActivity(new Intent(context, SalaChat.class));
                        } else {
                            funciones.SinGrupo();
                        }
                        break;
                }
                return false;
            }
        });

    }

    private void VerificandoLogin() {
        if (!funciones.Check_Log()) {
            startActivity(new Intent(this, Login.class));
        }
    }

    private void IniciandoFuncionDelmenu() {
        navigationView.setItemIconTintList(null);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);
        nombre = headerView.findViewById(R.id.nombre);
    }

    private void BotonEmergencia() {
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
                                databaseReference = firebaseDatabase.getReference("Ordenes/" + funciones.GetIdGrupo());//Sala de chat
                                databaseReference.setValue("");
                                databaseReference.push().setValue(new Ordenes(1,funciones.GetUIID(), funciones.GetIdUsuario(), funciones.GetNombre(), "Emergencia!!! \n", "{\"direccion\":\"" + funciones.GetDireccion() + "\",\"ubicacion\":\"" + latt + "," + lont + "\"}", funciones.GetDate()));
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
    }





    private void CrearMenu() {
        if (funciones.GetIdGrupo().replace(" ", "").length() != 32) {
            nav_Menu.findItem(R.id.nav_alertas).setVisible(false);
            nav_Menu.findItem(R.id.nav_avisos).setVisible(false);
            nav_Menu.findItem(R.id.nav_emergencias).setVisible(false);
            nav_Menu.findItem(R.id.nav_grupo).setTitle("Crear Grupo");
        }
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
    }



    @Override
    protected void onPause() {
        super.onPause();

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


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        funciones.Vibrar(funciones.VibrarPush());


        if (id == R.id.nav_alertas) {
            if (funciones.GetIdGrupo() != "") {
                startActivity(new Intent(this, AlertasLista.class));
            } else {
                funciones.SinGrupo();
            }
        } else if (id == R.id.nav_grupo) {
            startActivity(new Intent(this, GrupoView.class));
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

                            context.startActivity(new Intent(context.getApplicationContext(), LoginView.class));

                        }
                    });

            alertDialog.setNegativeButton("Cancelar",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            alertDialog.show();
        }



        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void setNombre() {
        nombre.setText(funciones.GetNombre());
    }








}

