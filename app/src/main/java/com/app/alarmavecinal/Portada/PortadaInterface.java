package com.app.alarmavecinal.Portada;

import com.google.gson.JsonArray;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PortadaInterface {
    @POST("api/GetGrupo")
    Call<JsonArray> GetGrupo(@Body JsonArray jsonArray);
}
