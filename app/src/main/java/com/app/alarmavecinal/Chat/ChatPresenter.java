package com.app.alarmavecinal.Chat;

import android.content.Context;

import com.app.alarmavecinal.Funciones;
import com.google.gson.JsonArray;

public class ChatPresenter implements Chat.ChatPresenter{
    ChatView chatView;
    ChatInteractor chatInteractor;
    Context context;
    Funciones funciones;
    public ChatPresenter(ChatView chatView, Context context) {
        this.chatView=chatView;
        this.context=context;
        funciones=new Funciones(context);
        chatInteractor=new ChatInteractor(this,context);
    }


    @Override
    public void GardarMensaje(String mensaje, String imagen, String audio, String video) {
        chatInteractor.GardarMensaje(mensaje,imagen,audio,video);
    }

    @Override
    public void GetMensajes(String id_mensaje_ultimo) {
        chatInteractor.GetMensajes(id_mensaje_ultimo);
    }

    @Override
    public void PintarPrimera(JsonArray mensajes) {

        chatView.PintarPrimera(mensajes);
    }

    @Override
    public void GetNuevos(String fecha) {
        chatInteractor.GetNuevos(fecha);
    }

    @Override
    public void PintaNuevos(JsonArray mensajes) {
        chatView.PintaNuevos(mensajes);
    }

    @Override
    public void CargaAnteriores(String id_mensaje_ultimo) {
        chatInteractor.CargaAnteriores(id_mensaje_ultimo);
    }

    @Override
    public void PintaAnteriores(JsonArray jsonArray) {
        chatView.PintaAnteriores(jsonArray);
    }
}
