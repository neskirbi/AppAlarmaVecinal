package com.app.alarmavecinal.FuncionAlertas;

import com.google.gson.JsonArray;

public interface Alertas {

    interface AlertasView{
        void LlenarLista(JsonArray jsonArray);
    }

    interface AlertasPresenter{
        void GetAlertas();
        void LlenarLista(JsonArray jsonArray);
    }

    interface AlertasInteractor{
        void GetAlertas();
    }
}
