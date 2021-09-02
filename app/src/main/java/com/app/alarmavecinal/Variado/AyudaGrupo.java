package com.app.alarmavecinal.Variado;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.app.alarmavecinal.R;

public class AyudaGrupo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayuda_grupo);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}