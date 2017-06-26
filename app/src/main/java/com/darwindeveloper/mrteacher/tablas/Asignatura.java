package com.darwindeveloper.mrteacher.tablas;

/**
 * modelo para la tabla carreras
 *
 * @author Darwin Morocho
 */

public class Asignatura {
    private String id, nombre, carrera_id;
    private int numerp_estudiantes;

    public Asignatura(String id, String nombre, String carrera_id, int numerp_estudiantes) {
        this.id = id;
        this.nombre = nombre;
        this.carrera_id = carrera_id;
        this.numerp_estudiantes = numerp_estudiantes;
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

    public String getCarrera_id() {
        return carrera_id;
    }

    public void setCarrera_id(String carrera_id) {
        this.carrera_id = carrera_id;
    }

    public int getNumerp_estudiantes() {
        return numerp_estudiantes;
    }

    public void setNumerp_estudiantes(int numerp_estudiantes) {
        this.numerp_estudiantes = numerp_estudiantes;
    }
}
