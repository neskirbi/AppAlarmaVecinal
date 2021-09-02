package com.app.alarmavecinal.Estructuras;

public class AlertasL {
    String id_alerta;
    String id_usuario;
    String titulo;
    String imagen;
    String nombre;
    String fecha;
    String json;

    public AlertasL() {

    }
    public AlertasL(String id_alerta,String id_usuario,String imagen,String titulo,String nombre, String fecha,String json) {
        this.id_alerta = id_alerta;
        this.id_usuario = id_usuario;
        this.imagen = imagen;
        this.titulo = titulo;
        this.nombre = nombre;
        this.fecha = fecha;
        this.json = json;
    }

    public String getId_alerta() {
        return id_alerta;
    }

    public void setId_alerta(String id_alerta) {
        this.id_alerta = id_alerta;
    }

    public String getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(String id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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
