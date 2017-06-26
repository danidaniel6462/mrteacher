package com.darwindeveloper.mrteacher.tablas;

/**
 * Created by Darwin Morocho on 7/3/2017.
 */

public class InstitucionEducativa {
    private String id, nombre, siglas, observaciones;

    public InstitucionEducativa(String id, String nombre, String siglas, String observaciones) {
        this.id = id;
        this.nombre = nombre;
        this.siglas = siglas;
        this.observaciones = observaciones;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSiglas() {
        return siglas;
    }

    public void setSiglas(String siglas) {
        this.siglas = siglas;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
