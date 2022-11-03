package com.app.alarmavecinal.Chat;

public interface Chat {
    interface ChatView{

    }

    interface ChatPresenter{
        void EnviarTexto(String msn);
        void EnviarAudio(String url);
        void EnviarImagen(String msn,String url);
    }

    interface ChatInteractor{
        void EnviarTexto(String msn);
        void EnviarAudio(String audio);
        void EnviarImagen(String msn,String imagen);
    }
}
