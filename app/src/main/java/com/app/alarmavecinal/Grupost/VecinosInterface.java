package com.app.alarmavecinal.Grupost;

import com.app.alarmavecinal.Models.Grupo;
import com.google.gson.JsonArray;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface VecinosInterface {
    @POST("api/GetVecinos")
    Call<JsonArray> GetVecinos(@Body JsonArray jsonArray);

    @POST("api/GetBloqueados")
    Call<JsonArray> GetBloqueados(@Body JsonArray jsonArray);

    @POST("api/BloquearVecino")
    Call<JsonArray> BloquearVecino(@Body JsonArray jsonArray);

    @POST("api/DesbloquearVecino")
    Call<JsonArray> DesbloquearVecino(@Body JsonArray jsonArray);

}
