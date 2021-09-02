package com.app.alarmavecinal.Usuario.Registro;

import android.widget.EditText;

import com.app.alarmavecinal.Models.Usuario;

public interface Registro {
    interface LoginView{
        void ShowDialog();
        void ErrorNombre();
        void ErrorApellido();
        void ErrorDireccion();
        void ErrorMail(String error);
        void ErrorPass(String error);
        void ErrorPass2(String error);
        void RegistroOk();
        void RegistroError();

    }

    interface RegistroPresenter{
        void Registrar(EditText nombre, EditText apellido, EditText direccion, EditText mail, EditText pass, EditText pass2);
        void RegistroOk();
        void RegistroError();
    }

    interface RegistroInteractor{
        void Registrar(Usuario usuario);
    }

}
