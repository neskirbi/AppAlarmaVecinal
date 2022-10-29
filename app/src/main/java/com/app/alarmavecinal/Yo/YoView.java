package com.app.alarmavecinal.Yo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.R;
import com.app.alarmavecinal.Yo.Datos.DatosView;
import com.app.alarmavecinal.Yo.Pass.PassView;

public class YoView extends AppCompatActivity {

    LinearLayout datos,pass,propinas;
    Context context;
    Funciones funciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yo_view);
        context=this;
        funciones=new Funciones(context);
        this.setTitle("Yo");
        datos=findViewById(R.id.datos);
        pass=findViewById(R.id.pass);
        propinas=findViewById(R.id.propinas);

        datos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funciones.Vibrar(funciones.VibrarPush());
                startActivity(new Intent(context, DatosView.class));
            }
        });

        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funciones.Vibrar(funciones.VibrarPush());
                startActivity(new Intent(context, PassView.class));
            }
        });

        propinas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funciones.Vibrar(funciones.VibrarPush());
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/donate?hosted_button_id=HM368K4HZ4RC6"));
                startActivity(browserIntent);
            }
        });
    }
}