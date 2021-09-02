package com.app.alarmavecinal.Estructuras;

public class Emergencias {

    String id_emergencia;
    String id_usuario;
    String nombre;
    String mensaje;
    String ubicacion;
    String fecha;


    public Emergencias() {

    }

    public Emergencias(String id_emergencia,String id_usuario,String nombre, String mensaje,String ubicacion,String fecha) {
        this.id_emergencia = id_emergencia;
        this.id_usuario = id_usuario;
        this.nombre = nombre;
        this.mensaje = mensaje;
        this.ubicacion = ubicacion;
        this.fecha = fecha;
    }


    public String getId_emergencia() {
        return id_emergencia;
    }

    public void setId_emergencia(String id_emergencia) {
        this.id_emergencia = id_emergencia;
    }

    public String getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(String id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
