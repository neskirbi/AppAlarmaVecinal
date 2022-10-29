package com.app.alarmavecinal.Orden;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.app.alarmavecinal.Alertas.AlertasInterface;
import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.Sqlite.Base;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrdenInteractor  implements Orden.OrdenInteractor {
    OrdenPresenter ordenPresenter;
    Context context;
    Funciones funciones;
    public OrdenInteractor(OrdenPresenter ordenPresenter, Context context) {
        this.ordenPresenter=ordenPresenter;
        this.context=context;
        funciones=new Funciones(context);
    }


    @Override
    public void GetAvisos() {
        funciones.AbrirConexion();

        JsonArray jsonArray=new JsonArray();
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("id_grupo",funciones.GetIdGrupo());
        jsonObject.add("ids",GetIdAvisos());
        jsonArray.add(jsonObject);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(funciones.GetUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        OrdenInterface peticion=retrofit.create(OrdenInterface.class);
        Call<JsonArray> call= peticion.GetAvisos(jsonArray);
        Log.i("GetAvisos", jsonArray+"");
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                Log.i("GetAvisos", "Code:"+response.code());
                if(response.body()!=null) {
                    Log.i("GetAvisos", response.body().toString());
                    if(funciones.IsSuccess(response.body())) {

                        GuardarAvisos(response.body());


                    } else {

                    }
                }

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.i("GetAvisos","Erro:"+t.getMessage());

            }
        });
    }

    private void GuardarAvisos(JsonArray body) {
        Base base = new Base(context);
        SQLiteDatabase db = base.getWritableDatabase();
        for(int i=0;i<body.size();i++){
            //funciones.Logo("GuardarAlertas",funciones.GetIndex2(funciones.GetData(body),i,"id_alerta"));

            try{
                if(!EstaAviso(funciones.GetIndex2(funciones.GetData(body),i,"id_alerta"))){
                    ContentValues aviso = new ContentValues();

                    aviso.put("id_aviso", funciones.GetIndex2(funciones.GetData(body),i,"id_aviso"));
                    aviso.put("id_grupo", funciones.GetIndex2(funciones.GetData(body),i,"id_grupo"));
                    aviso.put("id_usuario", funciones.GetIndex2(funciones.GetData(body),i,"id_usuario"));
                    aviso.put("created_at", funciones.GetIndex2(funciones.GetData(body),i,"created_at"));

                    aviso.put("asunto", funciones.GetIndex2(funciones.GetData(body),i,"asunto"));
                    aviso.put("mensaje", funciones.GetIndex2(funciones.GetData(body),i,"mensaje"));



                    db.insert("avisos", null, aviso);
                }

            }catch(Exception e){
                funciones.Logo("GuardarAvisos",e.getMessage());
            }


        }
        db.close();

    }

    private JsonArray GetIdAvisos() {


        JsonArray jsonArray=new JsonArray();
        Base base = new Base(context);
        SQLiteDatabase db = base.getWritableDatabase();




        Cursor c =  db.rawQuery("SELECT * from avisos ",null);
        c.moveToFirst();
        funciones.Logo("GetAvisos",c.getCount()+"<---");
        if(c.getCount()>0){
            while(!c.isLast()){
                JsonObject jsonObject=new JsonObject();
                jsonObject.addProperty("id_aviso",c.getString(c.getColumnIndex("id_aviso")));
                jsonArray.add(jsonObject);
                c.moveToNext();
            }
        }



        c.close();
        db.close();
        return jsonArray;

    }



    @Override
    public void GetAlerta() {
        funciones.AbrirConexion();

        JsonArray jsonArray=new JsonArray();
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("id_grupo",funciones.GetIdGrupo());
        jsonObject.add("ids",GetIdAlertas());
        jsonArray.add(jsonObject);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(funciones.GetUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        OrdenInterface peticion=retrofit.create(OrdenInterface.class);
        Call<JsonArray> call= peticion.GetAlertas(jsonArray);
        Log.i("GetAlertas", jsonArray+"");
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                Log.i("GetAlertas", "Code:"+response.code());
                if(response.body()!=null) {
                    Log.i("GetAlertas", response.body().toString());
                    if(funciones.IsSuccess(response.body())) {

                        GuardarAlertas(response.body());


                    } else {

                    }
                }

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.i("GetAlertas","Erro:"+t.getMessage());

            }
        });
    }



    private void GuardarAlertas(JsonArray body) {
        Base base = new Base(context);
        SQLiteDatabase db = base.getWritableDatabase();
        for(int i=0;i<body.size();i++){
            //funciones.Logo("GuardarAlertas",funciones.GetIndex2(funciones.GetData(body),i,"id_alerta"));

            try{
                if(!EstaAlerta(funciones.GetIndex2(funciones.GetData(body),i,"id_alerta"))){
                    ContentValues alerta = new ContentValues();

                    alerta.put("id_alerta", funciones.GetIndex2(funciones.GetData(body),i,"id_alerta"));
                    alerta.put("id_grupo", funciones.GetIndex2(funciones.GetData(body),i,"id_grupo"));
                    alerta.put("id_usuario", funciones.GetIndex2(funciones.GetData(body),i,"id_usuario"));
                    alerta.put("imagen", funciones.GetIndex2(funciones.GetData(body),i,"imagen"));

                    alerta.put("asunto", funciones.GetIndex2(funciones.GetData(body),i,"asunto"));
                    alerta.put("mensaje", funciones.GetIndex2(funciones.GetData(body),i,"mensaje"));

                    alerta.put("created_at", funciones.GetIndex2(funciones.GetData(body),i,"created_at"));



                    db.insert("alertas", null, alerta);
                }

            }catch(Exception e){
                funciones.Logo("GuardarAlertas","Error:"+e.getMessage());
            }


        }
        db.close();

    }

    private JsonArray GetIdAlertas() {


        JsonArray jsonArray=new JsonArray();
        Base base = new Base(context);
        SQLiteDatabase db = base.getWritableDatabase();


        Cursor c =  db.rawQuery("SELECT * from alertas ",null);
        c.moveToFirst();
        funciones.Logo("GuardarAlertas",c.getCount()+"<---");
        if(c.getCount()>0){
            while(!c.isLast()){
                JsonObject jsonObject=new JsonObject();
                jsonObject.addProperty("id_alerta",c.getString(c.getColumnIndex("id_alerta")));
                jsonArray.add(jsonObject);
                c.moveToNext();
            }
        }



        c.close();
        db.close();
        return jsonArray;

    }

    private boolean EstaAlerta(String id_alerta) {


        Base base = new Base(context);
        SQLiteDatabase db = base.getWritableDatabase();




        Cursor c =  db.rawQuery("SELECT * from alertas where id_alerta='"+id_alerta+"' ",null);

        if(c.getCount()>0){
           return true;
        }



        c.close();
        db.close();
        return false;

    }

    private boolean EstaAviso(String id_aviso) {


        Base base = new Base(context);
        SQLiteDatabase db = base.getWritableDatabase();




        Cursor c =  db.rawQuery("SELECT * from avisos where id_aviso='"+id_aviso+"' ",null);

        if(c.getCount()>0){
            return true;
        }



        c.close();
        db.close();
        return false;

    }
}
