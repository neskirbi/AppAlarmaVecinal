package com.app.alarmavecinal.Debug;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.alarmavecinal.R;

public class VisorTexto extends AppCompatActivity {
TextView visor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visor_texto);
        visor=findViewById(R.id.visor);

        visor.setText(getIntent().getStringExtra("string"));
    }
}
