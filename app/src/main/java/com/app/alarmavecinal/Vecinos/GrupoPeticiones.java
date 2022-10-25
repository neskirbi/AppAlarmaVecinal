package com.app.alarmavecinal.Vecinos;

import com.app.alarmavecinal.Models.Grupo;
import com.google.gson.JsonArray;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GrupoPeticiones {

    @POST("api/CrearGrupo")
    Call<Grupo> GuardarGrupo(@Body Grupo grupo);

    @POST("api/UnirseGrupo")
    Call<JsonArray> UnirseGrupo(@Body JsonArray grupo);

    @POST("api/DejarGrupo")
    Call<Grupo> DejarGrupo(@Body Grupo grupo);
}
