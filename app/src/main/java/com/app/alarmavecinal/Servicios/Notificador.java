package com.app.alarmavecinal.Servicios;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.alarmavecinal.ChatFb.SalaChat;
import com.app.alarmavecinal.Estructuras.AlertasL;
import com.app.alarmavecinal.Estructuras.Avisos;
import com.app.alarmavecinal.Estructuras.Mensaje;
import com.app.alarmavecinal.FuncionAlertas.AlertasLista;
import com.app.alarmavecinal.FuncioneAvisos.AvisosLista;
import com.app.alarmavecinal.FuncioneAvisos.NewAvisos;
import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Notificador extends Service {

    String id_grupo;
    Context context;
    Funciones funciones;
    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;

    FirebaseDatabase firebaseDatabaseChat;
    DatabaseReference databaseReferenceChat;
    ChildEventListener listenerChat;

    FirebaseDatabase firebaseDatabaseAviso;
    DatabaseReference databaseReferenceAviso;
    ChildEventListener listenerAviso;

    FirebaseDatabase firebaseDatabaseAlerta;
    DatabaseReference databaseReferenceAlerta;
    ChildEventListener listenerAlerta;

    public Notificador() {
        context=this;
        funciones=new Funciones(context);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyApp::MyWakelockTag");
        wakeLock.acquire();
        id_grupo=funciones.GetIdGrupo();

        NotificarChat();
        NotificarAlerta();
        NotificarAviso();


        return START_STICKY;
    }

    private void NotificarAviso() {

        firebaseDatabaseAviso = FirebaseDatabase.getInstance();
        databaseReferenceAviso = firebaseDatabaseAviso.getReference("Avisos-"+id_grupo);
        listenerAviso=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                funciones.Logo("Notificaciones", snapshot.getValue()+"");
                Avisos avisos=snapshot.getValue(Avisos.class);
                //funciones.Logo("Notificaciones",mensaje.getNombre()+" "+mensaje.getId_mensaje());
                if(!funciones.RevisarIdAviso(avisos.getId_aviso())){
                    funciones.GuardarIdAviso(avisos.getId_aviso());
                    if(!avisos.getId_usuario().contains(funciones.GetIdUsuario())){
                        funciones.Notificar(funciones.GetNombreGrupo(),
                                avisos.getTitulo()+"\n"+avisos.getMensaje(),
                                R.drawable.img_menu_aviso,new Intent(context, AvisosLista.class),1);
                    }
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

    private void NotificarAlerta() {

        firebaseDatabaseAlerta = FirebaseDatabase.getInstance();
        databaseReferenceAlerta = firebaseDatabaseAlerta.getReference("Alertas-"+id_grupo);//Sala de chat
        listenerAlerta=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                funciones.Logo("Notificaciones", snapshot.getValue()+"");
                AlertasL alertas=snapshot.getValue(AlertasL.class);
                //funciones.Logo("Notificaciones",mensaje.getNombre()+" "+mensaje.getId_mensaje());
                if(!funciones.RevisarIdAlerta(alertas.getId_alerta())){
                    funciones.GuardarIdAlerta(alertas.getId_alerta());
                    if(!alertas.getId_usuario().contains(funciones.GetIdUsuario())){
                        funciones.Notificar(funciones.GetNombreGrupo(),
                                alertas.getNombre()+":\n"+alertas.getTitulo(),
                                R.drawable.img_menu_alerta,new Intent(context, AlertasLista.class),2);
                    }
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

    private void NotificarChat() {

        firebaseDatabaseChat = FirebaseDatabase.getInstance();
        databaseReferenceChat = firebaseDatabaseChat.getReference("chat-"+id_grupo);//Sala de chat
        listenerChat=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                funciones.Logo("Notificaciones", snapshot.getValue()+"");
                Mensaje mensaje=snapshot.getValue(Mensaje.class);
                //funciones.Logo("Notificaciones",mensaje.getNombre()+" "+mensaje.getId_mensaje());
                if(!funciones.RevisarIdMensaje(mensaje.getId_mensaje())){
                    funciones.GuardarIdMensaje(mensaje.getId_mensaje());
                    if(!funciones.GetCurrentActivity().contains("SalaChat")){
                        if(!mensaje.getId_usuario().contains(funciones.GetIdUsuario())){
                            funciones.Notificar(funciones.GetNombreGrupo(),mensaje.getNombre()+":\n"+mensaje.getMensaje(), R.drawable.sobre,new Intent(context, SalaChat.class),3);
                        }
                    }
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

        databaseReferenceChat.addChildEventListener(listenerChat);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(databaseReferenceChat!=null){
            databaseReferenceChat.removeEventListener(listenerChat);
        }
        if(databaseReferenceAlerta!=null){
            databaseReferenceAlerta.removeEventListener(listenerAlerta);
        }

        if(databaseReferenceAviso!=null){
            databaseReferenceAviso.removeEventListener(listenerAviso);
        }
        if(wakeLock!=null){
            wakeLock.release();
        }

    }
}
