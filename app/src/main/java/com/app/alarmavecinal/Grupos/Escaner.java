package com.app.alarmavecinal.Grupos;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.app.alarmavecinal.Funciones;
import com.app.alarmavecinal.R;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Escaner extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    private ZXingScannerView escanerZXing;
    Funciones funciones;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escaner);
        context=this;
        funciones=new Funciones(context);

        escanerZXing = new ZXingScannerView(this);
        // Hacer que el contenido de la actividad sea el escaner
        setContentView(escanerZXing);
    }


    @Override
    public void onResume() {
        super.onResume();
        // El "manejador" del resultado es esta misma clase, por eso implementamos ZXingScannerView.ResultHandler
        escanerZXing.setResultHandler(this);
        escanerZXing.startCamera(); // Comenzar la cámara en onResume
    }

    @Override
    public void onPause() {
        super.onPause();
        escanerZXing.stopCamera(); // Pausar en onPause
    }

    // Estamos sobrescribiendo un método de la interfaz ZXingScannerView.ResultHandler
    @Override
    public void handleResult(Result resultado) {

        // Si quieres que se siga escaneando después de haber leído el código, descomenta lo siguiente:
        // Si la descomentas no recomiendo que llames a finish
//        escanerZXing.resumeCameraPreview(this);
        // Obener código/texto leído
        String codigo = resultado.getText();
        // Preparar un Intent para regresar datos a la actividad que nos llamó
        Intent intentRegreso = new Intent();
        intentRegreso.putExtra("codigo", codigo);
        setResult(Grupo.RESULT_OK, intentRegreso);
        funciones.Logo("Escaner1",codigo);
        // Cerrar la actividad. Ahora mira onActivityResult de MainActivity
        finish();
    }
}
