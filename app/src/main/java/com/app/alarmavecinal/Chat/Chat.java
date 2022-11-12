package com.app.alarmavecinal.Chat;

public interface Chat {
    interface ChatView{

    }

    interface ChatPresenter{
        void GardarMensaje(String mensaje,String imagen,String audio,String video);
    }

    interface ChatInteractor{
        void GardarMensaje(String mensaje,String imagen,String audio,String video);
    }
}
