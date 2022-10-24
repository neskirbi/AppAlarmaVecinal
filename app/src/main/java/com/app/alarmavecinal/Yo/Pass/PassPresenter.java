package com.app.alarmavecinal.Yo.Pass;

import android.content.Context;

import com.app.alarmavecinal.Metodos;

public class PassPresenter implements Pass.PassPresenter {
    PassView passView;
    PassInteractor passInteractor;
    Context context;
    Metodos metodos;

    public PassPresenter(PassView passView, Context context) {
        this.passView=passView;
        this.context=context;
        metodos=new Metodos(context);
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
