package com.app.alarmavecinal.Models;

public class Grupo {
    private String id_grupo;
    private String id_usuario;
    private String nombre;
    private String error;

    public Grupo(String id_grupo, String id_usuario, String nombre, String error) {
        this.id_grupo = id_grupo;
        this.id_usuario = id_usuario;
        this.nombre = nombre;
        this.error = error;
    }

    public String getId_grupo() {
        return id_grupo;
    }

    public void setId_grupo(String id_grupo) {
        this.id_grupo = id_grupo;
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

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
