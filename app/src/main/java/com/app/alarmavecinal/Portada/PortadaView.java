package com.app.alarmavecinal.Portada;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.app.alarmavecinal.Metodos;
import com.app.alarmavecinal.Principal.PrincipalView;
import com.app.alarmavecinal.R;
import com.app.alarmavecinal.Sqlite.Base;
import com.app.alarmavecinal.Usuario.Login.LoginView;

public class PortadaView extends AppCompatActivity implements Portada.PortadaView {


    PortadaPresenter portadaPresenter;
    Context context;
    Metodos metodos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portada);
        context=this;
        metodos=new Metodos(this);
        portadaPresenter=new PortadaPresenter(this,context);
        Empezar();
    }

    void Empezar(){
        new Handler().postDelayed(new Runnable(){
            public void run(){
                // Cuando pasen los 3 segundos, pasamos a la actividad principal de la aplicación
                if( metodos.Check_Log()){
                    portadaPresenter.GetGrupo();

                }else{
                    Intent intent = new Intent(PortadaView.this, LoginView.class);
                    startActivity(intent);
                }

                finish();
            };
        }, 1000);
    }




    @Override
    public void IrPrincipal() {
        startActivity(new Intent(PortadaView.this, PrincipalView.class));
    }
}
