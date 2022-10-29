package com.app.alarmavecinal.Alertas;

import com.google.gson.JsonArray;

public interface Alertas {

    interface AlertasView{
        void LlenarLista(JsonArray jsonArray);
    }

    interface AlertasPresenter{
        void GetAlertas();
        void LlenarLista(JsonArray jsonArray);
        void EnviarAlerta(com.app.alarmavecinal.Estructuras.Alertas json);
    }

    interface AlertasInteractor{
        void GetAlertas();
        void EnviarAlerta(com.app.alarmavecinal.Estructuras.Alertas json);
    }
}
