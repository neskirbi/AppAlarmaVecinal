package com.app.alarmavecinal.Avisos;

public interface Aviso {

    interface AvisoView{}

    interface AvisoPresenter{
        void EnviarAviso(String asunto, String mensaje);
    }

    interface AvisosInterface{
        void EnviarAviso(String asunto, String mensaje);

    }
}
