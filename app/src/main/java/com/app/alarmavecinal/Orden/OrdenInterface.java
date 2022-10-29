package com.app.alarmavecinal.Orden;

import com.google.gson.JsonArray;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface OrdenInterface {

    @POST("api/GetAlertas")
    Call<JsonArray> GetAlertas(@Body JsonArray jsonArray);

    @POST("api/GetAvisos")
    Call<JsonArray> GetAvisos(@Body JsonArray jsonArray);
}
