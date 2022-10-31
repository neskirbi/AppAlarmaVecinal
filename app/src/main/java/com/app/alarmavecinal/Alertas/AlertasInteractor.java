package com.app.alarmavecinal.Alertas;

import android.content.Context;
import android.util.Log;

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

public class AlertasInteractor implements Alertas.AlertasInteractor {

    AlertasPresenter alertasPresenter;
    Context context;
    Funciones funciones;
    FirebaseDatabase firebaseDatabaseAlerta;
    DatabaseReference databaseReferenceAlerta;
    public AlertasInteractor(AlertasPresenter alertasPresenter, Context context) {
        this.alertasPresenter=alertasPresenter;
        this.context=context;
        funciones=new Funciones(context);
    }

    @Override
    public void GetPreAlertas() {
        funciones.AbrirConexion();

        JsonArray jsonArray=new JsonArray();
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("id_usuario",funciones.GetIdUsuario());
        jsonArray.add(jsonObject);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(funciones.GetUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        AlertasInterface peticion=retrofit.create(AlertasInterface.class);
        Call<JsonArray> call= peticion.GetPreAlertas(jsonArray);
        Log.i("GetAlertas", jsonArray.toString());
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                Log.i("GetAlertas", response.body().toString());
                if(response.body()!=null) {
                    if(funciones.IsSuccess(response.body())) {
                        alertasPresenter.LlenarLista(response.body());


                    } else {
                        funciones.Toast(funciones.GetMsn(response.body()));
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.i("GetAlertas","Erro:"+t.getMessage());

            }
        });
    }


    public void EnviarOrdenAlerta(int i, String id_alerta){
        firebaseDatabaseAlerta = FirebaseDatabase.getInstance();
        databaseReferenceAlerta = firebaseDatabaseAlerta.getReference("Ordenes/"+funciones.GetIdGrupo());//Sala de chat
        try {

            //JSONObject jsonObject0=new JSONObject(json);
            databaseReferenceAlerta.setValue("");
            databaseReferenceAlerta.push().setValue(new Ordenes(i,id_alerta,"","","","",""));

            alertasPresenter.Salir();


        } catch (Exception e) {
            Log.i("SetAlerta","AError:"+e.getMessage());
        }

    }
    @Override
    public void EnviarAlerta(com.app.alarmavecinal.Estructuras.Alertas json) {
        funciones.AbrirConexion();

        JsonArray jsonArray=new JsonArray();
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("id_alerta",json.getId_alerta());
        jsonObject.addProperty("id_grupo",funciones.GetIdGrupo());
        jsonObject.addProperty("id_usuario",funciones.GetIdUsuario());
        jsonObject.addProperty("imagen",json.getImagen());
        jsonObject.addProperty("asunto",json.getTitulo());
        jsonObject.addProperty("mensaje","");


        jsonArray.add(jsonObject);
        Log.i("EnviarAlerta", jsonArray.toString());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(funciones.GetUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        AlertasInterface peticion=retrofit.create(AlertasInterface.class);
        Call<JsonArray> call= peticion.EnviarAlertas(jsonArray);
        Log.i("EnviarAlerta", jsonArray.toString());
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                Log.i("EnviarAlerta", response.code()+"");
                if(response.body()!=null) {
                    Log.i("EnviarAlerta", response.body()+"");
                    if(funciones.IsSuccess(response.body())) {
                        //alertasPresenter.LlenarLista(response.body());

                        EnviarOrdenAlerta(3,json.getId_alerta());

                    } else {
                        funciones.Toast(funciones.GetMsn(response.body()));
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.i("EnviarAlerta","Error:"+t.getMessage());

            }
        });

    }


}
