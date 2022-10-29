package com.app.alarmavecinal.Avisos;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.app.alarmavecinal.BuildConfig;
import com.app.alarmavecinal.Estructuras.Avisos;
import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewAvisos extends AppCompatActivity {
    AvisoPresenter avisoPresenter;
    Funciones funciones;
    Context context;
    EditText asunto,mensaje;
    TextView quien;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_aviso);
        context=this;
        funciones=new Funciones(context);
        avisoPresenter=new AvisoPresenter(this,context);

        AdView m = findViewById(R.id.banner);
        AdRequest adRequest = null;
        if (BuildConfig.DEBUG) {
            adRequest=new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
            funciones.Logo("anuncio","debug");
        }else{
            adRequest=new AdRequest.Builder().build();
        }
        m.loadAd(adRequest);


        asunto=findViewById(R.id.asunto);
        mensaje=findViewById(R.id.mensaje);
    }

    public void Enviar(View view){

        avisoPresenter.EnviarAviso(asunto.getText().toString(),mensaje.getText().toString());
        finish();

    }
}
