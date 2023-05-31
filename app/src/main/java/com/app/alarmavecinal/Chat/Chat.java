package com.app.alarmavecinal.Chat;

import com.google.gson.JsonArray;

public interface Chat {
    interface ChatView{

        void PintarPrimera(JsonArray mensajes);
        void PintaNuevos(JsonArray mensajes);
        void PintaAnteriores(JsonArray jsonArray);
    }

    interface ChatPresenter{
        void GardarMensaje(String mensaje,String imagen,String audio,String video);
        void GetMensajes(String id_mensaje_ultimo);
        void PintarPrimera(JsonArray mensajes);
        void GetNuevos(String fecha);
        void PintaNuevos(JsonArray mensajes);

        void CargaAnteriores(String id_mensaje_ultimo);

        void PintaAnteriores(JsonArray jsonArray);
    }

    interface ChatInteractor{
        void GardarMensaje(String mensaje,String imagen,String audio,String video);
        void GetMensajes(String id_mensaje_ultimo);
        void GetNuevos(String fecha);

        void CargaAnteriores(String id_mensaje_ultimo);
    }
}
