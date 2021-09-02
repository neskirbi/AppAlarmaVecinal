package com.app.alarmavecinal.Servicios;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.alarmavecinal.BuildConfig;
import com.app.alarmavecinal.Estructuras.Emergencias;
import com.app.alarmavecinal.Estructuras.Mensaje;
import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.Mapas.MapaEmergencia;
import com.app.alarmavecinal.R;
import com.app.alarmavecinal.VerEmergencia;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

public class Emergencia extends Service {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    ChildEventListener listener;
    String id_grupo;
    Context context;
    Funciones funciones;
    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;

    public Emergencia() {
        context=this;
        funciones=new Funciones(context);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyApp::MyWakelockTag");
        wakeLock.acquire();

        id_grupo=funciones.GetIdGrupo();
        funciones.Logo("Emergencia","Iniciando servicio notificaciones mensaje...");

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("EmergenciaVecinos-"+id_grupo);//Sala de chat
        listener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //funciones.Logo("Emergencialistener", snapshot.getValue()+"");
                Emergencias emergencia=snapshot.getValue(Emergencias.class);
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
        return START_STICKY;
    }

    private void Sonar( String mensaje,String nombre,String direccion,String ubicacion) {
        funciones.Notificar("¡Emergencia!",mensaje, R.drawable.logo,new Intent(context,MapaEmergencia.class).putExtra("nombre",nombre).putExtra("direccion",direccion).putExtra("ubicacion",ubicacion),0);

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
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

                startActivity(new Intent(context,MapaEmergencia.class).putExtra("nombre",nombre).putExtra("direccion",direccion).putExtra("ubicacion",ubicacion).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(databaseReference!=null){
            databaseReference.removeEventListener(listener);
        }

        if(wakeLock!=null){
            wakeLock.release();
        }
    }
}
