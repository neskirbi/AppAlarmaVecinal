package com.app.alarmavecinal.Yo.Pass;

public interface Pass {
    interface PassView{
        void Salir();
    }

    interface PassPresenter{
        void UpdatePass(String pass,String pass1,String pass2);

        void Salir();
    }

    interface PassInteractor{
        void UpdatePass(String pass,String pass1,String pass2);
    }
}
