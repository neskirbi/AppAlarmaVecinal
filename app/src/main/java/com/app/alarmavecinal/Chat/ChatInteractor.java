package com.app.alarmavecinal.Chat;

import android.content.Context;
import android.util.Log;

import com.app.alarmavecinal.Alertas.AlertasInterface;
import com.app.alarmavecinal.Estructuras.Mensaje;
import com.app.alarmavecinal.Funciones;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatInteractor implements Chat.ChatInteractor{
    ChatPresenter chatPresenter;
    Context context;
    Funciones funciones;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public ChatInteractor(ChatPresenter chatPresenter, Context context) {
        this.chatPresenter=chatPresenter;
        this.context=context;
        funciones=new Funciones(context);
    }

    @Override
    public void EnviarTexto(String msn) {
        funciones.AbrirConexion();
        String id= funciones.GetUIID();
        String nombre = funciones.GetNombre();
        String id_usuario= funciones.GetIdUsuario();
        JsonArray jsonArray=new JsonArray();
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("id_usuario",id_usuario);
        jsonObject.addProperty("nombre",nombre);
        jsonObject.addProperty("msn",msn);
        jsonArray.add(jsonObject);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(funciones.GetUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        AlertasInterface peticion=retrofit.create(AlertasInterface.class);
        Call<JsonArray> call= peticion.GetPreAlertas(jsonArray);
        Log.i("SetMensaje", jsonArray.toString());
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                Log.i("SetMensaje", response.body().toString());
                if(response.body()!=null) {
                    if(funciones.IsSuccess(response.body())) {
                        EnviarMensaje(new Mensaje(id,id_usuario,nombre,msn,"","","","","",funciones.GetDate()));

                    } else {
                        funciones.Toast(funciones.GetMsn(response.body()));
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.i("SetMensaje","Erro:"+t.getMessage());

            }
        });

    }

    @Override
    public void EnviarAudio(String audio) {

    }

    @Override
    public void EnviarImagen(String msn, String imagen) {

    }

    void EnviarMensaje(Mensaje mensaje){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(funciones.GetIdGrupo()+"/Chat");
        databaseReference.push().setValue(mensaje);

    }
}
