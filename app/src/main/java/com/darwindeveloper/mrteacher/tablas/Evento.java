package com.darwindeveloper.mrteacher.tablas;

/**
 * modelo para la tabla eventos
 * Created by Darwin Morocho on 16/3/2017.
 */

public class Evento {
    private String id, nombre, fecha_creacion, fecha, observaciones, alarma_id, carrera_id, materia_id;


    public Evento(String id, String nombre, String fecha_creacion, String fecha, String observaciones, String alarma_id, String carrera_id) {
        this.id = id;
        this.nombre = nombre;
        this.fecha_creacion = fecha_creacion;
        this.fecha = fecha;
        this.observaciones = observaciones;
        this.alarma_id = alarma_id;
        this.carrera_id = carrera_id;
    }

    public Evento(String id, String nombre, String fecha_creacion, String fecha, String observaciones, String alarma_id, String carrera_id, String materia_id) {
        this.id = id;
        this.nombre = nombre;
        this.fecha_creacion = fecha_creacion;
        this.fecha = fecha;
        this.observaciones = observaciones;
        this.alarma_id = alarma_id;
        this.carrera_id = carrera_id;
        this.materia_id = materia_id;
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

    public String getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(String fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getMateria_id() {
        return materia_id;
    }

    public void setMateria_id(String materia_id) {
        this.materia_id = materia_id;
    }

    public String getAlarma_id() {
        return alarma_id;
    }

    public void setAlarma_id(String alarma_id) {
        this.alarma_id = alarma_id;
    }

    public String getCarrera_id() {
        return carrera_id;
    }

    public void setCarrera_id(String carrera_id) {
        this.carrera_id = carrera_id;
    }
}
