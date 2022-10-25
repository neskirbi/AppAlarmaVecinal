package com.app.alarmavecinal.FuncionAlertas;

import android.content.Context;

import com.app.alarmavecinal.Metodos;
import com.google.gson.JsonArray;

public class AlertasPresenter implements Alertas.AlertasPresenter {
    NewAlerta newAlerta;
    AlertasInteractor alertasInteractor;
    Context context;
    Metodos metodos;
    public AlertasPresenter(NewAlerta newAlerta, Context context) {
        this.newAlerta=newAlerta;
        this.context=context;
        metodos=new Metodos(context);
        alertasInteractor=new AlertasInteractor(this,context);
    }

    @Override
    public void GetAlertas() {

        alertasInteractor.GetAlertas();
    }

    @Override
    public void LlenarLista(JsonArray jsonArray) {
        newAlerta.LlenarLista(jsonArray);
    }
}
