package com.app.alarmavecinal.Alertas;

import com.google.gson.JsonArray;

public interface Alertas {

    interface AlertasView{
        void LlenarLista(JsonArray jsonArray);

        void Salir();
    }

    interface AlertasPresenter{
        void GetPreAlertas();
        void LlenarLista(JsonArray jsonArray);
        void EnviarAlerta(com.app.alarmavecinal.Estructuras.Alertas json);

        void Salir();
    }

    interface AlertasInteractor{
        void GetPreAlertas();
        void EnviarAlerta(com.app.alarmavecinal.Estructuras.Alertas json);
    }
}
