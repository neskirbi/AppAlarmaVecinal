package com.app.alarmavecinal.Usuario.Registro;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;

import com.app.alarmavecinal.Metodos;
import com.app.alarmavecinal.Models.Usuario;
import com.app.alarmavecinal.Sqlite.Base;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistroInteractor implements Registro.RegistroInteractor {
    RegistroPresenter registroPresenter;
    Metodos metodos;
    Context context;
    public RegistroInteractor(RegistroPresenter registroPresenter,Context context) {
        this.registroPresenter=registroPresenter;
        metodos=new Metodos(context);
        this.context=context;
    }

    @Override
    public void Registrar(Usuario usuario) {
        Log.i("Registro","Url:"+metodos.GetUrl());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(metodos.GetUrl())
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
                        metodos.CreaLogin(response.body());
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
                registroPresenter.RegistroError("Error de conexi??n");
            }
        });


    }


}
