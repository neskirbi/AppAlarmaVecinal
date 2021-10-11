package com.app.alarmavecinal.Usuario.Registro;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.app.alarmavecinal.Metodos;
import com.app.alarmavecinal.Principal.PrincipalView;
import com.app.alarmavecinal.R;

public class RegistroView extends AppCompatActivity implements Registro.LoginView {
    RegistroPresenter registroPresenter;
    EditText nombre,apellido,direccion,mail,pass,pass2;
    private ProgressDialog dialog;
    Metodos metodos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        registroPresenter=new RegistroPresenter(this,this);
        metodos=new Metodos(this);
        nombre=findViewById(R.id.nombre);
        apellido=findViewById(R.id.apellidos);
        direccion=findViewById(R.id.direccion);
        mail=findViewById(R.id.mail);
        pass=findViewById(R.id.pass1);
        pass2=findViewById(R.id.pass2);
    }

    public void Registrar(View view){
        metodos.Vibrar(metodos.VibrarPush());
        ShowDialog();
        registroPresenter.Registrar(nombre,apellido,direccion,mail,pass,pass2);
    }


    public void ShowDialog() {
        dialog = ProgressDialog.show(RegistroView.this, "", "Registrando...", true);
    }

    @Override
    public void ErrorNombre() {
        dialog.dismiss();
        nombre.setError("Este campo no puede quedar vacío.");
    }

    @Override
    public void ErrorApellido() {
        dialog.dismiss();
        apellido.setError("Este campo no puede quedar vacío.");
    }

    @Override
    public void ErrorDireccion() {
        dialog.dismiss();
        direccion.setError("Este campo no puede quedar vacío.");
    }

    @Override
    public void ErrorMail(String error) {
        dialog.dismiss();
        mail.setError(error);
    }

    @Override
    public void ErrorPass(String error) {
        dialog.dismiss();
        pass.setError(error);
    }

    @Override
    public void ErrorPass2(String error) {
        dialog.dismiss();
        pass2.setError(error);
    }

    @Override
    public void RegistroOk() {
        dialog.dismiss();
        startActivity(new Intent(getApplicationContext(), PrincipalView.class));
    }

    @Override
    public void RegistroError(String error) {
        dialog.dismiss();
        Toast.makeText(getApplicationContext(), ""+error, Toast.LENGTH_SHORT).show();
    }

}