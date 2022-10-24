package com.app.alarmavecinal.Datos;

import com.google.gson.JsonArray;

public interface Datos {
    interface DatosView{
        void MostrarDatos(JsonArray body);

    }

    interface Datospresenter{
        void GetDatos();
        void MostrarDatos(JsonArray body);
        void Actualizar(String nombres,String apellidos,String direccion,String ubicacion);
    }

    interface DatosInteractor{
        void GetDatos();
        void Actualizar(String nombres,String apellidos,String direccion,String ubicacion);
    }
}
