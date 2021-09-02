package com.app.alarmavecinal.Usuario.Registro;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.app.alarmavecinal.Principal;
import com.app.alarmavecinal.R;

public class RegistroView extends AppCompatActivity implements Registro.LoginView {
    RegistroPresenter registroPresenter;
    EditText nombre,apellido,direccion,mail,pass,pass2;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        registroPresenter=new RegistroPresenter(this,this);
        nombre=findViewById(R.id.nombre);
        apellido=findViewById(R.id.apellidos);
        direccion=findViewById(R.id.direccion);
        mail=findViewById(R.id.mail);
        pass=findViewById(R.id.pass1);
        pass2=findViewById(R.id.pass2);
    }

    public void Registrar(View view){
        registroPresenter.Registrar(nombre,apellido,direccion,mail,pass,pass2);
    }

    @Override
    public void ShowDialog() {
        dialog = ProgressDialog.show(RegistroView.this, "", "Registrando...", true);
    }

    @Override
    public void ErrorNombre() {
        nombre.setError("Este campo no puede quedar vacío.");
    }

    @Override
    public void ErrorApellido() {
        apellido.setError("Este campo no puede quedar vacío.");
    }

    @Override
    public void ErrorDireccion() {
        direccion.setError("Este campo no puede quedar vacío.");
    }

    @Override
    public void ErrorMail(String error) {
        mail.setError(error);
    }

    @Override
    public void ErrorPass(String error) {
        pass.setError(error);
    }

    @Override
    public void ErrorPass2(String error) {
        pass2.setError(error);
    }

    @Override
    public void RegistroOk() {
        dialog.dismiss();
        startActivity(new Intent(getApplicationContext(), Principal.class));
    }

    @Override
    public void RegistroError() {
        dialog.dismiss();
        Toast.makeText(getApplicationContext(), "Error al registrar.", Toast.LENGTH_SHORT).show();
    }

}