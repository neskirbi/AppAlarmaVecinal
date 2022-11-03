package com.app.alarmavecinal.Chat;

import com.google.gson.JsonArray;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ChatInterface {

    @POST("api/GetAlertas")
    Call<JsonArray> GetAlertas(@Body JsonArray jsonArray);
}
