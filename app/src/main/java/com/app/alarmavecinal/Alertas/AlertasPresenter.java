package com.app.alarmavecinal.Alertas;

import android.content.Context;

import com.app.alarmavecinal.Funciones;
import com.google.gson.JsonArray;

public class AlertasPresenter implements Alertas.AlertasPresenter {
    NewAlerta newAlerta;
    AlertasInteractor alertasInteractor;
    Context context;
    Funciones funciones;
    public AlertasPresenter(NewAlerta newAlerta, Context context) {
        this.newAlerta=newAlerta;
        this.context=context;
        funciones=new Funciones(context);
        alertasInteractor=new AlertasInteractor(this,context);
    }

    @Override
    public void GetPreAlertas() {

        alertasInteractor.GetPreAlertas();
    }

    @Override
    public void LlenarLista(JsonArray jsonArray) {
        newAlerta.LlenarLista(jsonArray);
    }

    @Override
    public void EnviarAlerta(com.app.alarmavecinal.Estructuras.Alertas json) {
        alertasInteractor.EnviarAlerta(json);
    }

    @Override
    public void Salir() {
        newAlerta.Salir();
    }

}
