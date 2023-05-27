package com.app.alarmavecinal.Chat;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.app.alarmavecinal.Alertas.AlertasInterface;
import com.app.alarmavecinal.Alertas.AlertasLista;
import com.app.alarmavecinal.Estructuras.Mensaje;
import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.Models.Ordenes;
import com.app.alarmavecinal.R;
import com.app.alarmavecinal.Sqlite.Base;
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



    public ChatInteractor(ChatPresenter chatPresenter, Context context) {
        this.chatPresenter=chatPresenter;
        this.context=context;
        funciones=new Funciones(context);
    }

    @Override
    public void GardarMensaje(String mensaje,String imagen,String audio,String video) {
        Base base = new Base(context);
        SQLiteDatabase db = base.getWritableDatabase();
        try{


            String id_mensaje= funciones.GetUIID();
            String id_grupo= funciones.GetIdGrupo();
            String id_usuario= funciones.GetIdUsuario();


            ContentValues mensajes = new ContentValues();

            mensajes.put("id_mensaje", id_mensaje);
            mensajes.put("id_grupo", id_grupo);
            mensajes.put("id_usuario", id_usuario);
            mensajes.put("imagen", imagen);
            mensajes.put("mensaje", mensaje);
            mensajes.put("audio", audio);
            mensajes.put("video", video);
            mensajes.put("enviado", 0);
            mensajes.put("created_at", funciones.GetDate());
            mensajes.put("updated_at", funciones.GetDate());


            db.insert("mensajes", null, mensajes);

            funciones.Toast("Mensaje Enviado");
            funciones.EnviarMensajes();

            //funciones.EnviarOrden(new Ordenes(4,id_mensaje,funciones.GetIdUsuario(),funciones.GetNombre(),"","",""));
        }catch(Exception e){
            funciones.Toast(e.getMessage());
        }



        db.close();

    }


}
