package com.app.alarmavecinal.Usuario.Registro;

import android.content.Context;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import com.app.alarmavecinal.Metodos;
import com.app.alarmavecinal.Models.Usuario;

public class RegistroPresenter implements Registro.RegistroPresenter{
    RegistroView registroview;
    RegistroInteractor registroInteractor;
    Metodos metodos;
    public RegistroPresenter(RegistroView registroview, Context context) {
        this.registroview=registroview;
        registroInteractor=new RegistroInteractor(this,context);
        metodos=new Metodos(context);
    }

    @Override
    public void Registrar(EditText etnombre, EditText etapellido, EditText etdireccion, EditText etmail, EditText etpass, EditText etpass2) {
        metodos.Vibrar(metodos.VibrarPush());
        if(registroview!=null){

            String nombre=etnombre.getText().toString();
            String apellido=etapellido.getText().toString();
            String direccion=etdireccion.getText().toString();
            String mail=etmail.getText().toString();
            String pass=etpass.getText().toString();
            String pass2=etpass2.getText().toString();

            if(nombre.trim().length()==0) {
                registroview.ErrorNombre();
                return;
            }
            if(apellido.trim().length()==0) {
                registroview.ErrorApellido();
                return;
            }
            if(direccion.trim().length()==0) {
                registroview.ErrorDireccion();
                return;
            }
            if(mail.trim().length()==0) {
                registroview.ErrorMail("Este campo no puede quedar vacío.");
                return;
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                registroview.ErrorMail("No es una dirección valida de correo.");
                return;
            }

            if(pass.trim().length()==0) {
                registroview.ErrorPass("Este campo no puede quedar vacío.");
                return;
            }

            if(pass2.trim().length()==0) {
                registroview.ErrorPass2("Este campo no puede quedar vacío.");
                return;
            }

            if(pass.equals(pass2)){
                registroview.ShowDialog();
                Usuario usuario=new Usuario("","",nombre,apellido,direccion,mail,pass,"","","");
                registroInteractor.Registrar(usuario);
            }else{
                registroview.ErrorPass("Las contraseñas deben ser iguales");
                registroview.ErrorPass2("Las contraseñas deben ser iguales");
                return;
            }
        }
    }

    @Override
    public void RegistroOk() {
        if(registroview!=null){
            registroview.RegistroOk();
        }
    }

    @Override
    public void RegistroError() {
        if(registroview!=null){
            registroview.RegistroError();
        }
    }
}
