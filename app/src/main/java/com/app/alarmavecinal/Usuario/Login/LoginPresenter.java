package com.app.alarmavecinal.Usuario.Login;

import android.content.Context;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import com.app.alarmavecinal.Metodos;
import com.app.alarmavecinal.Models.Usuario;

public class LoginPresenter implements Login.LoginPresenter{
    Context context;
    LoginView loginView;
    LoginInteractor loginInteractor;
    Metodos metodos;
    public LoginPresenter(LoginView loginView, Context context) {
        this.loginView=loginView;
        this.context=context;
        loginInteractor=new LoginInteractor(this,context);
        metodos=new Metodos(context);
    }

    @Override
    public void HacerLogin(EditText etmail, EditText etpass) {
        metodos.Vibrar(metodos.VibrarPush());
        if(loginView!=null){
            String mail=etmail.getText().toString();
            String pass=etpass.getText().toString();

            if(mail.trim().length()==0) {
                loginView.ErrorMail("Este campo no puede quedar vacío.");
                return;
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                loginView.ErrorMail("No es una dirección valida de correo.");
                return;
            }

            if(pass.trim().length()==0) {
                loginView.ErrorPass("Este campo no puede quedar vacío.");
                return;
            }

            loginInteractor.HacerLogin(new Usuario("","","","","",mail,pass,"","",""));
        }
    }

    @Override
    public void LoginOk() {
        if(loginView!=null){
            loginView.LoginOk();
        }
    }

    @Override
    public void LoginError() {
        if(loginView!=null){
            loginView.LoginError();
        }
    }
}
