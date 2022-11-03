package com.app.alarmavecinal.Chat;

import android.content.Context;

import com.app.alarmavecinal.Funciones;

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
    public void EnviarTexto(String msn) {

    }

    @Override
    public void EnviarAudio(String url) {

    }

    @Override
    public void EnviarImagen(String msn, String url) {

    }
}
