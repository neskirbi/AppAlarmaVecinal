package com.app.alarmavecinal.Orden;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.app.alarmavecinal.Alertas.AlertasInterface;
import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.Sqlite.Base;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrdenInteractor  implements Orden.OrdenInteractor {
    OrdenPresenter ordenPresenter;
    Context context;
    Funciones funciones;
    public OrdenInteractor(OrdenPresenter ordenPresenter, Context context) {
        this.ordenPresenter=ordenPresenter;
        this.context=context;
        funciones=new Funciones(context);
    }


    @Override
    public void GetAlerta() {
        funciones.AbrirConexion();

        JsonArray jsonArray=new JsonArray();
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("id_grupo",funciones.GetIdGrupo());
        jsonObject.addProperty("id_alerta",GetIdAlertas());
        jsonArray.add(jsonObject);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(funciones.GetUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        OrdenInterface peticion=retrofit.create(OrdenInterface.class);
        Call<JsonArray> call= peticion.GetAlerta(jsonArray);
        Log.i("GetAlertas", jsonArray.toString());
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                Log.i("GetAlertas", response.body().toString());
                if(response.body()!=null) {
                    if(funciones.IsSuccess(response.body())) {



                    } else {

                    }
                }

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.i("GetAlertas","Erro:"+t.getMessage());

            }
        });
    }

    private String GetIdAlertas() {

        Base base = new Base(context);
        SQLiteDatabase db = base.getWritableDatabase();

        String nombre="";

        Cursor c =  db.rawQuery("SELECT * from aletas ",null);
        c.moveToFirst();
        int cont=c.getCount();
        c.close();
        db.close();

    }
}
