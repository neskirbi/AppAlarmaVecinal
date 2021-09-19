package com.app.alarmavecinal.Usuario.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.app.alarmavecinal.Metodos;
import com.app.alarmavecinal.Principal;
import com.app.alarmavecinal.R;
import com.app.alarmavecinal.Usuario.Registro.RegistroView;

public class LoginView extends AppCompatActivity implements Login.LoginView{
    Login.LoginPresenter loginPresenter;
    EditText mail,pass;
    private ProgressDialog dialog;
    Metodos metodos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        metodos=new Metodos(this);
        mail=findViewById(R.id.mail);
        pass=findViewById(R.id.pass);
        loginPresenter=new LoginPresenter(this,this);
    }

    public void Login(View view){
        metodos.Vibrar(metodos.VibrarPush());
        ShowDialog();
        loginPresenter.HacerLogin(mail,pass);
    }

    public void Registrar(View view){
        metodos.Vibrar(metodos.VibrarPush());
        startActivity(new Intent(getApplicationContext(), RegistroView.class));
    }

    public void ShowDialog() {
        dialog = ProgressDialog.show(this, "", "Registrando...", true);
    }


    @Override
    public void ErrorMail() {
        dialog.dismiss();
    }

    @Override
    public void ErrorPass() {
        dialog.dismiss();
    }

    @Override
    public void LoginOk() {
        dialog.dismiss();
        startActivity(new Intent(getApplicationContext(), Principal.class));
    }

    @Override
    public void LoginError(String error) {
        dialog.dismiss();
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
    }
}