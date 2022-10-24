package com.app.alarmavecinal.Grupost;

import com.google.gson.JsonArray;

public interface Vecinos {
    interface VecinosView{
        void OcultarOpciones();

        void LlenarVecinos(JsonArray jsonArray);
        void Llenabloqueados(JsonArray jsonArray);
    }
    interface VecinosPresenter{
        void OcultarOpciones();
        void GetVecinos();
        void LlenarVecinos(JsonArray jsonArray);
        void GetBloqueados();
        void Llenabloqueados(JsonArray jsonArray);

        void BloquearVecino(String idv);
        void DesbloquearVecino(String idv);
    }
    interface VecinosInteractor{
        void GetVecinos();
        void GetBloqueados();
        void BloquearVecino(String idv);

        void DesbloquearVecino(String idv);
    }
}
