package com.app.alarmavecinal.Estructuras;

public class Alertas {
    String id_alerta;
    String imagen;
    String titulo;
    String fecha;
    String json;

    public Alertas(String id_alerta,String imagen,String titulo, String fecha,String json) {
        this.id_alerta = id_alerta;
        this.imagen = imagen;
        this.titulo = titulo;
        this.fecha = fecha;
        this.json = json;
    }



    public String getId_alerta() {
        return id_alerta;
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

    public String getFecha() {
        return fecha;
    }



    public String getJson() {
        return json;
    }
}
