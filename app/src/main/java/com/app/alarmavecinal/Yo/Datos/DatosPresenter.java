package com.app.alarmavecinal.Yo.Datos;

import android.content.Context;

import com.app.alarmavecinal.Funciones;
import com.google.gson.JsonArray;

public class DatosPresenter implements Datos.Datospresenter {
    DatosView datosView;
    Context context;
    Funciones funciones;
    DatosInteractor datosInteractor;
    public DatosPresenter(DatosView datosView,Context context) {
        this.datosView=datosView;
        this.context=context;
        funciones=new Funciones(context);
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
    public void Actualizar(String nombres, String apellidos, String direccion,String ubicacion ) {
        datosInteractor.Actualizar(nombres,apellidos,direccion,ubicacion);
    }


}
