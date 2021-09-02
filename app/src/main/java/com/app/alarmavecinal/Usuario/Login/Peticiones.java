package com.app.alarmavecinal.Usuario.Login;

import com.app.alarmavecinal.Models.Usuario;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Peticiones {

    @POST("api/Login")
    Call<Usuario> Login(@Body Usuario usuario);
}
