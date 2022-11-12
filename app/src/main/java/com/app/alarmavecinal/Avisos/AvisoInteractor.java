package com.app.alarmavecinal.Avisos;

import android.content.Context;
import android.util.Log;

import com.app.alarmavecinal.Alertas.AlertasInterface;
import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.Models.Ordenes;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AvisoInteractor implements Aviso.AvisosInterface {

    AvisoPresenter avisoPresenter;
    Context context;
    Funciones funciones;
    public AvisoInteractor(AvisoPresenter avisoPresenter, Context context) {
        this.avisoPresenter=avisoPresenter;
        this.context=context;
        funciones=new Funciones(context);
    }

    @Override
    public void EnviarAviso(String asunto, String mensaje) {
        funciones.AbrirConexion();

        JsonArray jsonArray=new JsonArray();
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("id_aviso",funciones.GetUIID());
        jsonObject.addProperty("id_grupo",funciones.GetIdGrupo());
        jsonObject.addProperty("id_usuario",funciones.GetIdUsuario());
        jsonObject.addProperty("asunto",asunto);
        jsonObject.addProperty("mensaje",mensaje);


        jsonArray.add(jsonObject);
        Log.i("EnviarAviso", jsonArray.toString());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(funciones.GetUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        AvisoInterface peticion=retrofit.create(AvisoInterface.class);
        Call<JsonArray> call= peticion.EnviarAvisos(jsonArray);
        Log.i("EnviarAviso", jsonArray.toString());
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                Log.i("EnviarAviso", response.code()+"");
                if(response.body()!=null) {
                    Log.i("EnviarAviso", response.body()+"");
                    if(funciones.IsSuccess(response.body())) {
                        //alertasPresenter.LlenarLista(response.body());


                        funciones.EnviarOrden(new Ordenes(2,"",funciones.GetIdUsuario(),funciones.GetNombre(),"","",""));

                    } else {
                        funciones.Toast(funciones.GetMsn(response.body()));
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.i("EnviarAviso","Error:"+t.getMessage());

            }
        });
    }


}
