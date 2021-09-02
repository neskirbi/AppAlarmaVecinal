package com.app.alarmavecinal.Estructuras;

public class Mensaje {
    String id_mensaje;
    String id_usuario;
    String nombre;
    String mensaje;
    String audio;
    String imagen;
    String file;
    String nombrefile;
    String ubicacion;
    String fecha;

    public Mensaje() {
    }

    public Mensaje(String id_mensaje,String id_usuario,String nombre, String mensaje, String audio, String imagen, String file, String nombrefile,String ubicacion,String fecha) {
        this.id_mensaje = id_mensaje;
        this.id_usuario = id_usuario;
        this.nombre = nombre;
        this.mensaje = mensaje;
        this.audio = audio;
        this.imagen = imagen;
        this.file = file;
        this.nombrefile = nombrefile;
        this.ubicacion = ubicacion;
        this.fecha = fecha;
    }

    public String getId_mensaje() {
        return id_mensaje;
    }

    public void setId_mensaje(String id_mensaje) {
        this.id_mensaje = id_mensaje;
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

    public String getMensaje() {
        return mensaje;
    }

    public String getAudio() {
        return audio;
    }

    public String getImagen() {
        return imagen;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getNombrefile() {
        return nombrefile;
    }

    public void setNombrefile(String nombrefile) {
        this.nombrefile = nombrefile;
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

    public void setAudio(String audio) {
        this.audio = audio;
    }
}
