package com.app.alarmavecinal.Transmision;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.alarmavecinal.BuildConfig;
import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListaTransmision extends AppCompatActivity {
    Funciones funciones;
    ImageView agregar;
    ListView alertas_lista;
    ArrayList<Avisos> avisos = new ArrayList();
    AdaptadorAvisos adaptadorAvisos;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_transmision);


        context=this;
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
        alertas_lista=findViewById(R.id.alertas_lista);

        adaptadorAvisos=new AdaptadorAvisos(context);



        alertas_lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(context, VerTransmision.class).putExtra("id_transmision",avisos.get(position).getTransmision()));
            }
        });

        Cargar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Cargar();
    }

    public void Agregar(View view){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Transmitir");
        alertDialog.setMessage("¿Desea iniciar una transmision en el grupo?");



        alertDialog.setIcon(R.drawable.boton_alerta);

        alertDialog.setPositiveButton("Transmitir",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(context,Grabacion.class));

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


    public void Descargar(View view){
        funciones.Vibrar(funciones.VibrarPush());

    }



    public void Cargar(){

        avisos.clear();

        String data="{\"id_grupo\":\""+funciones.GetIdGrupo()+"\"}";
        String respuesta=funciones.Conexion(data,funciones.GetUrl()+getString(R.string.url_GetListaStreamming),"POST");
        funciones.Logo("gettrans",respuesta);
        try {

            JSONArray jsonArray=new JSONArray(respuesta);
            if(jsonArray.length()!=0){
                for (int i =0; i < jsonArray.length();i++){
                    JSONObject jsonObject=new JSONObject(jsonArray.get(i).toString());
                    avisos.add(new Avisos(jsonObject.get("id_transmision").toString(),jsonObject.get("nombre").toString(),jsonObject.get("fecha").toString()));
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        alertas_lista.setAdapter(adaptadorAvisos);




    }


    public class Avisos {
        String id_transmision;
        String nombre;
        String fecha;

        public Avisos(String id_transmision, String nombre,String fecha) {
            this.id_transmision = id_transmision;
            this.nombre = nombre;
            this.fecha = fecha;
        }


        public String getTransmision() {
            return id_transmision;
        }

        public String getNombre() {
            return nombre;
        }

        public String getFecha() {
            return fecha;
        }


    }


    class AdaptadorAvisos extends ArrayAdapter<Avisos> {

        public AdaptadorAvisos(Context context) {
            super(context,R.layout.streamming_lista, avisos);
        }



        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View item = inflater.inflate(R.layout.streamming_lista, null);

            TextView textView1 = item.findViewById(R.id.nombre);
            textView1.setText(avisos.get(position).getNombre());


            TextView textView2 = item.findViewById(R.id.fecha);
            textView2.setText(avisos.get(position).getFecha());

            return(item);
        }
    }
}
