package com.app.alarmavecinal.Usuario.Registro;

import android.content.Context;
import android.util.Log;

import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.Models.Usuario;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistroInteractor implements Registro.RegistroInteractor {
    RegistroPresenter registroPresenter;
    Funciones funciones;
    Context context;
    public RegistroInteractor(RegistroPresenter registroPresenter,Context context) {
        this.registroPresenter=registroPresenter;
        funciones=new Funciones(context);
        this.context=context;
    }

    @Override
    public void Registrar(Usuario usuario) {
        Log.i("Registro","Url:"+funciones.GetUrl());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(funciones.GetUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Peticiones peticion=retrofit.create(Peticiones.class);
        Call<Usuario> call= peticion.RegistroUsuario(usuario);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if(response.body()!=null){
                    if(response.body().getId_usuario()==null) {
                        Log.i("Registro",response.body().getError());
                        registroPresenter.RegistroError(response.body().getError());
                    }else{
                        Log.i("Registro",response.body().getId_usuario());
                        funciones.CreaLogin(response.body());
                        registroPresenter.RegistroOk();
                    }
                }else{
                    Log.i("Registro","Null response.");
                    registroPresenter.RegistroError("Null response.");
                }

            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.i("Registro",t.getMessage());
                registroPresenter.RegistroError("Error de conexión");
            }
        });


    }


}
