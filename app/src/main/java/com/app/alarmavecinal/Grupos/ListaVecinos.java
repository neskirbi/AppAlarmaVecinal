package com.app.alarmavecinal.Grupos;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.alarmavecinal.BuildConfig;
import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.Principal;
import com.app.alarmavecinal.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ListaVecinos extends AppCompatActivity {
    Context context;
    Funciones funciones;
    FrameLayout opciones,titlebar;
    LinearLayout content,menu,dummy,quitar,bloquear,desbloquear;
    ImageView abrir_menu;
    TextView titulog,menu_grupo,menu_actualizar,menu_bloqueados,opcnombre,opcdireccion;
    String nombrev="",idv="";

    boolean vista=true;

    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;

    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_vecinos);
        titlebar=findViewById(R.id.titlebar);
        opciones=findViewById(R.id.opciones);
        opciones.bringToFront();
        opcnombre=findViewById(R.id.opcnombre);
        opcdireccion=findViewById(R.id.opcdireccion);
        quitar=findViewById(R.id.quitar);
        bloquear=findViewById(R.id.bloquear);
        desbloquear=findViewById(R.id.desbloquear);

        content=findViewById(R.id.content);
        titulog=findViewById(R.id.titulog);
        menu_grupo=findViewById(R.id.menu_grupo);
        menu_actualizar=findViewById(R.id.menu_actualizar);
        menu_bloqueados=findViewById(R.id.menu_bloqueados);
        abrir_menu=findViewById(R.id.abrir_menu);
        dummy=findViewById(R.id.dummy);
        menu=findViewById(R.id.menu);
        context=this;
        funciones=new Funciones(context);
        titulog.setText(funciones.GetNombreGrupo());

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




        abrir_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funciones.Vibrar(funciones.VibrarPush());
                menu.setVisibility(View.VISIBLE);
                dummy.setVisibility(View.VISIBLE);
                dummy.bringToFront();
                menu.bringToFront();
            }
        });

        dummy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OcultarMenu();
            }
        });

        menu_grupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vista=true;
                ActualizarVecinos();
            }
        });

        menu_bloqueados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vista=false;
                ActualizarVecinos();
            }
        });

        menu_actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActualizarVecinos();
            }
        });


        opciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opciones.setVisibility(View.GONE);
            }
        });



        quitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Quitar");
                alertDialog.setMessage("¿Seguro que quiere quitar a "+nombrev+" del grupo?");



                alertDialog.setIcon(R.drawable.img_quitar);

                alertDialog.setPositiveButton("Sí",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(idv.equals(funciones.GetIdUsuario())){
                                    Toast.makeText(context, "No puedes sacarte tu mismo del grupo.", Toast.LENGTH_SHORT).show();
                                }else {
                                    String respuesta=funciones.Conexion("{\"id_usuario\":\""+idv+"\",\"id_grupo\":\""+funciones.GetIdGrupo()+"\"}",funciones.GetUrl()+getString(R.string.url_SetSacarGrupo),"POST");
                                    try {
                                        JSONObject jsonObject=new JSONObject(respuesta);
                                        if (jsonObject.get("response").toString().contains("1")){
                                            Toast.makeText(context, nombrev+" fue sacado del grupo.", Toast.LENGTH_SHORT).show();
                                            ActualizarVecinos();
                                            opciones.setVisibility(View.GONE);
                                        }else{
                                            Toast.makeText(context, nombrev+" no se pudo eliminar del grupo.", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        Toast.makeText(context, "Error al procesar la respuesta.", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }

                            }
                        });

                alertDialog.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();

            }
        });

        bloquear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Bloquear");
                alertDialog.setMessage("¿Seguro que quiere bloquear a "+nombrev+" del grupo?");



                alertDialog.setIcon(R.drawable.img_bloqueo);

                alertDialog.setPositiveButton("Sí",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(idv.equals(funciones.GetIdUsuario())){
                                    Toast.makeText(context, "No puedes bloquearte tu mismo del grupo.", Toast.LENGTH_SHORT).show();
                                }else{
                                    String respuesta=funciones.Conexion("{\"id_usuario\":\""+idv+"\",\"id_grupo\":\""+funciones.GetIdGrupo()+"\"}",funciones.GetUrl()+getString(R.string.url_SetBloqueo),"POST");
                                    try {
                                        JSONObject jsonObject=new JSONObject(respuesta);
                                        if (jsonObject.get("response").toString().contains("1")){
                                            Toast.makeText(context, nombrev+" se ha bloquedo del grupo.", Toast.LENGTH_SHORT).show();
                                            ActualizarVecinos();
                                            opciones.setVisibility(View.GONE);
                                        }else{
                                            Toast.makeText(context, nombrev+" no se pudo bloquear del grupo.", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        Toast.makeText(context, "Error al procesar la respuesta.", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }

                            }
                        });

                alertDialog.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();

            }
        });

        desbloquear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Desbloquear");
                alertDialog.setMessage("¿Seguro que quiere desbloquear a "+nombrev+" del grupo?");



                alertDialog.setIcon(R.drawable.img_menu_palomita);

                alertDialog.setPositiveButton("Sí",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String respuesta=funciones.Conexion("{\"id_usuario\":\""+idv+"\",\"id_grupo\":\""+funciones.GetIdGrupo()+"\"}",funciones.GetUrl()+getString(R.string.url_SetDesbloqueo),"POST");
                                try {
                                    JSONObject jsonObject=new JSONObject(respuesta);
                                    if (jsonObject.get("response").toString().contains("1")){
                                        Toast.makeText(context, nombrev+" se ha debloquedo del grupo.", Toast.LENGTH_SHORT).show();
                                        ActualizarVecinos();
                                        opciones.setVisibility(View.GONE);
                                    }else{
                                        Toast.makeText(context, nombrev+" no se pudo debloquear del grupo.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(context, "Error al procesar la respuesta.", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }

                            }
                        });

                alertDialog.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActualizarVecinos();
    }

    private void ActualizarVecinos() {
        if(vista){
            titlebar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            funciones.Vibrar(funciones.VibrarPush());
            CargarVecinos cargarVecinos=new CargarVecinos();
            cargarVecinos.executeOnExecutor(threadPoolExecutor);
        }else{
            titlebar.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
            funciones.Vibrar(funciones.VibrarPush());
            CargarBloqueados cargarBloqueados=new CargarBloqueados();
            cargarBloqueados.executeOnExecutor(threadPoolExecutor);
        }

        OcultarMenu();
    }

    public void OcultarMenu(){
        menu.setVisibility(View.GONE);
        dummy.setVisibility(View.GONE);
    }
    public void Invitar(){
        funciones.Vibrar(funciones.VibrarPush());
        startActivity(new Intent(context,Grupo.class));
    }

    class CargarVecinos extends AsyncTask {
        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);


            View  item=null;


            try {
                JSONArray jsonArray=new JSONArray(values[0].toString());
                content.removeAllViews();
                Toast.makeText(context, "Lista actualizada.", Toast.LENGTH_SHORT).show();
                for(int i=0;i<jsonArray.length();i++ ){

                    LayoutInflater inflater = getLayoutInflater();
                    item = inflater.inflate(R.layout.vecinos_card, null);
                    final JSONObject jsonObject=new JSONObject(jsonArray.get(i).toString());


                    TextView nombre=item.findViewById(R.id.nombre);
                    nombre.setText(jsonObject.get("nombre").toString());

                    TextView direccion=item.findViewById(R.id.direccion);
                    direccion.setText(jsonObject.get("direccion").toString());



                    ImageView opcusuario=item.findViewById(R.id.opcusuario);
                    if(funciones.GetIdUsuario().contains(funciones.GetIdUsuarioGrupo())){
                        opcusuario.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                funciones.Vibrar(funciones.VibrarPush());
                                try {
                                    nombrev=jsonObject.get("nombre").toString();
                                    idv=jsonObject.get("id_usuario").toString();
                                    opcnombre.setText(jsonObject.get("nombre").toString());
                                    opcdireccion.setText(jsonObject.get("direccion").toString());

                                    VerOpciones();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }else{
                        opcusuario.setVisibility(View.GONE);
                    }

                    content.addView(item);

                }

                if(jsonArray.length()==0){
                }
            } catch (JSONException e) {
                funciones.Logo("lista_vecinos",e.getMessage());
            }
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            String respuesta=funciones.Conexion("{\"id_grupo\":\""+funciones.GetIdGrupo()+"\"}",funciones.GetUrl()+getString(R.string.url_GetVecinosGrupo),"POST");
            publishProgress(respuesta);
            return null;
        }
    }

    private void VerOpciones() {
        if(vista){
            bloquear.setVisibility(View.VISIBLE);
            quitar.setVisibility(View.VISIBLE);
            desbloquear.setVisibility(View.GONE);
        }else{
            bloquear.setVisibility(View.GONE);
            quitar.setVisibility(View.GONE);
            desbloquear.setVisibility(View.VISIBLE);
        }
        opciones.setVisibility(View.VISIBLE);
    }

    class CargarBloqueados extends AsyncTask {
        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);


            View  item=null;


            try {
                JSONArray jsonArray=new JSONArray(values[0].toString());
                content.removeAllViews();
                Toast.makeText(context, "Lista actualizada.", Toast.LENGTH_SHORT).show();
                for(int i=0;i<jsonArray.length();i++ ){

                    LayoutInflater inflater = getLayoutInflater();
                    item = inflater.inflate(R.layout.vecinos_card, null);
                    final JSONObject jsonObject=new JSONObject(jsonArray.get(i).toString());


                    TextView nombre=item.findViewById(R.id.nombre);
                    nombre.setText(jsonObject.get("nombre").toString());

                    TextView direccion=item.findViewById(R.id.direccion);
                    direccion.setText(jsonObject.get("direccion").toString());



                    ImageView opcusuario=item.findViewById(R.id.opcusuario);
                    if(funciones.GetIdUsuario().contains(funciones.GetIdUsuarioGrupo())){
                        opcusuario.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                funciones.Vibrar(funciones.VibrarPush());
                                try {
                                    nombrev=jsonObject.get("nombre").toString();
                                    idv=jsonObject.get("id_usuario").toString();
                                    opcnombre.setText(jsonObject.get("nombre").toString());
                                    opcdireccion.setText(jsonObject.get("direccion").toString());
                                    VerOpciones();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }else{
                        opcusuario.setVisibility(View.GONE);
                    }

                    content.addView(item);

                }

                if(jsonArray.length()==0){
                }
            } catch (JSONException e) {
                funciones.Logo("lista_vecinos",e.getMessage());
            }
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            String respuesta=funciones.Conexion("{\"id_grupo\":\""+funciones.GetIdGrupo()+"\"}",funciones.GetUrl()+getString(R.string.url_GetBloqueados),"POST");
            publishProgress(respuesta);
            return null;
        }
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(opciones.getVisibility()==View.VISIBLE){
            opciones.setVisibility(View.GONE);
        }else{
            //startActivity(new Intent(this, Principal.class));
            finish();
        }
    }
}
