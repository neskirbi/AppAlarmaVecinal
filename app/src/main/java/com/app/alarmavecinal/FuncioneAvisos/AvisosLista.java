package com.app.alarmavecinal.FuncioneAvisos;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.app.alarmavecinal.Adapters.AdapterAlertas;
import com.app.alarmavecinal.Adapters.AdapterAvisos;
import com.app.alarmavecinal.BuildConfig;
import com.app.alarmavecinal.Estructuras.Alertas;
import com.app.alarmavecinal.Estructuras.Avisos;
import com.app.alarmavecinal.Estructuras.Emergencias;
import com.app.alarmavecinal.FuncionAlertas.NewAlerta;
import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AvisosLista extends AppCompatActivity implements AdapterAvisos.RecyclerItemClick {
    Funciones funciones;
    FloatingActionButton agregar;
    RecyclerView avisos_lista;
    ArrayList<Avisos> avisos = new ArrayList();
    Context context;
    ProgressDialog dialog;

    FirebaseDatabase firebaseDatabaseAviso;
    DatabaseReference databaseReferenceAviso;
    ChildEventListener listenerAviso;
    private AdapterAvisos.RecyclerItemClick algo;


    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avisos_lista);
        context=this;
        algo=this;
        funciones=new Funciones(context);


        MobileAds.initialize(this);
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

        //adaptadorAvisos=new AdaptadorAvisos(context);

        if(funciones.GetIdUsuario().equals(funciones.GetIdUsuarioGrupo())){
            agregar.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
       avisos.clear();
        Cargar();
    }

    public void Agregar(View view){
        funciones.Vibrar(funciones.VibrarPush());
        startActivity(new Intent(this, NewAvisos.class));
    }


    public void Descargar(View view){
        funciones.Vibrar(funciones.VibrarPush());
        Descarga descarga=new Descarga();
        descarga.executeOnExecutor(threadPoolExecutor);
    }



    class Descarga extends AsyncTask {
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
            avisos.clear();

            String data="{\"id_grupo\":\""+funciones.GetIdGrupo()+"\"}";
            String respuesta=funciones.Conexion(data,funciones.GetUrl()+getString(R.string.url_GetAvisos),"POST");
            try {

                JSONArray jsonArray=new JSONArray(respuesta);
                if(jsonArray.length()!=0){
                    for (int i =0; i < jsonArray.length();i++){
                        JSONObject jsonObject=new JSONObject(jsonArray.get(i).toString());
                        avisos.add(new Avisos(jsonObject.get("id_aviso").toString(),jsonObject.get("id_usuario").toString(),jsonObject.get("asunto").toString(),jsonObject.get("mensaje").toString(),jsonObject.get("fecha").toString(),jsonObject.toString()));
                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            publishProgress();
            return null;
        }
    }

    void Cargar(){
        firebaseDatabaseAviso = FirebaseDatabase.getInstance();
        databaseReferenceAviso = firebaseDatabaseAviso.getReference("Avisos-"+funciones.GetIdGrupo());
        listenerAviso=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                funciones.Logo("Notificaciones", snapshot.getValue()+"");
                Avisos aviso=snapshot.getValue(Avisos.class);
                avisos.add(0,new Avisos(aviso.getId_aviso(),aviso.getId_usuario(),aviso.getTitulo(),aviso.getMensaje(),aviso.getFecha(),aviso.getJson()));



                avisos_lista=findViewById(R.id.avisos_lista);
                avisos_lista.setLayoutManager(new LinearLayoutManager(context));
                avisos_lista.setAdapter(new AdapterAvisos(avisos,algo));
                if(avisos.size()==0){
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

        databaseReferenceAviso.addChildEventListener(listenerAviso);



    }

    @Override
    public void itemClick(Avisos avisos) {
        funciones.Vibrar(funciones.VibrarPush());
        //startActivity(new Intent(context, AvisosMensaje.class).putExtra("mensaje",avisos.getJson()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(databaseReferenceAviso!=null){
            databaseReferenceAviso.removeEventListener(listenerAviso);
        }

    }
}
