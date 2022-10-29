package com.app.alarmavecinal.Orden;

public interface Orden {

    interface OrdenPresenter{
        
        void IniciarListener();

        void DetenerListener();
    }

    interface OrdenInteractor{

        void GetAlerta();

    }
}
