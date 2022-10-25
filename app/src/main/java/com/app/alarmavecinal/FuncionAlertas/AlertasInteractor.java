package com.app.alarmavecinal.FuncionAlertas;

import android.content.Context;
import android.util.Log;

import com.app.alarmavecinal.Metodos;
import com.app.alarmavecinal.Portada.PortadaInterface;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AlertasInteractor implements Alertas.AlertasInteractor {

    AlertasPresenter alertasPresenter;
    Context context;
    Metodos metodos;
    public AlertasInteractor(AlertasPresenter alertasPresenter, Context context) {
        this.alertasPresenter=alertasPresenter;
        this.context=context;
        metodos=new Metodos(context);
    }

    @Override
    public void GetAlertas() {
        metodos.AbrirConexion();

        JsonArray jsonArray=new JsonArray();
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("id_usuario",metodos.GetIdUsuario());
        jsonArray.add(jsonObject);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(metodos.GetUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        AlertasInterface peticion=retrofit.create(AlertasInterface.class);
        Call<JsonArray> call= peticion.GetAlertas(jsonArray);
        Log.i("GetAlertas", jsonArray.toString());
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                Log.i("GetAlertas", response.body().toString());
                if(response.body()!=null) {
                    if(metodos.IsSuccess(response.body())) {
                        alertasPresenter.LlenarLista(response.body());


                    } else {
                        metodos.Toast(metodos.GetMsn(response.body()));
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.i("GetAlertas","Erro:"+t.getMessage());

            }
        });
    }
}
