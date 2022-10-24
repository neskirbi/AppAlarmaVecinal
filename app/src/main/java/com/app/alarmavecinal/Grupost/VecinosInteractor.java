package com.app.alarmavecinal.Grupost;

import android.content.Context;
import android.util.Log;

import com.app.alarmavecinal.Metodos;
import com.app.alarmavecinal.Yo.Datos.DatosInterface;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VecinosInteractor implements Vecinos.VecinosInteractor {
    VecinosPresenter vecinosPresenter;
    Context context;
    Metodos metodos;
    public VecinosInteractor(VecinosPresenter vecinosPresenter, Context context) {
        this.vecinosPresenter=vecinosPresenter;
        this.context=context;
        metodos=new Metodos(context);
    }

    @Override
    public void GetVecinos() {
        metodos.AbrirConexion();


        JsonArray jsonArray=new JsonArray();
        JsonObject jsonObject=new JsonObject();

        jsonObject.addProperty("id_grupo",metodos.GetIdGrupo());
        jsonArray.add(jsonObject);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(metodos.GetUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        VecinosInterface peticion=retrofit.create(VecinosInterface.class);
        Log.i("GetVecinos", jsonArray+"");
        Call<JsonArray> call= peticion.GetVecinos(jsonArray);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                Log.i("GetVecinos", String.valueOf(response.code()));
                if(response.body()!=null){
                    Log.i("GetVecinos",response.body().toString());
                    Log.i("GetVecinos",response.body().get(0).getAsJsonObject().get("status").toString());
                    if(metodos.IsSuccess(response.body())){
                        vecinosPresenter.LlenarVecinos(response.body().get(0).getAsJsonObject().get("datos").getAsJsonArray());

                    }else{
                        metodos.Toast(metodos.GetMsn(response.body()));
                    }


                }else{
                    metodos.Toast("Error de servidor.");
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.i("GetVecinos","Error:"+t.getMessage());
                metodos.Toast("Erro:"+t.getMessage());
            }
        });

    }

    @Override
    public void GetBloqueados() {

        metodos.AbrirConexion();


        JsonArray jsonArray=new JsonArray();
        JsonObject jsonObject=new JsonObject();

        jsonObject.addProperty("id_grupo",metodos.GetIdGrupo());
        jsonArray.add(jsonObject);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(metodos.GetUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        VecinosInterface peticion=retrofit.create(VecinosInterface.class);
        Log.i("GetBloqueados", jsonArray+"");
        Call<JsonArray> call= peticion.GetBloqueados(jsonArray);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                Log.i("GetBloqueados", String.valueOf(response.code()));
                if(response.body()!=null){
                    Log.i("GetBloqueados",response.body().toString());
                    Log.i("GetBloqueados",response.body().get(0).getAsJsonObject().get("status").toString());
                    if(metodos.IsSuccess(response.body())){
                        vecinosPresenter.Llenabloqueados(response.body().get(0).getAsJsonObject().get("datos").getAsJsonArray());

                    }else{
                        metodos.Toast(metodos.GetMsn(response.body()));
                    }


                }else{
                    metodos.Toast("Error de servidor.");
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.i("GetBloqueados","Error:"+t.getMessage());
                metodos.Toast("Erro:"+t.getMessage());
            }
        });


    }

    @Override
    public void BloquearVecino(String idv) {
        metodos.AbrirConexion();


        JsonArray jsonArray=new JsonArray();
        JsonObject jsonObject=new JsonObject();

        jsonObject.addProperty("id_grupo",metodos.GetIdGrupo());
        jsonObject.addProperty("id_usuario",idv);
        jsonArray.add(jsonObject);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(metodos.GetUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        VecinosInterface peticion=retrofit.create(VecinosInterface.class);
        Log.i("GetBloqueados", jsonArray+"");
        Call<JsonArray> call= peticion.BloquearVecino(jsonArray);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                Log.i("GetBloqueados", String.valueOf(response.code()));
                if(response.body()!=null){
                    Log.i("GetBloqueados",response.body().toString());
                    Log.i("GetBloqueados",response.body().get(0).getAsJsonObject().get("status").toString());
                    if(metodos.IsSuccess(response.body())){
                        GetVecinos();
                        vecinosPresenter.OcultarOpciones();

                    }else{
                        metodos.Toast(metodos.GetMsn(response.body()));
                    }


                }else{
                    metodos.Toast("Error de servidor.");
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.i("GetBloqueados","Error:"+t.getMessage());
                metodos.Toast("Erro:"+t.getMessage());
            }
        });
    }

    @Override
    public void DesbloquearVecino(String idv) {
        metodos.AbrirConexion();


        JsonArray jsonArray=new JsonArray();
        JsonObject jsonObject=new JsonObject();

        jsonObject.addProperty("id_grupo",metodos.GetIdGrupo());
        jsonObject.addProperty("id_usuario",idv);
        jsonArray.add(jsonObject);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(metodos.GetUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        VecinosInterface peticion=retrofit.create(VecinosInterface.class);
        Log.i("GetBloqueados", jsonArray+"");
        Call<JsonArray> call= peticion.DesbloquearVecino(jsonArray);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                Log.i("GetBloqueados", String.valueOf(response.code()));
                if(response.body()!=null){
                    Log.i("GetBloqueados",response.body().toString());
                    Log.i("GetBloqueados",response.body().get(0).getAsJsonObject().get("status").toString());
                    if(metodos.IsSuccess(response.body())){
                        GetBloqueados();
                        vecinosPresenter.OcultarOpciones();
                    }else{
                        metodos.Toast(metodos.GetMsn(response.body()));
                    }


                }else{
                    metodos.Toast("Error de servidor.");
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.i("GetBloqueados","Error:"+t.getMessage());
                metodos.Toast("Erro:"+t.getMessage());
            }
        });
    }
}
