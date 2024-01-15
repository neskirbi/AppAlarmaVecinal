package com.app.alarmavecinal.Usuario.Login;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.Models.Usuario;
import com.app.alarmavecinal.Sqlite.Base;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginInteractor implements Login.LoginInteractor{
    Context context;
    Funciones metodos;
    LoginPresenter loginPresenter;
    Funciones funciones;
    public LoginInteractor(LoginPresenter loginPresenter,Context context) {
        this.context=context;
        this.metodos=new Funciones(context);
        this.loginPresenter=loginPresenter;
        funciones=new Funciones(context);
    }



    @Override
    public void HacerLogin(String mail, String pass) {

        JsonArray login = new JsonArray();
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("mail",mail);
        jsonObject.addProperty("pass",pass);
        login.add(jsonObject);
        Log.i("Logueo", login.toString());


        metodos.AbrirConexion();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(metodos.GetUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Peticiones peticion=retrofit.create(Peticiones.class);
        Call<JsonArray> call= peticion.Login(login);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                Log.i("Logueo", String.valueOf(response.code()));
                Log.i("Logueo",response.body().toString());
                if(response.body()!=null){
                    loginPresenter.CerrarDialogo();

                    if(funciones.IsSuccess(response.body())) {

                        CreaLogin(response.body());
                        if(funciones.GetIndex2(funciones.GetData(response.body()),0,"id_grupo")!=null){

                            metodos.GuardarGrupoLogin(funciones.GetIndex2(funciones.GetData(response.body()),0,"id_grupo"),
                                    funciones.GetIndex2(funciones.GetData(response.body()),0,"id_usuario"),
                                    funciones.GetIndex2(funciones.GetData(response.body()),0,"nombre"));

                        }
                        loginPresenter.LoginOk();
                    }else{

                        funciones.Toast(funciones.GetMsn(response.body()));
                    }

                    JsonObject jsonObject = response.body().get(0).getAsJsonObject();






                }else{

                    loginPresenter.CerrarDialogo();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.i("Logueo","Erro:"+t.getMessage());
                loginPresenter.LoginError("Erro:"+t.getMessage());
            }
        });
    }



    public void CreaLogin(JsonArray body) {

        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();
            String id_usuario=funciones.GetIndex2(funciones.GetData(body),0,"id_usuario");
            String nombre=funciones.GetIndex2(funciones.GetData(body),0,"nombres")+" "+funciones.GetIndex2(funciones.GetData(body),0,"apellidos");
            String direccion=funciones.GetIndex2(funciones.GetData(body),0,"direccion");


            ContentValues login = new ContentValues();
            login.put("id_usuario", id_usuario);
            login.put("nombre",nombre);
            login.put("direccion",direccion);


            db.insert("login", null, login);

            db.close();

        } catch (Exception e) {
            metodos.Logo("login",e.getMessage());
        }

    }

}
