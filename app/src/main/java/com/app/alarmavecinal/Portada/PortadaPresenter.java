package com.app.alarmavecinal.Portada;

import android.content.Context;

import com.app.alarmavecinal.Metodos;

public class PortadaPresenter implements Portada.PortadaPresenter {


    PortadaView portadaView;
    PortadaInteractor portadaInteractor;
    Context context;
    Metodos metodos;

    public PortadaPresenter(PortadaView portadaView, Context context) {
        this.portadaView=portadaView;
        this.context=context;
        metodos=new Metodos(context);
        portadaInteractor=new PortadaInteractor(this,context);
    }

    @Override
    public void GetGrupo() {
        portadaInteractor.GetGrupo();

    }

    @Override
    public void IrPrincipal() {
        portadaView.IrPrincipal();
    }
}
