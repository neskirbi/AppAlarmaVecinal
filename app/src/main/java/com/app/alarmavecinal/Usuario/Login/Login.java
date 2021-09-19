package com.app.alarmavecinal.Usuario.Login;

import android.widget.EditText;

import com.app.alarmavecinal.Models.Usuario;

public interface Login {
    interface LoginView{
        void ErrorMail();
        void ErrorPass();
        void LoginOk();
        void LoginError(String error);
    }
    interface LoginPresenter{
        void HacerLogin(EditText mail,EditText pass);
        void LoginOk();
        void LoginError(String error);
    }
    interface LoginInteractor{
        void HacerLogin(Usuario usuario);
    }
}
