package com.app.alarmavecinal.Orden;

public interface Orden {

    interface OrdenPresenter{
        
        void IniciarListener();

        void DetenerListener();

        void IniciarEnviador();
    }

    interface OrdenInteractor{

        void GetAlerta();
        void GetAvisos();
        void ActualizarMensajes();

    }
}
