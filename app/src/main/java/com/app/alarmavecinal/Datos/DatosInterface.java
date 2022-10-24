package com.app.alarmavecinal.Datos;

import com.app.alarmavecinal.Models.Usuario;
import com.google.gson.JsonArray;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DatosInterface {
    @POST("api/GetDatos")
    Call<JsonArray> GetDatos(@Body JsonArray jsonArray);

    @POST("api/UpdateDatos")
    Call<JsonArray> UpdateDatos(@Body JsonArray jsonArray);
}
