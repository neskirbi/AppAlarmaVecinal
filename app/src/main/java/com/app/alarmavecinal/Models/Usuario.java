package com.app.alarmavecinal.Models;

public class Usuario {
    String id_usuario;
    String id_grupo;
    String nombres;
    String apellidos;
    String direccion;
    String mail;
    String pass;
    String avatar;
    String ubicacion;
    String nombre;
    String error;

    public Usuario(String id_usuario, String id_grupo, String nombres, String apellidos, String direccion, String mail, String pass, String avatar, String ubicacion, String nombre, String error) {
        this.id_usuario = id_usuario;
        this.id_grupo = id_grupo;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.direccion = direccion;
        this.mail = mail;
        this.pass = pass;
        this.avatar = avatar;
        this.ubicacion = ubicacion;
        this.nombre = nombre;
        this.error = error;
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

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
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
        this.error = nombre;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
