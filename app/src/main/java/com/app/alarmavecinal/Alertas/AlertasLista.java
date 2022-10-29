package com.app.alarmavecinal.Alertas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.alarmavecinal.Adapters.AdapterAlertasLista;
import com.app.alarmavecinal.BuildConfig;
import com.app.alarmavecinal.Estructuras.AlertasL;
import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AlertasLista extends AppCompatActivity {
    Funciones funciones;
    ImageView agregar;
    LinearLayout alertas_lista;
    ArrayList<AlertasL> alertas = new ArrayList();
    Context context;



    LinearLayout avisos_lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alertas_lista);
        context=this;

        funciones=new Funciones(context);

        avisos_lista=findViewById(R.id.avisos_lista);

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

    private void Cargar() {
        JsonArray jsonArray=funciones.GetAlertas();
        funciones.Logo("ListaAlertas",jsonArray+"");
        for (int i=0;i<jsonArray.size();i++){

            LayoutInflater inflater = getLayoutInflater();
            View item = inflater.inflate(R.layout.row_alerta, null);

            TextView titulo=item.findViewById(R.id.titulo);
            titulo.setText(funciones.GetIndex2(jsonArray,i,"asunto"));

            TextView mensaje=item.findViewById(R.id.mensaje);
            mensaje.setText(funciones.GetIndex2(jsonArray,i,"mensaje"));

            TextView fecha=item.findViewById(R.id.fecha);
            fecha.setText(funciones.GetIndex2(jsonArray,i,"created_at"));
            avisos_lista.addView(item);
        }




    }

    public void Agregar(View view){
        funciones.Vibrar(funciones.VibrarPush());
        startActivity(new Intent(context,NewAlerta.class));
    }


    public void Descargar(View view){
        funciones.Vibrar(funciones.VibrarPush());

    }






    @Override
    protected void onPause() {
        super.onPause();

    }
}
