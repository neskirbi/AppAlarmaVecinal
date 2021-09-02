package com.app.alarmavecinal.Usuario.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.app.alarmavecinal.Principal;
import com.app.alarmavecinal.R;
import com.app.alarmavecinal.Usuario.Registro.RegistroView;

public class LoginView extends AppCompatActivity implements Login.LoginView{
    Login.LoginPresenter loginPresenter;
    EditText mail,pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mail=findViewById(R.id.mail);
        pass=findViewById(R.id.pass);
        loginPresenter=new LoginPresenter(this,this);
    }

    public void Login(View view){
        loginPresenter.HacerLogin(mail,pass);
    }

    public void Registrar(View view){
        startActivity(new Intent(getApplicationContext(), RegistroView.class));
    }

    @Override
    public void ErrorMail(String error) {

    }

    @Override
    public void ErrorPass(String error) {

    }

    @Override
    public void LoginOk() {
        startActivity(new Intent(getApplicationContext(), Principal.class));
    }

    @Override
    public void LoginError() {
        Toast.makeText(getApplicationContext(), "Datos Incorrectos.", Toast.LENGTH_SHORT).show();
    }
}