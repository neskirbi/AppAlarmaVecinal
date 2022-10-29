package com.app.alarmavecinal.Avisos;

import com.google.gson.JsonArray;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AvisoInterface {

    @POST("api/EnviarAvisos")
    Call<JsonArray> EnviarAvisos(@Body JsonArray jsonArray);
}
