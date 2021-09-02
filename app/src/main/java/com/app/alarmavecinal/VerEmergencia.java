package com.app.alarmavecinal;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.app.alarmavecinal.LoginPack.Login;
import com.google.firebase.analytics.FirebaseAnalytics;

public class VerEmergencia extends AppCompatActivity {

    Funciones funciones;
    Context context;
    String respuesta="";
    TextView nombre,direccion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergencia);
        context=this;
        funciones=new Funciones(context);
        setTitle("¡Emergencia!");
        nombre=findViewById(R.id.nombre);
        direccion=findViewById(R.id.direccion);
    }

    @Override
    protected void onResume() {
        super.onResume();
        nombre.setText(getIntent().getStringExtra("nombre"));
        direccion.setText(getIntent().getStringExtra("direccion"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(context, Login.class));

    }
}
