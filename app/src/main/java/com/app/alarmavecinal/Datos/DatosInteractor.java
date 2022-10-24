package com.app.alarmavecinal.Datos;

import android.content.Context;
import android.util.Log;

import com.app.alarmavecinal.Metodos;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DatosInteractor implements Datos.DatosInteractor {

    DatosPresenter datosPresenter;
    Context context;
    Metodos metodos;
    public DatosInteractor(DatosPresenter datosPresenter, Context context) {
        this.datosPresenter=datosPresenter;
        this.context=context;
        metodos=new Metodos(context);
    }

    @Override
    public void GetDatos() {

        metodos.AbrirConexion();

        JsonArray jsonArray=new JsonArray();
        JsonObject jsonObject=new JsonObject();

        jsonObject.addProperty("id",metodos.GetIdUsuario());
        jsonArray.add(jsonObject);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(metodos.GetUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        DatosInterface peticion=retrofit.create(DatosInterface.class);

        Call<JsonArray> call= peticion.GetDatos(jsonArray);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                Log.i("GetDatos", String.valueOf(response.code()));
                if(response.body()!=null){
                    Log.i("GetDatos",response.body().toString());
                    Log.i("GetDatos",response.body().get(0).getAsJsonObject().get("status").toString());
                    if(metodos.IsSuccess(response.body())){
                          datosPresenter.MostrarDatos(response.body());
                    }else{
                          metodos.Toast(metodos.GetMsn(response.body()));
                    }


                }else{
                    metodos.Toast(metodos.GetMsn(response.body()));
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.i("GetDatos","Erro:"+t.getMessage());
                metodos.Toast("Erro:"+t.getMessage());
            }
        });
    }



    @Override
    public void Actualizar(String nombres,String apellidos,String direccion,String ubicacion) {

        metodos.AbrirConexion();

        int cont = 0;
        if (nombres.length() == 0) {
            metodos.Toast("Debe ingresar su nombre.");
            cont++;
        }

        if (apellidos.length() == 0) {
            metodos.Toast( "Debe ingresar sus apellidos.");
            cont++;
        }

        if (direccion.length() == 0) {
            metodos.Toast( "Debe ingresar su dirección o numero de casa.");
            cont++;
        }



        if (cont == 0) {

            JsonArray jsonArray=new JsonArray();
            JsonObject jsonObject=new JsonObject();

            jsonObject.addProperty("id",metodos.GetIdUsuario());
            jsonObject.addProperty("nombres",nombres);
            jsonObject.addProperty("apellidos",apellidos);
            jsonObject.addProperty("direccion",direccion);
            jsonObject.addProperty("ubicacion",ubicacion);
            jsonArray.add(jsonObject);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(metodos.GetUrl())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            DatosInterface peticion=retrofit.create(DatosInterface.class);

            Call<JsonArray> call= peticion.UpdateDatos(jsonArray);
            call.enqueue(new Callback<JsonArray>() {
                @Override
                public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                    Log.i("Actualizar", String.valueOf(response.code()));
                    if(response.body()!=null){
                        Log.i("Actualizar",response.body().toString());
                        Log.i("Actualizar",response.body().get(0).getAsJsonObject().get("status").toString());
                        if(metodos.IsSuccess(response.body())){
                            metodos.Toast(metodos.GetMsn(response.body()));
                            metodos.UpdateDatos(nombres+" "+apellidos,direccion,ubicacion);
                        }else{
                            metodos.Toast(metodos.GetMsn(response.body()));
                        }


                    }else{
                        metodos.Toast("Error de servidor.");
                    }
                }

                @Override
                public void onFailure(Call<JsonArray> call, Throwable t) {
                    Log.i("Actualizar","Erro:"+t.getMessage());
                    metodos.Toast("Erro:"+t.getMessage());
                }
            });



        }
    }

}
