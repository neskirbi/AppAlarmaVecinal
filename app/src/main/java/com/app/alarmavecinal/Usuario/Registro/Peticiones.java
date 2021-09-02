package com.app.alarmavecinal.Usuario.Registro;

import com.app.alarmavecinal.Models.Usuario;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Peticiones {

    @POST("api/Registro")
    Call<Usuario> RegistroUsuario(@Body Usuario usuario);
}
