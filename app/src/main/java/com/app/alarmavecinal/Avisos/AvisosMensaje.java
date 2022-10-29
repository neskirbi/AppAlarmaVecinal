package com.app.alarmavecinal.FuncioneAvisos;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.R;

import org.json.JSONException;
import org.json.JSONObject;

public class AvisosMensaje extends AppCompatActivity {
TextView quien,titulo,mensaje;
Context context;
Funciones funciones;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avisos_mensaje);
        context=this;
        funciones=new Funciones(context);
        Intent intent=getIntent();
        quien=findViewById(R.id.quien);
        titulo=findViewById(R.id.titulo);
        mensaje=findViewById(R.id.mensaje);
        funciones.Logo("mensajes",intent.getExtras().getString("mensaje"));

        try {
            JSONObject jsonObject =new JSONObject(intent.getExtras().getString("mensaje"));

            quien.setText(jsonObject.get("nombres").toString()+" "+jsonObject.get("apellidos").toString());
            titulo.setText(funciones.ToString(jsonObject.get("asunto").toString()));
            mensaje.setText(funciones.ToString(jsonObject.get("mensaje").toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
