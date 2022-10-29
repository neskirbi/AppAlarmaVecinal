package com.app.alarmavecinal.Yo.Pass;

import android.content.Context;

import com.app.alarmavecinal.Funciones;

public class PassPresenter implements Pass.PassPresenter {
    PassView passView;
    PassInteractor passInteractor;
    Context context;
    Funciones funciones;

    public PassPresenter(PassView passView, Context context) {
        this.passView=passView;
        this.context=context;
        funciones=new Funciones(context);
        passInteractor=new PassInteractor(this,context);
    }

    @Override
    public void UpdatePass(String pass, String pass1, String pass2) {
        passInteractor.UpdatePass(pass,pass1,pass2);
    }

    @Override
    public void Salir() {
        passView.Salir();
    }
}
