package com.app.alarmavecinal.Vecinos;

import android.content.Context;
import android.util.Log;

import com.app.alarmavecinal.Metodos;
import com.app.alarmavecinal.Models.Grupo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GrupoInteractor implements GrupoInteface.GrupoInteractor {
    Context context;
    GrupoPresenter grupoPresenter;
    Metodos metodos;
    public GrupoInteractor(GrupoPresenter grupoPresenter, Context context) {
        this.context=context;
        this.grupoPresenter= grupoPresenter;
        metodos=new Metodos(context);
    }

    @Override
    public void GuardarGrupo(String nombre) {
        Log.i("CrearGrupo","interactor");
        Grupo grupo=new Grupo("", metodos.GetIdUsuario(), nombre,"");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(metodos.GetUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Peticiones peticion=retrofit.create(Peticiones.class);
        Call<Grupo> call= peticion.GuardarGrupo(grupo);
        call.enqueue(new Callback<Grupo>() {
            @Override
            public void onResponse(Call<Grupo> call, Response<Grupo> response) {
                Log.i("CrearGrupo", String.valueOf(response.code()));
                if(response.body()!=null){
                    if(response.body().getError()!=null){
                        grupoPresenter.Error(response.body().getError());
                    }else{
                        Log.i("CrearGrupo",response.body().getId_grupo()+"");
                        metodos.GuardarGrupoCreado(response.body());
                        metodos.VerificarServicios();
                        grupoPresenter.IraGrupo();
                    }


                }else{
                    grupoPresenter.Error("Body nulo");
                }
            }

            @Override
            public void onFailure(Call<Grupo> call, Throwable t) {
                Log.i("Login","Erro:"+t.getMessage());
                grupoPresenter.Error("Erro:"+t.getMessage());
            }
        });
    }

    @Override
    public void DejarGrupo() {
        Log.i("DejarGrupo",metodos.GetIdUsuario());
        Grupo grupo=new Grupo(metodos.GetIdGrupo(), metodos.GetIdUsuario(), "","");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(metodos.GetUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Peticiones peticion=retrofit.create(Peticiones.class);
        Call<Grupo> call= peticion.DejarGrupo(grupo);
        call.enqueue(new Callback<Grupo>() {
            @Override
            public void onResponse(Call<Grupo> call, Response<Grupo> response) {
                Log.i("DejarGrupo", String.valueOf(response.code()));
                if(response.body()!=null){
                    if(response.body().getError()!=null){
                        grupoPresenter.Error(response.body().getError());
                    }else{
                        Log.i("DejarGrupo",response.body()+"");
                        metodos.SalirGrupo();
                        metodos.VerificarServicios();
                        grupoPresenter.IraGrupo();

                    }


                }else{
                    grupoPresenter.Error("Server Error");
                }
            }

            @Override
            public void onFailure(Call<Grupo> call, Throwable t) {
                Log.i("Login","Erro:"+t.getMessage());
                grupoPresenter.Error("Erro:"+t.getMessage());
            }
        });
    }
}
