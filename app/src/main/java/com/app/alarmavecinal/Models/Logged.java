package com.app.alarmavecinal.Models;

public class Logged {
    String id_usuario;
    String id_grupo;
    String nombres;
    String apellidos;
    String direccion;
    String mail;
    String avatar;
    String ubicacion;
    String nombre;

    public Logged(String id_usuario, String id_grupo, String nombres, String apellidos, String direccion, String mail, String avatar, String ubicacion, String nombre) {
        this.id_usuario = id_usuario;
        this.id_grupo = id_grupo;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.direccion = direccion;
        this.mail = mail;
        this.avatar = avatar;
        this.ubicacion = ubicacion;
        this.nombre = nombre;
    }

    public String getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(String id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getId_grupo() {
        return id_grupo;
    }

    public void setId_grupo(String id_grupo) {
        this.id_grupo = id_grupo;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
