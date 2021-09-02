package com.app.alarmavecinal;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.app.alarmavecinal.Sqlite.Base;
import com.app.alarmavecinal.Usuario.Login.LoginView;

public class Portada extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portada);
        Empezar();
    }

    void Empezar(){
        new Handler().postDelayed(new Runnable(){
            public void run(){
                // Cuando pasen los 3 segundos, pasamos a la actividad principal de la aplicación
                if( Check_Log()){
                    startActivity(new Intent(Portada.this,Principal.class));
                }else{
                    Intent intent = new Intent(Portada.this, LoginView.class);
                    startActivity(intent);
                }

                finish();
            };
        }, 1000);
    }

    public boolean Check_Log() {

        try {
            Base base = new Base(this);
            SQLiteDatabase db = base.getWritableDatabase();


            String nombre="";

            Cursor c =  db.rawQuery("SELECT * from login ",null);
            c.moveToFirst();
            int cont=c.getCount();
            c.close();
            db.close();


            if(cont>0)
            {
                return true;
            }
        }catch (Exception e){}


        return false;


    }
}
