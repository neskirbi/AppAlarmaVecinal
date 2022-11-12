package com.app.alarmavecinal.Orden;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.app.alarmavecinal.BuildConfig;
import com.app.alarmavecinal.Estructuras.Emergencias;
import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.Mapas.MapaEmergencia;
import com.app.alarmavecinal.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

public class OrdenService extends Service {

    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;
    OrdenPresenter ordenPresenter;
    Context context;
    Funciones funciones;
    public OrdenService() {
        context=this;
        ordenPresenter=new OrdenPresenter(this,context);
        funciones=new Funciones(context);
    }


    @Override
    public void onCreate() {
        super.onCreate();


        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyApp::AlarmaVecinal");
        wakeLock.acquire();


        ordenPresenter.IniciarListener();

        ordenPresenter.IniciarEnviador();



        return START_STICKY;
    }




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }






    @Override
    public void onDestroy() {
        super.onDestroy();
        ordenPresenter.DetenerListener();


        if(wakeLock!=null){
            wakeLock.release();
        }
    }
}
