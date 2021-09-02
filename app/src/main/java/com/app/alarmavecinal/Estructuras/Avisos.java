package com.app.alarmavecinal.Estructuras;

public class Avisos {
    String id_aviso;
    String id_usuario;
    String titulo;
    String mensaje;
    String fecha;
    String json;

    public Avisos() {

    }

    public Avisos(String id_aviso, String id_usuario, String titulo, String mensaje, String fecha, String json) {
        this.id_aviso = id_aviso;
        this.id_usuario = id_usuario;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.fecha = fecha;
        this.json = json;
    }

    public String getId_aviso() {
        return id_aviso;
    }

    public void setId_aviso(String id_aviso) {
        this.id_aviso = id_aviso;
    }

    public String getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(String id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}

