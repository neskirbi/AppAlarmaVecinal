package com.app.alarmavecinal.Orden;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.app.alarmavecinal.Alertas.AlertasInterface;
import com.app.alarmavecinal.Alertas.AlertasLista;
import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.R;
import com.app.alarmavecinal.Sqlite.Base;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrdenInteractor  implements Orden.OrdenInteractor {
    OrdenPresenter ordenPresenter;
    Context context;
    Funciones funciones;
    public OrdenInteractor(OrdenPresenter ordenPresenter, Context context) {
        this.ordenPresenter=ordenPresenter;
        this.context=context;
        funciones=new Funciones(context);
    }





    @Override
    public void GetAvisos() {
        funciones.GetAvisosServer();
    }

    @Override
    public void ActualizarMensajes() {
        funciones.ActualizarMensajes();
    }


    @Override
    public void GetAlerta() {
        funciones.GetAlertasServer();
    }






}
