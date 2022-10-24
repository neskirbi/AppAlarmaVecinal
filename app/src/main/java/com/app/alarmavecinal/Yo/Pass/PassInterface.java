package com.app.alarmavecinal.Yo.Pass;

import com.google.gson.JsonArray;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PassInterface {
    @POST("api/UpdatePass")
    Call<JsonArray> UpdatePass(@Body JsonArray jsonArray);
}
