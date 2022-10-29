package com.app.alarmavecinal.Avisos;

import android.content.Context;

import com.app.alarmavecinal.Funciones;

public class AvisoPresenter implements Aviso.AvisoPresenter {
    NewAvisos newAvisos;
    AvisoInteractor avisoInteractor;
    Context context;
    Funciones funciones;


    public AvisoPresenter(NewAvisos newAvisos, Context context) {
        this.newAvisos=newAvisos;
        this.context=context;
        funciones=new Funciones(context);
        avisoInteractor=new AvisoInteractor(this,context);
    }

    @Override
    public void EnviarAviso(String asunto, String mensaje) {

        avisoInteractor.EnviarAviso(asunto,mensaje);
    }
}
