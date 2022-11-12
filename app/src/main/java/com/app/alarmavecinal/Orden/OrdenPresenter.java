package com.app.alarmavecinal.Orden;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.alarmavecinal.Alertas.AlertasInteractor;
import com.app.alarmavecinal.BuildConfig;
import com.app.alarmavecinal.Chat.ChatView;
import com.app.alarmavecinal.Estructuras.Emergencias;
import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.Mapas.MapaEmergencia;
import com.app.alarmavecinal.Models.Ordenes;
import com.app.alarmavecinal.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

public class OrdenPresenter implements Orden.OrdenPresenter {
    OrdenInteractor ordenInteractor;
    OrdenService ordenService;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    ChildEventListener listener;
    Context context;
    Funciones funciones;


    public OrdenPresenter(OrdenService ordenService, Context context) {
        this.ordenService=ordenService;
        this.context=context;
        funciones=new Funciones(context);
        ordenInteractor=new OrdenInteractor(this,context);
    }



    @Override
    public void IniciarListener() {
        funciones.Logo("Emergencialistener", funciones.GetIdGrupo()+"/Ordenes");
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(funciones.GetIdGrupo()+"/Ordenes");//Sala de chat
        listener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Ordenes orden=snapshot.getValue(Ordenes.class);


                funciones.Logo("GetOrdenes", "Val:"+snapshot.getValue());

                switch (orden.getOrden()){
                    case 1://Emergencia
                        funciones.Logo("Emergencialistener","Iniciando Emergencia");
                        Emergencia(orden);
                    break;


                    case 2://Avisos

                        ordenInteractor.GetAvisos();
                        break;


                    case 3://Alertas
                        ordenInteractor.GetAlerta();
                    break;



                    case 4://Chat
                        if(!funciones.GetIdUsuario().equals(orden.getId_usuario())){
                            funciones.Notificar(orden.getNombre(),"Nuevo Mensaje",R.drawable.sobre,new Intent(context, ChatView.class),4);
                        }

                    break;
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

    @Override
    public void DetenerListener() {
        if(databaseReference!=null){
            databaseReference.removeEventListener(listener);
        }
    }

    @Override
    public void IniciarEnviador() {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                funciones.EnviarMensajes();
               handler.postDelayed(this,10000);
            }
        }, 10000);
    }

    void Emergencia(Ordenes orden){
        Emergencias emergencia=new Emergencias(orden.getId(),orden.getId_usuario(),orden.getNombre(),orden.getMensaje(),orden.getUbicacion(),orden.getFecha());
        if (funciones.EsHoy(emergencia.getFecha()) && funciones.EsUnMinuto(emergencia.getFecha())) {
            if(!funciones.ChecarIdEmergencia(emergencia.getId_emergencia())) {
                funciones.GuardarIdEmergencia(emergencia.getId_emergencia());

                try {

                    JSONObject ubicacion = new JSONObject(emergencia.getUbicacion());
                    String body = emergencia.getNombre() + "\n" + ubicacion.get("direccion");
                    Sonar(body, emergencia.getNombre(), ubicacion.get("direccion").toString(), ubicacion.get("ubicacion").toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                    funciones.Logo("Emergencialistener", "Errrror:" + e.getMessage());
                }

            }
        }
    }



    private void Sonar( String mensaje,String nombre,String direccion,String ubicacion) {
        funciones.Notificar("¡Emergencia!",mensaje, R.drawable.logo,new Intent(context, MapaEmergencia.class).putExtra("nombre",nombre).putExtra("direccion",direccion).putExtra("ubicacion",ubicacion),0);

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolumePercentage = 100 * currentVolume/maxVolume;

        funciones.Logo("volumen",currentVolume+"----"+maxVolume+"------"+currentVolumePercentage);
        //AudioManager mobilemode = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
        if(maxVolume>currentVolume){
            if(!BuildConfig.DEBUG){
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),0);
            }

        }
        MediaPlayer alarma = MediaPlayer.create(context, R.raw.atencionintruso);
        if (!alarma.isPlaying()){
            alarma.start();
            funciones.Logo("volumen","corriendo");
        }
        if(!funciones.GetCurrentActivity().contains("MapaEmergencia")){
            if(!funciones.GetCurrentActivity().contains("Ruta")){

                context.startActivity(new Intent(context,MapaEmergencia.class).putExtra("nombre",nombre).putExtra("direccion",direccion).putExtra("ubicacion",ubicacion).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        }

    }
}
