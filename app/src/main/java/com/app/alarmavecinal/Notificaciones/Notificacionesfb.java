package com.app.alarmavecinal.Notificaciones;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.app.alarmavecinal.ChatFb.SalaChat;
import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class Notificacionesfb extends FirebaseMessagingService {
    Funciones funciones;
    Context context;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        context=this;
        funciones=new Funciones(context);
        Log.i("Notificacion","Titulo: "+remoteMessage.getNotification().getTitle()+" Mensaje: "+remoteMessage.getNotification().getBody());
        //funciones.Notificar(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody().replace("<br>","\n"),R.drawable.sobre,new Intent(context, SalaChat.class),2);
    }


}
