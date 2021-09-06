package com.app.alarmavecinal.FuncionAlertas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.alarmavecinal.Adapters.AdapterAlertas;
import com.app.alarmavecinal.Adapters.AdapterAlertasLista;
import com.app.alarmavecinal.Adapters.AdapterAvisos;
import com.app.alarmavecinal.BuildConfig;
import com.app.alarmavecinal.Estructuras.Alertas;
import com.app.alarmavecinal.Estructuras.AlertasL;
import com.app.alarmavecinal.Estructuras.Avisos;
import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.Principal;
import com.app.alarmavecinal.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AlertasLista extends AppCompatActivity implements AdapterAlertasLista.RecyclerItemClick {
    Funciones funciones;
    ImageView agregar;
    RecyclerView alertas_lista;
    ArrayList<AlertasL> alertas = new ArrayList();
    Context context;

    FirebaseDatabase firebaseDatabaseAlerta;
    DatabaseReference databaseReferenceAlerta;
    ChildEventListener listenerAlerta;

    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
    private AdapterAlertasLista.RecyclerItemClick algo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alertas_lista);
        context=this;
        algo=this;
        funciones=new Funciones(context);


        AdView m = findViewById(R.id.banner);
        AdRequest adRequest = null;
        if (BuildConfig.DEBUG) {
            adRequest=new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
            funciones.Logo("anuncio","debug");
        }else{
            adRequest=new AdRequest.Builder().build();
        }
        m.loadAd(adRequest);
        agregar=findViewById(R.id.agregar);


    }

    @Override
    protected void onResume() {
        super.onResume();
        alertas.clear();
        Cargar();
    }

    public void Agregar(View view){
        funciones.Vibrar(funciones.VibrarPush());
        startActivity(new Intent(context,NewAlerta.class));
    }


    public void Descargar(View view){
        funciones.Vibrar(funciones.VibrarPush());

    }
    ProgressDialog dialog;
    class Descarga2 extends AsyncTask{
        @Override
        protected void onPreExecute() {

            dialog=ProgressDialog.show(context, "",
                    "Cargando...", true);
            dialog.setCancelable(true);
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if(dialog!=null)
                dialog.dismiss();
        }
        @Override
        protected void onProgressUpdate(Object[] values) {

            Cargar();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            alertas.clear();

            String data="{\"id_grupo\":\""+funciones.GetIdGrupo()+"\"}";
            String respuesta=funciones.Conexion(data,funciones.GetUrl()+getString(R.string.url_GetAlertas),"POST");
            try {

                JSONArray jsonArray=new JSONArray(respuesta);
                if(jsonArray.length()!=0){
                    for (int i =0; i < jsonArray.length();i++){
                        JSONObject jsonObject=new JSONObject(jsonArray.get(i).toString());
                        alertas.add(new AlertasL(jsonObject.get("id_alerta").toString(),jsonObject.get("id_usuario").toString(),jsonObject.get("imagen").toString(),jsonObject.get("asunto").toString(),jsonObject.get("nombre").toString(),jsonObject.get("fecha").toString(),jsonObject.toString()));
                    }


                }
            } catch (JSONException e) {
               funciones.Logo("carga","Error: "+e.getMessage());
            }
            publishProgress();
            return null;
        }
    }


    void Cargar(){

        firebaseDatabaseAlerta = FirebaseDatabase.getInstance();
        databaseReferenceAlerta = firebaseDatabaseAlerta.getReference("Alertas-"+funciones.GetIdGrupo());
        listenerAlerta=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                funciones.Logo("Notificaciones", snapshot.getValue()+"");
                AlertasL alerta=snapshot.getValue(AlertasL.class);
                alertas.add(0,new AlertasL(alerta.getId_alerta(),alerta.getId_usuario(),alerta.getImagen(),alerta.getTitulo(),alerta.getNombre(),alerta.getFecha(),alerta.getJson()));


                alertas_lista=findViewById(R.id.alertas_lista);

                alertas_lista.setLayoutManager(new LinearLayoutManager(context));
                alertas_lista.setAdapter(new AdapterAlertasLista(alertas,algo));
                if(alertas.size()==0){
                    funciones.VerificaConexion();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                funciones.Logo("Notificaciones","onChildChanged");

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                funciones.Logo("Notificaciones","onChildRemoved");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                funciones.Logo("Notificaciones","onChildMoved");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                funciones.Logo("Notificaciones","onCancelled");
            }
        };

        databaseReferenceAlerta.addChildEventListener(listenerAlerta);


       

    }
    @Override
    public void itemClick(AlertasL alertas) {
        funciones.Vibrar(funciones.VibrarPush());
        //startActivity(new Intent(context,AlertasMensaje.class).putExtra("mensaje",alertas.get(position).getJson()));
    }


    @Override
    protected void onPause() {
        super.onPause();

        if(databaseReferenceAlerta!=null){
            databaseReferenceAlerta.removeEventListener(listenerAlerta);
        }
    }
}
