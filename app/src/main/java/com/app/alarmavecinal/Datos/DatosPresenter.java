package com.app.alarmavecinal.Datos;

import android.content.Context;

import com.app.alarmavecinal.Metodos;
import com.google.gson.JsonArray;

public class DatosPresenter implements Datos.Datospresenter {
    DatosView datosView;
    Context context;
    Metodos metodos;
    DatosInteractor datosInteractor;
    public DatosPresenter(DatosView datosView,Context context) {
        this.datosView=datosView;
        this.context=context;
        metodos=new Metodos(context);
        datosInteractor=new DatosInteractor(this,context);
    }

    @Override
    public void GetDatos() {
        datosInteractor.GetDatos();

    }

    @Override
    public void MostrarDatos(JsonArray body) {
        datosView.MostrarDatos(body);
    }

    @Override
    public void Actualizar(String nombres, String apellidos, String direccion) {
        datosInteractor.Actualizar(nombres,apellidos,direccion);
    }


}
