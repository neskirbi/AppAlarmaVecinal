package com.app.alarmavecinal.FuncioneAvisos;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.alarmavecinal.BuildConfig;
import com.app.alarmavecinal.Estructuras.AlertasL;
import com.app.alarmavecinal.Estructuras.Avisos;
import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

public class NewAvisos extends AppCompatActivity {
    Funciones funciones;
    Context context;
    EditText asunto,mensaje;
    TextView quien;


    FirebaseDatabase firebaseDatabaseAvisos;
    DatabaseReference databaseReferenceAvisos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_aviso);
        context=this;
        funciones=new Funciones(context);

        AdView m = findViewById(R.id.banner);
        AdRequest adRequest = null;
        if (BuildConfig.DEBUG) {
            adRequest=new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
            funciones.Logo("anuncio","debug");
        }else{
            adRequest=new AdRequest.Builder().build();
        }
        m.loadAd(adRequest);


        quien=findViewById(R.id.quien);
        asunto=findViewById(R.id.asunto);
        mensaje=findViewById(R.id.mensaje);
        quien.setText(funciones.GetNombre());
    }

    public void Enviar(View view){

        firebaseDatabaseAvisos = FirebaseDatabase.getInstance();
        databaseReferenceAvisos = firebaseDatabaseAvisos.getReference("Avisos-"+funciones.GetIdGrupo());//Sala de chat
        databaseReferenceAvisos.push().setValue(new Avisos(funciones.GetUIID(),funciones.GetIdUsuario(),asunto.getText().toString(),mensaje.getText().toString(),funciones.GetDate(),""));
        finish();

    }
}
