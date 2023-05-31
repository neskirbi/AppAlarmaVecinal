package com.app.alarmavecinal.Chat;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
            String nombre= funciones.GetNombre();


            ContentValues mensajes = new ContentValues();

            mensajes.put("id_mensaje", id_mensaje);
            mensajes.put("id_grupo", id_grupo);
            mensajes.put("id_usuario", id_usuario);
            mensajes.put("nombre", nombre);
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

    @Override
    public void GetMensajes(String id_mensaje_ultimo) {
        Log.i("Pintar","GetMensajes");
        JsonArray jsonArray=new JsonArray();

        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            Cursor c =  db.rawQuery("SELECT * from mensajes order by created_at desc limit 0,25",null);

            if(c.getCount()>0){
                c.moveToFirst();
                while (!c.isAfterLast()){

                    JsonObject jsonObject=new JsonObject();
                    jsonObject.addProperty("id_mensaje",c.getString(c.getColumnIndex("id_mensaje")));
                    jsonObject.addProperty("id_usuario",c.getString(c.getColumnIndex("id_usuario")));
                    jsonObject.addProperty("id_grupo",c.getString(c.getColumnIndex("id_grupo")));
                    jsonObject.addProperty("nombre",c.getString(c.getColumnIndex("nombre")));
                    jsonObject.addProperty("imagen",c.getString(c.getColumnIndex("imagen")));
                    jsonObject.addProperty("mensaje",c.getString(c.getColumnIndex("mensaje")));
                    jsonObject.addProperty("audio",c.getString(c.getColumnIndex("audio")));
                    jsonObject.addProperty("video",c.getString(c.getColumnIndex("video")));
                    jsonObject.addProperty("enviado",c.getString(c.getColumnIndex("enviado")));
                    jsonObject.addProperty("created_at",(c.getString(c.getColumnIndex("created_at"))));
                    jsonObject.addProperty("updated_at",c.getString(c.getColumnIndex("updated_at")));
                    jsonArray.add(jsonObject);
                    c.moveToNext();
                }


            }
            c.close();
            db.close();

            chatPresenter.PintarPrimera(jsonArray);
        }catch (Exception e){

            Log.i("Pintar",e.getMessage());
        }
    }

    @Override
    public void GetNuevos(String fecha ) {
        Log.i("Pintar","GetMensajes");
        JsonArray jsonArray=new JsonArray();

        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            Cursor c =  db.rawQuery("SELECT * from mensajes where created_at>'"+fecha+"' order by created_at ",null);

            if(c.getCount()>0){
                c.moveToFirst();
                while (!c.isAfterLast()){

                    JsonObject jsonObject=new JsonObject();
                    jsonObject.addProperty("id_mensaje",c.getString(c.getColumnIndex("id_mensaje")));
                    jsonObject.addProperty("id_usuario",c.getString(c.getColumnIndex("id_usuario")));
                    jsonObject.addProperty("id_grupo",c.getString(c.getColumnIndex("id_grupo")));
                    jsonObject.addProperty("nombre",c.getString(c.getColumnIndex("nombre")));
                    jsonObject.addProperty("imagen",c.getString(c.getColumnIndex("imagen")));
                    jsonObject.addProperty("mensaje",c.getString(c.getColumnIndex("mensaje")));
                    jsonObject.addProperty("audio",c.getString(c.getColumnIndex("audio")));
                    jsonObject.addProperty("video",c.getString(c.getColumnIndex("video")));
                    jsonObject.addProperty("enviado",c.getString(c.getColumnIndex("enviado")));
                    jsonObject.addProperty("created_at",(c.getString(c.getColumnIndex("created_at"))));
                    jsonObject.addProperty("updated_at",c.getString(c.getColumnIndex("updated_at")));
                    jsonArray.add(jsonObject);
                    c.moveToNext();
                }


            }
            c.close();
            db.close();

            chatPresenter.PintaNuevos(jsonArray);
        }catch (Exception e){

            Log.i("Pintar",e.getMessage());
        }
    }

    @Override
    public void CargaAnteriores(String id_mensaje_ultimo) {
        Log.i("Pintar","GetMensajes");
        JsonArray jsonArray=new JsonArray();

        try {
            Base base = new Base(context);
            SQLiteDatabase db = base.getWritableDatabase();

            Cursor c =  db.rawQuery("SELECT * from mensajes where created_at<(select created_at from mensajes where id_mensaje='"+id_mensaje_ultimo+"') order by created_at desc limit 0,20 ",null);

            if(c.getCount()>0){
                c.moveToFirst();
                while (!c.isAfterLast()){

                    JsonObject jsonObject=new JsonObject();
                    jsonObject.addProperty("id_mensaje",c.getString(c.getColumnIndex("id_mensaje")));
                    jsonObject.addProperty("id_usuario",c.getString(c.getColumnIndex("id_usuario")));
                    jsonObject.addProperty("id_grupo",c.getString(c.getColumnIndex("id_grupo")));
                    jsonObject.addProperty("nombre",c.getString(c.getColumnIndex("nombre")));
                    jsonObject.addProperty("imagen",c.getString(c.getColumnIndex("imagen")));
                    jsonObject.addProperty("mensaje",c.getString(c.getColumnIndex("mensaje")));
                    jsonObject.addProperty("audio",c.getString(c.getColumnIndex("audio")));
                    jsonObject.addProperty("video",c.getString(c.getColumnIndex("video")));
                    jsonObject.addProperty("enviado",c.getString(c.getColumnIndex("enviado")));
                    jsonObject.addProperty("created_at",(c.getString(c.getColumnIndex("created_at"))));
                    jsonObject.addProperty("updated_at",c.getString(c.getColumnIndex("updated_at")));
                    jsonArray.add(jsonObject);
                    c.moveToNext();
                }


            }
            c.close();
            db.close();

            chatPresenter.PintaAnteriores(jsonArray);
        }catch (Exception e){

            Log.i("Pintar",e.getMessage());
        }
    }


}
