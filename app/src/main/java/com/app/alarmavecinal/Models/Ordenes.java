package com.app.alarmavecinal.Models;

public class Ordenes {

    int orden;
    String id;
    String id_usuario;
    String nombre;
    String mensaje;
    String ubicacion;
    String fecha;

    public Ordenes() {
    }

    public Ordenes(int orden, String id, String id_usuario, String nombre, String mensaje, String ubicacion, String fecha) {
        this.orden = orden;
        this.id = id;
        this.id_usuario = id_usuario;
        this.nombre = nombre;
        this.mensaje = mensaje;
        this.ubicacion = ubicacion;
        this.fecha = fecha;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
