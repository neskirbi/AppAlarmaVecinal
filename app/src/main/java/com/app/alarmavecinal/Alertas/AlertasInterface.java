package com.app.alarmavecinal.Alertas;

import com.google.gson.JsonArray;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AlertasInterface {

    @POST("api/GetAlertas")
    Call<JsonArray> GetAlertas(@Body JsonArray jsonArray);

    @POST("api/EnviarAlertas")
    Call<JsonArray> EnviarAlertas(@Body JsonArray jsonArray);
}
