package com.app.alarmavecinal.Alertas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
//import android.support.v7.app.AlertDialog;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.app.alarmavecinal.Adapters.AdapterAlertas;
import com.app.alarmavecinal.BuildConfig;
import com.app.alarmavecinal.Estructuras.Alertas;
import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NewAlerta extends AppCompatActivity implements AdapterAlertas.RecyclerItemClick, com.app.alarmavecinal.Alertas.Alertas.AlertasView {
    Funciones funciones;
    ImageView agregar;
    RecyclerView alertas_lista;
    ArrayList<Alertas> alertas = new ArrayList();
    AlertasPresenter alertasPresenter;
    Context context;
    ProgressDialog dialog;

    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alerta);
        context=this;
        funciones=new Funciones(context);
        funciones=new Funciones(context);
        alertasPresenter=new AlertasPresenter(this,context);

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
        alertasPresenter.GetPreAlertas();
    }

    public void Agregar(View view){
        funciones.Vibrar(funciones.VibrarPush());
        startActivity(new Intent(context,NewAlerta.class));
    }


    public void Descargar(View view){
        funciones.Vibrar(funciones.VibrarPush());
        alertasPresenter.GetPreAlertas();
    }




    void Cargar(){

        alertas_lista=findViewById(R.id.alertas_lista);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        alertas_lista.setLayoutManager(mLayoutManager);
        alertas_lista.setAdapter(new AdapterAlertas(alertas,this));
        if(alertas.size()==0){
            funciones.VerificaConexion();
        }


    }


    public void Pop(final Alertas json) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Alerta");
        alertDialog.setMessage("¿Enviar Alerta?");



        alertDialog.setIcon(R.drawable.img_menu_grupo);

        alertDialog.setPositiveButton("Enviar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        funciones.Vibrar(funciones.VibrarPush());

                        alertasPresenter.EnviarAlerta(json);

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


    @Override
    public void itemClick(Alertas alertas) {
        funciones.Vibrar(funciones.VibrarPush());
        Pop(alertas);
    }


    @Override
    public void LlenarLista(JsonArray jsonArray) {

        if(funciones.GetData(jsonArray).size()!=0){
            for (int i =0; i < funciones.GetData(jsonArray).size();i++){
                alertas.add(new Alertas(funciones.GetIndex2(funciones.GetData(jsonArray),i,"id_alerta"),funciones.GetIndex2(funciones.GetData(jsonArray),i,"imagen") ,funciones.GetIndex2(funciones.GetData(jsonArray),i,"asunto"), funciones.GetIndex2(funciones.GetData(jsonArray),i,"created_at"),funciones.GetData(jsonArray).get(i).toString()));
            }

            Cargar();

        }
    }

    @Override
    public void Salir() {
        //finish();
    }
}
