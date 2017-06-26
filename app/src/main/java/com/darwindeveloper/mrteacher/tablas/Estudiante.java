package com.darwindeveloper.mrteacher.tablas;

/**
 * Created by Darwin Morocho on 15/3/2017.
 */

public class Estudiante {
    private String id, nombres, apellidos, email, telefono, descripcion, carrera_id;
    private boolean selected;

    public Estudiante(String id, String nombres, String apellidos, String email, String telefono, String descripcion, String carrera_id) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.email = email;
        this.telefono = telefono;
        this.descripcion = descripcion;
        this.carrera_id = carrera_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCarrera_id() {
        return carrera_id;
    }

    public void setCarrera_id(String carrera_id) {
        this.carrera_id = carrera_id;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
