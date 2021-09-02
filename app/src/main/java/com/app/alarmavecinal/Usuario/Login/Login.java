package com.app.alarmavecinal.Usuario.Login;

import android.widget.EditText;

import com.app.alarmavecinal.Models.Usuario;

public interface Login {
    interface LoginView{
        void ErrorMail(String error);
        void ErrorPass(String error);
        void LoginOk();
        void LoginError();
    }
    interface LoginPresenter{
        void HacerLogin(EditText mail,EditText pass);
        void LoginOk();
        void LoginError();
    }
    interface LoginInteractor{
        void HacerLogin(Usuario usuario);
    }
}
