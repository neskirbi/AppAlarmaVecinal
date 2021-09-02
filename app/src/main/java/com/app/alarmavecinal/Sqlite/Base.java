package com.app.alarmavecinal.Sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Base extends SQLiteOpenHelper {

    private static String COPY_TABLE = "";
    private static String LOGIN_TABLE_CREATET ="" ;
    private static String REMOVE_TABLE = "";
    private static String ADD_COLUM_LOGIN ="" ;
    private static  String CHAT_TABLE_CREATE = "";
    private static  String CHAT_IN_TABLE_CREATE ="" ;
    private static  String LOGIN_TABLE_CREATE= "",GRUPO_TABLE_CREATE= "",ALERTAS_TABLE_CREATE= "",AVISOS_TABLE_CREATE= "",AVISO_NOTIFICA ="",ALERTA_NOTIFICA ="",CHAT_NOTIFICA="",EMERGENCIA_NOTIFICA="";
    private static  String DB_NAME = "alerta.sqlite";
    private static  int DB_VERSION = 6;



    public Base(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        LOGIN_TABLE_CREATE= "CREATE TABLE IF NOT EXISTS  login(" +
                "id_usuario TEXT PRIMARY KEY NOT NULL," +
                "nombre TEXT NOT NULL," +
                "direccion TEXT DEFAULT ''," +
                "avatar Text DEFAULT ''," +
                "ubicacion Text DEFAULT '')" ;

        GRUPO_TABLE_CREATE= "CREATE TABLE IF NOT EXISTS  grupo(" +
                "id_grupo TEXT PRIMARY KEY NOT NULL," +
                "id_usuario TEXT not null," +
                "nombre TEXT NOT NULL," +
                "enviado int not null)" ;

        ALERTAS_TABLE_CREATE= "CREATE TABLE IF NOT EXISTS  alertas(" +
                "id_alerta TEXT PRIMARY KEY NOT NULL)" ;

        AVISOS_TABLE_CREATE= "CREATE TABLE IF NOT EXISTS  avisos(" +
                "id_aviso TEXT PRIMARY KEY NOT NULL)" ;



        CHAT_NOTIFICA= "CREATE TABLE IF NOT EXISTS  chat_notifica(" +
                "id_mensaje TEXT PRIMARY KEY NOT NULL)" ;

        AVISO_NOTIFICA= "CREATE TABLE IF NOT EXISTS  aviso_notifica(" +
                "id_aviso TEXT PRIMARY KEY NOT NULL)" ;
        ALERTA_NOTIFICA= "CREATE TABLE IF NOT EXISTS  alerta_notifica(" +
                "id_alerta TEXT PRIMARY KEY NOT NULL)" ;

        EMERGENCIA_NOTIFICA= "CREATE TABLE IF NOT EXISTS  emergencias(" +
                "id_emergencia TEXT PRIMARY KEY NOT NULL)" ;



        db.execSQL(LOGIN_TABLE_CREATE);
        db.execSQL(GRUPO_TABLE_CREATE);
        db.execSQL(ALERTAS_TABLE_CREATE);
        db.execSQL(AVISOS_TABLE_CREATE);
        db.execSQL(CHAT_NOTIFICA);
        db.execSQL(AVISO_NOTIFICA);
        db.execSQL(ALERTA_NOTIFICA);
        db.execSQL(EMERGENCIA_NOTIFICA);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if(oldVersion==1 && newVersion==2){
            CHAT_TABLE_CREATE= "CREATE TABLE IF NOT EXISTS  chat(" +
                    "id_chat TEXT PRIMARY KEY NOT NULL," +
                    "mensaje TEXT NOT NULL," +
                    "enviado TEXT NOT NULL)" ;


            db.execSQL(CHAT_TABLE_CREATE);
        }

        if(oldVersion==1 && newVersion==3){

            CHAT_TABLE_CREATE= "CREATE TABLE IF NOT EXISTS  chat(" +
                    "id_chat TEXT PRIMARY KEY NOT NULL," +
                    "mensaje TEXT NOT NULL," +
                    "enviado TEXT NOT NULL)" ;


            db.execSQL(CHAT_TABLE_CREATE);

            CHAT_IN_TABLE_CREATE= "CREATE TABLE IF NOT EXISTS  chat_in(" +
                    "id_chat TEXT PRIMARY KEY NOT NULL," +
                    "id_usuario TEXT NOT NULL," +
                    "nombre TEXT NOT NULL," +
                    "mensaje TEXT NOT NULL," +
                    "tipo TEXT NOT NULL," +
                    "fecha DATETIME NOT NULL)" ;


            db.execSQL(CHAT_IN_TABLE_CREATE);
        }


        if(oldVersion==2 && newVersion==3){
            CHAT_IN_TABLE_CREATE= "CREATE TABLE IF NOT EXISTS  chat_in(" +
                    "id_chat TEXT PRIMARY KEY NOT NULL," +
                    "id_usuario TEXT NOT NULL," +
                    "nombre TEXT NOT NULL," +
                    "mensaje TEXT NOT NULL," +
                    "tipo TEXT NOT NULL," +
                    "fecha DATETIME NOT NULL)" ;


            db.execSQL(CHAT_IN_TABLE_CREATE);
        }

        if(oldVersion==3 && newVersion==4){

            ADD_COLUM_LOGIN="ALTER TABLE login ADD COLUMN direccion Text DEFAULT '' ";
            Log.i("addcolumn",ADD_COLUM_LOGIN+" ov:"+oldVersion+" nv:"+newVersion);

            CHAT_NOTIFICA= "CREATE TABLE IF NOT EXISTS  chat_notifica(" +
                    "id_mensaje TEXT PRIMARY KEY NOT NULL)" ;

            EMERGENCIA_NOTIFICA= "CREATE TABLE IF NOT EXISTS  emergencias(" +
                    "id_emergencia TEXT PRIMARY KEY NOT NULL)" ;


            db.execSQL(ADD_COLUM_LOGIN);
            db.execSQL(CHAT_NOTIFICA);
            db.execSQL(EMERGENCIA_NOTIFICA);

        }

        if(oldVersion==4 && newVersion==5){


            LOGIN_TABLE_CREATET= "CREATE TABLE IF NOT EXISTS  tlogin(" +
                    "id_usuario TEXT PRIMARY KEY NOT NULL," +
                    "nombre TEXT NOT NULL," +
                    "direccion TEXT DEFAULT '');" ;
            Log.i("addcolumn",LOGIN_TABLE_CREATET+" ov:"+oldVersion+" nv:"+newVersion);
            db.execSQL(LOGIN_TABLE_CREATET);

            COPY_TABLE="INSERT INTO tlogin  SELECT * FROM login ;";
            Log.i("addcolumn",COPY_TABLE+" ov:"+oldVersion+" nv:"+newVersion);
            db.execSQL(COPY_TABLE);


            REMOVE_TABLE="DROP TABLE login";
            Log.i("addcolumn",REMOVE_TABLE+" ov:"+oldVersion+" nv:"+newVersion);
            db.execSQL(REMOVE_TABLE);

            LOGIN_TABLE_CREATE= "CREATE TABLE IF NOT EXISTS  login(" +
                    "id_usuario TEXT PRIMARY KEY NOT NULL," +
                    "nombre TEXT NOT NULL," +
                    "direccion TEXT DEFAULT '');" ;
            Log.i("addcolumn",LOGIN_TABLE_CREATE+" ov:"+oldVersion+" nv:"+newVersion);
            db.execSQL(LOGIN_TABLE_CREATE);

            COPY_TABLE="INSERT INTO login  SELECT * FROM tlogin ;";
            Log.i("addcolumn",COPY_TABLE+" ov:"+oldVersion+" nv:"+newVersion);
            db.execSQL(COPY_TABLE);

            REMOVE_TABLE="DROP TABLE tlogin";
            Log.i("addcolumn",REMOVE_TABLE+" ov:"+oldVersion+" nv:"+newVersion);
            db.execSQL(REMOVE_TABLE);




        }


        //Agregando coordenadas al usuario
        if(oldVersion==5 && newVersion==6) {
            ADD_COLUM_LOGIN = "ALTER TABLE login ADD COLUMN avatar Text DEFAULT '' ";
            Log.i("addcolumn",ADD_COLUM_LOGIN);
            db.execSQL(ADD_COLUM_LOGIN);
            ADD_COLUM_LOGIN = "ALTER TABLE login ADD COLUMN ubicacion Text DEFAULT '' ";
            Log.i("addcolumn",ADD_COLUM_LOGIN);
            db.execSQL(ADD_COLUM_LOGIN);

            AVISO_NOTIFICA= "CREATE TABLE IF NOT EXISTS  aviso_notifica(" +
                    "id_aviso TEXT PRIMARY KEY NOT NULL)" ;
            ALERTA_NOTIFICA= "CREATE TABLE IF NOT EXISTS  alerta_notifica(" +
                    "id_alerta TEXT PRIMARY KEY NOT NULL)" ;

            db.execSQL(AVISO_NOTIFICA);
            db.execSQL(ALERTA_NOTIFICA);
        }

    }
}
