package com.darwindeveloper.mrteacher.tablas;

/**
 * Created by Darwin Morocho on 12/3/2017.
 */

public class Carrera {
    private String id, nombre, institucion_id;

    public Carrera(String id, String nombre, String institucion_id) {
        this.id = id;
        this.nombre = nombre;
        this.institucion_id = institucion_id;
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

    public String getInstitucion_id() {
        return institucion_id;
    }

    public void setInstitucion_id(String institucion_id) {
        this.institucion_id = institucion_id;
    }
}
