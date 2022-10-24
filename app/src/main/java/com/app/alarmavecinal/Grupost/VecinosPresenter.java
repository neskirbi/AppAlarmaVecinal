package com.app.alarmavecinal.Grupost;

import android.content.Context;

import com.app.alarmavecinal.Metodos;
import com.google.gson.JsonArray;

public class VecinosPresenter implements Vecinos.VecinosPresenter {
    VecinosView vecinosView;
    VecinosInteractor vecinosInteractor;
    Context context;
    Metodos metodos;
    public VecinosPresenter(VecinosView vecinosView, Context context) {
        this.vecinosView=vecinosView;
        this.context=context;
        metodos=new Metodos(context);
        vecinosInteractor=new VecinosInteractor(this,context);

    }

    @Override
    public void OcultarOpciones() {
        vecinosView.OcultarOpciones();
    }

    @Override
    public void GetVecinos() {
        vecinosInteractor.GetVecinos();
    }

    @Override
    public void LlenarVecinos(JsonArray jsonArray) {
        vecinosView.LlenarVecinos(jsonArray);
    }

    @Override
    public void GetBloqueados() {
        vecinosInteractor.GetBloqueados();
    }

    @Override
    public void Llenabloqueados(JsonArray jsonArray) {
        vecinosView.Llenabloqueados(jsonArray);
    }

    @Override
    public void BloquearVecino(String idv) {
        vecinosInteractor.BloquearVecino(idv);
    }

    @Override
    public void DesbloquearVecino(String idv) {
        vecinosInteractor.DesbloquearVecino(idv);

    }
}
