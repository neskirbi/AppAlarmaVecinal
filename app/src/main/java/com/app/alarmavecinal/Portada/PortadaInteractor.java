package com.app.alarmavecinal.Portada;

import android.content.Context;
import android.util.Log;

import com.app.alarmavecinal.Funciones;
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
    Funciones funciones;
    public PortadaInteractor(PortadaPresenter portadaPresenter, Context context) {
        this.portadaPresenter=portadaPresenter;
        this.context=context;
        funciones=new Funciones(context);
    }

    @Override
    public void GetGrupo() {
        funciones.AbrirConexion();

        JsonArray jsonArray=new JsonArray();
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("id_usuario",funciones.GetIdUsuario());
        jsonArray.add(jsonObject);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(funciones.GetUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PortadaInterface peticion=retrofit.create(PortadaInterface.class);
        Call<JsonArray> call= peticion.GetGrupo(jsonArray);
        Log.i("Logueo", jsonArray.toString());
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                Log.i("Logueo", response.code()+"");
                if(response.body()!=null) {
                    Log.i("Logueo", response.body().toString());
                    if(funciones.IsSuccess(response.body())) {
                        if (funciones.GetIndex(response.body(), "id_grupo").length() == 0) {
                            funciones.SalirGrupo();
                        }

                    } else {
                        funciones.Toast(funciones.GetMsn(response.body()));
                    }
                }
                portadaPresenter.IrPrincipal();
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                //Log.i("Logueo","Errorrrrrr:"+t.getMessage());
                //funciones.Toast("Error:"+t.getMessage());
                portadaPresenter.IrPrincipal();
            }
        });
    }
}
