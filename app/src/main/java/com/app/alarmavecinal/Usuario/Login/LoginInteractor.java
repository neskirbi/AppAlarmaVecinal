package com.app.alarmavecinal.Usuario.Login;

import android.content.Context;
import android.util.Log;

import com.app.alarmavecinal.Metodos;
import com.app.alarmavecinal.Models.Usuario;
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
    Metodos metodos;
    LoginPresenter loginPresenter;
    public LoginInteractor(LoginPresenter loginPresenter,Context context) {
        this.context=context;
        this.metodos=new Metodos(context);
        this.loginPresenter=loginPresenter;
    }



    @Override
    public void HacerLogin(Usuario usuario) {

        metodos.AbrirConexion();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(metodos.GetUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Peticiones peticion=retrofit.create(Peticiones.class);
        Call<Usuario> call= peticion.Login(usuario);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                Log.i("Logueo", String.valueOf(response.code()));
                if(response.body()!=null){
                    Log.i("Logueo",response.body().toString());
                    if(response.body().getError()!=null){
                        loginPresenter.LoginError(response.body().getError());
                    }else{
                        metodos.CreaLogin(response.body());

                        Log.i("Logueo",response.body().getId_grupo());
                        if(response.body().getId_grupo()!=null)
                            metodos.GuardarGrupoLogin(response.body());
                        loginPresenter.LoginOk();
                    }

                }else{
                    loginPresenter.LoginError(response.body().getError());
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.i("Logueo","Erro:"+t.getMessage());
                loginPresenter.LoginError("Erro:"+t.getMessage());
            }
        });
    }


}
