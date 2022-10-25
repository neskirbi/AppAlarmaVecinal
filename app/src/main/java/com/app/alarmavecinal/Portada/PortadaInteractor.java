package com.app.alarmavecinal.Portada;

import android.content.Context;
import android.util.Log;

import com.app.alarmavecinal.Metodos;
import com.app.alarmavecinal.Usuario.Login.Peticiones;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PortadaInteractor implements Portada.PortadaInteractor {

    PortadaPresenter portadaPresenter;

    Context context;
    Metodos metodos;
    public PortadaInteractor(PortadaPresenter portadaPresenter, Context context) {
        this.portadaPresenter=portadaPresenter;
        this.context=context;
        metodos=new Metodos(context);
    }

    @Override
    public void GetGrupo() {
        metodos.AbrirConexion();

        JsonArray jsonArray=new JsonArray();
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("id_usuario",metodos.GetIdUsuario());
        jsonArray.add(jsonObject);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(metodos.GetUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PortadaInterface peticion=retrofit.create(PortadaInterface.class);
        Call<JsonArray> call= peticion.GetGrupo(jsonArray);
        Log.i("Logueo", jsonArray.toString());
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                Log.i("Logueo", response.body().toString());
                if(response.body()!=null) {
                    if(metodos.IsSuccess(response.body())) {
                        if (metodos.GetIndex(response.body(), "id_grupo").length() == 0) {
                            metodos.SalirGrupo();

                        }

                    } else {
                        metodos.Toast(metodos.GetMsn(response.body()));
                    }
                }
                portadaPresenter.IrPrincipal();
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.i("Logueo","Erro:"+t.getMessage());
                metodos.Toast("Error:"+t.getMessage());
                portadaPresenter.IrPrincipal();
            }
        });
    }
}