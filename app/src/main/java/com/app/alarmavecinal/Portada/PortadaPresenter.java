package com.app.alarmavecinal.Portada;

import android.content.Context;
import android.util.Log;

import com.app.alarmavecinal.Funciones;

public class PortadaPresenter implements Portada.PortadaPresenter {


    PortadaView portadaView;
    PortadaInteractor portadaInteractor;
    Context context;
    Funciones funciones;

    public PortadaPresenter(PortadaView portadaView, Context context) {
        this.portadaView=portadaView;
        this.context=context;
        funciones=new Funciones(context);
        portadaInteractor=new PortadaInteractor(this,context);
    }

    @Override
    public void GetGrupo() {
        portadaInteractor.GetGrupo();

    }

    @Override
    public void IrPrincipal() {
        Log.i("Logueo","IrPrincipal");
        portadaView.IrPrincipal();
    }
}
