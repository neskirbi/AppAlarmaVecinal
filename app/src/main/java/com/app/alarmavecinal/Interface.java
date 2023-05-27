package com.app.alarmavecinal;

import com.google.gson.JsonArray;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Interface {


    @POST("api/GuardarMensaje")
    Call<JsonArray> GuardarMensaje(@Body JsonArray jsonArray);

    @POST("api/ActualizarMensajes")
    Call<JsonArray> ActualizarMensajes(@Body JsonArray jsonArray);
}
