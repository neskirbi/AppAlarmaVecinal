package com.app.alarmavecinal.Servicios;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.app.alarmavecinal.Funciones;

public class Lanzador extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Funciones funciones=new Funciones(context);
        funciones.VerificarServicios();
      


    }



}
