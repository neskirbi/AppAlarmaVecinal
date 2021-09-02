package com.app.alarmavecinal.Transmision;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class VerTransmision extends AppCompatActivity {
    String id_transmision="";
    FrameLayout contenedor,avance;
    int Nfotos=0;
    boolean bandera=true;
    ImageView viewer;

    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    int orden=0;
    Context context;
    String shot="";

    Funciones funciones;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_transmision);
        context=this;
        funciones=new Funciones(context);
        id_transmision=getIntent().getStringExtra("id_transmision");
        viewer=findViewById(R.id.viewer);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Descargar enviar=new Descargar();
        enviar.executeOnExecutor(threadPoolExecutor);
    }

    @Override
    protected void onStop() {
        super.onStop();
        bandera=false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bandera=false;
    }

    class Descargar extends AsyncTask {


        @Override
        protected Object doInBackground(Object[] objects) {

            while (bandera){
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(id_transmision==""){
                    bandera=false;
                    startActivity(new Intent(context,ListaTransmision.class));
                }
                String respuesta=funciones.Conexion("{\"id_transmision\":\""+id_transmision+"\"}",funciones.GetUrl()+context.getResources().getString(R.string.url_GetStreamming),"POST");
                funciones.Logo("GetStreamming",respuesta);
                try {
                    JSONObject jsonObject=new JSONObject(respuesta);
                    if(jsonObject.getString("vivo").contains("0")){
                        bandera=false;
                    }else{
                        if(shot!=jsonObject.getString("shot")){
                            URL url = new URL(funciones.GetUrl()+getResources().getString(R.string.url_GetShots));
                            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            shot=jsonObject.getString("shot");
                            viewer.setImageBitmap(image);
                            image.recycle();
                        }

                    }
                } catch (Exception e) {
                }
            }
            return null;
        }
    }
}
