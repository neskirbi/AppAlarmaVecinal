package com.app.alarmavecinal.Vecinos;

import com.app.alarmavecinal.Models.Grupo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GrupoPeticiones {

    @POST("api/CrearGrupo")
    Call<Grupo> GuardarGrupo(@Body Grupo grupo);

    @POST("api/UnirseGrupo")
    Call<Grupo> UnirseGrupo(@Body Grupo grupo);

    @POST("api/DejarGrupo")
    Call<Grupo> DejarGrupo(@Body Grupo grupo);
}
