package com.app.alarmavecinal.Yo.Datos;

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

public class DatosInteractor implements Datos.DatosInteractor {

    DatosPresenter datosPresenter;
    Context context;
    Funciones funciones;
    public DatosInteractor(DatosPresenter datosPresenter, Context context) {
        this.datosPresenter=datosPresenter;
        this.context=context;
        funciones=new Funciones(context);
    }

    @Override
    public void GetDatos() {

        funciones.AbrirConexion();

        JsonArray jsonArray=new JsonArray();
        JsonObject jsonObject=new JsonObject();

        jsonObject.addProperty("id",funciones.GetIdUsuario());
        jsonArray.add(jsonObject);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(funciones.GetUrl())
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
                    if(funciones.IsSuccess(response.body())){
                          datosPresenter.MostrarDatos(response.body());
                    }else{
                          funciones.Toast(funciones.GetMsn(response.body()));
                    }


                }else{
                    funciones.Toast(funciones.GetMsn(response.body()));
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.i("GetDatos","Erro:"+t.getMessage());
                funciones.Toast("Erro:"+t.getMessage());
            }
        });
    }



    @Override
    public void Actualizar(String nombres,String apellidos,String direccion,String ubicacion) {

        funciones.AbrirConexion();

        int cont = 0;
        if (nombres.length() == 0) {
            funciones.Toast("Debe ingresar su nombre.");
            cont++;
        }

        if (apellidos.length() == 0) {
            funciones.Toast( "Debe ingresar sus apellidos.");
            cont++;
        }

        if (direccion.length() == 0) {
            funciones.Toast( "Debe ingresar su dirección o numero de casa.");
            cont++;
        }
        if (ubicacion.length() == 0) {
            funciones.Toast( "Espere a cargar su ubicación.");
            cont++;
        }



        if (cont == 0) {

            JsonArray jsonArray=new JsonArray();
            JsonObject jsonObject=new JsonObject();

            jsonObject.addProperty("id",funciones.GetIdUsuario());
            jsonObject.addProperty("nombres",nombres);
            jsonObject.addProperty("apellidos",apellidos);
            jsonObject.addProperty("direccion",direccion);
            jsonObject.addProperty("ubicacion",ubicacion);
            jsonArray.add(jsonObject);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(funciones.GetUrl())
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
                        if(funciones.IsSuccess(response.body())){
                            funciones.Toast(funciones.GetMsn(response.body()));
                            funciones.UpdateDatos(nombres+" "+apellidos,direccion,ubicacion);
                        }else{
                            funciones.Toast(funciones.GetMsn(response.body()));
                        }


                    }else{
                        funciones.Toast("Error de servidor.");
                    }
                }

                @Override
                public void onFailure(Call<JsonArray> call, Throwable t) {
                    Log.i("Actualizar","Erro:"+t.getMessage());
                    funciones.Toast("Erro:"+t.getMessage());
                }
            });



        }
    }

}
